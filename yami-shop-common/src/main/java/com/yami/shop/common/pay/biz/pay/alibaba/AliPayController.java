package com.yami.shop.common.pay.biz.pay.alibaba;

import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.mowanka.common.sameurl.SameUrlData;
import com.mowanka.common.utils.Log;
import com.mowanka.framework.index.TL;
import com.mowanka.framework.web.controller.BaseController;
import com.mowanka.framework.web.result.GenericResult;
import com.mowanka.framework.web.result.StateCode;
import com.mowanka.project.biz.order.order.service.IOrderService;
import com.mowanka.project.biz.user.userToken.kit.UserTokenKit;
import com.mowanka.project.thirdParty.alibaba.kit.AliPayKit;
import com.mowanka.project.thirdParty.common.OrderPayDTO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 支付宝支付
 * @author xhq
 * @version 1.0
 * @date 2019/5/22
 */
@Controller
@RequestMapping("/alipay")
public class AliPayController extends BaseController {

    private static Logger log = Log.get();

    @Autowired
    private IOrderService orderService;

    /**
     * 根据订单详情获取支付宝支付签名
     * @param orderId 订单Id
     * @return
     */
    @SameUrlData
    @PostMapping("/getAlipaySign")
    @ResponseBody
    public String getAlipaySign(@RequestParam(value = "orderId")Long orderId){
        log.info("获取支付宝签名");
        GenericResult gr ;
        try {
            //验证token
            if(!UserTokenKit.verifyToken(TL.getUserId(), TL.getToken())) {
                return getWrite(new GenericResult(StateCode.INVALID_TOKEN, messagesMap.get("token_error")));
            }
            //调用支付之前先执行业务逻辑和封装支付所需参数
            OrderPayDTO payDTO = orderService.beforeGetPaySign(orderId);
            //验证不通过
            if (!payDTO.getCheck()) {
                return getWrite(new GenericResult(StateCode.ERROR_VERIFY, messagesMap.get(payDTO.getErrorMessage())));
            }
            //验证通过-调用支付宝SDK
            AlipayTradeAppPayResponse response = AliPayKit.callAlipay(payDTO);
            gr = new GenericResult(StateCode.SUCCESS, response.getBody(), messagesMap.get("success"));
        } catch (Exception e) {
            e.printStackTrace();
            gr = new GenericResult(StateCode.ERROR_SERVER, messagesMap.get("network_error"));
        }
        return getWrite(gr);
    }

    /**
     * 补款订单合并支付 获取支付宝支付签名
     * @param orderIds 订单id数组,逗号隔开
     * @return
     */
    @SameUrlData
    @PostMapping("/getMergeAlipaySign")
    @ResponseBody
    public String getMergeAlipaySign(@RequestParam(value = "orderIds")String orderIds){
        log.info("获取支付宝补款订单合并支付签名,orderIds={}", orderIds);
        GenericResult gr ;
        try {
            //验证token
            if(!UserTokenKit.verifyToken(TL.getUserId(), TL.getToken())) {
                return getWrite(new GenericResult(StateCode.INVALID_TOKEN, messagesMap.get("token_error")));
            }
            //调用支付之前先执行业务逻辑和封装支付所需参数
            OrderPayDTO payDTO = orderService.beforeGetMergePaySign(orderIds);
            //验证不通过
            if (!payDTO.getCheck()) {
                return getWrite(new GenericResult(StateCode.ERROR_VERIFY, messagesMap.get(payDTO.getErrorMessage())));
            }
            //验证通过-调用支付宝SDK
            AlipayTradeAppPayResponse response = AliPayKit.callAlipay(payDTO);
            gr = new GenericResult(StateCode.SUCCESS, response.getBody(), messagesMap.get("success"));
        } catch (Exception e) {
            e.printStackTrace();
            gr = new GenericResult(StateCode.ERROR_SERVER, messagesMap.get("network_error"));
        }
        return getWrite(gr);
    }

}
