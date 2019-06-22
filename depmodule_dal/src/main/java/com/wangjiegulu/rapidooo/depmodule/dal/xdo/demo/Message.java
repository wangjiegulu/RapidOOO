package com.wangjiegulu.rapidooo.depmodule.dal.xdo.demo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-17.
 *
 * https://core.telegram.org/bots/api#message
 */
public class Message implements Parcelable {
    private Integer messageId;
    private User from;
    private Integer date;
    private Chat chat;
    private Integer editDate;
    private String text;

    private List<String> comments;
    private String[] commentArray;
    private List<String> replyIds;
    private List<Chat> chats;
    private Chat[] otherChats;
    private Map<String, Chat> chatMapper;
//    private SpannableString haha;
//    private Bundle bundle;
//    private PersistableBundle persistableBundle;
//    private SparseArray sparseArray;
//    private IBinder iBinder;
//    private Size size;
//    private SizeF sizeF;

    private List<Foo> foos;
    private Foo[] fooArray;

    public List<Foo> getFoos() {
        return foos;
    }

    public void setFoos(List<Foo> foos) {
        this.foos = foos;
    }

    public Foo[] getFooArray() {
        return fooArray;
    }

    public void setFooArray(Foo[] fooArray) {
        this.fooArray = fooArray;
    }

    public Map<String, Chat> getChatMapper() {
        return chatMapper;
    }

    public void setChatMapper(Map<String, Chat> chatMapper) {
        this.chatMapper = chatMapper;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }


    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public Chat[] getOtherChats() {
        return otherChats;
    }

    public void setOtherChats(Chat[] otherChats) {
        this.otherChats = otherChats;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Integer getEditDate() {
        return editDate;
    }

    public void setEditDate(Integer editDate) {
        this.editDate = editDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getReplyIds() {
        return replyIds;
    }

    public void setReplyIds(List<String> replyIds) {
        this.replyIds = replyIds;
    }

    public String[] getCommentArray() {
        return commentArray;
    }

    public void setCommentArray(String[] commentArray) {
        this.commentArray = commentArray;
    }

    public Message() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.messageId);
        dest.writeParcelable(this.from, flags);
        dest.writeValue(this.date);
        dest.writeParcelable(this.chat, flags);
        dest.writeValue(this.editDate);
        dest.writeString(this.text);
        dest.writeStringList(this.comments);
        dest.writeStringArray(this.commentArray);
        dest.writeStringList(this.replyIds);
        dest.writeTypedList(this.chats);
        dest.writeTypedArray(this.otherChats, flags);
        dest.writeInt(this.chatMapper.size());
        for (Map.Entry<String, Chat> entry : this.chatMapper.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }

    protected Message(Parcel in) {
        this.messageId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.from = in.readParcelable(User.class.getClassLoader());
        this.date = (Integer) in.readValue(Integer.class.getClassLoader());
        this.chat = in.readParcelable(Chat.class.getClassLoader());
        this.editDate = (Integer) in.readValue(Integer.class.getClassLoader());
        this.text = in.readString();
        this.comments = in.createStringArrayList();
        this.commentArray = in.createStringArray();
        this.replyIds = in.createStringArrayList();
        this.chats = in.createTypedArrayList(Chat.CREATOR);
        this.otherChats = in.createTypedArray(Chat.CREATOR);
        int chatMapperSize = in.readInt();
        this.chatMapper = new HashMap<String, Chat>(chatMapperSize);
        for (int i = 0; i < chatMapperSize; i++) {
            String key = in.readString();
            Chat value = in.readParcelable(Chat.class.getClassLoader());
            this.chatMapper.put(key, value);
        }
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
