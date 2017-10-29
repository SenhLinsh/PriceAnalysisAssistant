package com.linsh.paa.tools;

import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.paa.model.bean.JingdongDetail;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/27
 *    desc   :
 * </pre>
 */
public class JingdongDataParser {

    public static JingdongDetail parseItemDetailHtml(String id, String html) {
        int start, end = 0;
        String title = null, firstPic = null, itemPrice = null, shopName = null, startKey;
        // 标题
        start = html.indexOf("\"title-text\"");
        if (start > 0) {
            start = html.indexOf(startKey = ">", start) + startKey.length();
            end = html.indexOf("</span>", start);
            title = html.substring(start, end);
        }
        // 价格
        start = html.indexOf("\"big-price\"", end);
        if (start > 0) {
            start = html.indexOf(startKey = ">", start) + startKey.length();
            end = html.indexOf("</span>", start);
            String bigPrice = html.substring(start, end);
            start = html.indexOf("\"small-price\"", end);
            start = html.indexOf(startKey = ">", start) + startKey.length();
            end = html.indexOf("</span>", start);
            String smallPrice = html.substring(start, end);
            itemPrice = bigPrice + smallPrice;
        }
        // 图片
        start = html.indexOf("\"spec-first-pic\"", end);
        if (start > 0) {
            start = html.indexOf(startKey = "src=\"", start) + startKey.length();
            end = html.indexOf("\"", start);
            firstPic = html.substring(start, end);
        }
        // 店铺名
        start = html.indexOf("\"shop\":", end);
        if (start > 0) {
            start = html.indexOf(startKey = "\"name\":\"", start) + startKey.length();
            end = html.indexOf("\"", start);
            shopName = html.substring(start, end);
        }
        if (LshStringUtils.isAllNotEmpty(title, firstPic, itemPrice, shopName)) {
            return new JingdongDetail(id, title, firstPic, itemPrice, shopName);
        }
        return new JingdongDetail();
    }
}
