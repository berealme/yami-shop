package com.yami.shop.common.pay.thirdParty.alibaba.kit;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.mowanka.common.utils.Log;
import com.mowanka.project.thirdParty.alibaba.config.AlipayConfig;
import com.mowanka.project.thirdParty.common.OrderPayDTO;
import org.slf4j.Logger;

import java.math.BigDecimal;

/**
 * 支付宝单笔转账到支付宝账户工具类
 *
 * @author xhq
 * @version 1.0
 * @date 2019/9/18 9:55
 */
public class AliTransferKit {

    private static Logger log = Log.get();

    public static AlipayFundTransToaccountTransferResponse callAliTransfer(OrderPayDTO payDTO) throws AlipayApiException {
        log.info("发生支付宝单笔转账到支付宝账户,开始执行");
        //初始化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL,
                AlipayConfig.APP_ID,
                AlipayConfig.APP_PRIVATE_KEY,
                AlipayConfig.FORMAT,
                AlipayConfig.CHARSET,
                AlipayConfig.ALIPAY_PUBLIC_KEY,
                AlipayConfig.SIGN_TYPE);
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.fund.trans.toaccount.transfer
        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayFundTransToaccountTransferModel model = new AlipayFundTransToaccountTransferModel();
        //商户转账唯一订单号
        model.setOutBizNo(payDTO.getOrderCode());
        //付款方姓名
        model.setPayerShowName("xxxx-"+payDTO.getPayerShowName());
        //收款方账户类型,可取值：
        //1、ALIPAY_USERID：支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成。
        //2、ALIPAY_LOGONID：支付宝登录号，支持邮箱和手机号格式。
        model.setPayeeType(AlipayConfig.TRANSFER_PAYEE_TYPE);
        //收款方账户,与payee_type配合使用,付款方和收款方不能是同一个账户。
        model.setPayeeAccount(payDTO.getAliUserId());
        //转账金额,单位:元。支持两位小数,金额必须大于等于0.1元,最大转账金额以实际签约的限额为准
        model.setAmount(payDTO.getOrderMoney().toString());
        //转账备注
        model.setRemark("订单"+payDTO.getOrderCode()+"收款");
        request.setBizModel(model);
        //这里和普通的接口调用不同，使用的是sdkExecute
        AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);
        log.info(response.getBody());
        if (response.isSuccess()) {
            log.info("订单：{}单笔转账【成功】,转账金额为{}元", payDTO.getOrderCode(), payDTO.getOrderMoney());
        } else {
            log.error("订单：{}单笔转账【失败】,转账金额为{}元", payDTO.getOrderCode(), payDTO.getOrderMoney());
        }
        return response;
    }

    public static void main(String[] args) {
        OrderPayDTO o = new OrderPayDTO();
        o.setPayerShowName("张三");
        o.setOrderCode("X1909171117038016");
        o.setAliUserId("2088112544233444");
        o.setOrderMoney(new BigDecimal(0.1).setScale(2, BigDecimal.ROUND_HALF_UP));
        try {
            callAliTransfer(o);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }
}
