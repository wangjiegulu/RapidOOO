package com.wangjiegulu.rapidooo.library.compiler;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;

import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.library.compiler.base.BaseAbstractProcessor;
import com.wangjiegulu.rapidooo.library.compiler.objs.OOOProcess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
//        logger("[process]roundEnv: " + roundEnv);
        try {

            HashMap<String, OOOProcess> mapper = new HashMap<>();

            for (Element e : roundEnv.getElementsAnnotatedWith(OOOs.class)) {
                doTableAnnotation(e, mapper);
            }

            for (Map.Entry<String, OOOProcess> entry : mapper.entrySet()) {
                logger("oooProcess: " + entry.getValue());
                String key = entry.getKey();
                OOOProcess oooProcess = entry.getValue();
                try {
                    logger("TableConfig generate START -> " + key);
                    oooProcess.brewJava(filer);
                    logger("TableConfig generate END -> " + key + ", oooProcess: " + oooProcess);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Throwable throwable) {
                    logger("TableConfig generate FAILED -> " + key + ", oooProcess: " + oooProcess);
                    loggerE(throwable);
                }
            }

        }/*catch (Throwable throwable) {
            loggerE(throwable);
        }*/ finally {
            logger("[process] tasks: " + (System.currentTimeMillis() - start) + "ms");
        }


        return true;
    }

    private void doTableAnnotation(Element ele, HashMap<String, OOOProcess> mapper) {
        obtainTableEntrySafe(ele, mapper);
    }

    private OOOProcess obtainTableEntrySafe(Element ele, HashMap<String, OOOProcess> tableMapper) {
        Element classEle = getElementOwnerElement(ele);
        String className = classEle.asType().toString();
        OOOProcess oooProcess = tableMapper.get(MoreElements.asType(classEle).getQualifiedName().toString());
        if (null == oooProcess) {
            oooProcess = new OOOProcess();
            oooProcess.setGeneratorClassEl(classEle);
            tableMapper.put(className, oooProcess);
        }
        return oooProcess;
    }


}
