package com.yami.shop.common.pay.thirdParty.common;

import com.mowanka.framework.web.domain.BaseCheck;

import java.math.BigDecimal;

/**
 * OrderPayDTO 调起支付所需参数
 *
 * @author xhq
 * @version 1.0
 * @date 2019/8/2 14:40
 */
public class OrderPayDTO extends BaseCheck {

    /** 订单编号 */
    private String orderCode;

    /** 退款编号 */
    private String refundCode;

    /** 商品名称 */
    private String pName;

    /** 支付金额 */
    private BigDecimal orderMoney;

    /** 请求支付的ip地址 */
    private String ip;

    /** 退款原因 */
    private String refundReason;

    /** 支付宝唯一用户号,单笔转账时使用 */
    private String aliUserId;

    /** 付款方姓名,单笔转账时使用 */
    private String payerShowName;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public BigDecimal getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(BigDecimal orderMoney) {
        this.orderMoney = orderMoney;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getRefundCode() {
        return refundCode;
    }

    public void setRefundCode(String refundCode) {
        this.refundCode = refundCode;
    }

    public String getAliUserId() {
        return aliUserId;
    }

    public void setAliUserId(String aliUserId) {
        this.aliUserId = aliUserId;
    }

    public String getPayerShowName() {
        return payerShowName;
    }

    public void setPayerShowName(String payerShowName) {
        this.payerShowName = payerShowName;
    }

}
