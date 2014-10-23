package com.btt.wxservice.action;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseAction implements SessionAware, ServletRequestAware,
		ServletResponseAware {
	protected static Logger logger = LoggerFactory.getLogger(BaseAction.class.getName());

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	@SuppressWarnings("rawtypes")
	protected SessionMap session;

	@SuppressWarnings("rawtypes")
	public void setSession(Map session) {
		this.session = (SessionMap) session;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
}

/*
 * 在struts1中，获得到系统的session/request/response对象非常方便，都是按照形参传递的，但是在struts2中，这些对象都被隐藏了
 * 。 一般利用SPRING里面所说的IOC即控制反转方式来访问session/request/response对象，具体实现如下：
 * action类实现ServletRequestAware/ServletResponseAware/SessionAware接口
 * 并创建HttpServletRequest request，HttpServletResponse response，MAP对象session
 * public class UserLoginAction extends ActionSupport implements
 * ServletRequestAware{ public void setServletRequest(HttpServletRequest
 * request) { this.request=request; } public void
 * setServletResponse(HttpServletResponse response) { this.response = response;
 * } public void setSession(Map session) { this.session=session; } }
 * 在Struts2中底层的session都被封装成了Map类型
 * ，我们称之为SessionMap，而平常我们所说的session则是指HttpSession对象。
 * 可以首先得到HttpServletRequest对象，然后通过request
 * .getSession()来取得原始的HttpSession对象，之后我们就可以对session进行读写了。
 * 一般情况下SessionMap已经可以完成所有的工作，我们不必再去碰底层的session了。
 */
