package com.flower.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.flower.Dao.FCDao;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.flower.util.Constant;
import com.google.gson.Gson;

/**
 * 1.请求打到代理，
 * 2.代理请求微信获得code等信息
 * 3.代理把信息打回来
 * REDIRECT_URI为微信重定向的地址，域名必须和微信后台设置的【网页授权域名】保持一致
 * @author SHICHUNXIANG
 *
 */
public class FrontInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		
		Controller c = inv.getController();
		Record account = c.getSessionAttr("account");
		
		if(account == null){
			String state = c.getPara("state");
			if("openid".equalsIgnoreCase(state)){
				String code = c.getPara("code");
				
				Map<String, Object> map = FCDao.getAccessToken(code);
				
				// 根据openId获取用户
				account = FCDao.setAccount(map.get("access_token").toString(), map.get("openid").toString(), null, null);
				c.setSessionAttr("account", account);
				int state_user = Db.queryInt("select state from f_account where id=?", account.getInt("id"));
				if(state_user == 1){
					c.redirect("/freeze");
				}else{
					inv.invoke();
				}
				
			}else{
				//此处加了一个技巧
				//source参数加上了代理重定向的url
				String getcodeurl = Constant.getCode
						.replace("APPID", PropKit.get("appId"))
						.replace("REDIRECT_URI",  Constant.getHost+"?source="+Constant.getHostBbs + c.getRequest().getRequestURI())
						.replace("SCOPE", "snsapi_userinfo")
						.replace("STATE", "openid");
				//System.out.println(getcodeurl);
				c.redirect(getcodeurl);
				
				
			}
		}else{
			int state_user = Db.queryInt("select state from f_account where id=?", account.getInt("id"));
			//System.out.println(state_user);
			if(state_user == 1){
				c.redirect("/freeze");
			}else{
				inv.invoke();
			}
		}
	}
	
	
	
}
