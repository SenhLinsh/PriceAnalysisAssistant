package com.linsh.paa.model.bean.db;

import com.linsh.lshutils.utils.Basic.LshLogUtils;
import com.linsh.lshutils.utils.LshTimeUtils;
import com.linsh.paa.tools.BeanHelper;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   : 淘宝宝贝信息表格
 * </pre>
 */
public class Item extends RealmObject implements Sortable {

    @PrimaryKey
    private String id;
    private String title;
    private String image;
    private String price;
    private String shopName;
    private String tag;
    private String display;
    private int sort;
    private long lastModified;
    /**
     * 是否已失效
     */
    private boolean cartDisable;
    /**
     * 添加时的价格
     */
    private int initialPrice;
    /**
     * 正常价格(手动设置)
     */
    private int normalPrice;
    /**
     * 允许发送提醒通知的价格(手动设置)
     */
    private int notifiedPrice;

    @Deprecated
    public Item() {
    }


    public Item(String id) {
        this(id, null, null, null, null);
    }

    public Item(Platform platform, String itemId) {
        this(platform, itemId, null, null, null, null);
    }

    public Item(Platform platform, String itemId, String title, String image, String price, String shopName) {
        this.title = title;
        this.image = image;
        this.price = price;
        this.shopName = shopName;
        setId(platform, itemId);
        refreshLastModified();
    }

    public Item(String id, String title, String image, String price, String shopName) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.price = price;
        this.shopName = shopName;
        refreshLastModified();
    }

    public String getItemId() {
        return BeanHelper.getItemId(id);
    }

    public String getId() {
        return id;
    }

    public void setId(Platform platform, String itemId) {
        this.id = BeanHelper.getId(platform, itemId);
    }

    public void setId(String id) {
        this.id = id;
    }

    public Platform getPlatform() {
        return BeanHelper.getPlatform(id);
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

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public void refreshLastModified() {
        this.lastModified = System.currentTimeMillis();
    }

    /**
     * @return 不超过半小时, 不应该刷新 Item (不用尝试去请求网络了)
     */
    public boolean shouldUpdateItem() {
        if (lastModified == 0) {
            LshLogUtils.w("Item -> lastModified 为 0");
        } else {
            LshLogUtils.i("距离上次更新", LshTimeUtils.date2StringCN(new Date(lastModified), false));
        }
        return System.currentTimeMillis() - lastModified > 1000L * 60 * 30;
    }

    /**
     * @return 超过 12 小时应该刷新 ItemHistory (即使没有变化)
     */
    public boolean shouldUpdateHistory() {
        return System.currentTimeMillis() - lastModified > 1000L * 60 * 60 * 12;
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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public int getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(int initialPrice) {
        this.initialPrice = initialPrice;
    }
}
