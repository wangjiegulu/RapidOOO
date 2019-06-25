package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-25.
 */
public class ParcelableStatementUtil {
    public static List<IParcelableStatementBrew> parcelableStatementBrews = new ArrayList<>();

    static {
        parcelableStatementBrews.add(new ParcelablePrimitiveStatementBrew());
        parcelableStatementBrews.add(new ParcelableBoxPrimitiveStatementBrew());
        parcelableStatementBrews.add(new ParcelableObjectStatementBrew());
        parcelableStatementBrews.add(new ParcelableListStatementBrew());
        parcelableStatementBrews.add(new ParcelableArrayStatementBrew());
        parcelableStatementBrews.add(new ParcelableMapStatementBrew());
    }



}
