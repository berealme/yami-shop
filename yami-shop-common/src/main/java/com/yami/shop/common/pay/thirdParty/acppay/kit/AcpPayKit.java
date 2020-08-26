package com.yami.shop.common.pay.thirdParty.acppay.kit;

import com.google.common.collect.Maps;
import com.mowanka.common.constant.Constants;
import com.mowanka.common.support.BasicConfiguration;
import com.mowanka.common.utils.DateUtil;
import com.mowanka.project.thirdParty.acppay.sdk.AcpService;
import com.mowanka.project.thirdParty.acppay.sdk.LogUtil;
import com.mowanka.project.thirdParty.acppay.sdk.SDKConfig;
import com.mowanka.project.thirdParty.common.OrderPayDTO;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 银联支付工具类
 *
 * @author xhq
 * @version 1.0
 * @date 2019/8/12 10:26
 */
public class AcpPayKit {

    /**
     * 调用银联SDK获取tn号
     * 参数 orderCode 订单编号
     *      pName 商品名称
     *      orderMoney 支付金额
     */
    public static String callAcppay(OrderPayDTO payDTO) {
        Map<String,String> requestData = Maps.newHashMap();
        /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        //版本号，全渠道默认值
        requestData.put("version", SDKConfig.getConfig().getVersion());
        //字符集编码，可以使用UTF-8,GBK两种方式
        requestData.put("encoding", Constants.UTF8);
        //签名方法
        requestData.put("signMethod", SDKConfig.getConfig().getSignMethod());
        //交易类型 ，01：消费
        requestData.put("txnType", "01");
        //交易子类型， 01：自助消费
        requestData.put("txnSubType", "01");
        //业务类型 000202: B2B 000201: B2C
        requestData.put("bizType", "000201");
        //渠道类型 05：语音07：互联网08：移动 16：数字机顶盒
        requestData.put("channelType", "08");
        /***商户接入参数***/
        //商户号码，请改成自己申请的正式商户号或者open上注册得来的777测试商户号
        requestData.put("merId", BasicConfiguration.getInstance().getValue("acppay_merId"));
        //接入类型，0：直连商户
        requestData.put("accessType", "0");
        requestData.put("orderId",payDTO.getOrderCode());
        //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        //订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        requestData.put("txnTime", DateUtil.dateToStr(DateUtil.YYYYMMDDHHMMSS, DateUtil.getNowDate()));
        //账号类型 01：银行卡02：存折03：IC卡帐号类型(卡介质)
        requestData.put("accType", "01");
        //交易金额，单位分，不要带小数点
        //前台传入金额保留两位小数 再 乘以100
        BigDecimal bd = payDTO.getOrderMoney();
        int lastMoney = bd.setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).intValue();
        requestData.put("txnAmt", String.valueOf(lastMoney));
        //交易币种（境内商户一般是156 人民币）
        requestData.put("currencyCode", "156");
        //异步回调地址
        requestData.put("backUrl", SDKConfig.getConfig().getBackUrl());

        String tnNumber = "";
        /**对请求参数进行签名并发送http post请求，接收同步应答报文**/
        //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        Map<String, String> reqData = AcpService.sign(requestData, Constants.UTF8);
        //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒; 这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
        Map<String, String> rspData = AcpService.post(reqData, SDKConfig.getConfig().getAppRequestUrl(), Constants.UTF8);
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, Constants.UTF8)){
                String respCode = rspData.get("respCode");
                if(("00").equals(respCode)){
                    //成功,获取tn号
                    tnNumber = rspData.get("tn");
                    LogUtil.writeLog("获取银联tn号成功,tn=" + tnNumber);
                }
            }
        }
        if (StringUtils.isBlank(tnNumber)) {
            LogUtil.writeErrorLog("验证签名失败,获取银联tn号失败");
        }
        return tnNumber;
    }
}
