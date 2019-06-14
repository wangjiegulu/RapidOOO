package com.wangjiegulu.rapidooo.library.compiler.v1;

import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOIgnore;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.GlobalEnvironment;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-12.
 */
public class OOOSEntry {
    private OOOGenerator oooGenerator;

    private OOOs ooosAnno;

    private String fromSuffix;
    private String suffix;
    private List<String> ooosPackages;

    private HashMap<String, OOOEntry> ooos = new LinkedHashMap<>();

    private Map<String, OOOEntry> allTypeIds = new HashMap<>();

    public OOOSEntry(OOOGenerator oooGenerator, OOOs ooosAnno) {
        this.oooGenerator = oooGenerator;
        this.ooosAnno = ooosAnno;

        fromSuffix = ooosAnno.fromSuffix();
        suffix = ooosAnno.suffix();
        ooosPackages = Arrays.asList(ooosAnno.ooosPackages());

        // 添加使用 @OOO 显式的配置
        for(OOO ooo : ooosAnno.ooos()){
            OOOEntry oooEntry = new OOOEntry(this, ooo);
            ooos.put(ElementUtil.getName(oooEntry.getFrom()).toString(), oooEntry);
        }

        // 使用 package 隐式的配置
        for (String ooosPackage : ooosPackages) {
            LogUtil.logger(">>>>>ooosPackage: " + ooosPackage);
            PackageElement packageElement = GlobalEnvironment.getElementUtils().getPackageElement(ooosPackage);
            if (null == packageElement) {
                throw new RuntimeException("package[" + ooosPackage + "] is not exist.");
            }

            List<? extends Element> oooClassesElements = packageElement.getEnclosedElements();
            if (null != oooClassesElements) {
                for (Element oooClassElement : oooClassesElements) {
                    if (null != oooClassElement.getAnnotation(OOOs.class)) {
                        continue;
                    }

                    if (null != oooClassElement.getAnnotation(OOOIgnore.class)) {
                        LogUtil.logger("Ignore `From Class` [" + oooClassElement.toString() + "](@OOOIgnore).");
                        continue;
                    }

                    String qualifiedName = ElementUtil.getName(oooClassElement.asType()).toString();
                    if(!ooos.containsKey(qualifiedName)){ // 显式设置过，则增加
                        ooos.put(qualifiedName, new OOOEntry(this, oooClassElement));
                    }
                }
            }
        }
    }

    public OOOSEntry prepare(){
        for(Map.Entry<String, OOOEntry> oooEE : ooos.entrySet()){
            oooEE.getValue().prepare();
        }
        return this;
    }

    public void parse(){
        for(Map.Entry<String, OOOEntry> oooEE : ooos.entrySet()){
            oooEE.getValue().parse();
        }
    }


    public OOOGenerator getOooGenerator() {
        return oooGenerator;
    }

    public String getFromSuffix() {
        return fromSuffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public List<String> getOoosPackages() {
        return ooosPackages;
    }

    public HashMap<String, OOOEntry> getOoos() {
        return ooos;
    }

    public OOOEntry queryTypeIds(String id) {
        return allTypeIds.get(id);
    }

    public void addTypeIds(String id, OOOEntry oooEntry) {
        if(allTypeIds.containsKey(id)){
            throw new RuntimeException("[" + oooGenerator.getGeneratorClassEl().getSimpleName() + "]id[" + "] is already exist in " + oooEntry.getFromClassName().simpleName() + ".");
        }
        this.allTypeIds.put(id, oooEntry);
    }

    public Map<String, OOOEntry> getAllTypeIds() {
        return allTypeIds;
    }

    @Override
    public String toString() {
        return "OOOSEntry{" +
                ", fromSuffix='" + fromSuffix + '\'' +
                ", suffix='" + suffix + '\'' +
                ", ooosPackages=" + ooosPackages +
                ", ooos=" + ooos +
                '}';
    }
}
