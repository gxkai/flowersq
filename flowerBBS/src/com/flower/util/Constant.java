package com.flower.util;

import com.jfinal.kit.PropKit;

/**
 * @see 系统常量设计
 * @author yeqing
 * @date 2016/08/09
 */
public class Constant {
   
   // 域名
   public static String getHost = PropKit.get("host");
   public static String getHostBbs=PropKit.get("hostbbs");
   //证书地址，用于发红包、退款、提现等
   public static String certPath=PropKit.get("certPath");
   
   // 用户同意授权，获取code
   public static String getCode = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
   // 通过code换取网页授权access_token
   public static String getAccess_Token = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
   // 拉取用户信息(需scope为 snsapi_userinfo)
   public static String getSnsapi_userinfo = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
   
   //从总代理获取全局access_token
   public static String getTokenUrl=PropKit.get("getTokenUrl");
   
  
 

}