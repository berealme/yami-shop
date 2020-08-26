package com.yami.shop.common.pay.thirdParty.alibaba.kit;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.mowanka.common.utils.Log;
import com.mowanka.project.thirdParty.alibaba.config.AlipayConfig;
import com.mowanka.project.thirdParty.common.OrderPayDTO;
import org.slf4j.Logger;

/**
 * 支付宝支付工具类
 *
 * @author xhq
 * @version 1.0
 * @date 2019/8/12 10:31
 */
public class AliPayKit {

    private static Logger log = Log.get();

    /**
     * 调用alipay SDK
     * 参数 orderCode 订单编号
     *      pName 商品名称
     *      orderMoney 支付金额
     */
    public static AlipayTradeAppPayResponse callAlipay(OrderPayDTO payDTO) throws AlipayApiException {
        //初始化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL,
                AlipayConfig.APP_ID,
                AlipayConfig.APP_PRIVATE_KEY,
                AlipayConfig.FORMAT,
                AlipayConfig.CHARSET,
                AlipayConfig.ALIPAY_PUBLIC_KEY,
                AlipayConfig.SIGN_TYPE);
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alibaba.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        //商品的备注、描述、明细等
        model.setBody(payDTO.getpName());
        //商品名称
        model.setSubject(payDTO.getpName());
        //商户订单号
        model.setOutTradeNo(payDTO.getOrderCode());
        //交易超时时间
        model.setTimeoutExpress("30m");
        //支付金额
        model.setTotalAmount(payDTO.getOrderMoney().toString());
        //销售产品码,商家和支付宝签约的产品码 (如 QUICK_MSECURITY_PAY)
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(AlipayConfig.NOTIFY_URL);
        //这里和普通的接口调用不同，使用的是sdkExecute
        AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
        //就是orderString 可以直接给客户端请求，无需再做处理。
        log.info(response.getBody());
        return response;
    }

}
