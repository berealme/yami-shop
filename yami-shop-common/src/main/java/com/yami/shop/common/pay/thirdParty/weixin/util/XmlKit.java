package com.yami.shop.common.pay.thirdParty.weixin.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * XmlKit
 *
 * @author xhq
 * @version 1.0
 * @date 2019/9/6 16:59
 */
public class XmlKit {


    /**
     * 将map转为xml-string
     *
     * @param map
     * @return
     */
    public static String mapToXml(Map map) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("<xml>");
            Set<String> keys = map.keySet();
            for (String key : keys) {
                sb.append("<"+key+">"+String.valueOf(map.get(key))+"</"+key+">");
            }
            sb.append("</xml>");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 将xml-string转为map
     *
     * @param xml
     * @return
     */
    public static Map<String, String> xmlToMap(String xml) {
        try {
            Map<String, String> maps = new HashMap<>();
            Document document = DocumentHelper.parseText(xml);
            Element root = document.getRootElement();
            List<Element> eles = root.elements();
            for (Element e : eles) {
                maps.put(e.getName(), e.getTextTrim());
            }
            return maps;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }
}
