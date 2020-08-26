package com.yami.shop.common.pay.thirdParty.weixin.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 预订单
 * 
 * @author xhq
 *
 */
public class OrderInfo {
	/** appID */
	private String appid;
	/** 商户号 */
	private String mch_id;
	/** 随机字符串 */
	private String nonce_str;
	/** 签名类型 */
	private String sign_type;
	/** 签名 */
	private String sign;
	/** 商品描述 */
	private String body;
	/** 商户订单号 */
	private String out_trade_no;
	/** 标价金额 ,单位为分 */
	private int total_fee;
	/** 终端IP */
	private String spbill_create_ip;
	/** 通知地址 */
	private String notify_url;
	/** 交易类型 */
	private String trade_type;
	/** 用户标识	 */
	private String openid;

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public int getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(int total_fee) {
		this.total_fee = total_fee;
	}

	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}

	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

}
