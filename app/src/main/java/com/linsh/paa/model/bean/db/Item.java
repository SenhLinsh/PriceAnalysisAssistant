package com.linsh.paa.model.bean.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   : 淘宝宝贝信息表格
 * </pre>
 */
public class Item extends RealmObject {

    @PrimaryKey
    private String id;
    private String title;
    private String image;
    private String price;
    private String shopName;
    private long lastModified;
    /**
     * 是否已失效
     */
    private boolean cartDisable;
    /**
     * 设置的正常价格
     */
    private int normalPrice;
    /**
     * 允许发送提醒通知的价格
     */
    private int notifiedPrice;

    public Item() {
    }

    public Item(String id) {
        this(id, null, null, null, null);
    }

    public Item(String id, String title, String image, String price, String shopName) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.price = price;
        this.shopName = shopName;
        refreshLastModified();
    }

    public Item getCopy() {
        return new Item(id, title, image, price, shopName);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void refreshLastModified() {
        this.lastModified = System.currentTimeMillis();
    }

    public boolean isCartDisable() {
        return cartDisable;
    }

    public void setCartDisable(boolean cartDisable) {
        this.cartDisable = cartDisable;
    }

    public int getNormalPrice() {
        return normalPrice;
    }

    public void setNormalPrice(int normalPrice) {
        this.normalPrice = normalPrice;
    }

    public int getNotifiedPrice() {
        return notifiedPrice;
    }

    public void setNotifiedPrice(int notifiedPrice) {
        this.notifiedPrice = notifiedPrice;
    }
}
