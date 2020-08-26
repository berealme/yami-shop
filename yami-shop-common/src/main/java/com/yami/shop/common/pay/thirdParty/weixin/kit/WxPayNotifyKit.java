package com.yami.shop.common.pay.thirdParty.weixin.kit;

import com.mowanka.common.utils.Log;
import com.mowanka.project.thirdParty.weixin.util.WXSignature;
import org.slf4j.Logger;

import java.util.Map;

/**
 * 微信支付回调工具类
 *
 * @author xhq
 * @version 1.0
 * @date 2019/8/12 10:34
 */
public class WxPayNotifyKit {

    private static Logger log = Log.get();

    /**
     * 验证回调签名
     * @return true：验证通过 false：验证失败
     */
    public static boolean checkSign(Map<String, String> map) {
        //微信回调返回回来的sign
        String signFromResponse = map.get("sign");
        if (signFromResponse == null || "".equals(signFromResponse)) {
            log.error("微信回调返回的数据签名数据不存在，有可能被第三方篡改!!!");
            return false;
        }
        //不能把返回回来的sign也加入签名计算
        map.remove("sign");
        //将API返回的数据根据签名算法进行计算新的签名
        String signFromCheck = WXSignature.getMapSign(map);

        //将新的签名跟微信返回的签名进行比较是否相同
        return signFromResponse.equals(signFromCheck);
    }

    /**
     * 回复微信的报文
     * @param returnCode
     * @return
     */
    public static String returnXML(String returnCode) {
        return "<xml><return_code><![CDATA["+ returnCode +
                "]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }
}
