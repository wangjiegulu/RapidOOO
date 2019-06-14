package com.wangjiegulu.rapidooo.library.compiler;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;

import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.library.compiler.base.BaseAbstractProcessor;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOProcessV1;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 6/28/16.
 */
@AutoService(Processor.class)
public class RapidOOOProcessor extends BaseAbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedTypesSet = new HashSet<>();
        supportedTypesSet.add(OOOs.class.getCanonicalName());
        return supportedTypesSet;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        long start = System.currentTimeMillis();
        logger("[process]annotations: " + Arrays.toString(annotations.toArray()));
        logger("[process]roundEnv: " + roundEnv);
        try {

            HashMap<String, OOOProcessV1> mapper = new HashMap<>();

            for (Element element : roundEnv.getElementsAnnotatedWith(OOOs.class)) {
                OOOProcessV1 oooProcess = doTableAnnotation(element, mapper);
                try {
                    logger("OOO generate START -> " + oooProcess);
                    oooProcess.brewJava(filer);
                    logger("OOO generate END -> " + oooProcess + ", oooProcess: " + oooProcess);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Throwable throwable) {
                    logger("OOO generate FAILED -> " + oooProcess + ", oooProcess: " + oooProcess);
                    loggerE(throwable);
                }
            }

//            for (Map.Entry<String, OOOProcess> entry : mapper.entrySet()) {
//                logger("oooProcess: " + entry.getValue());
//                String key = entry.getKey();
//                OOOProcess oooProcess = entry.getValue();
//                try {
//                    logger("TableConfig generate START -> " + key);
//                    oooProcess.brewJava(filer);
//                    logger("TableConfig generate END -> " + key + ", oooProcess: " + oooProcess);
//                } catch (RuntimeException e) {
//                    throw e;
//                } catch (Throwable throwable) {
//                    logger("TableConfig generate FAILED -> " + key + ", oooProcess: " + oooProcess);
//                    loggerE(throwable);
//                }
//            }

        }/*catch (Throwable throwable) {
            loggerE(throwable);
        }*/ finally {
            logger("[process] tasks: " + (System.currentTimeMillis() - start) + "ms");
        }


        return true;
    }

    private OOOProcessV1 doTableAnnotation(Element ele, HashMap<String, OOOProcessV1> mapper) {
        return obtainTableEntrySafe(ele, mapper);
    }

    private OOOProcessV1 obtainTableEntrySafe(Element ele, HashMap<String, OOOProcessV1> tableMapper) {
        Element classEle = getElementOwnerElement(ele);
        String className = classEle.asType().toString();
        OOOProcessV1 oooProcess = tableMapper.get(MoreElements.asType(classEle).getQualifiedName().toString());
        if (null == oooProcess) {
            oooProcess = new OOOProcessV1();
            oooProcess.setGeneratorClassEl(classEle);
            tableMapper.put(className, oooProcess);
        }
        return oooProcess;
    }


}
