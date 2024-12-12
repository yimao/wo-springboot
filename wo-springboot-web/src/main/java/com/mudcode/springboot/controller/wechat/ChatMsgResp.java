package com.mudcode.springboot.controller.wechat;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * <pre>
 * <xml>
 *   <ToUserName><![CDATA[toUser]]></ToUserName>
 *   <FromUserName><![CDATA[fromUser]]></FromUserName>
 *   <CreateTime>12345678</CreateTime>
 *   <MsgType><![CDATA[text]]></MsgType>
 *   <Content><![CDATA[你好]]></Content>
 * </xml>
 * </pre>
 */
@JacksonXmlRootElement(localName = "xml")
@Data
public class ChatMsgResp {

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "ToUserName")
    private String toUserName;

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "FromUserName")
    private String fromUserName;

    @JacksonXmlProperty(localName = "CreateTime")
    private long createTime;

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "MsgType")
    private String msgType;

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Content")
    private String content;

    public ChatMsgResp() {
    }

    public ChatMsgResp(ChatMsgReq msgReq, String content) {
        this.msgType = "text";
        int maxLength = 550;
        this.content = content.length() > maxLength
                ? content.substring(0, maxLength) + "\n\n\n……文字太长了……"
                : content
        ;
        this.toUserName = msgReq.getFromUserName();
        this.fromUserName = msgReq.getToUserName();
        this.createTime = System.currentTimeMillis() / 1000;
    }
}
