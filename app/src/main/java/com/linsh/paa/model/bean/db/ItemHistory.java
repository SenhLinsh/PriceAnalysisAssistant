package com.linsh.paa.model.bean.db;

import io.realm.RealmObject;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   : 宝贝历史 (价格 / 标题等)
 * </pre>
 */
public class ItemHistory extends RealmObject {

    private String id;
    private String price;
    private String title;
    private boolean buyDisable;
    private long timestamp;

    public ItemHistory() {
    }

    public ItemHistory(String id) {
        this(id, null, null);
    }

    public ItemHistory(String id, String price, String title) {
        this(id, price, title, false);
    }

    public ItemHistory(String id, String price, String title, boolean buyDisable) {
        this.id = id;
        this.price = price;
        this.title = title;
        this.buyDisable = buyDisable;
        refreshTimestamp();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isBuyDisable() {
        return buyDisable;
    }

    public void setBuyDisable(boolean buyDisable) {
        this.buyDisable = buyDisable;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void refreshTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }
}
