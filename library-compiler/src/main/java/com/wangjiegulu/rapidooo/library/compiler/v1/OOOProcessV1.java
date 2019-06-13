package com.wangjiegulu.rapidooo.library.compiler.v1;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.v1.part.CreateMethodPartBrew;
import com.wangjiegulu.rapidooo.library.compiler.v1.part.DefaultConstructorMethodPartBrew;
import com.wangjiegulu.rapidooo.library.compiler.v1.part.FieldAndGetterSetterPartBrew;
import com.wangjiegulu.rapidooo.library.compiler.v1.part.FromMethodPartBrew;
import com.wangjiegulu.rapidooo.library.compiler.v1.part.PartBrew;
import com.wangjiegulu.rapidooo.library.compiler.v1.part.ToMethod1PartBrew;
import com.wangjiegulu.rapidooo.library.compiler.v1.part.ToMethod2PartBrew;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 11/04/2018.
 */
public class OOOProcessV1 {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:SSS", Locale.getDefault());

    private OOOGenerator oooGenerator;

    private List<PartBrew> partBrews = new ArrayList<>();

    public OOOProcessV1() {
        partBrews.add(new DefaultConstructorMethodPartBrew());
        partBrews.add(new FieldAndGetterSetterPartBrew());
        partBrews.add(new FromMethodPartBrew());
        partBrews.add(new CreateMethodPartBrew());
        partBrews.add(new ToMethod1PartBrew());
        partBrews.add(new ToMethod2PartBrew());
    }

    public void setGeneratorClassEl(Element mGeneratorClassEl) {
        oooGenerator = new OOOGenerator(mGeneratorClassEl);
        oooGenerator.parse();
    }


    public void brewJava(Filer filer) throws Throwable {
        OOOSEntry ooosEntry = oooGenerator.getOoosEntry();
        for(Map.Entry<String, OOOEntry> oooE : ooosEntry.getOoos().entrySet()){
            OOOEntry oooEntry = oooE.getValue();

            ///////////////////// class /////////////////////
            TypeSpec.Builder result = TypeSpec.classBuilder(oooEntry.getTargetClassSimpleName())
                    .addModifiers(Modifier.PUBLIC)
                    .addJavadoc("From POJO: {@link $T}\nGenerate By: {@link $T}\nGenerate Time: " + DATE_FORMAT.format(new Date(System.currentTimeMillis())) + "\n",
                            oooEntry.getFromTypeName(), oooGenerator.getGeneratorClassType()
                    );


            ///////////////////// super class /////////////////////
            TypeName supperTypeName = oooEntry.getTargetSupperType();
            if (!ElementUtil.isSameType(supperTypeName, TypeName.OBJECT)) {
                result.superclass(supperTypeName);
            }

            ///////////////////// interfaces /////////////////////
            // TODO: 2019-06-12 wangjie

            for(PartBrew partBrew : partBrews){
                partBrew.brew(oooEntry, result);
            }

            JavaFile.builder(oooEntry.getTargetClassPackage(), result.build())
                    .addFileComment("GENERATED CODE BY RapidOOO. DO NOT MODIFY! https://github.com/wangjiegulu/RapidOOO")
                    .skipJavaLangImports(true)
                    .build()
                    .writeTo(filer);


        }


    }








}
