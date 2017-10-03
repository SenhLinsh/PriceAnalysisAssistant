package com.linsh.paa.task.network;

import com.linsh.lshutils.utils.Basic.LshStringUtils;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   : 集中所有 API 接口链接
 * </pre>
 */
public class Url {

    public static final String TAOBAO_DETAIL_HTML = "http://h5.m.taobao.com/awp/core/detail.htm?id={itemId}";

    public static final String TAOBAO_GET_DETAIL = "http://h5api.m.taobao.com/h5/mtop.taobao.detail.getdetail/6.0/?" +
            "data=%7B%22exParams%22%3A%22%7B%5C%22id%5C%22%3A%5C%22{itemId}%5C%22%7D%22%2C%22itemNumId%22%3A%22{itemId}%22%7D";

    public static String getTaobaoDetailUrl(String itemId) {
        return LshStringUtils.format(TAOBAO_GET_DETAIL, itemId, itemId);
    }

    public static String getTaobaoDetailHtmlUrl(String itemId) {
        return LshStringUtils.format(TAOBAO_DETAIL_HTML, itemId);
    }
}
