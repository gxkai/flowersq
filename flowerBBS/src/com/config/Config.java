package com.config;

import com.controller.WeixinApiCtrl;
import com.flower.FrontCtrl.FrontIndexCtrl;
import com.flower.FrontCtrl.FrontPorxyCtrl;
import com.flower.handler.WebSocketHandler;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

public class Config extends JFinalConfig  {

	@Override
	public void configConstant(Constants me) {
		//果园
		PropKit.use("config_develop_gy.txt");
		//e合作社
		//PropKit.use("config_develop_e.txt");
		//花美美
		//PropKit.use("config.txt");
		me.setDevMode(true);
		ApiConfigKit.setDevMode(me.getDevMode());
		
	}

	@Override
	public void configRoute(Routes me) {
		
		me.add("/", FrontIndexCtrl.class,"/Front");
		me.add("/porxy",FrontPorxyCtrl.class,"/Front");
		
		/*************** 微信 **************/
		me.add("/weixin", WeixinApiCtrl.class);
	}

	@Override
	public void configPlugin(Plugins me) {
		C3p0Plugin c3p0Plugin = new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
		me.add(c3p0Plugin);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		me.add(arp);
		
	}

	@Override
	public void configInterceptor(Interceptors me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configHandler(Handlers me) {
		me.add(new WebSocketHandler());
		
	}

}
