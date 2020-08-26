package com.yami.shop.common.pay.thirdParty.acppay.kit;

import com.mowanka.common.constant.Constants;
import com.mowanka.common.support.BasicConfiguration;
import com.mowanka.project.thirdParty.acppay.sdk.AcpService;
import com.mowanka.project.thirdParty.acppay.sdk.SDKConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 银联支付回调工具类
 *
 * @author xhq
 * @version 1.0
 * @date 2019/8/12 10:29
 */
public class AcpNotifyKit {

    /**
     * 获取请求参数中所有的信息
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
        Map<String, String> res = new HashMap<>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                //在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                if (null == res.get(en) || "".equals(res.get(en))) {
                    res.remove(en);
                }
            }
        }
        return res;
    }

    /**
     * 发起查询接口查询交易结果
     * @return true:交易成功 false:交易失败
     */
    public static boolean queryAcpResult(String orderId, String txnTime) {
        //默认交易失败
        boolean flag = false;
        Map<String, String> data = new HashMap<>(10);
        /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        data.put("version", SDKConfig.getConfig().getVersion());
        data.put("encoding", Constants.UTF8);
        data.put("signMethod", SDKConfig.getConfig().getSignMethod());
        data.put("txnType", "00");
        data.put("txnSubType", "00");
        data.put("bizType", "000201");
        /***商户接入参数***/
        data.put("merId", "acppay_merId***********");
        data.put("accessType", "0");
        /***要调通交易以下字段必须修改***/
        //商户订单号，每次发交易测试需修改为被查询的交易的订单号
        data.put("orderId", orderId);
        //订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间
        data.put("txnTime", txnTime);

        /**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
        Map<String, String> reqData = AcpService.sign(data, Constants.UTF8);
        Map<String, String> rspData = AcpService.post(reqData, SDKConfig.getConfig().getSingleQueryUrl(), Constants.UTF8);

        //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
        try{
            if (!rspData.isEmpty()) {
                if (AcpService.validate(rspData, Constants.UTF8)) {
                    if (("00").equals(rspData.get("respCode"))) {
                        String origRespCode = rspData.get("origRespCode");
                        if (("00").equals(origRespCode)) {
                            /**交易成功!!!**/
                            flag = true;
                        } else if (("03").equals(origRespCode) ||
                                ("04").equals(origRespCode) ||
                                ("05").equals(origRespCode)) {
                            //订单处理中或交易状态未明，需稍后发起交易状态查询交易 【如果最终尚未确定交易是否成功请以对账文件为准】
                            //TODO
                        } else {
                            //其他应答码为交易失败
                            //TODO
                        }
                    } else if (("34").equals(rspData.get("respCode"))) {
                        //订单不存在，可认为交易状态未明，需要稍后发起交易状态查询，或依据对账结果为准
                    } else {//查询交易本身失败，如应答码10/11检查查询报文是否正确
                        //TODO
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
