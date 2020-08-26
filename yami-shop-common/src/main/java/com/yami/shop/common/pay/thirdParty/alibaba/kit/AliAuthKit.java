package com.yami.shop.common.pay.thirdParty.alibaba.kit;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.google.common.collect.Maps;
import com.mowanka.common.utils.Log;
import com.mowanka.common.utils.StringUtil;
import com.mowanka.project.thirdParty.alibaba.config.AlipayConfig;
import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * 支付宝授权登录工具类
 *
 * @author xhq
 * @version 1.0
 * @date 2019/8/12 10:57
 */
public class AliAuthKit {

    private static Logger log = Log.get();

    /**
     * 调用支付宝接口获取用户信息 并 注册
     * @param authCode 支付宝用户授权码
     * @return
     */
    public static Map<String, Object> getUserInfoFromAli(String authCode) {
        Map<String, Object> returnUser = Maps.newHashMap();
        try {
            // 第一步：在SDK调用前需要进行初始化
            AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL,
                    AlipayConfig.APP_ID,
                    AlipayConfig.APP_PRIVATE_KEY,
                    AlipayConfig.FORMAT,
                    AlipayConfig.CHARSET,
                    AlipayConfig.ALIPAY_PUBLIC_KEY,
                    AlipayConfig.SIGN_TYPE);
            // 第二步：使用auth_code换取接口access_token及用户userId
            AlipaySystemOauthTokenRequest asotRequest = new AlipaySystemOauthTokenRequest();
            asotRequest.setCode(authCode);
            asotRequest.setGrantType("authorization_code");
            AlipaySystemOauthTokenResponse asotResponse = alipayClient.execute(asotRequest);
            String accessToken = asotResponse.getAccessToken();
            String asotUserId = asotResponse.getUserId();
            // 第三步：根据accessToken调用接口获取用户信息
            AlipayUserInfoShareRequest requestUser = new AlipayUserInfoShareRequest();
            AlipayUserInfoShareResponse userInfo = alipayClient.execute(requestUser,accessToken);
            if (userInfo.getUserId() != null) {
                returnUser.put("nickName", userInfo.getNickName());
                returnUser.put("gender", ("m").equals(userInfo.getGender())?1:0);
                returnUser.put("avatar", userInfo.getAvatar());
                returnUser.put("uniqueId", userInfo.getUserId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnUser;
    }

    /**
     * 调用alipay SDK
     */
    public static String callAliAuth() {
        String suthStr = null;
        try {
            //拼接返回客户端参数
            suthStr = "apiname=com.alipay.account.auth&app_id=" + AlipayConfig.APP_ID +
                      "&app_name=mc&auth_type=AUTHACCOUNT&biz_type=openservice&method=alipay.open.auth.sdk.code.get" +
                      "&pid=" + AlipayConfig.P_ID + "&product_id=APP_FAST_LOGIN&scope=kuaijie&sign_type=RSA2" +
                      "&target_id=" + StringUtil.getRandomString(32, -1);
            //签名
            String sign = AlipaySignature.rsaSign(suthStr, AlipayConfig.APP_PRIVATE_KEY, AlipayConfig.CHARSET, AlipayConfig.SIGN_TYPE);
            //拼接
            suthStr = suthStr + "&sign=" + URLDecoder.decode(sign, AlipayConfig.CHARSET);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        log.info("获取支付宝授权登录签名={}", suthStr);
        return suthStr;
    }

}
