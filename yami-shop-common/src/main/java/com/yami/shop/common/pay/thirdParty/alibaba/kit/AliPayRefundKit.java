package com.yami.shop.common.pay.thirdParty.alibaba.kit;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.mowanka.common.utils.Log;
import com.mowanka.project.thirdParty.alibaba.config.AlipayConfig;
import com.mowanka.project.thirdParty.common.OrderPayDTO;
import org.slf4j.Logger;

import java.math.BigDecimal;

/**
 * 支付宝退款工具类
 *
 * @author xhq
 * @version 1.0
 * @date 2019/9/4 11:34
 */
public class AliPayRefundKit {

    private static Logger log = Log.get();

    /**
     * 调用alipay SDK
     * 参数 orderCode 订单编号
     *      pName 商品名称
     *      orderMoney 支付金额
     */
    public static boolean callAliRefund(OrderPayDTO payDTO) {
        log.info("发生支付宝退款,开始执行");
        // 定义处理结果
        boolean result = false;
        try {
            //初始化客户端
            AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL,
                    AlipayConfig.APP_ID,
                    AlipayConfig.APP_PRIVATE_KEY,
                    AlipayConfig.FORMAT,
                    AlipayConfig.CHARSET,
                    AlipayConfig.ALIPAY_PUBLIC_KEY,
                    AlipayConfig.SIGN_TYPE);
            //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alibaba.trade.refund
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            //订单支付时传入的商户订单号,不能和 trade_no同时为空。
            model.setOutTradeNo(payDTO.getOrderCode());
            //需要退款的金额，该金额不能大于订单金额,单位为元，支持两位小数
            model.setRefundAmount(payDTO.getOrderMoney().toString());
            //退款的原因说明(可选)
            model.setRefundReason(payDTO.getRefundReason());
            request.setBizModel(model);
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            log.info(response.getBody());

            if (response.isSuccess()) {
                result = true;
                log.info("订单：{}退款【成功】,退款金额为{}元", payDTO.getOrderCode(), payDTO.getOrderMoney());
            } else {
                log.error("订单：{}退款【失败】,退款金额为{}元", payDTO.getOrderCode(), payDTO.getOrderMoney());
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        OrderPayDTO o = new OrderPayDTO();
        o.setOrderCode("X1909041409350910");
        o.setOrderMoney(new BigDecimal(0.01).setScale(2, BigDecimal.ROUND_HALF_UP));
        o.setRefundReason("退款");
        callAliRefund(o);
    }
}
