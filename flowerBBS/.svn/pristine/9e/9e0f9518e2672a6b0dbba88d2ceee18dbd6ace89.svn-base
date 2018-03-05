// config权限验证
function wxconfig(url){
	$.post('/weixin/configvalid', {'url':url.toString()}, function(data){
		wx.config({
		    debug: false,
		    appId: data.appId,
		    timestamp: data.timestamp,
		    nonceStr: data.nonceStr,
		    signature: data.signature,
		   /* jsApiList: ['onMenuShareTimeline','onMenuShareAppMessage']*/
		    jsApiList: [
		                'checkJsApi',
		                'onMenuShareTimeline',
		                'onMenuShareAppMessage',
		                'onMenuShareQQ',
		                'onMenuShareWeibo',
		                'onMenuShareQZone',
		                'hideMenuItems',
		                'showMenuItems',
		                'hideAllNonBaseMenuItem',
		                'showAllNonBaseMenuItem',
		                'translateVoice',
		                'startRecord',
		                'stopRecord',
		                'onVoiceRecordEnd',
		                'playVoice',
		                'onVoicePlayEnd',
		                'pauseVoice',
		                'stopVoice',
		                'uploadVoice',
		                'downloadVoice',
		                'chooseImage',
		                'previewImage',
		                'uploadImage',
		                'downloadImage',
		                'getNetworkType',
		                'openLocation',
		                'getLocation',
		                'hideOptionMenu',
		                'showOptionMenu',
		                'closeWindow',
		                'scanQRCode',
		                'chooseWXPay',
		                'openProductSpecificView',
		                'addCard',
		                'chooseCard',
		                'openCard',
		                'openAddress',
		                'getLocalImgData'
		            ]
		});
	});
}


