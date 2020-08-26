package com.yami.shop.common.pay.thirdParty.weixin.util;

import com.mowanka.common.support.BasicConfiguration;

/**
 * 微信支付工具类
 * @author xhq
 * @version 1.0
 * @date 2019/7/31 14:55
 */
public class WXConfigure {

    /** 微信统一下单接口地址 */
    public static final String WX_PAY_URL = BasicConfiguration.getInstance().getValue("wx_unify_order_url");
    /** 支付成功后的服务器回调url */
    public static final String WX_PAY_NOTIFY_URL = BasicConfiguration.getInstance().getValue("wx_notify_url");
    /** 微信退款url */
    public static final String WX_REFUND_URL = BasicConfiguration.getInstance().getValue("wx_refund_url");
    /** appid */
    public static final String WX_APP_ID = BasicConfiguration.getInstance().getValue("weixin_appid");
    /** AppSecret */
    public static final String WX_APP_SECRET = BasicConfiguration.getInstance().getValue("weixin_secret");
    /** 微信支付的商户id */
    public static final String WX_MCH_ID = BasicConfiguration.getInstance().getValue("weixin_pay_mchid");
    /** 微信支付的商户密钥 */
    public static final String WX_PAY_KEY = BasicConfiguration.getInstance().getValue("weixin_pay_key");
    /** 签名方式 */
    public static final String SIGNTYPE = "MD5";
    /** 交易类型 */
    public static final String TRADETYPE = "APP";
    /** 支付成功状态标识 */
    public static String TRADE_SUCCESS = "SUCCESS";
    /** 支付失败状态标识 */
    public static String TRADE_FAIL = "FAIL";
    /** 已支付状态标识 */
    public static String TRADE_PAID = "ORDERPAID";
}
