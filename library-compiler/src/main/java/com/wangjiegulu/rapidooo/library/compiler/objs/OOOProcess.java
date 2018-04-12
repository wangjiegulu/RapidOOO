package com.wangjiegulu.rapidooo.library.compiler.objs;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Optional;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOConstants;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.library.compiler.util.GlobalEnvironment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 11/04/2018.
 */
public class OOOProcess {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:SSS", Locale.getDefault());

    private Element generatorClassEl;
    //    private List<FromElement> fromElements = new ArrayList<>();
//    OOOs ooosAnnotation;
    private FromEntry fromEntry;

    public void setGeneratorClassEl(Element mGeneratorClassEl) {
        this.generatorClassEl = mGeneratorClassEl;
        fromEntry = new FromEntry();
        fromEntry.setOoosAnno(mGeneratorClassEl.getAnnotation(OOOs.class));
    }


    public void brewJava(Filer filer) throws Throwable {
        fromEntry.parse();

        for (Map.Entry<String, FromElement> from : fromEntry.getAllFromElements().entrySet()) {
            FromElement fromElement = from.getValue();

            Element fromClassElement = fromElement.getElement();
            TypeName fromClassTypeName = ClassName.get(fromClassElement.asType());

            String fromSuffix = fromElement.getFromSuffix();
            String fromClassName = fromClassElement.getSimpleName().toString();
            // eg. replace "BO" when generate VO
            String targetClassSimpleName =
                    (oooParamIsNotSet(fromSuffix) ? fromClassName : fromClassName.substring(0, fromClassName.length() - fromSuffix.length()))
                            + fromElement.getSuffix();


            String targetPackage = generatorClassEl.getEnclosingElement().toString();

            TypeSpec.Builder result = TypeSpec.classBuilder(targetClassSimpleName)
                    .addModifiers(Modifier.PUBLIC)
                    .addJavadoc("From POJO: {@link $T}\n", fromClassTypeName);

            // FIXME: 12/04/2018 wangjie super class ?
            // super class
            Optional<DeclaredType> superClass = MoreTypes.nonObjectSuperclass(GlobalEnvironment.getProcessingEnv().getTypeUtils(),
                    GlobalEnvironment.getProcessingEnv().getElementUtils(), (DeclaredType) fromClassElement.asType());
            if (superClass.isPresent()) {
                result.superclass(ClassName.get(superClass.get().asElement().asType()));
            }

            // interfaces
            List<? extends TypeMirror> interfaces = MoreTypes.asTypeElement(fromClassElement.asType()).getInterfaces();
            if (null != interfaces && interfaces.size() > 0) {
                for (TypeMirror interf : interfaces) {
                    result.addSuperinterface(ClassName.get(interf));
                }
            }

            List<Element> fieldList = new ArrayList<>();
            List<? extends Element> eles = fromClassElement.getEnclosedElements();
            for (Element e : eles) {
                if (ElementKind.FIELD == e.getKind()) {
                    String fieldName = e.getSimpleName().toString();
//                    // TODO: 11/04/2018 wangjie
//                    FromFieldConversion fromFieldConversion = fromElement.getSpecialFromConversions().get(fieldName);
//                    if (null == fromFieldConversion) {
//
//                    } else {
//
//                    }

                    FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(ClassName.get(e.asType()), fieldName, getModifiersArray(e))
                            .addJavadoc("field name: {@link $T#$L}\n",
                                    fromClassTypeName,
                                    e.getSimpleName().toString()
                            );
                    result.addField(fieldSpecBuilder.build());
                    fieldList.add(e);

                    // getter / setter method
                    if (MoreElements.hasModifiers(Modifier.PRIVATE).apply(e)
                            || MoreElements.hasModifiers(Modifier.PROTECTED).apply(e)
                            ) {
                        addGetterSetterMethods(result, e);
                    }

                } else if (ElementKind.METHOD == e.getKind()) {
//                    MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(e.getSimpleName().toString());
//
//                    ExecutableType executableType = MoreTypes.asExecutable(e.asType());
//                    // parameters
//                    for(TypeMirror paramTypeMirror : executableType.getParameterTypes()){
//                        methodSpecBuilder.addParameter(
//                                ParameterSpec.get(MoreElements.asVariable(MoreTypes.asElement(paramTypeMirror)))
//                        );
//                    }
//
//                    // return type
//                    methodSpecBuilder.returns(ClassName.get(executableType.getReturnType()));
//
//                    MoreElements.asExecutable(e).

                }
            }

            // Constructor method
            MethodSpec.Builder defaultConstructorMethod = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC);
            result.addMethod(defaultConstructorMethod.build());


            String fromParamName = firstCharLower(fromClassName);

            // from to target constructor method
            MethodSpec.Builder constructorMethod = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(fromClassTypeName, fromParamName);
            for (Element fieldElement : fieldList) {
                GetterSetterMethodNames getterSetterMethodNames = generateGetterSetterMethodName(fieldElement);

                // TODO: 11/04/2018 wangjie conversion
                constructorMethod.addStatement(firstCharLower(fieldElement.getSimpleName().toString()) + " = " + fromParamName + "." + getterSetterMethodNames.getGetterMethodName() + "()");
            }
            result.addMethod(constructorMethod.build());

            // convert to from method
            MethodSpec.Builder toFromMethod = MethodSpec.methodBuilder("to" + fromClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(fromClassTypeName)
                    .addStatement(fromClassName + " " + fromParamName + " = new " + fromClassName + "()");
            for (Element fieldElement : fieldList) {
                GetterSetterMethodNames getterSetterMethodNames = generateGetterSetterMethodName(fieldElement);
                // TODO: 12/04/2018 wangjie conversion
                toFromMethod.addStatement(fromParamName + "." + getterSetterMethodNames.getSetterMethodName() + "(" + fieldElement.getSimpleName().toString() + ")");
            }
            toFromMethod.addStatement("return " + fromParamName);
            result.addMethod(toFromMethod.build());

            // TODO: 11/04/2018 wangjie

            JavaFile.builder(targetPackage, result.build())
                    .addFileComment("GENERATED CODE BY RapidOOO. DO NOT MODIFY! $S\nPOJOGenerator: $S",
                            DATE_FORMAT.format(new Date(System.currentTimeMillis())),
                            generatorClassEl.asType().toString())
                    .skipJavaLangImports(true)
                    .build()
                    .writeTo(filer);

        }

    }


    private void addGetterSetterMethods(TypeSpec.Builder typeSpecBuilder, Element fieldElement) {
        GetterSetterMethodNames getterSetterMethodNames = generateGetterSetterMethodName(fieldElement);

        TypeName fieldTypeName = ClassName.get(fieldElement.asType());
        String fieldName = fieldElement.getSimpleName().toString();

        MethodSpec.Builder getterMethodSpecBuilder = MethodSpec.methodBuilder(getterSetterMethodNames.getGetterMethodName())
                .addModifiers(Modifier.PUBLIC)
                .returns(fieldTypeName)
                .addStatement("return " + fieldName);

        typeSpecBuilder.addMethod(getterMethodSpecBuilder.build());


        MethodSpec.Builder setterMethodSpecBuilder = MethodSpec.methodBuilder(getterSetterMethodNames.getSetterMethodName())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldTypeName, fieldName)
                .returns(void.class)
                .addStatement("this." + fieldName + " = " + fieldName);

        typeSpecBuilder.addMethod(setterMethodSpecBuilder.build());

    }

    private GetterSetterMethodNames generateGetterSetterMethodName(Element fieldElement) {
        GetterSetterMethodNames getterSetterMethodNames = new GetterSetterMethodNames();

        String fieldName = fieldElement.getSimpleName().toString();
        String firstCharUpperFieldName = firstCharUpper(fieldName);

        if (ClassName.get(fieldElement.asType()) == TypeName.BOOLEAN) {
            if ("is".equalsIgnoreCase(fieldName.substring(0, 2))) {
                getterSetterMethodNames.setGetterMethodName(fieldName);
                getterSetterMethodNames.setSetterMethodName("set" + firstCharUpper(fieldName.substring(2)));
            } else {
                getterSetterMethodNames.setGetterMethodName("is" + firstCharUpperFieldName);
                getterSetterMethodNames.setSetterMethodName("set" + firstCharUpperFieldName);
            }
        } else if (Boolean.class.getCanonicalName().equals(ClassName.get(fieldElement.asType()).toString())) {
            if ("is".equalsIgnoreCase(fieldName.substring(0, 2))) {
                getterSetterMethodNames.setGetterMethodName("get" + fieldName.substring(2));
                getterSetterMethodNames.setSetterMethodName("set" + firstCharUpper(fieldName.substring(2)));
            } else {
                getterSetterMethodNames.setGetterMethodName("get" + firstCharUpperFieldName);
                getterSetterMethodNames.setSetterMethodName("set" + firstCharUpperFieldName);
            }
        } else {
            getterSetterMethodNames.setGetterMethodName("get" + firstCharUpperFieldName);
            getterSetterMethodNames.setSetterMethodName("set" + firstCharUpperFieldName);
        }
        return getterSetterMethodNames;
    }

    private String firstCharUpper(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private String firstCharLower(String fieldName) {
        return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
    }

    private Modifier[] getModifiersArray(Element e) {
        Set<Modifier> modifierSet = e.getModifiers();
        Iterator<Modifier> iter = modifierSet.iterator();
        Modifier[] modifiers = new Modifier[modifierSet.size()];
        for (int i = 0; i < modifiers.length; i++) {
            modifiers[i] = iter.next();
        }
        return modifiers;
    }


    private static TypeMirror getFromTypeMirror(OOO ooo) {
        try {
            ooo.from();
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        return null;
    }


    private boolean oooParamIsNotSet(String stuff) {
        return OOOConstants.NOT_SET.equals(stuff);
    }


    @Override
    public String toString() {
        return "OOOProcess{" +
                "generatorClassEl=" + generatorClassEl +
                ", fromEntry=" + fromEntry +
                '}';
    }
}
