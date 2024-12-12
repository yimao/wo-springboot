package com.mudcode.springboot.controller.wechat;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * text: 文本消息
 * <pre>
 * <xml>
 *   <ToUserName><![CDATA[toUser]]></ToUserName>
 *   <FromUserName><![CDATA[fromUser]]></FromUserName>
 *   <CreateTime>1348831860</CreateTime>
 *   <MsgType><![CDATA[text]]></MsgType>
 *   <Content><![CDATA[this is a test]]></Content>
 *   <MsgId>1234567890123456</MsgId>
 *   <MsgDataId>xxxx</MsgDataId>
 *   <Idx>xxxx</Idx>
 * </xml>
 * </pre>
 * <p>
 * image: 图片消息
 * <pre>
 * <xml>
 * 	<ToUserName><![CDATA[gh_bcd23cabcdd0]]></ToUserName>
 * 	<FromUserName><![CDATA[oz_fT6uSseqrTnv8116zZPwmVoos]]></FromUserName>
 * 	<CreateTime>1727172386</CreateTime>
 * 	<MsgType><![CDATA[image]]></MsgType>
 * 	<PicUrl><![CDATA[http://mmbiz.qpic.cn/mmbiz_jpg/l5nneIzzIrPDAibM2SauwJpe4liaz7mVmZvSicL2o4pawDJ1QNtc2lQiaPghI70dnnS7X2D4TQnElK7oBGUmMJp9bA/0]]></PicUrl>
 * 	<MsgId>24727163277095340</MsgId>
 * <MediaId><![CDATA[DLMa8Lxvtr6wuTum-IfRhi7pLAtfG2vThxaqpjULNWnBZJxavGew3UvW0gj5WnT_]]></MediaId>
 * </xml>
 * </pre>
 */
@Data
public class ChatMsgReq {

    @JacksonXmlProperty(localName = "ToUserName")
    private String toUserName;

    @JacksonXmlProperty(localName = "FromUserName")
    private String fromUserName;

    @JacksonXmlProperty(localName = "CreateTime")
    private long createTime;

    @JacksonXmlProperty(localName = "MsgType")
    private String msgType;

    @JacksonXmlProperty(localName = "Content")
    private String content;

    @JacksonXmlProperty(localName = "PicUrl")
    private String PicUrl;

    @JacksonXmlProperty(localName = "MsgId")
    private long msgId;

    @JacksonXmlProperty(localName = "MsgDataId")
    private String msgDataId;

    @JacksonXmlProperty(localName = "Idx")
    private String idx;

    public boolean validMsgType() {
        return textMsgType() || imgMsgType();
    }

    public boolean textMsgType() {
        return "text".equals(msgType);
    }

    public boolean imgMsgType() {
        return "image".equals(msgType);
    }
}
