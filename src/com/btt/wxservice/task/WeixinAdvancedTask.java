package com.btt.wxservice.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.btt.web.util.HttpHelper;

/**
 * 微信高级功能 
 * 包含
 * 用户管理 
 * 自定义菜单等
 * @author song
 *
 */
public class WeixinAdvancedTask {
	private static Logger logger = LoggerFactory
			.getLogger(WeixinAdvancedTask.class);
	/**
	 * 创建自定义菜单
	 */
	public void createCustomMenu(){
		String json = "{\"button\":[{\"type\":\"click\",\"name\":\"常用功能\",\"key\":\"common_function\"},{\"type\":\"click\",\"name\":\"功能大全\",\"key\":\"function_menu\"},{\"name\":\"帮助\",\"sub_button\":[{\"type\":\"view\",\"name\":\"公司网站\",\"url\":\"http://www.baidu.com/\"},{\"type\":\"click\",\"name\":\"关于公司\",\"key\":\"about_company\"},{\"type\":\"click\",\"name\":\"赞一下我们\",\"key\":\"support_company\"}]}]}";
		HttpHelper helper = new HttpHelper();
		helper.init("https://api.weixin.qq.com/cgi-bin/menu/create");
		helper.append("access_token", WeixinGlobalTask.getAccess_token());
		String result = helper.doSendWithBody(json);
		if(JSON.parseObject(result).getString("errcode").equalsIgnoreCase("0")){
			logger.info("自定义菜单创建成功");
		}else{
			logger.info("自定义菜单创建失败,错误代码>>{}",result);
		}
	}
	
	/**
	 * 查询自定义菜单
	 */
	public void queryCustomMenu(){
		HttpHelper helper = new HttpHelper();
		helper.init("https://api.weixin.qq.com/cgi-bin/menu/get");
		helper.append("access_token", WeixinGlobalTask.getAccess_token());
		String result = helper.doSend();
		logger.info("查询自定义菜单,json>>{}",result);
	}
	
	/**
	 * 删除自定义菜单
	 */
	public void deleteCustomMenu(){
		HttpHelper helper = new HttpHelper();
		helper.init("https://api.weixin.qq.com/cgi-bin/menu/delete");
		helper.append("access_token", WeixinGlobalTask.getAccess_token());
		String result = helper.doSend();
		if(JSON.parseObject(result).getString("errcode").equalsIgnoreCase("0")){
			logger.info("删除自定义菜单成功");
		}else{
		logger.info("删除自定义菜单失败，{}",result);
		}
	}
	
	public static void main(String args[]){
		new WeixinAdvancedTask().createCustomMenu();
		new WeixinAdvancedTask().queryCustomMenu();
		return;
	}
}
