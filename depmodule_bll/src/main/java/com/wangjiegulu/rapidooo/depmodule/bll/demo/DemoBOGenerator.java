package com.wangjiegulu.rapidooo.depmodule.bll.demo;

import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOConversion;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.demo.Chat;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.demo.Message;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.demo.User;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-17.
 */
@OOOs(
        fromSuffix = "",
        suffix = "BO",
        ooos = {
                @OOO(from = User.class, id = "#id__UserBO"),
                @OOO(from = Chat.class, id = "#id__ChatBO"),
                @OOO(from = Message.class,
                        excludes = {"from", "chat", "chats", "otherChats", "chatMapper", "replyIds", "text", "commentArray", "scoreMap"},
                        conversions = {
                                @OOOConversion(
                                        targetFieldName = "textRaw",
                                        targetFieldType = String.class,
                                        attachFieldName = "text"
                                ),
                                @OOOConversion(
                                        targetFieldName = "textLength",
                                        targetFieldType = int.class,
                                        bindMethodName = "bindTextLength"
                                ),
                                @OOOConversion(
                                        targetFieldName = "fromBO",
                                        targetFieldTypeId = "#id__UserBO",
                                        attachFieldName = "from"
                                ),
                                @OOOConversion(
                                        targetFieldName = "chatBO",
                                        targetFieldTypeId = "#id__ChatBO",
                                        attachFieldName = "chat"
                                ),
                                @OOOConversion(
                                        targetFieldName = "commentArrays",
                                        targetFieldTypeId = "java.lang.String[]",
                                        attachFieldName = "commentArray"
                                ),
                                @OOOConversion(
                                        targetFieldName = "replyIds",
                                        targetFieldTypeId = "java.util.List<java.lang.String>",
                                        attachFieldName = "replyIds"
                                ),
                                @OOOConversion(
                                        targetFieldName = "chatBOs",
                                        targetFieldTypeId = "java.util.List<#id__ChatBO>",
                                        attachFieldName = "chats"
                                ),
                                @OOOConversion(
                                        targetFieldName = "otherChatBOs",
                                        targetFieldTypeId = "#id__ChatBO[]",
                                        attachFieldName = "otherChats"
                                ),
                                @OOOConversion(
                                        targetFieldName = "chatBOMapper",
                                        targetFieldTypeId = "java.util.TreeMap<java.lang.String, #id__ChatBO>",
                                        attachFieldName = "chatMapper"
                                ),
                                @OOOConversion(
                                        targetFieldName = "scoreMapper",
                                        targetFieldTypeId = "java.util.Map<java.lang.String, java.lang.Integer>",
                                        attachFieldName = "scoreMap"
                                )
                        }
                )
        }
)
public class DemoBOGenerator {
    public static int bindTextLength(String textRaw){
        return null == textRaw ? 0 : textRaw.length();
    }
}
