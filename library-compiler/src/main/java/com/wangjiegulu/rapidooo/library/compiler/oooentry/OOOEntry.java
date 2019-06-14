package com.wangjiegulu.rapidooo.library.compiler.oooentry;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOConstants;
import com.wangjiegulu.rapidooo.api.OOOConversion;
import com.wangjiegulu.rapidooo.library.compiler.exception.RapidOOOCompileException;
import com.wangjiegulu.rapidooo.library.compiler.util.AnnoUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func0R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-12.
 */
public class OOOEntry {
    private OOOSEntry ooosEntry;

    private OOO ooo;

    private String id;
    private String fromSuffix;
    private String suffix;

    private TypeMirror from;
    private Element fromElement;
    private ClassName fromClassName;
    private TypeName fromTypeName;
    private String fromSimpleName;

    private List<String> includes = new ArrayList<>();
    private List<String> excludes = new ArrayList<>();

    private TypeName targetSupperType;
    private String targetSupperTypeId = OOOConstants.NOT_SET;

    private OOOPoolEntry pool;

    //    private List<OOOConversionEntry> conversions = new ArrayList<>();
    private HashMap<String, OOOConversionEntry> conversions = new LinkedHashMap<>();

    private String targetClassPackage;
    private String targetClassSimpleName;
    private TypeName targetClassType;

    private HashMap<String, OOOFieldEntry> allContinuingFields = new LinkedHashMap<>();

    /**
     * 显式配置
     */
    public OOOEntry(OOOSEntry ooosEntry, final OOO ooo) {
        this.ooosEntry = ooosEntry;
        this.ooo = ooo;

        id = ooo.id();
        // 缓存 id
        if (!AnnoUtil.oooParamIsNotSet(id)) {
            ooosEntry.addTypeIds(id, this);
        }

        String _fromSuffix = ooo.fromSuffix();
        fromSuffix = AnnoUtil.oooParamIsNotSet(_fromSuffix) ? ooosEntry.getFromSuffix() : _fromSuffix;

        String _suffix = ooo.suffix();
        suffix = AnnoUtil.oooParamIsNotSet(_suffix) ? ooosEntry.getSuffix() : _suffix;

        from = AnnoUtil.getType(new Func0R<Object>() {
            @Override
            public Object call() {
                return ooo.from();
            }
        });
        fromElement = MoreTypes.asElement(from);

        includes = Arrays.asList(ooo.includes());
        excludes = Arrays.asList(ooo.excludes());
        validateIncludesExcludes();

        targetSupperType = ElementUtil.getTypeName(AnnoUtil.getType(new Func0R<Object>() {
            @Override
            public Object call() {
                return ooo.targetSupperType();
            }
        }));
        targetSupperTypeId = ooo.targetSupperTypeId();

        pool = new OOOPoolEntry(this, ooo.pool());

        for (OOOConversion oooConversion : ooo.conversions()) {
            OOOConversionEntry oce = new OOOConversionEntry(this, oooConversion);
            conversions.put(oce.getTargetFieldName(), oce);
        }

        init();
    }


    /**
     * 隐式配置
     */
    public OOOEntry(OOOSEntry ooosEntry, final Element element) {
        this.ooosEntry = ooosEntry;
        from = element.asType();
        fromElement = element;

        fromSuffix = ooosEntry.getFromSuffix();
        suffix = ooosEntry.getSuffix();

        targetSupperType = ElementUtil.getTypeName(MoreElements.asType(element).getSuperclass());

        init();
    }

    private void init() {
        fromClassName = ElementUtil.getClassName(from);
        fromSimpleName = ElementUtil.getSimpleName(from);
        fromTypeName = ElementUtil.getTypeName(from);

        targetClassPackage = ooosEntry.getOooGenerator().getGeneratorClassEl().getEnclosingElement().toString();
        // eg. replace "BO" when generate VO
        targetClassSimpleName =
                (AnnoUtil.oooParamIsNotSet(fromSuffix) ? fromSimpleName : fromSimpleName.substring(0, fromSimpleName.length() - fromSuffix.length()))
                        + suffix;

        targetClassType = EasyType.bestGuess(targetClassPackage + "." + targetClassSimpleName);

        // All fields need to add to target new Class
        List<? extends Element> eles = fromElement.getEnclosedElements();
        for (Element field : eles) {
            if (ElementKind.FIELD == field.getKind()) {
                if (MoreElements.hasModifiers(Modifier.STATIC).apply(field)) {
                    continue;
                }
                String fieldName = field.getSimpleName().toString();
                if (includeField(fieldName)) {
                    allContinuingFields.put(fieldName, new OOOFieldEntry(field));
                }
            }
        }

        // All conversion fields need to add to target new Class -> `conversions`
        // it's `conversions`

    }

    public OOOEntry prepare() {
        // 设置了父类的 type id，则从缓存中查询
        if (!AnnoUtil.oooParamIsNotSet(targetSupperTypeId)) {
            targetSupperType = ooosEntry.queryTypeIds(targetSupperTypeId).targetClassType;
        }

        for (Map.Entry<String, OOOConversionEntry> ee : conversions.entrySet()) {
            ee.getValue().prepare();
        }

        return this;
    }

    public void parse() {
        if (null != pool) {
            pool.parse();
        }

        for (Map.Entry<String, OOOConversionEntry> ee : conversions.entrySet()) {
            ee.getValue().parse();
        }

    }

    /**
     * validate fields available
     */
    private void validateIncludesExcludes() {
        for (String field : includes) {
            if (null == TextUtil.pickFirst(fromElement.getEnclosedElements(), o -> TextUtil.equals(o.getSimpleName().toString(), field))) {
                throw new RapidOOOCompileException("Field[" + field + "] is not found in `includes` \n" + this.from + "\n" + ooosEntry.getOooGenerator().getGeneratorClassType());
            }
        }
        for (String field : excludes) {
            if (null == TextUtil.pickFirst(fromElement.getEnclosedElements(), o -> TextUtil.equals(o.getSimpleName().toString(), field))) {
                throw new RapidOOOCompileException("Field[" + field + "] is not found in `excludes` \n" + this.from + "\n" + ooosEntry.getOooGenerator().getGeneratorClassType());
            }
        }
    }

    public boolean includeField(String fieldName) {
        if (includes.isEmpty() && excludes.isEmpty()) {
            return true;
        }

        if (!includes.isEmpty() && !excludes.isEmpty()) {
            throw new RapidOOOCompileException("`includes` and `excludes` can only be set up to one.");
        }

        if (!includes.isEmpty()) {
            return includes.contains(fieldName);
        }

        return !excludes.contains(fieldName);
    }

    public OOOSEntry getOoosEntry() {
        return ooosEntry;
    }

    public String getId() {
        return id;
    }

    public String getFromSuffix() {
        return fromSuffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public TypeMirror getFrom() {
        return from;
    }

    public TypeName getTargetSupperType() {
        return targetSupperType;
    }

    public String getTargetSupperTypeId() {
        return targetSupperTypeId;
    }

    public OOOPoolEntry getPool() {
        return pool;
    }

    public HashMap<String, OOOConversionEntry> getConversions() {
        return conversions;
    }

    public ClassName getFromClassName() {
        return fromClassName;
    }

    public String getFromSimpleName() {
        return fromSimpleName;
    }

    public String getTargetClassPackage() {
        return targetClassPackage;
    }

    public String getTargetClassSimpleName() {
        return targetClassSimpleName;
    }

    public TypeName getFromTypeName() {
        return fromTypeName;
    }

    public TypeName getTargetClassType() {
        return targetClassType;
    }

    public HashMap<String, OOOFieldEntry> getAllContinuingFields() {
        return allContinuingFields;
    }

    public boolean isTargetSupperTypeId() {
        return null != targetSupperTypeId && !AnnoUtil.oooParamIsNotSet(targetSupperTypeId);
    }

    public boolean isPoolUsed() {
        return null != pool && pool.isPoolUsed();
    }

}
