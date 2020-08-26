package com.yami.shop.common.pay.thirdParty.alibaba.config;

import com.mowanka.common.support.BasicConfiguration;

/**
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 */
public class AlipayConfig {

    /** 应用ID,APPID，创建应用后生成 */
    public static String APP_ID = "alipay_appid";

    /** 支付宝唯一用户号 */
    public static String P_ID = "alipay_pid";

    /** 商户私钥，PKCS8格式RSA2私钥 */
    public static String APP_PRIVATE_KEY = "alipay_private_key";

    /** 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。*/
    public static String ALIPAY_PUBLIC_KEY = "alipay_public_key";

    /** 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 */
    public static String NOTIFY_URL = "alipay_notify_url";

    /** 商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2 */
    public static String SIGN_TYPE = "RSA2";

    /** 编码集，支持GBK/UTF-8 */
    public static String CHARSET = "utf-8";

    /** 参数返回格式，只支持json */
    public static String FORMAT = "json";

    /** 支付宝网关 */
    public static String URL = "alipay_url";

    /** 支付成功状态标识 */
    public static String TRADE_SUCCESS = "TRADE_SUCCESS";

    /** 单笔转账收款方账户类型  支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成 */
    public static String TRANSFER_PAYEE_TYPE = "ALIPAY_USERID";

}

