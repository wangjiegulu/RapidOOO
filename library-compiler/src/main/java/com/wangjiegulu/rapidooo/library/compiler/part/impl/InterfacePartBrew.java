package com.wangjiegulu.rapidooo.library.compiler.part.impl;

import com.google.auto.common.MoreTypes;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.RapidOOOConstants;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOFieldEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.PartBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable.ParcelableStatementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;

import java.io.Serializable;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-14.
 */
public class InterfacePartBrew implements PartBrew {

    public InterfacePartBrew() {

    }

    @Override
    public void brew(OOOEntry oooEntry, TypeSpec.Builder result) {
        for(Map.Entry<String, TypeMirror> ee : oooEntry.getSupportedInterfaces().entrySet()){
            String interfaceName = ee.getKey();
            TypeMirror interf = ee.getValue();
            if(Serializable.class.getCanonicalName().equals(interfaceName)){
                result.addSuperinterface(ClassName.get(interf));
            } else if(RapidOOOConstants.CLASS_NAME_PARCELABLE.equals(interfaceName)){
                if(oooEntry.isParcelable()){
                    result.addSuperinterface(ClassName.get(interf));
                    boolean supperParcelableInterface = ElementUtil.isSupperParcelableInterfaceDeep(MoreTypes.asElement(oooEntry.getFrom()));
                    generateParcelableElements(result, oooEntry, supperParcelableInterface);
                }
            } else {
                LogUtil.logger("[WARN]Interface[" + interf.toString() + "] is not supported yet, ignored.");
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
        FieldSpec.Builder fieldSpec = FieldSpec.builder(EasyType.bestGuessDeep2(RapidOOOConstants.CLASS_NAME_PARCELABLE + ".Creator<" + targetClassSimpleName + ">"), "CREATOR", Modifier.STATIC, Modifier.PUBLIC)
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
                for(IParcelableStatementBrew parcelableStatementBrew : ParcelableStatementUtil.parcelableStatementBrews){
//                    TypeName fieldTypeName = fieldEntry.getTypeName();
//                    LogUtil.logger("[fieldTypeName]" + fieldTypeName.getClass() + ", " + fieldTypeName);
                    if(parcelableStatementBrew.match(fieldEntry.getOooTypeEntry())){
                        parcelableStatementBrew.read(parcelConstructorMethodBuilder, "", RapidOOOConstants.EMPTY_ARRAY, "this." + fieldName, fieldEntry.getOooTypeEntry(), fieldName);
                        parcelableStatementBrew.write(writeToParcelMethod, "", RapidOOOConstants.EMPTY_ARRAY, "this." + fieldName, fieldEntry.getOooTypeEntry(), fieldName);
                        break;
                    }
                }
            }
        }
        // Conversion fields
        for (Map.Entry<String, OOOConversionEntry> conversionFieldE : oooEntry.getConversions().entrySet()) {
            OOOConversionEntry conversionEntry = conversionFieldE.getValue();
            if(conversionEntry.isParcelable()){
                String fieldName = conversionEntry.getTargetFieldName();
                for(IParcelableStatementBrew parcelableStatementBrew : ParcelableStatementUtil.parcelableStatementBrews){
                    if(parcelableStatementBrew.match(conversionEntry.getTargetFieldTypeEntry())){
                        parcelableStatementBrew.read(parcelConstructorMethodBuilder, "", RapidOOOConstants.EMPTY_ARRAY, "this." + fieldName, conversionEntry.getTargetFieldTypeEntry(), fieldName);
                        parcelableStatementBrew.write(writeToParcelMethod, "", RapidOOOConstants.EMPTY_ARRAY, "this." + fieldName, conversionEntry.getTargetFieldTypeEntry(), fieldName);
                        break;
                    }
                }
            }
        }

        result.addMethod(parcelConstructorMethodBuilder.build());
        result.addMethod(writeToParcelMethod.build());
    }
}
