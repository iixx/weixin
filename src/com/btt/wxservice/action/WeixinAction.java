package com.btt.wxservice.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Enumeration;

import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btt.wxservice.task.WeixinBasicTask;

public class WeixinAction extends BaseAction {
    private static Logger logger = LoggerFactory.getLogger(WeixinAction.class);

	public void execute() {
		PrintWriter writer = null;
		response.setCharacterEncoding("utf-8");
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String token = "SXY";// token
		// 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");// 时间戳
		String nonce = request.getParameter("nonce");// 随机数
		String echostr = request.getParameter("echostr");// 随机字符串
		if (echostr != null) {// 如果随机字符串不为空，说明是微信首次验证服务器合法,理论上只会在首次提交验证申请时触发
			String[] str = new String[] { timestamp, nonce, token };
			Arrays.sort(str);
			String sha1 = DigestUtils.shaHex(str[0] + str[1] + str[2]);
			if (sha1.equalsIgnoreCase(signature)) {
				writer.print(echostr);
				writer.flush();
				writer.close();
			}
			return;
		}

		// 验证是否是微信发起的合法请求,使用网页调试工具时要跳过该验证
//		String[] strArray = new String[] { timestamp, nonce, token };
//		Arrays.sort(strArray);
//		String sha1 = DigestUtils.shaHex(strArray[0] + strArray[1]
//				+ strArray[2]);
//		if (!sha1.equalsIgnoreCase(signature)) {
//			logger.debug("微信请求验证失败");
//			return;
//		}

		// 假如服务器无法保证在五秒内处理并回复，可以直接回复空串，微信服务器不会对此作任何处理，并且不会发起重试。
		writer.print("");
		writer.flush();
		writer.close();

		// 枚举所有URL参数
		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = request.getParameter(key);
			try {
				key = new String(key.getBytes("ISO-8859-1"), "UTF-8");
				value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			logger.info("参数：KEY  {} VALUE  {}", key, value);
		}

		// 获取微信发来的消息体 xml
		StringBuilder body = new StringBuilder();
		String line = null;
		BufferedReader br = null;
		try {
			br = request.getReader();
			while ((line = br.readLine()) != null) {
				body.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
		    if(br != null){
		        try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
		    }
		}
		logger.debug("body : " + body);

		//处理消息
//		Document dom = null;
//		try {
//			dom = DocumentHelper.parseText(body.toString());
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//		Element root = dom.getRootElement();
//		String msgtype = root.element("MsgType").getTextTrim();
//		if (msgtype.equalsIgnoreCase("text")) {
//			new WeixinBasicTask().ReceiveUserMsg(body.toString());
//		}else if(msgtype.equalsIgnoreCase("image")){
//			
//		}
	}
}
