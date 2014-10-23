package com.btt.wxservice.task;

import java.text.ParseException;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.btt.web.util.HttpHelper;

/**
 * 微信全局任务 全局获取access_token等
 * 
 * @author song
 * 
 */
public class WeixinGlobalTask implements Job {
	private static Logger logger = LoggerFactory
			.getLogger(WeixinGlobalTask.class);
	/**
	 * access_token是公众号的全局唯一票据 有效期为7200秒 由于获取access_token的api调用次数非常有限
	 * 建议开发者全局存储与更新access_token 频繁刷新access_token会导致api调用受限 影响自身业务
	 */
	private static String access_token = "";
	private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
	private static final String ACCESS_TOKEN_PARAM_GRANT_TYPE = "client_credential";
	// 以下两项根据实际情况配置
	private static final String ACCESS_TOKEN_PARAM_APPID = "wx7083c50d2556a1fd";
	private static final String ACCESS_TOKEN_PARAM_SECRET = "e761a5b91a02c46d069402c55a5b9b99";

	// 定时任务
	private static Scheduler sched = null;
	private static WeixinGlobalTask task = new WeixinGlobalTask();
	
	private static HttpHelper h = new HttpHelper();
	static{
		h.init(ACCESS_TOKEN_URL);
		h.append("grant_type", ACCESS_TOKEN_PARAM_GRANT_TYPE);
		h.append("appid", ACCESS_TOKEN_PARAM_APPID);
		h.append("secret", ACCESS_TOKEN_PARAM_SECRET);
	}
	/**
	 * 全局调用access_token 自动更新access_token
	 * 
	 * @return
	 */
	public static String getAccess_token() {
		if (access_token.equalsIgnoreCase("") || access_token == null) {
			String access_token = h.doSend();
			
			WeixinGlobalTask.access_token = JSON.parseObject(access_token).getString(
					"access_token");
			if(WeixinGlobalTask.access_token==null){
				logger.info("获取access_token失败,{}",access_token);
			}else{
				// 获取access_token后激活定时任务
				try {
					task.run();
					logger.debug("定时任务启动,7200秒后关闭任务");
				} catch (SchedulerException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			return WeixinGlobalTask.access_token;
		} else {
			return access_token;
		}
	}
	
	/**
	 * 全局解析微信下发的XML分属哪一条支线
	 * @param xml
	 * @return
	 */
	public static String getXml2Where(String xml){
		Document dom = null;
		try {
			dom = DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
		Element root = dom.getRootElement();
		String msgtype = root.element("MsgType").getTextTrim();
		if(msgtype.equalsIgnoreCase("text")){
			new WeixinBasicTask().ReceiveUserMsg(xml);
		}
		return null;
	}

	/**
	 * 定时刷新access_token 刷新间隔7200秒
	 * 7200秒后access_token自动重置为空字符串
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		access_token = "";
		// 停止任务
		try {
			this.stop();
			logger.debug("任务停止");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 定时任务
	 * 
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public void run() throws SchedulerException, ParseException {
		System.setProperty("org.quartz.scheduler.skipUpdateCheck", "true");
		SchedulerFactory factory = new StdSchedulerFactory();
		sched = factory.getScheduler();
		JobDetail job = new JobDetail("access_tokenJob",
				Scheduler.DEFAULT_GROUP, this.getClass());
		// 每7190秒执行一次 冗余10秒容差
		SimpleTrigger trigger = new SimpleTrigger("access_tokenTrigger",new Date(System.currentTimeMillis() + 7190000L));
		sched.scheduleJob(job, trigger);
		sched.start();
	}

	public void stop() throws Exception {
		sched.shutdown();
	}
}
