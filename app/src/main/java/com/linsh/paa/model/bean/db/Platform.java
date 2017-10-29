package com.linsh.paa.model.bean.db;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/27
 *    desc   : 商品的隶属平台, 如淘宝\京东等
 * </pre>
 */
public enum Platform {

    Taobao("淘宝", "TB"), Jingdong("京东", "JD"), Unknown("未知", "UK");

    private String name;
    private String code;

    Platform(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public static Platform getPlatform(String code) {
        switch (code) {
            case "TB":
                return Taobao;
            case "JD":
                return Jingdong;
            default:
                return Unknown;
        }
    }

    public static String getPlatformName(String code) {
        switch (code) {
            case "TB":
                return "淘宝";
            case "JD":
                return "京东";
            default:
                return "未知";
        }
    }
}
