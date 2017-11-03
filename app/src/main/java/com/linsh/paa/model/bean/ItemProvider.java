package com.linsh.paa.model.bean;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/27
 *    desc   :
 * </pre>
 */
public interface ItemProvider {

    boolean isSuccess();

    String getMessage();

    String getId();

    String getItemTitle();

    String getItemImage();

    String getItemPrice();

    String getShopName();
}
