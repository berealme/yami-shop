package com.yami.shop.common.pay.biz.pay.weixin;

import com.mowanka.common.sameurl.SameUrlData;
import com.mowanka.common.utils.IpUtils;
import com.mowanka.common.utils.Log;
import com.mowanka.framework.index.TL;
import com.mowanka.framework.web.controller.BaseController;
import com.mowanka.framework.web.result.GenericResult;
import com.mowanka.framework.web.result.StateCode;
import com.mowanka.project.biz.order.order.service.IOrderService;
import com.mowanka.project.biz.user.userToken.kit.UserTokenKit;
import com.mowanka.project.thirdParty.common.OrderPayDTO;
import com.mowanka.project.thirdParty.weixin.kit.WxPayKit;
import com.mowanka.project.thirdParty.weixin.util.WXConfigure;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 微信支付
 *
 * @author xhq
 * @version 1.0
 * @date 2019/7/31 15:02
 */
@Controller
@RequestMapping("/wxpay")
public class WxPayController extends BaseController {

    private static Logger log = LoggerFactory.getLogger(WxPayController.class);

    @Autowired
    private IOrderService orderService;

    /**
     * 根据订单详情获取微信支付签名
     * @param orderId 订单Id
     */
    @PostMapping("getWxpaySign")
    @ResponseBody
    public String getWxpaySign(@RequestParam(value = "orderId")Long orderId){
        log.info("获取微信支付签名");
        GenericResult gr ;
        try {
            //调用支付之前先执行业务逻辑和封装支付所需参数
            OrderPayDTO payDTO = new OrderPayDTO();
			payDTO.setOrderCode(orderCode);
			payDTO.setOrderMoney(orderMoney);
			payDTO.setpName(pName);
            payDTO.setIp(IpUtils.getIpAddr(getRequest()));
            //验证不通过
            if (!payDTO.getCheck()) {
                return getWrite(new GenericResult(StateCode.ERROR_VERIFY, messagesMap.get(payDTO.getErrorMessage())));
            }
            //验证通过-调起支付
            Map<String, String> resultJson = WxPayKit.callWxPay(payDTO);
            //判断是否已支付
            if (WXConfigure.TRADE_PAID.equals(resultJson.get("isPay"))) {
                return getWrite(new GenericResult(StateCode.ERROR_VERIFY, messagesMap.get("order_paid_error")));
            }
            gr = new GenericResult(StateCode.SUCCESS, resultJson, messagesMap.get("success"));
        }catch (Exception e) {
            e.printStackTrace();
            gr = new GenericResult(StateCode.ERROR, messagesMap.get("network_error"));
        }
        return getWrite(gr);
    }

}
