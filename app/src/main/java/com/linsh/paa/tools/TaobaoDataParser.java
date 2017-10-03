package com.linsh.paa.tools;

import com.google.gson.Gson;
import com.linsh.paa.model.bean.json.TaobaoDetail;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   : 解析淘宝 API 返回结果
 * </pre>
 */
public class TaobaoDataParser {

    public static TaobaoDetail parseGetDetailData(String data) {
        data = checkGetDetailData(data);
        return new Gson().fromJson(data, TaobaoDetail.class);
    }

    /**
     * 1. 删除部分冗余信息 2. 格式化成正确的格式
     */
    private static String checkGetDetailData(String data) {
        data = deleteThisObj(data, "\"skuCore");
        data = deleteThisObj(data, "\"skuCore");
        data = deleteThisObj(data, "\"skuBase");
        data = data.replaceAll("\\\\\"\"|\"\\\\\"|\\\\\"", "\"")
                .replaceAll("\"\\{", "{")
                .replaceAll("\\}\"", "}");
        return data;
    }

    private static String deleteThisObj(String json, String obj) {
        int index = json.indexOf(obj);
        if (index > 0) {
            int firstIndex = index;
            for (int i = index - 1; i > 0; i--) {
                if (json.charAt(i) == '}') {
                    firstIndex = i + 1;
                    break;
                }
            }
            int deep = 0;
            for (int i = index + obj.length(); i < json.length(); i++) {
                if (json.charAt(i) == '{') {
                    deep--;
                } else if (json.charAt(i) == '}') {
                    deep++;
                    if (deep >= 0) {
                        json = json.substring(0, firstIndex) + json.substring(i + 1);
                        break;
                    }
                }
            }
        }
        return json;
    }

}
