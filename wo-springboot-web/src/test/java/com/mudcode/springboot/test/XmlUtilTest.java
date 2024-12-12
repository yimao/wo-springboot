package com.mudcode.springboot.test;

import com.mudcode.springboot.bean.IdNameItem;
import com.mudcode.springboot.common.util.XmlUtil;
import com.mudcode.springboot.controller.wechat.ChatMsgReq;
import com.mudcode.springboot.controller.wechat.ChatMsgResp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

class XmlUtilTest {
    private IdNameItem beanTest;

    @BeforeEach
    public void before() {
        beanTest = new IdNameItem();
        beanTest.setId(101);
        beanTest.setName("Hello, World!");
        beanTest.setDateTime(new Date());
    }

    @Test
    public void testXml() throws Exception {
        String xml = XmlUtil.toXml(beanTest);
        System.out.println(xml);
        IdNameItem item = XmlUtil.fromXml(xml, IdNameItem.class);
        System.out.println(item);
    }

    @Test
    public void testChatMsg() {
        String chatMsgStr = """
                <xml>
                  <ToUserName><![CDATA[toUser]]></ToUserName>
                  <FromUserName><![CDATA[fromUser]]></FromUserName>
                  <CreateTime>1348831860</CreateTime>
                  <MsgType><![CDATA[text]]></MsgType>
                  <Content><![CDATA[this is a test]]></Content>
                  <MsgId>1234567890123456</MsgId>
                  <MsgDataId>xxxx</MsgDataId>
                  <Idx>xxxx</Idx>
                </xml>
                """;
        ChatMsgReq chatMsg = XmlUtil.fromXml(chatMsgStr, ChatMsgReq.class);
        System.out.println(chatMsg);
        System.out.println(chatMsg.textMsgType());
    }

    @Test
    public void testChatMsg2() {
        String chatMsgStr = """
                <xml>
                    <ToUserName><![CDATA[toUser]]></ToUserName>
                    <FromUserName><![CDATA[fromUser]]></FromUserName>
                    <CreateTime>1348831860</CreateTime>
                    <MsgType><![CDATA[image]]></MsgType>
                    <PicUrl><![CDATA[this is a url]]></PicUrl>
                    <MediaId><![CDATA[media_id]]></MediaId>
                    <MsgId>1234567890123456</MsgId>
                     <MsgDataId>xxxx</MsgDataId>
                    <Idx>xxxx</Idx>
                </xml>
                """;
        ChatMsgReq chatMsg = XmlUtil.fromXml(chatMsgStr, ChatMsgReq.class);
        System.out.println(chatMsg);
        System.out.println(chatMsg.textMsgType());
    }

    @Test
    public void testChatMsgResp() {
        ChatMsgResp chatMsgResp = new ChatMsgResp();
        chatMsgResp.setToUserName(UUID.randomUUID().toString());
        chatMsgResp.setFromUserName(UUID.randomUUID().toString());
        chatMsgResp.setCreateTime(System.currentTimeMillis() / 1000);
        chatMsgResp.setMsgType("text");
        chatMsgResp.setContent("Hello, World!");
        System.out.println(XmlUtil.toXml(chatMsgResp));
    }
}
