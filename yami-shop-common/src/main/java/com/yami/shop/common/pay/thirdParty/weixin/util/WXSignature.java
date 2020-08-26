package com.yami.shop.common.pay.thirdParty.weixin.util;

import com.mowanka.common.utils.Log;
import com.mowanka.common.utils.MD5Kit;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.*;


/**
 * 签名
 * @author xhq
 *
 * 第一步，设所有发送或者接收到的数据为集合M，将集合M内非空参数值的参数按照参数名ASCII码从小到大排序（字典序），使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串stringA。
 *
 * 特别注意以下重要规则：
 *
 * ◆ 参数名ASCII码从小到大排序（字典序）；
 * ◆ 如果参数的值为空不参与签名；
 * ◆ 参数名区分大小写；
 * ◆ 验证调用返回或微信主动通知签名时，传送的sign参数不参与签名，将生成的签名与该sign值作校验。
 * ◆ 微信接口可能增加字段，验证签名时必须支持增加的扩展字段
 * 第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。
 *
 */
public class WXSignature {

    private static Logger log = Log.get();

	/**
     * 签名算法
     * @param o 要参与签名的数据对象
     * @return 签名
     * @throws IllegalAccessException
     */
    public static String getSign(Object o) throws IllegalAccessException {
        ArrayList<String> list = new ArrayList<>();
        Class cls = o.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.get(o) != null && f.get(o) != "") {
            	String name = f.getName();
            	XStreamAlias anno = f.getAnnotation(XStreamAlias.class);
            	if(anno != null){
                    name = anno.value();
                }
                list.add(name + "=" + f.get(o) + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + WXConfigure.WX_PAY_KEY;
        log.info("签名数据：{}", result);
        result = MD5Kit.MD5Encode(result).toUpperCase();
        return result;
    }

    /**
     * Map 签名算法
     *
     *
     * @param map
     * @return
     */
    public static String getMapSign(Map<String, String> map) {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() != "") {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + WXConfigure.WX_PAY_KEY;
        log.info("签名数据：{}", result);
        result = MD5Kit.MD5Encode(result).toUpperCase();
        return result;
    }

}
