package com.linsh.paa.model.bean;

import com.linsh.lshutils.utils.Basic.LshStringUtils;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/27
 *    desc   :
 * </pre>
 */
public class JingdongDetail implements ItemProvider {

    private boolean success;
    private String id;
    private String itemTitle;
    private String itemImage;
    private String itemPrice;
    private String shopName;

    public JingdongDetail() {
    }

    public JingdongDetail(String id, String itemTitle, String itemImage, String itemPrice, String shopName) {
        this.id = id;
        this.itemTitle = itemTitle;
        this.itemImage = itemImage;
        this.itemPrice = itemPrice;
        this.shopName = shopName;
        if (LshStringUtils.isAllNotEmpty(id, itemTitle, itemImage, itemPrice, shopName)) {
            success = true;
        }
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getItemTitle() {
        return itemTitle;
    }

    @Override
    public String getItemImage() {
        return itemImage;
    }

    @Override
    public String getItemPrice() {
        return itemPrice;
    }

    @Override
    public String getShopName() {
        return shopName;
    }
}
