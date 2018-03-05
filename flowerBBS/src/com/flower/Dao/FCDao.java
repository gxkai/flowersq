package com.flower.Dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.UserApi;
import com.util.Constant;

public class FCDao {

	public static Map<String, Object> getAccessToken(String code) {
		String rUrl = Constant.getAccess_Token
				.replace("APPID", PropKit.get("appId"))
				.replace("SECRET", PropKit.get("appSecret"))
				.replace("CODE", code);
		Map<String, Object> map = new HashMap<>();
		try {
			URL url = new URL(rUrl);
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}

			br.close();
			isr.close();
			is.close();
			
			ApiResult ar = new ApiResult(builder.toString());
			map.put("access_token", ar.get("access_token"));
			map.put("openid", ar.get("openid"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 注册或启用用户
	 * @param access_token
	 * @param openId
	 * @param typeId
	 * @param eventUserId
	 * @return
	 */
	public static Record setAccount(String access_token, String openId, String typeId, String eventUserId){
		Record account = Db.findFirst("select id,openid,nick,headimg,tel,recommend,isbuy,qrurl,state,isfans,ctime from f_account where openid = ?", openId);
		if(account == null){
			ApiResult ar;
			account = new Record();
			account.set("openid", openId);
			if(access_token == null){
				ar = UserApi.getUserInfo(openId);
				account.set("isfans", 0);
			}else{
				String userinfo = HttpKit.get(Constant.getSnsapi_userinfo
												.replace("ACCESS_TOKEN", access_token)
												.replace("OPENID", openId));
				ar = new ApiResult(userinfo);
				account.set("isfans", 3);
			}
			try {
				account.set("nick", filterOffUtf8Mb4_2(ar.get("nickname").toString()));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			//  存入用户  unionid 
			/*if (ar.get("unionid") != null) {
				account.set("unionid", ar.get("unionid"));
			}*/
			account.set("headimg", ar.get("headimgurl"));
			account.set("isbuy", 0);
			account.set("ctime", new Date());
			account.set("state", 0);
			if(typeId!=null && eventUserId!=null){
				account.set("tjid", typeId+"_"+eventUserId);
			}
			
			boolean result = Db.save("f_account", account);
			if(result){
				account.set("id", account.getLong("id").intValue());//添加数据库后，获取自增长的ID号
			}
		}else{
			//isfans=3只是注册了，但是没有关注，例如自己通过连接打开了主页
			if(account.getInt("isfans")==3){
				
				int i=0;
				if(typeId!=null && eventUserId!=null){
					i=Db.update("update f_account set isfans = 0,state = 0,tjid=? where openid = ?",typeId+"_"+eventUserId, openId);
				}else{
					i=Db.update("update f_account set isfans = 0,state = 0 where openid = ?", openId);
				}
				if(i==1){
				}
			}
			//isfans=1 表示以前关注过，但是后来取消了关注
			if(account.getInt("isfans")==1){
				Db.update("update f_account set isfans = 0,state = 0 where openid = ?", openId);
				
			}
		}
		return account;
	}
	
	/**
	 * Java 过滤非汉字的utf8的字符
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String filterOffUtf8Mb4_2(String text) throws UnsupportedEncodingException {
		byte[] bytes = text.getBytes("utf-8");
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		int i = 0;
		while (i < bytes.length) {
		short b = bytes[i];
		if (b > 0) {
		buffer.put(bytes[i++]);
		continue;
		}

		b += 256; //去掉符号位

		if (((b >> 5) ^ 0x06) == 0) {
		buffer.put(bytes, i, 2);
		i += 2;
		/*System.out.println("2");*/
		} else if (((b >> 4) ^ 0x0E) == 0) {
		/*System.out.println("3");*/
		buffer.put(bytes, i, 3);
		i += 3;
		} else if (((b >> 3) ^ 0x1E) == 0) {
		i += 4;
		/*System.out.println("4");*/
		} else if (((b >> 2) ^ 0xBE) == 0) {
		i += 5;
		/*System.out.println("5");*/
		} else {
		i += 6;
		/*System.out.println("6");*/
		}
		}
		buffer.flip();
		return new String(buffer.array(), "utf-8");
		}
	
}
