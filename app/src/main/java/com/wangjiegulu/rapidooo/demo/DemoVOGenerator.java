package com.wangjiegulu.rapidooo.demo;

import android.media.MediaPlayer;
import android.text.SpannableString;

import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOConversion;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.depmodule.bll.demo.ChatBO;
import com.wangjiegulu.rapidooo.depmodule.bll.demo.MessageBO;
import com.wangjiegulu.rapidooo.depmodule.bll.demo.UserBO;

import java.io.IOException;
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
                        excludes = {"fromBO", "chatBO", "chatBOs", "otherChatBOs", "foos", "fooArray", "mapps"},
//                        parcelable = false,
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
                                        bindMethodName = "bindTextSp",
                                        inverseBindMethodName = "inverseBindTextSp"
                                ),
                                @OOOConversion(
                                        targetFieldName = "videoPlayer",
                                        targetFieldType = MediaPlayer.class,
                                        conversionMethodName = "conversionVideo",
                                        parcelable = false
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
                                ),
                                @OOOConversion(
                                        targetFieldName = "emptyChatVOs",
                                        targetFieldTypeId = "#id__ChatVO[]"
                                )
                        }
                )
        }
)
public class DemoVOGenerator {

    public static SpannableString bindTextSp(String textRaw){
        // convert to SpannableString with `@User` / emoticon / sticker
        return new SpannableString(textRaw);
    }

    public static void inverseBindTextSp(SpannableString textSp, MessageVO self){
        self.setTextRaw(textSp.toString());
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
    public static MediaPlayer conversionVideo(MessageVO self, String videoUrl){
        MediaPlayer mediaPlayer = self.getVideoPlayer();
        if(null != mediaPlayer){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if(null == videoUrl){
            return mediaPlayer;
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(videoUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaPlayer;
    }

}
