package com.btt.wxservice.task;

import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.btt.web.util.HttpHelper;

/**
 * 微信基础权限接口 包括： 接收用户消息 向用户回复消息 接受事件推送
 * 
 * @author song
 * 
 */
public class WeixinBasicTask {
	private static Logger logger = LoggerFactory
			.getLogger(WeixinBasicTask.class.getName());

	/**
	 * 接收用户消息 并使用客服接口回复消息
	 * 包括 
	 * 文字 语音 图片 视频 链接 地理位置
	 * @param xml
	 */
	public void ReceiveUserMsg(String xml) {
		Document dom = null;
		try {
			dom = DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element root = dom.getRootElement();
		for (Iterator<?> i = root.elementIterator(); i.hasNext();) {
			Element element = (Element) i.next();
			System.out.println(element.getName() + ">>" + element.getTextTrim());
		}
		// 用户消息类型 text image voice video location link
		String msgtype = root.element("MsgType").getTextTrim();
		if (msgtype.equalsIgnoreCase("text")) {
			String content = "你输入的是：" + root.element("Content").getTextTrim();
			// 异步返回数据 调用客服消息接口 时限48小时 可重复发送
			String access_token = WeixinGlobalTask.getAccess_token();
			HttpHelper h = new HttpHelper();
			h.init("https://api.weixin.qq.com/cgi-bin/message/custom/send");
			h.append("access_token", access_token);
			String json = "{\"touser\":\""
					+ root.element("FromUserName").getText()
					+ "\",\"msgtype\":\"text\",\"text\":{\"content\":\""
					+ content + "\"}}";
			String result = h.doSendWithBody(json);
			String errcode = JSON.parseObject(result).getString("errcode");
			if (errcode.equalsIgnoreCase("0")) {
				logger.debug("响应成功");
			} else if (errcode.equalsIgnoreCase("40002")) {
				logger.info("响应失败，access_token错误,{}", access_token);
				logger.info(result);
			}
		}
	}
	
	/**
	 * 接收事件推送
	 * 包括
	 * 关注/取消关注事件
	 * 扫描带参数二维码事件
	 * 上报地理位置事件
	 * 自定义菜单事件
	 * 点击菜单拉取消息时的事件推送
	 * 点击菜单跳转链接时的事件推送
	 * @param xml
	 */
	public void ReceiveEventPush(String xml){
		
	}
}
