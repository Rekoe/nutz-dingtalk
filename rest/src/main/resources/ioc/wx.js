var ioc = {
	wxJsapiTicketStore : {
		type : "org.nutz.weixin.at.impl.MemoryJsapiTicketStore"
	},
	wxApi : {
		type : "org.nutz.weixin.impl.WxApi2Impl",
		fields : {
			token : {
				java : "$conf.get('weixin.token')"
			},
			appid : {
				java : "$conf.get('weixin.appid')"
			},
			appsecret : {
				java : "$conf.get('weixin.secret')"
			},
			encodingAesKey : {
				java : "$conf.get('weixin.aes')"
			},
			jsapiTicketStore : {
				refer : 'wxJsapiTicketStore'
			}
		}
	}
}