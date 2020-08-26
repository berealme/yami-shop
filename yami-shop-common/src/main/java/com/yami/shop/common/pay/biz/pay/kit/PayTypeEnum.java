package com.yami.shop.common.pay.biz.pay.kit;

/**
 * 支付类型枚举
 *
 * @author xhq
 * @version 1.0
 * @date 2019/8/30 10:41
 */
public enum PayTypeEnum {

    /** 支付方式 支付宝 0 */
    PAY_TYPE_ALIPAY(0 ,"支付宝"),
    /** 支付方式 微信 1 */
    PAY_TYPE_WEIXIN(1, "微信"),
    /** 支付方式 银联 2 */
    PAY_TYPE_ACPPAY(2, "银联");

    /** 成员变量 */
    private int index;
    private String title;

    /** 构造方法 */
    PayTypeEnum(int index, String title) {
        this.index = index;
        this.title = title;
    }

    /** get set 方法  */
    public int getIndex() {
        return index;
    }

    /** 普通方法 - 获取通知标题 */
    public static String getTitle(int index) {
        for (PayTypeEnum m : PayTypeEnum.values()) {
            if (m.getIndex() == index) {
                return m.title;
            }
        }
        return null;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
