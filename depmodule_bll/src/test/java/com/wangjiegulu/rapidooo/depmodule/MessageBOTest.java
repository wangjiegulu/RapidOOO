package com.wangjiegulu.rapidooo.depmodule;

import com.wangjiegulu.rapidooo.depmodule.bll.demo.ChatBO;
import com.wangjiegulu.rapidooo.depmodule.bll.demo.MessageBO;
import com.wangjiegulu.rapidooo.depmodule.bll.demo.UserBO;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.demo.Chat;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.demo.Message;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.demo.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-26.
 */
public class MessageBOTest {
    @Test
    public void textRaw_attach() {
        String textContent = "hello text";
        Message message = new Message();
        message.setText(textContent);

        MessageBO messageBO = MessageBO.create(message);
        assertEquals(textContent, messageBO.getTextRaw());

        Message message1 = messageBO.toMessage();
        assertEquals(textContent, message1.getText());
    }

    @Test
    public void fromBO_attach() {
        User user = new User();
        user.setId(1);
        user.setBot(false);
        user.setFirstName("Angelia");
        user.setLastName("Wang");
        user.setUsername(null);
        Message message = new Message();
        message.setFrom(user);

        MessageBO messageBO = MessageBO.create(message);
        UserBO userBO = messageBO.getFromBO();
        assertNotNull(userBO);
        assertEquals(1, userBO.getId().intValue());
        assertFalse(userBO.getBot());
        assertEquals("Angelia", userBO.getFirstName());
        assertEquals("Wang", userBO.getLastName());
        assertNull(userBO.getUsername());

        messageBO.getFromBO().setFirstName("Angelia1");

        Message message1 = messageBO.toMessage();
        User user1 = message1.getFrom();
        assertNotNull(user1);
        assertEquals(1, user1.getId().intValue());
        assertFalse(user1.getBot());
        assertEquals("Angelia1", user1.getFirstName());
        assertEquals("Wang", user1.getLastName());
        assertNull(user1.getUsername());

    }

    @Test
    public void commentArrays_attach() {
        String[] commentArrayData = new String[]{
                "comment0", "comment1", "comment2"
        };

        Message message = new Message();
        message.setCommentArray(commentArrayData);

        MessageBO messageBO = MessageBO.create(message);
        String[] commentArray = messageBO.getCommentArrays();
        assertNotNull(commentArray);
        assertEquals(3, commentArray.length);
        assertEquals("comment0", commentArray[0]);
        assertEquals("comment1", commentArray[1]);
        assertEquals("comment2", commentArray[2]);

        Message message1 = messageBO.toMessage();
        String[] commentArray1 = message1.getCommentArray();
        assertNotNull(commentArray1);
        assertEquals(3, commentArray1.length);
        assertEquals("comment0", commentArray1[0]);
        assertEquals("comment1", commentArray1[1]);
        assertEquals("comment2", commentArray1[2]);
    }

    @Test
    public void replyIds_attach() {
        List<String> replyIdsData = new ArrayList<>();
        replyIdsData.add("id0");
        replyIdsData.add("id1");
        replyIdsData.add("id2");

        Message message = new Message();
        message.setReplyIds(replyIdsData);

        MessageBO messageBO = MessageBO.create(message);
        List<String> replyIds = messageBO.getReplyIds();
        assertNotNull(replyIds);
        assertEquals(3, replyIds.size());
        assertEquals("id0", replyIds.get(0));
        assertEquals("id1", replyIds.get(1));
        assertEquals("id2", replyIds.get(2));

        messageBO.getReplyIds().add("id3");

        Message message1 = messageBO.toMessage();
        List<String> replyIds1 = message1.getReplyIds();
        assertNotNull(replyIds1);
        assertEquals(4, replyIds1.size());
        assertEquals("id0", replyIds1.get(0));
        assertEquals("id1", replyIds1.get(1));
        assertEquals("id2", replyIds1.get(2));
        assertEquals("id3", replyIds1.get(3));
    }

    @Test
    public void chatBOs_attach() {
        List<Chat> chatsData = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            Chat chat = new Chat();
            chat.setId(i);
            chat.setTitle("chat title " + i);
            chatsData.add(chat);
        }
        Message message = new Message();
        message.setChats(chatsData);

        MessageBO messageBO = MessageBO.create(message);
        List<ChatBO> chatBOs = messageBO.getChatBOs();
        assertNotNull(chatBOs);
        assertEquals(5, chatBOs.size());
        for(int i = 0; i < 5; i++){
            assertEquals(i, chatBOs.get(i).getId().intValue());
            assertEquals("chat title " + i, chatBOs.get(i).getTitle());
        }

        Message message1 = messageBO.toMessage();
        List<Chat> chats1 = message1.getChats();
        assertNotNull(chats1);
        assertEquals(5, chats1.size());
        for(int i = 0; i < 5; i++){
            assertEquals(i, chats1.get(i).getId().intValue());
            assertEquals("chat title " + i, chats1.get(i).getTitle());
        }
    }

    @Test
    public void otherChatBOs_attach() {
        Chat[] otherChatsData = new Chat[5];
        for(int i = 0; i < 5; i++){
            Chat chat = new Chat();
            chat.setId(i);
            chat.setTitle("chat title " + i);
            otherChatsData[i] = chat;
        }
        Message message = new Message();
        message.setOtherChats(otherChatsData);

        MessageBO messageBO = MessageBO.create(message);
        ChatBO[] otherChatBOs = messageBO.getOtherChatBOs();

        assertNotNull(otherChatBOs);
        assertEquals(5, otherChatBOs.length);
        for(int i = 0; i < 5; i++){
            assertEquals(i, otherChatBOs[i].getId().intValue());
            assertEquals("chat title " + i, otherChatBOs[i].getTitle());
        }
        Message message1 = messageBO.toMessage();
        Chat[] otherChats = message1.getOtherChats();
        assertNotNull(otherChats);
        assertEquals(5, otherChats.length);
        for(int i = 0; i < 5; i++){
            assertEquals(i, otherChats[i].getId().intValue());
            assertEquals("chat title " + i, otherChats[i].getTitle());
        }
    }


    @Test
    public void chatBOMapper_attach() {
        Map<String, Chat> chatMapperData = new HashMap<>();
        for(int i = 0; i < 5; i++){
            Chat chat = new Chat();
            chat.setId(i);
            chat.setTitle("chat title " + i);
            chatMapperData.put(String.valueOf(i), chat);
        }
        Message message = new Message();
        message.setChatMapper(chatMapperData);

        MessageBO messageBO = MessageBO.create(message);
        TreeMap<String, ChatBO> chatBOMapper = messageBO.getChatBOMapper();
        assertNotNull(chatBOMapper);
        assertEquals(5, chatBOMapper.size());
        for(int i = 0; i < 5; i++){
            ChatBO chatBO = chatBOMapper.get(String.valueOf(i));
            assertNotNull(chatBO);
            assertEquals(i, chatBO.getId().intValue());
            assertEquals("chat title " + i, chatBO.getTitle());
        }

        Message message1 = messageBO.toMessage();
        Map<String, Chat> chatMapper = message1.getChatMapper();
        assertNotNull(chatMapper);
        assertEquals(5, chatMapper.size());
        for(int i = 0; i < 5; i++){
            Chat chat = chatMapper.get(String.valueOf(i));
            assertNotNull(chat);
            assertEquals(i, chat.getId().intValue());
            assertEquals("chat title " + i, chat.getTitle());
        }
    }

    @Test
    public void scoreMapper_attach() {
        Map<String, Integer> scoreMapData = new HashMap<>();
        for(int i = 0; i < 5; i++){
            scoreMapData.put(String.valueOf(i), i * 1000);
        }
        Message message = new Message();
        message.setScoreMap(scoreMapData);

        MessageBO messageBO = MessageBO.create(message);
        Map<String, Integer> scoreMapper = messageBO.getScoreMapper();
        assertNotNull(scoreMapper);
        assertEquals(5, scoreMapper.size());
        for(int i = 0; i < 5; i++){
            Integer score = scoreMapper.get(String.valueOf(i));
            assertNotNull(score);
            assertEquals(i * 1000, score.intValue());
        }

        Message message1 = messageBO.toMessage();
        Map<String, Integer> scoreMap = message1.getScoreMap();
        assertNotNull(scoreMap);
        assertEquals(5, scoreMap.size());
        for(int i = 0; i < 5; i++){
            Integer score = scoreMap.get(String.valueOf(i));
            assertNotNull(score);
            assertEquals(i * 1000, score.intValue());
        }

    }
}
