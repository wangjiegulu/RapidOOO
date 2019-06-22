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
                        excludes = {"from", "chat", "chats", "otherChats", "chatMapper", "replyIds", "text", "commentArray"},
                        conversions = {
                                @OOOConversion(
                                        targetFieldName = "textRaw",
                                        targetFieldType = String.class,
                                        attachFieldName = "text"
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
                                )/*,
                                @OOOConversion(
                                        targetFieldName = "chatBOMapper",
                                        targetFieldTypeId = "java.util.Map<String, #id__ChatBO>",
                                        attachFieldName = "chatMapper"
                                )*/
                        }
                )
        }
)
public class DemoBOGenerator {
}
