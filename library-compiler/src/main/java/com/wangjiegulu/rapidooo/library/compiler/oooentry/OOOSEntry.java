package com.wangjiegulu.rapidooo.library.compiler.oooentry;

import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOIgnore;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.library.compiler.exception.RapidOOOCompileException;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.GlobalEnvironment;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func1R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    private List<OOOEntry> ooos = new ArrayList<>();

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
            ooos.add(oooEntry);
        }

        // 使用 package 隐式的配置
        for (String ooosPackage : ooosPackages) {
            PackageElement packageElement = GlobalEnvironment.getElementUtils().getPackageElement(ooosPackage);
            if (null == packageElement) {
                throw new RapidOOOCompileException("package[" + ooosPackage + "] is not exist.");
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

                    final String qualifiedName = ElementUtil.getName(oooClassElement.asType()).toString();

                    if(TextUtil.pickFirst(ooos, new Func1R<OOOEntry, Boolean>() {
                        @Override
                        public Boolean call(OOOEntry oooEntry) {
                            return TextUtil.equals(ElementUtil.getName(oooEntry.getFrom()).toString(), qualifiedName);
                        }
                    }) == null){ // 显式设置过，则增加
                        ooos.add(new OOOEntry(this, oooClassElement));
                    }
                }
            }
        }
    }

    public OOOSEntry prepare(){
        for(OOOEntry oooEE : ooos){
            oooEE.prepare();
        }
        return this;
    }

    public void parse(){
        for(OOOEntry oooEE : ooos){
            oooEE.parse();
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

    public List<OOOEntry> getOoos() {
        return ooos;
    }

    public OOOEntry queryTypeIds(String id) {
        return allTypeIds.get(id);
    }

    public void addTypeIds(String id, OOOEntry oooEntry) {
        if(allTypeIds.containsKey(id)){
            throw new RapidOOOCompileException("[" + oooGenerator.getGeneratorClassEl().getSimpleName() + "]id[" + "] is already exist in " + oooEntry.getFromClassName().simpleName() + ".");
        }
        this.allTypeIds.put(id, oooEntry);
    }

    public Map<String, OOOEntry> getAllTypeIds() {
        return allTypeIds;
    }

}
