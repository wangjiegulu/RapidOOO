package com.wangjiegulu.rapidooo.demo;

import android.text.SpannableString;

import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOConversion;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.depmodule.bll.demo.ChatBO;
import com.wangjiegulu.rapidooo.depmodule.bll.demo.MessageBO;
import com.wangjiegulu.rapidooo.depmodule.bll.demo.UserBO;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-17.
 */
@OOOs(
        fromSuffix = "BO",
        suffix = "VO",
        ooos = {
                @OOO(from = UserBO.class, id = "#id__UserVO"),
                @OOO(from = ChatBO.class, id = "#id__ChatVO"),
                @OOO(from = MessageBO.class,
                        excludes = {"fromBO", "chatBO", "chatBOs", "otherChatBOs", "foos", "fooArray"},
                        conversions = {
                                @OOOConversion(
                                        targetFieldName = "fromVO",
                                        targetFieldTypeId = "#id__UserVO",
                                        attachFieldName = "fromBO"
                                ),
                                @OOOConversion(
                                        targetFieldName = "chatVO",
                                        targetFieldTypeId = "#id__ChatVO",
                                        attachFieldName = "chatBO"
                                ),
                                @OOOConversion(
                                        targetFieldName = "textSp",
                                        targetFieldType = SpannableString.class,
                                        conversionMethodName = "conversionTextSp",
                                        inverseConversionMethodName = "inverseConversionTextSp"
                                ),
                                @OOOConversion(
                                        targetFieldName = "commentLengthList",
                                        targetFieldTypeId = "java.util.List<java.lang.Integer>",
                                        bindMethodName = "bindCommentLengthList"
                                ),
                                @OOOConversion(
                                        targetFieldName = "chatVOs",
                                        targetFieldTypeId = "java.util.List<#id__ChatVO>",
                                        attachFieldName = "chatBOs"
                                ),
                                @OOOConversion(
                                        targetFieldName = "otherChatVOs",
                                        targetFieldTypeId = "#id__ChatVO[]",
                                        attachFieldName = "otherChatBOs",

                                        parcelable = false
                                )
                        }
                )
        }
)
public class DemoVOGenerator {

    public static SpannableString conversionTextSp(String textRaw){
        return new SpannableString("text");
    }

    public static void inverseConversionTextSp(SpannableString textSp, MessageBO other){
        other.setTextRaw(textSp.toString());
    }

    public static List<Integer> bindCommentLengthList(List<String> comments){
        if(null == comments){
            return null;
        }else{
            List<Integer> commentLengthList = new ArrayList<>();
            for(String comment : comments){
                commentLengthList.add(comment.length());
            }
            return commentLengthList;
        }
    }

}
