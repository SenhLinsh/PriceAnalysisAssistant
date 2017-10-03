package com.linsh.paa.model.bean.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   : 宝贝历史 (价格 / 标题等)
 * </pre>
 */
public class ItemHistory extends RealmObject {

    @PrimaryKey
    private String id;
    private String price;
    private String title;
    private String buyDisable;
    private long timestamp;

    public ItemHistory() {
    }

    public ItemHistory(String id) {
        this(id, null, null);
    }

    public ItemHistory(String id, String price, String title) {
        this(id, price, title, null);
    }

    public ItemHistory(String id, String price, String title, String buyDisable) {
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

    public String getBuyDisable() {
        return buyDisable;
    }

    public void setBuyDisable(String buyDisable) {
        this.buyDisable = buyDisable;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void refreshTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }
}
