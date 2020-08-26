package com.yami.shop.common.pay.biz.pay.acppay;

import com.mowanka.common.sameurl.SameUrlData;
import com.mowanka.common.utils.Log;
import com.mowanka.framework.index.TL;
import com.mowanka.framework.web.controller.BaseController;
import com.mowanka.framework.web.result.GenericResult;
import com.mowanka.framework.web.result.StateCode;
import com.mowanka.project.biz.order.order.service.IOrderService;
import com.mowanka.project.biz.user.userToken.kit.UserTokenKit;
import com.mowanka.project.thirdParty.acppay.kit.AcpPayKit;
import com.mowanka.project.thirdParty.common.OrderPayDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 银联支付
 * @author xhq
 * @version 1.0
 * @date 2019/8/05
 */
@Controller
@RequestMapping("/acp")
public class AcpPayController extends BaseController {

    private static Logger log = Log.get();

    @Autowired
    private IOrderService orderService;

    /**
     * 根据订单详情获取银联支付签名
     * @param orderId 订单Id
     * @return tn号
     */
    @PostMapping("/getAcppaySign")
    @ResponseBody
    public String getAcppaySign(@RequestParam(value = "orderId")Long orderId){
        log.info("获取银联签名,orderId={}", orderId);
        GenericResult gr ;
        try {
            //验证token
            if(!UserTokenKit.verifyToken(TL.getUserId(), TL.getToken())) {
                return getWrite(new GenericResult(StateCode.INVALID_TOKEN, messagesMap.get("token_error")));
            }
            //调用支付之前先执行业务逻辑和封装支付所需参数
            OrderPayDTO payDTO = new OrderPayDTO();
			payDTO.setOrderCode(orderCode);
			payDTO.setOrderMoney(orderMoney);
			payDTO.setpName(pName);
            //验证不通过
            if (!payDTO.getCheck()) {
                return getWrite(new GenericResult(StateCode.ERROR_VERIFY, messagesMap.get(payDTO.getErrorMessage())));
            }
            //验证通过-调用银联SDK
            String response = AcpPayKit.callAcppay(payDTO);
            if (StringUtils.isNotBlank(response)) {
                gr = new GenericResult(StateCode.SUCCESS, response, messagesMap.get("success"));
            } else {
                gr = new GenericResult(StateCode.ERROR_SERVER, messagesMap.get("order_sign_error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            gr = new GenericResult(StateCode.ERROR_SERVER, messagesMap.get("network_error"));
        }
        return getWrite(gr);
    }

}
