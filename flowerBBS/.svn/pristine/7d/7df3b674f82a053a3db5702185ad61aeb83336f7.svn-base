package com.controller;


import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;



import com.google.gson.Gson;
import com.jfinal.kit.HttpKit;

import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.JsTicket;
import com.jfinal.weixin.sdk.api.JsTicketApi;
import com.jfinal.weixin.sdk.api.JsTicketApi.JsApiType;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.util.Sign;
import com.util.Signature;
import com.util.XMLParser;
import com.flower.util.*;



/**
 * @Desc 微信设置
 * @author lxx
 * @date 2016/8/23
 * */
public class WeixinApiCtrl extends ApiController {
	@Override
	public ApiConfig getApiConfig() {
		ApiConfig ac = new ApiConfig();
        // 配置微信 API 相关常量
        ac.setToken(PropKit.get("token"));
        ac.setAppId(PropKit.get("appId"));
        ac.setAppSecret(PropKit.get("appSecret"));
        ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
        ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));
        return ac;
        
	}
    
    // config接口注入权限验证配置
    public void configvalid(){
    	JsTicket jt = JsTicketApi.getTicket(JsApiType.jsapi);
    	Map<String, String> map = Sign.sign1(jt.getTicket(), getPara("url"));
    	renderJson(map);
    }
    
	/**
 	 * 绑定 apiConfig 到当前 线程
 	 */
	public static void setApiConfig() {

		ApiConfig ac = new ApiConfig();
		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));

		ApiConfigKit.setThreadLocalApiConfig(ac);

	}

	/**
	 * 打赏支付成功，微信异步回调
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void wxpayback() throws IOException, ParserConfigurationException, SAXException{
 		ServletInputStream in = getRequest().getInputStream();
		int size = getRequest().getContentLength();
		if(size>0){
			byte[] bdata = new byte[size];
			in.read(bdata);
			String xmlStr = new String(bdata, XMLParser.getCharacterEncoding(getRequest(), getResponse()));
			if(Signature.SignValidIsFromWeXin(xmlStr)){
				Map<String,Object> paramsMap = XMLParser.getMapFromXML(xmlStr);
				String return_code = paramsMap.get("return_code").toString();
				String out_trade_no = paramsMap.get("out_trade_no").toString();
				if ("SUCCESS".equals(return_code)) {
					if(GiveTip.paySuccessNew(out_trade_no)){
						renderJson("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
					}else{
						renderJson("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERR]]></return_msg></xml>");
					}
				}else{
					renderJson("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERR]]></return_msg></xml>");
				}
			}
		}
 	}


	// 获取模板ID
 	public static String gettemplateId(String title){
 		String token=HttpUtils.get(Constant.getTokenUrl);
 		//System.out.println("token:"+token);
 		String url = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token="+token;
 		String json = HttpUtils.get(url);
 		String temp = json.substring(json.indexOf("["), json.indexOf("]")+1);
 		Gson gson = new Gson();
 		String tid = new String();
		List<Map<String, Object>> tlist = gson.fromJson(temp, ArrayList.class);
		for (Map<String, Object> map : tlist) {
			if(title.equalsIgnoreCase((String) map.get("title"))){
				tid = (String)map.get("template_id");
			}
		}
		return tid;
 	}
 	
 	// 获得模版消息
 	public void gettemplate(){
 		String token=HttpUtils.get(Constant.getTokenUrl);
 		String url = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token="+token;
 		String json = HttpUtils.get(url);
 		renderJson(json);
 	}
 	
 	//发送模板消息
    public static ApiResult sendTemplateMsg(String jsonStr) {
    	String token=HttpUtils.get(Constant.getTokenUrl);
        String jsonResult = HttpKit.post("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token, jsonStr);
        return new ApiResult(jsonResult);
    }
 	
 	
 	// 执行一次 存入所有用户的  unionid
 	/*public void saveUnionid() {
 		
 		ApiResult users = UserApi.getFollows();
		Map<?, ?> data = users.getMap("data");
		ArrayList<String> openids = (ArrayList<String>) data.get("openid");
		for (int i = 0; i < openids.size(); i++) {
			ApiResult ar = UserApi.getUserInfo(openids.get(i));
			Db.update("update f_account set unionid = ? where openid = ?", ar.get("unionid"),openids.get(i));
		}
	    renderJson(true);
	}*/
 	
}