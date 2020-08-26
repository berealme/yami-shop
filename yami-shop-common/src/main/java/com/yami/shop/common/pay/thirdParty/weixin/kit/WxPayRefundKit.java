package com.yami.shop.common.pay.thirdParty.weixin.kit;

import com.mowanka.common.utils.Log;
import com.mowanka.common.utils.StringUtil;
import com.mowanka.project.thirdParty.common.OrderPayDTO;
import com.mowanka.project.thirdParty.weixin.model.WxPayResult;
import com.mowanka.project.thirdParty.weixin.util.WXConfigure;
import com.mowanka.project.thirdParty.weixin.util.WXSignature;
import com.mowanka.project.thirdParty.weixin.util.XmlKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信退款工具类
 *
 * @author xhq
 * @version 1.0
 * @date 2019/9/6 16:15
 */
public class WxPayRefundKit {

    private static Logger log = Log.get();

    /**
     * 发起微信退款
     *
     * 参数：
     *      orderCode 订单编号
     *      refundCode 退款编号
     *      totalMoney  订单总金额
     *      refundMoney	退款金额
     * @return true:成功 false:失败
     */
    public static boolean callWxRefund(OrderPayDTO payDTO){
        log.info("发生微信退款,开始执行");
        boolean result = false;
        try {
            // 1.构造请求参数
            Map<String, String> requParam = createRefundRequMap(payDTO);
            // 获得签名
            String sign = WXSignature.getMapSign(requParam);
            // 加入签名参数
            requParam.put("sign", sign);
            // map组装为xml格式
            String xmlInfo = XmlKit.mapToXml(requParam);

            // 2.发送请求
            String respInfo = doit(xmlInfo);
            log.info("订单---【退款】与微信服务器交互的返回数据是：{}"+respInfo);
            // 3.请求结果
            if(StringUtils.isBlank(respInfo)) {
                return false;
            }
            // xml转为map
            Map<String, String> respMap = XmlKit.xmlToMap(respInfo);
            if(StringUtils.isNotBlank(respMap.get(WxPayResult.RESULT_CODE))
                    && WxPayResult.SUCCESS.equals(respMap.get(WxPayResult.RETURN_CODE))
                    && respMap.get(WxPayResult.RESULT_CODE).equals(WxPayResult.SUCCESS)){
                // 4.处理结果
                result = true;
                log.info("订单：{}退款【成功】,退款金额为{}元", payDTO.getOrderCode(), payDTO.getOrderMoney());
            }else{
                log.error("订单：{}退款【失败】,退款金额为{}元", payDTO.getOrderCode(), payDTO.getOrderMoney());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("订单--orderCode={}的订单申请微信退款，出现错误！{}", payDTO.getOrderCode(), e.getMessage());
        }
        return result;
    }


    /**
     * 生成微信退款请求参数【服务于退款】【此处不会添加签名】
     */
    private static Map<String, String> createRefundRequMap(OrderPayDTO payDTO) {
        Map<String, String> parameters = new HashMap<>(9);
        //公众账号ID
        parameters.put("appid", WXConfigure.WX_APP_ID);
        //商户号
        parameters.put("mch_id", WXConfigure.WX_MCH_ID);
        //随机字符串,不超过32位
        parameters.put("nonce_str", StringUtil.getRandomString(32, -1));
        //商户系统内部订单号
        parameters.put("out_trade_no", payDTO.getOrderCode());
        //商户退款单号
        parameters.put("out_refund_no", payDTO.getRefundCode());
        //金额转化为【分】的单位 (乘以100再去除小数点)
        BigDecimal orderMoney = payDTO.getOrderMoney().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        //订单总金额，单位为分，只能为整数
        parameters.put("total_fee", orderMoney.toString());
        //退款总金额，订单总金额，单位为分，只能为整数
        parameters.put("refund_fee", orderMoney.toString());
        //退款理由(**注意!!该字段不能填写中文,不然会报签名失败)
        parameters.put("refund_desc", "mokeng");
        //账户退款类型 使用可用余额退款
        parameters.put("refund_account", "REFUND_SOURCE_RECHARGE_FUNDS");
        return parameters;
    }

    /**
     * 发送退款请求
     * @param xmlInfo
     * @return
     * @throws Exception
     */
    private static String doit(String xmlInfo) throws Exception {
        StringBuilder buffer = new StringBuilder();
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        InputStream in = null;
        try {
            //加载证书
            in = WxPayRefundKit.class.getClassLoader().getResourceAsStream("apiclient_cert.p12");
            keyStore.load(in, WXConfigure.WX_MCH_ID.toCharArray());
        } finally {
            if (in != null) {
                in.close();
            }
        }
        SSLContext sslcontext = SSLContexts
                .custom()
                .loadKeyMaterial(keyStore, WXConfigure.WX_MCH_ID.toCharArray())
                .build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext, new String[] { "TLSv1" }, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf).build();
        try {
            // 退款接口
            HttpPost httpPost = new HttpPost(WXConfigure.WX_REFUND_URL);
            StringEntity reqEntity = new StringEntity(xmlInfo);
            // 设置类型
            reqEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(reqEntity);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                System.out.println(response.getStatusLine());
                if (entity != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                    String text;
                    while ((text = bufferedReader.readLine()) != null) {
                        buffer.append(text);
                    }
                }
                EntityUtils.consume(entity);
                return buffer.toString();
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
}
