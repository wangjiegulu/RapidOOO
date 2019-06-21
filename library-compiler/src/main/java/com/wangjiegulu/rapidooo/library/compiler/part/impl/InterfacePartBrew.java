package com.wangjiegulu.rapidooo.library.compiler.part.impl;

import com.google.auto.common.MoreTypes;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.RapidOOOConstants;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOFieldEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.PartBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable.ParcelableBoxPrimitiveStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable.ParcelableListStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable.ParcelableObjectStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable.ParcelablePrimitiveStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-14.
 */
public class InterfacePartBrew implements PartBrew {

    private List<IParcelableStatementBrew> parcelableStatementBrews = new ArrayList<>();

    public InterfacePartBrew() {
        parcelableStatementBrews.add(new ParcelablePrimitiveStatementBrew());
        parcelableStatementBrews.add(new ParcelableBoxPrimitiveStatementBrew());
        parcelableStatementBrews.add(new ParcelableObjectStatementBrew());
        parcelableStatementBrews.add(new ParcelableListStatementBrew());
    }

    @Override
    public void brew(OOOEntry oooEntry, TypeSpec.Builder result) {
        for(Map.Entry<String, TypeMirror> ee : oooEntry.getSupportedInterfaces().entrySet()){
            String interfaceName = ee.getKey();
            TypeMirror interf = ee.getValue();
            if(Serializable.class.getCanonicalName().equals(interfaceName)){
                result.addSuperinterface(ClassName.get(interf));
            } else if(RapidOOOConstants.PARCELABLE_CLASS_NAME.equals(interfaceName)){
                result.addSuperinterface(ClassName.get(interf));
                boolean supperParcelableInterface = ElementUtil.isSupperParcelableInterfaceDeep(MoreTypes.asElement(oooEntry.getFrom()));
                generateParcelableElements(result, oooEntry, supperParcelableInterface);
            }
        }
    }

    private void generateParcelableElements(TypeSpec.Builder result, OOOEntry oooEntry, boolean supperParcelableInterface) {
        ClassName parcelClassName = ClassName.bestGuess("android.os.Parcel");
        MethodSpec.Builder parcelConstructorMethodBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PROTECTED)
                .addParameter(parcelClassName, "parcel");

        if (supperParcelableInterface) {
            parcelConstructorMethodBuilder.addStatement("super(parcel)");
        }

        String targetClassSimpleName = oooEntry.getTargetClassSimpleName();
        FieldSpec.Builder fieldSpec = FieldSpec.builder(EasyType.bestGuessDeep2("android.os.Parcelable.Creator<" + targetClassSimpleName + ">"), "CREATOR", Modifier.STATIC, Modifier.PUBLIC)
                .initializer("new Parcelable.Creator<" + targetClassSimpleName + ">() {\n" +
                        "        @Override\n" +
                        "        public " + targetClassSimpleName + " createFromParcel($T source) {\n" +
                        "            return new " + targetClassSimpleName + "(source);\n" +
                        "        }\n" +
                        "\n" +
                        "        @Override\n" +
                        "        public " + targetClassSimpleName + "[] newArray(int size) {\n" +
                        "            return new " + targetClassSimpleName + "[size];\n" +
                        "        }\n" +
                        "    }", parcelClassName);
        result.addField(fieldSpec.build());

        // describeContents()
        MethodSpec.Builder describeContentsMethod = MethodSpec.methodBuilder("describeContents")
                .returns(int.class)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return 0");

        result.addMethod(describeContentsMethod.build());


        MethodSpec.Builder writeToParcelMethod = MethodSpec.methodBuilder("writeToParcel")
                .returns(void.class)
                .addAnnotation(Override.class)
                .addParameter(parcelClassName, "dest")
                .addParameter(int.class, "flags")
                .addModifiers(Modifier.PUBLIC);

        if (supperParcelableInterface) {
            writeToParcelMethod.addStatement("super.writeToParcel(dest, flags)");
        }


        // Continuing fields
        for (Map.Entry<String, OOOFieldEntry> fieldE : oooEntry.getAllContinuingFields().entrySet()) {
            OOOFieldEntry fieldEntry = fieldE.getValue();

            if(fieldEntry.isParcelable()){
                String fieldName = fieldEntry.getSimpleName();
                for(IParcelableStatementBrew parcelableStatementBrew : parcelableStatementBrews){
                    TypeName fieldTypeName = fieldEntry.getTypeName();
                    LogUtil.logger("[fieldTypeName]" + fieldTypeName.getClass() + ", " + fieldTypeName);
                    if(parcelableStatementBrew.match(fieldEntry)){
                        parcelableStatementBrew.read(parcelConstructorMethodBuilder, fieldName, fieldEntry.getOooTypeEntry());
                        parcelableStatementBrew.write(writeToParcelMethod, fieldName, fieldEntry.getOooTypeEntry());
                        break;
                    }
                }
            }
        }
        // Conversion fields
        for (Map.Entry<String, OOOConversionEntry> conversionFieldE : oooEntry.getConversions().entrySet()) {
            OOOConversionEntry conversionEntry = conversionFieldE.getValue();

            String fieldName = conversionEntry.getTargetFieldName();
            for(IParcelableStatementBrew parcelableStatementBrew : parcelableStatementBrews){
                if(parcelableStatementBrew.match(conversionEntry)){
                    parcelableStatementBrew.read(parcelConstructorMethodBuilder, fieldName, conversionEntry.getTargetFieldTypeEntry());
                    parcelableStatementBrew.write(writeToParcelMethod, fieldName, conversionEntry.getTargetFieldTypeEntry());
                    break;
                }
            }
        }

        result.addMethod(parcelConstructorMethodBuilder.build());
        result.addMethod(writeToParcelMethod.build());
    }
}
