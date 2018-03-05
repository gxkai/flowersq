package com.flower.FrontCtrl;


import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.flower.util.Constant;

import com.controller.WeixinApiCtrl;


public class FrontPorxyCtrl extends Controller{

	//代理，用于测试
		public void getToken(){
			WeixinApiCtrl.setApiConfig();
			String token = AccessTokenApi.getAccessToken().getAccessToken();
			renderJson(token);
		}
		//代理，用于测试
		public void index_Proxy(){
			String state = getPara("state");
			String source=getPara("source");
			String code=getPara("code");
			if("openid".equalsIgnoreCase(state)){
				String url=source+"?code="+code+"&state="+state;
				redirect(url);
			}else{
				String getcodeUrl = Constant.getCode
						.replace("APPID", PropKit.get("appId"))
						.replace("REDIRECT_URI", Constant.getHost+getRequest().getRequestURI())
						.replace("SCOPE", "snsapi_userinfo")
						.replace("STATE", "openid");
				
				redirect(getcodeUrl);
			}
			
		}
}
