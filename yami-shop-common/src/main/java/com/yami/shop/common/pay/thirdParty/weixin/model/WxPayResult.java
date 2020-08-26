package com.yami.shop.common.pay.thirdParty.weixin.model;

/**
 * WxPayResult
 *
 * @author xhq
 * @version 1.0
 * @date 2019/9/6 17:29
 */
public class WxPayResult {

    /** SUCCESS/FAIL 此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断 */
    public final static String RETURN_CODE = "return_code";

    /** SUCCESS/FAIL 此字段是业务结果标识 */
    public final static String RESULT_CODE = "result_code";

    /** 成功标识 */
    public final static String SUCCESS = "SUCCESS";

}
