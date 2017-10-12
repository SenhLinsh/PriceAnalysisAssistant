package com.linsh.paa.tools;

import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.bean.json.TaobaoDetail;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   :
 * </pre>
 */
public class BeanHelper {

    public static String checkItem(String text) {
        if (LshStringUtils.isEmpty(text)) {
            return null;
        } else if (text.matches("\\d{8,}")) {
            return text;
        } else if (text.matches("https?://.+/(item|detail)\\.htm\\?(.+&)?id=\\d+.*")) {
            String itemId = text.replaceAll(".+[?&]id=(\\d+).*", "$1");
            return itemId.matches("\\d+") ? itemId : null;
        }
        return null;
    }

    /**
     * 获取需要存储的 Item 和 ItemHistory
     *
     * @param item 作为对比的 Item, 如果数据库中没有则传入 null
     * @return Object[0] 为需要更新的 Item, 为 null 时表示不需要更新; Object[1] 为需要更新的 ItemHistory, 为 null 时表示数据解析失败
     */
    public static Object[] getItemAndHistoryToSave(Item item, TaobaoDetail detail) {
        if (detail.isSuccess()) {
            ItemHistory history = null;
            Item itemCopy = null;
            if (item == null) {
                history = new ItemHistory(detail.getItemId());
                itemCopy = new Item(detail.getItemId());
                itemCopy.setPrice(detail.getItemPrice());
                history.setPrice(detail.getItemPrice());
                itemCopy.setTitle(detail.getItemTitle());
                history.setTitle(detail.getItemTitle());
                itemCopy.setImage(detail.getItemImage());
                itemCopy.setShopName(detail.getShopName());
            } else if (item.getId().equals(detail.getItemId())) {
                history = new ItemHistory(item.getId());
                history.setPrice(detail.getItemPrice());
                if (LshStringUtils.notEmpty(detail.getItemPrice())
                        && !LshStringUtils.isEquals(item.getPrice(), detail.getItemPrice())) {
                    itemCopy = item.getCopy();
                    itemCopy.setPrice(detail.getItemPrice());
                }
                if (LshStringUtils.notEmpty(detail.getItemTitle())
                        && !LshStringUtils.isEquals(item.getTitle(), detail.getItemTitle())) {
                    if (itemCopy == null) itemCopy = item.getCopy();
                    itemCopy.setTitle(detail.getItemTitle());
                    history.setTitle(detail.getItemTitle());
                }
                if (LshStringUtils.notEmpty(detail.getItemImage())
                        && !LshStringUtils.isEquals(item.getImage(), detail.getItemImage())) {
                    if (itemCopy == null) itemCopy = item.getCopy();
                    itemCopy.setImage(detail.getItemImage());
                }
                if (LshStringUtils.notEmpty(detail.getShopName())
                        && !LshStringUtils.isEquals(item.getShopName(), detail.getShopName())) {
                    if (itemCopy == null) itemCopy = item.getCopy();
                    itemCopy.setShopName(detail.getShopName());
                }
            }
            return new Object[]{check(itemCopy), history};
        }
        return new Object[2];
    }

    private static Item check(Item item) {
        if (item == null || LshStringUtils.isEmpty(item.getId())
                || LshStringUtils.isEmpty(item.getTitle()) || LshStringUtils.isEmpty(item.getPrice()))
            return null;
        return item;
    }

    public static boolean isSame(ItemHistory history1, ItemHistory history2) {
        if (history1 != null && history2 != null) {
            return LshStringUtils.isEquals(history1.getId(), history2.getId())
                    && LshStringUtils.isEquals(history1.getTitle(), history2.getTitle())
                    && LshStringUtils.isEquals(history1.getPrice(), history2.getPrice())
                    && history1.isBuyDisable() == history2.isBuyDisable();
        }
        return false;
    }
}
