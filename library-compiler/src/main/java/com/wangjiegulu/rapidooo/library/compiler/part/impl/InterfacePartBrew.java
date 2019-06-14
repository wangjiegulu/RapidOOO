package com.wangjiegulu.rapidooo.library.compiler.part.impl;

import com.google.auto.common.MoreTypes;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.exception.RapidOOOCompileException;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.LogicUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TypeNameUtil;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOFieldEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.PartBrew;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-14.
 */
public class InterfacePartBrew implements PartBrew {
    @Override
    public void brew(OOOEntry oooEntry, TypeSpec.Builder result) {

        List<? extends TypeMirror> interfaces = MoreTypes.asTypeElement(oooEntry.getFrom()).getInterfaces();
        if (null != interfaces && interfaces.size() > 0) {
            for (TypeMirror interf : interfaces) {
                if (ElementUtil.isSameType(interf, Serializable.class)) {
                    result.addSuperinterface(ClassName.get(interf));
                } else if (ElementUtil.isSameType(interf, ClassName.bestGuess("android.os.Parcelable"))) {
                    result.addSuperinterface(ClassName.get(interf));
                    boolean supperParcelableInterface = ElementUtil.isSupperParcelableInterfaceDeep(MoreTypes.asElement(oooEntry.getFrom()));
                    generateParcelableElements(result, oooEntry, supperParcelableInterface);
                } else {
                    throw new RapidOOOCompileException("Not supported super interface [" + interf.toString() + "] for " + oooEntry.getFromClassName().toString());
                }
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

            addParcelableReadStatement(parcelConstructorMethodBuilder, fieldEntry.getSimpleName(), fieldEntry.getTypeName());
            addParcelableWriteStatement(writeToParcelMethod, fieldEntry.getSimpleName(), fieldEntry.getTypeName());
        }
        // Conversion fields
        for (Map.Entry<String, OOOConversionEntry> conversionFieldE : oooEntry.getConversions().entrySet()) {
            OOOConversionEntry conversionEntry = conversionFieldE.getValue();

            addParcelableReadStatement(parcelConstructorMethodBuilder, conversionEntry.getTargetFieldName(), conversionEntry.getTargetFieldType());
            addParcelableWriteStatement(writeToParcelMethod, conversionEntry.getTargetFieldName(), conversionEntry.getTargetFieldType());
        }

        result.addMethod(parcelConstructorMethodBuilder.build());
        result.addMethod(writeToParcelMethod.build());
    }

    private void addParcelableReadStatement(MethodSpec.Builder parcelConstructorMethodBuilder, String fieldName, TypeName fieldTypeName) {
        if (fieldTypeName.isPrimitive()) {
            parcelConstructorMethodBuilder.addStatement("this." + fieldName + " = " + LogicUtil.checkNullCondition(TypeNameUtil.getParcelablePrimitiveReadStatement(fieldTypeName),
                    "failed parcelable field: " + fieldTypeName + " " + fieldName
            ));
        } else if (fieldTypeName.isBoxedPrimitive()) {
            parcelConstructorMethodBuilder.addStatement("this." + fieldName + " = "
                    + LogicUtil.checkNullCondition(TypeNameUtil.getParcelableBoxPrimitiveReadStatement(fieldTypeName),
                    "failed parcelable field: " + fieldTypeName + " " + fieldName
            ));
        } else {
            parcelConstructorMethodBuilder.addStatement("this." + fieldName + " = " + LogicUtil.checkNullCondition(TypeNameUtil.getParcelableOtherReadStatement(fieldTypeName),
                    "failed parcelable field: " + fieldTypeName + " " + fieldName
            ));
        }
    }

    private void addParcelableWriteStatement(MethodSpec.Builder parcelConstructorMethodBuilder, String fieldName, TypeName fieldTypeName) {
        if (fieldTypeName.isPrimitive()) {
            parcelConstructorMethodBuilder.addStatement(LogicUtil.checkNullCondition(TypeNameUtil.getParcelablePrimitiveWriteStatement(fieldTypeName, fieldName),
                    "failed parcelable field: " + fieldTypeName + " " + fieldName
            ));
        } else if (fieldTypeName.isBoxedPrimitive()) {
            parcelConstructorMethodBuilder.addStatement("dest.writeValue(this." + fieldName + ")");
        } else {
            parcelConstructorMethodBuilder.addStatement(LogicUtil.checkNullCondition(TypeNameUtil.getParcelableOtherWriteStatement(fieldTypeName, fieldName),
                    "failed parcelable field: " + fieldTypeName + " " + fieldName
            ));
        }
    }
}
