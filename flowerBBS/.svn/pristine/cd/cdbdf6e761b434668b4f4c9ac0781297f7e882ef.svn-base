package com.flower.util;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.api.PaymentApi.TradeType;
import com.jfinal.weixin.sdk.api.TemplateData;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;
import com.controller.WeixinApiCtrl;
import com.flower.Dao.FCDao;
import com.flower.util.Constant;
import com.util.Signature;

/**
 * 打赏,给小费
 * @author SHICHUNXIANG
 *
 */
public class GiveTip {
	
	

	/**
	 * 微信统一下单
	 * @param body 商品描述,如：帖子打赏
	 * @param ordercode 打赏的单号，我方生成
	 * @param price 打赏金额
	 * @param ip
	 * @param openid 付款openid
	 * @return
	 */
	public static String wxPushOrder(String body, String ordercode, double price, String ip, String openid){
		Map<String, String> params = new HashMap<>();
    	params.put("appid", PropKit.get("appId"));
    	params.put("mch_id", PropKit.get("mchId"));
    	params.put("nonce_str", System.currentTimeMillis() + "");
    	params.put("body", body);
    	params.put("out_trade_no", ordercode);
    	params.put("total_fee", (int)mul(price,100) + "");
    	params.put("spbill_create_ip", ip);
    	params.put("notify_url", Constant.getHostBbs+ "/weixin/wxpayback");//回调地址
    	params.put("trade_type", TradeType.JSAPI.name());
    	params.put("openid", openid);
    	params.put("sign", Signature.getSign(params));
    	String xml = PaymentApi.pushOrder(params);
    	return xml;
	}
	/**
	 * 为了解决下面的问题
	 * params.put("total_fee", (int)(price*100) + ""); 这个问题有点迷茫，64.99*100变成了6498
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double mul(double d1,double d2){  
        BigDecimal b1=new BigDecimal(Double.toString(d1));  
        BigDecimal b2=new BigDecimal(Double.toString(d2));  
        return b1.multiply(b2).doubleValue();  
          
    }
	
	/**
	 * 打赏支付成功之后，微信回调的主要处理方法
	 * @param out_trade_no
	 * @return
	 */
	public static boolean paySuccessNew(String out_trade_no){
	   boolean R=	Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				Record rd=Db.findFirst("select a.tipMoney,a.tipAid,a.postId,b.aid,c.headimg,c.nick,c.openid from f_tips as a left join f_post as b on a.postId=b.id LEFT JOIN f_account as c on a.tipAid=c.id where tipCode=?",out_trade_no);
				boolean flag=false;
				flag=(Db.update("update f_tips set state=1,payTime=NOW() where tipCode=?",out_trade_no))==1?true:false;
				if(flag==true){
					//钱包变化
					double oldMoney=Db.queryDouble("select wallet from f_account where id=?",rd.getInt("aid"));
					Record f_updateWallet=new Record();
					f_updateWallet.set("aid", rd.getInt("aid"));
					f_updateWallet.set("iMoney", rd.getDouble("tipMoney"));
					f_updateWallet.set("restMoney",oldMoney+ rd.getDouble("tipMoney"));
					f_updateWallet.set("cTime",new Date());
					f_updateWallet.set("type", 11);
					f_updateWallet.set("state", 1);
					Db.save("f_updateWallet", f_updateWallet);
					flag=(Db.update("update f_account set wallet=wallet+? where id=?",rd.getDouble("tipMoney"),rd.getInt("aid")))==1?true:false;
					//帖子的【打赏人数】加1
					flag=(Db.update("update f_post set tipsNum=tipsNum+1 where id=?", rd.getInt("postId")))==1?true:false;	
					
					String openid1=rd.getStr("openid");
					String nick=rd.getStr("nick");
					Integer aid=rd.getInt("aid");
					String openid2 = Db.queryStr("SELECT openid FROM f_account WHERE id = ?",aid);
					
					//if(openid1.equals(openid2)==false){
						// 绑定 apiConfig 到当前线程
						WeixinApiCtrl.setApiConfig();
						//给发帖人发模板消息（服务状态提醒）
						ApiResult result = TemplateMsgApi.send(TemplateData.New()
								// 消息接收者
								.setTouser(openid2)
								// 模板id
								.setTemplate_id(WeixinApiCtrl.gettemplateId("服务状态提醒"))
								.setTopcolor("#eb414a")
								.setUrl(Constant.getHostBbs + "/detail/"+rd.getInt("postId"))
								.add("first", nick+"在美美社区给你做了打赏，快去看看吧！", "#FF8040")
								.add("keyword1", "社区提醒", "#FF8040")
								.add("keyword2", "已打赏", "#FF8040").build());
						//WeixinApiCtrl.sendTemplateMsg(result.getJson());
					//}
				}

				return flag;
			}
		});
		return R;
	}
	
}
