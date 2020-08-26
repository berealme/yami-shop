package com.yami.shop.common.pay.thirdParty.weixin.kit;

import com.google.common.collect.Maps;
import com.mowanka.common.utils.Log;
import com.mowanka.common.utils.StringUtil;
import com.mowanka.project.thirdParty.common.OrderPayDTO;
import com.mowanka.project.thirdParty.weixin.model.OrderInfo;
import com.mowanka.project.thirdParty.weixin.model.OrderReturnInfo;
import com.mowanka.project.thirdParty.weixin.util.WXConfigure;
import com.mowanka.project.thirdParty.weixin.util.HttpRequest;
import com.mowanka.project.thirdParty.weixin.util.WXSignature;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 微信支付工具类
 *
 * @author xhq
 * @version 1.0
 * @date 2019/8/12 10:34
 */
public class WxPayKit {

    private static Logger log = Log.get();

    /**
     * 调用微信 SDK
     * 参数 orderCode 订单编号
     *      pName 商品名称
     *      orderMoney 支付金额
     */
    public static Map<String, String> callWxPay(OrderPayDTO payDTO) {
        Map<String, String> resultMap = Maps.newHashMap();
        try {
            //生成的随机字符串
            String nonceStr = StringUtil.getRandomString(32, 4);
            //获取本机的ip地址
            String spbillCreateIp = payDTO.getIp();
            //支付金额，单位：分，这边需要转成字符串类型，否则后面的签名会失败
            BigDecimal bd = payDTO.getOrderMoney();
            //前台传入金额保留两位小数 再 乘以100
            int lastMoney = bd.setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).intValue();
            log.info("支付金额为：{}", lastMoney);

            OrderInfo order = new OrderInfo();
            order.setAppid(WXConfigure.WX_APP_ID);
            order.setMch_id(WXConfigure.WX_MCH_ID);
            order.setNonce_str(nonceStr);
            order.setBody(payDTO.getpName());
            order.setOut_trade_no(payDTO.getOrderCode());
            order.setTotal_fee(lastMoney);
            order.setSpbill_create_ip(spbillCreateIp);
            order.setNotify_url(WXConfigure.WX_PAY_NOTIFY_URL);
            order.setTrade_type(WXConfigure.TRADETYPE);
            //生成签名
            String sign = WXSignature.getSign(order);
            order.setSign(sign);
            //先调用【统一下单API】生成预付单,获取到prepay_id
            String result = HttpRequest.sendPost(WXConfigure.WX_PAY_URL, order);
            log.info("---------下单返回:"+result);
            XStream xStream = new XStream();
            xStream.alias("xml", OrderReturnInfo.class);
            OrderReturnInfo returnInfo = (OrderReturnInfo)xStream.fromXML(result);
            resultMap.put("appid", WXConfigure.WX_APP_ID);
            resultMap.put("partnerid", returnInfo.getMch_id());
            resultMap.put("prepayid", returnInfo.getPrepay_id());
            resultMap.put("package", "Sign=WXPay");
            resultMap.put("noncestr", StringUtil.getRandomString(32, 4));
            resultMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            //获取到prepay_id后将参数再次签名传输给APP发起支付
            String sign2 = WXSignature.getMapSign(resultMap);
            resultMap.put("sign", sign2);
            log.info("返回前端数据：{}", resultMap);
            //判断是否已支付
            String isPay = "";
            if (StringUtils.isNotBlank(returnInfo.getErr_code()) && returnInfo.getErr_code().equals(WXConfigure.TRADE_PAID)) {
                isPay = returnInfo.getErr_code();
            }
            resultMap.put("isPay", isPay);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }
}
