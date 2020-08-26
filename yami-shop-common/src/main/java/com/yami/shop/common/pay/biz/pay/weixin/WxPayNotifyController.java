package com.yami.shop.common.pay.biz.pay.weixin;

import com.mowanka.common.utils.Log;
import com.mowanka.project.biz.order.order.service.IOrderService;
import com.mowanka.project.biz.order.pay.kit.PayTypeEnum;
import com.mowanka.project.thirdParty.weixin.kit.WxPayNotifyKit;
import com.mowanka.project.thirdParty.weixin.util.PayUtil;
import com.mowanka.project.thirdParty.weixin.util.WXConfigure;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 微信支付回调controller
 *
 * @author xhq
 * @version 1.0
 * @date 2019/8/2 15:25
 */
@Controller
@RequestMapping("/wxpayNotify")
public class WxPayNotifyController {

    private static Logger log = Log.get();

    @Autowired
    private IOrderService orderService;

    /**
     * 微信支付成功后,回调该接口
     */
    @PostMapping("/payNotify")
    @ResponseBody
    public void payNotify(HttpServletRequest request, HttpServletResponse response)throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line ;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine()) != null){
            sb.append(line);
        }
        br.close();
        //sb为微信返回的xml
        String notityXml = sb.toString();
        String resXml = "";
        log.info("进入微信支付回调,接收到的报文：{}", notityXml);

        Map<String, String> map = PayUtil.doXMLParse(notityXml);

        String returnCode = map.get("return_code");
        if(WXConfigure.TRADE_SUCCESS.equals(returnCode)){
            //验证签名是否正确
            if (WxPayNotifyKit.checkSign(map)) {
                log.info("订单{}微信回调签名认证成功", map.get("out_trade_no"));
                /*此处业务逻辑代码start*/
                /*此处业务逻辑代码end*/
                //通知微信服务器已经支付成功
                resXml = WxPayNotifyKit.returnXML(WXConfigure.TRADE_SUCCESS);
            } else {
                log.error("微信支付回调失败!签名不一致");
            }
        }else{
            resXml = WxPayNotifyKit.returnXML(WXConfigure.TRADE_FAIL);
        }
        log.info("回复微信报文", resXml);
        log.info("微信支付回调数据结束");

        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        out.write(resXml.getBytes());
        out.flush();
        out.close();
    }

}
