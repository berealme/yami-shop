package com.yami.shop.common.pay.biz.pay.alibaba;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.google.common.collect.Maps;
import com.mowanka.common.utils.Log;
import com.mowanka.project.biz.order.order.entity.OrderEntity;
import com.mowanka.project.biz.order.order.service.IOrderService;
import com.mowanka.project.biz.order.pay.kit.PayTypeEnum;
import com.mowanka.project.thirdParty.alibaba.config.AlipayConfig;
import com.mowanka.project.thirdParty.alibaba.config.AlipayNotifyParam;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付宝支付回调controller
 * @author xhq
 * @version 1.0
 * @date 2019/6/28 14:25
 */
@Controller
@RequestMapping("/alipayNotify")
public class AlipayNotifyController {

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
        // 1.从支付宝回调的request域中取值
        Map<String, String[]> requestParams = request.getParameterMap();
        //将request中的参数转换成Map
        Map<String, String> params = convertRequestParamsToMap(requestParams);
        log.info("进入支付宝App支付异步回调方法,回调参数为：{}", params.toString());
        //将回调参数转换为java对象
        AlipayNotifyParam param = buildAlipayNotifyParam(params);
        try {
            //2.调用SDK验证签名(对支付宝返回的数据验证，确定是支付宝返回的)
            boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, AlipayConfig.SIGN_TYPE);
            //3.对验签进行处理
            if (signVerified) {
                //验签通过,对支付结果中的业务内容进行1\2\3\4二次校验
                this.check(param);
                log.info("订单{}支付宝回调签名认证成功", param.getOutTradeNo());
                if(param.getTradeStatus().equals(AlipayConfig.TRADE_SUCCESS)) {
                    //交易状态为成功: 修改订单表状态,支付成功, 新增支付流水记录
                    log.info("订单{}支付宝回调：交易状态成功,开始执行业务逻辑", param.getOutTradeNo());
                    try {
                        /*此处业务逻辑代码start*/
                        log.info("订单{}支付宝回调,执行业务逻辑成功,支付流程结束", param.getOutTradeNo());
                        /*此处业务逻辑代码end*/
                    } catch (Exception e) {
                        log.error("支付宝回调业务处理报错,params:" + params, e);
                    }
                    // 如果签名验证正确，立即返回success，后续业务另起线程单独处理
                    // 业务处理失败，可查看日志进行补偿，跟支付宝已经没多大关系。
                    return "success";
                }else{
                    log.error("订单{}没有处理支付宝回调业务，支付宝交易状态为:{}", param.getOutTradeNo(), param.getTradeStatus());
                    return "failure";
                }
            } else {
                log.error("支付宝回调签名认证失败,signVerified=false,订单号为{}", param.getOutTradeNo());
                return "failure";
            }
        } catch (AlipayApiException e) {
            log.error("支付宝回调签名认证失败,requestParams:{},errorMsg:{}", requestParams, e.getMessage());
            return "failure";
        }
    }

    /**
     * 将request中的参数转换成Map
     * @return
     */
    private Map<String, String> convertRequestParamsToMap(Map<String, String[]> requestParams) {
        Map<String, String> params = Maps.newHashMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }

    /**
     * 将支付宝返回结果对应的参数转换为java对象
     * @param params
     * @return
     */
    private AlipayNotifyParam buildAlipayNotifyParam(Map<String, String> params) {
        String json = JSON.toJSONString(params);
        return JSON.parseObject(json, AlipayNotifyParam.class);
    }

    /**
     * 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
     * 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
     * 3、校验通知中的seller_id（或者seller_email)是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email），
     * 4、验证app_id是否为该商户本身。上述1、2、3、4有任何一个验证不通过，则表明本次通知是异常通知，务必忽略。
     * 在上述验证通过后商户必须根据支付宝不同类型的业务通知，正确的进行不同的业务处理，并且过滤重复的通知结果数据。
     * 在支付宝的业务通知中，只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
     *
     * @param param 回调参数对象
     * @throws AlipayApiException
     */
    private void check(AlipayNotifyParam param) throws AlipayApiException {

        String outTradeNo = param.getOutTradeNo();

        // 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
        OrderEntity order = orderService.getOrderByCode(outTradeNo);
        if (order == null) {
            throw new AlipayApiException("out_trade_no错误");
        }

        // 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
//        long total_amount = new BigDecimal(params.get("total_amount")).multiply(new BigDecimal(100)).longValue();
//        if (total_amount != order.getPayPrice().longValue()) {
//            throw new AlipayApiException("total_amount与订单金额不符");
//        }

        // 3、校验通知中的seller_id（或者seller_email)是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email），
        // 第三步可根据实际情况省略

        // 4、验证app_id是否为该商户本身。
        if (!param.getAppId().equals(AlipayConfig.APP_ID)) {
            throw new AlipayApiException("app_id不一致");
        }
    }
}
