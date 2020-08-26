package com.yami.shop.common.pay.biz.pay.acppay;;

import com.mowanka.common.utils.Log;
import com.mowanka.project.biz.order.order.service.IOrderService;
import com.mowanka.project.biz.order.pay.kit.PayTypeEnum;
import com.mowanka.project.thirdParty.acppay.kit.AcpNotifyKit;
import com.mowanka.project.thirdParty.acppay.sdk.AcpService;
import com.mowanka.project.thirdParty.acppay.sdk.LogUtil;
import com.mowanka.project.thirdParty.acppay.sdk.SDKConstants;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 银联支付回调controller
 * @author xhq
 * @version 1.0
 * @date 2019/8/05 14:25
 */
@Controller
@RequestMapping("/acppayNotify")
public class AcppayNotifyController {

    private static Logger log = Log.get();

    @Autowired
    private IOrderService orderService;

    /**
     * 支付宝支付成功后,回调该接口
     * @return
     */
    @PostMapping("/payNotify")
    @ResponseBody
    public String payNotify(HttpServletRequest request){
        String encoding = request.getParameter(SDKConstants.param_encoding);
        // // 获取银联通知服务器发送的后台通知参数
        Map<String, String> reqParam = AcpNotifyKit.getAllRequestParam(request);
        log.info("进入银联App支付异步回调方法,回调参数为：{}", reqParam.toString());
        //重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
        if (!AcpService.validate(reqParam, encoding)) {
            LogUtil.writeErrorLog("银联回调验证签名结果[失败].");
        } else {
            LogUtil.writeLog("银联回调验证签名结果[成功].");
            //商户订单号
            String orderId = reqParam.get("orderId");
            //订单发送时间
            String txnTime = reqParam.get("txnTime");
            //应答码
            String respCode = reqParam.get("respCode");
            //消费交易的流水号
            String queryId = reqParam.get("queryId");
            //交易金额
            String txnAmt = reqParam.get("txnAmt");
            //判断respCode=00、A6后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。
            if ("00".equals(respCode) || "A6".equals(respCode)) {
                boolean check = AcpNotifyKit.queryAcpResult(orderId, txnTime);
                if (check) {
                    LogUtil.writeLog("orderId="+orderId+",银联回调查询交易结果[成功].");
                    /*此处业务逻辑代码start*/
                    //交易金额除以100才是真实金额
                    
                    /*此处业务逻辑代码end*/
                    log.info("订单{}银联回调,执行业务逻辑成功,支付流程结束", orderId);
                } else {
                    LogUtil.writeErrorLog("orderId="+orderId+",银联回调查询交易结果[失败].");
                }
            }
        }
        LogUtil.writeLog("银联回调处理结束");
        //返回给银联服务器http 200  状态码
        return "ok";
    }

}
