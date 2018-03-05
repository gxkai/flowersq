package com.flower.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.HttpUtils;

/**
 * 用户打赏提现
 * @author SHICHUNXIANG
 *
 */
public class WithdrawCash {
	
	/**
	 * 企业付款到个人零钱
	 * com.util.SendCashRed.ComToUser(getRequest(), "oB_7EwDrolCk3yXP38m7IeZmBfLw",
			   "100","打赏提现")
	 * @param request
	 * @param openid
	 * @param amount 付款金额，单位：分
	 * @param desc 企业付款描述信息
	 * @return
	 */
	public static boolean ComToUser(HttpServletRequest request,String openid,
			String amount,String desc){
		String ip = getRemortIP(request);//获取用户ip
		String mchBillno ="CU"+System.currentTimeMillis() + "";
		Map<String, String> params = new HashMap<String, String>();
		params.put("mch_appid", PropKit.get("appId"));//公众号appid
		params.put("mchid", PropKit.get("mchId"));//商户号
		params.put("nonce_str", System.currentTimeMillis() / 1000 + "");//随机字符串
		params.put("partner_trade_no", mchBillno);// 商户订单号
		params.put("openid", openid);//用户公众号唯一识别
		params.put("check_name", "NO_CHECK");//校验用户姓名选项。NO_CHECK：不校验真实姓名;FORCE_CHECK：强校验真实姓名
		params.put("re_user_name", "");//收款用户姓名。当check_name为FORCE_CHECK的时候必填
		params.put("amount", amount);// 因为微信的amount的单位是分，所有需要乘以100
		params.put("desc", desc); //企业付款描述信息
		params.put("spbill_create_ip", ip);// Ip地址

		String sign = PaymentKit.createSign(params, PropKit.get("key"));
		params.put("sign", sign);

		String xmlResult = HttpUtils.postSSL("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", PaymentKit.toXml(params), Constant.certPath, PropKit.get("mchId"));

        Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
        
        //此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
        String return_code = result.get("return_code");
        //业务结果
        String result_code = result.get("result_code");
        
        if (StrKit.isBlank(return_code) || !"SUCCESS".equals(return_code)) {
            return false;
        }
        if (StrKit.notBlank(result_code) && "SUCCESS".equals(result_code)) {
            
            return true;
        }
        return false;


		
		
		
	}
	
	
	
	/**
	 * 获取请求的IP地址
	 * @param request
	 * @return
	 */
	public static String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr(); 
		}
		String ipStr = request.getHeader("x-forwarded-for");
		String[] ipArr = ipStr.split(",");
		String ip = new String();
		for(String i : ipArr){
			if(!"unknown".equals(ip)){
				ip = i;
				break;
			}
		}
		return ip;
	}

}
