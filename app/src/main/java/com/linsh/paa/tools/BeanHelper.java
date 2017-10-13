package com.linsh.paa.tools;

import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.bean.json.TaobaoDetail;

import java.util.Locale;

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
     * @return Object[0] 为需要创建的 Item, 为 null 时表示数据解析失败;
     * <br/> Object[1] 为需要创建的 ItemHistory, 为 null 时表示数据解析失败.
     */
    public static Object[] getItemAndHistoryToSave(TaobaoDetail detail) {
        if (detail.isSuccess()) {
            ItemHistory history = new ItemHistory(detail.getItemId());
            Item itemNew = new Item(detail.getItemId());
            itemNew.setPrice(detail.getItemPrice());
            itemNew.setInitialPrice(TaobaoDataParser.parsePrice(detail.getItemPrice())[0]);
            history.setPrice(detail.getItemPrice());
            itemNew.setTitle(detail.getItemTitle());
            history.setTitle(detail.getItemTitle());
            itemNew.setImage(detail.getItemImage());
            itemNew.setShopName(detail.getShopName());
            return new Object[]{check(itemNew), check(history)};
        }
        return new Object[2];
    }

    /**
     * 获取需要存储的 Item 和 ItemHistory
     *
     * @param item 作为对比的 Item
     * @return Object[0] 为需要更新的 ItemCopy, 为 null 时表示不需要更新; Object[1] 为需要更新的 ItemHistory, 为 null 时表示数据解析失败
     */
    public static Object[] getItemAndHistoryToSave(Item item, Item itemCopy, TaobaoDetail detail) {
        if (detail.isSuccess()) {
            ItemHistory history = null;
            boolean needUpdate = false;
            if (item.getId().equals(detail.getItemId())) {
                history = new ItemHistory(item.getId());
                String detailPrice = detail.getItemPrice();
                history.setPrice(detailPrice);
                if (LshStringUtils.notEmpty(detailPrice)
                        && !LshStringUtils.isEquals(item.getPrice(), detailPrice)) {
                    analysisPrice(itemCopy, detailPrice);
                    itemCopy.setPrice(detailPrice);
                    needUpdate = true;
                }
                if (LshStringUtils.notEmpty(detail.getItemTitle())
                        && !LshStringUtils.isEquals(item.getTitle(), detail.getItemTitle())) {
                    itemCopy.setTitle(detail.getItemTitle());
                    history.setTitle(detail.getItemTitle());
                    needUpdate = true;
                }
                if (LshStringUtils.notEmpty(detail.getItemImage())
                        && !LshStringUtils.isEquals(item.getImage(), detail.getItemImage())) {
                    itemCopy.setImage(detail.getItemImage());
                    needUpdate = true;
                }
                if (LshStringUtils.notEmpty(detail.getShopName())
                        && !LshStringUtils.isEquals(item.getShopName(), detail.getShopName())) {
                    itemCopy.setShopName(detail.getShopName());
                    needUpdate = true;
                }
            }
            return new Object[]{needUpdate ? itemCopy : null, check(history)};
        }
        return new Object[2];
    }

    private static void analysisPrice(Item item, String price) {
        String display = "";
        int[] newPrice = TaobaoDataParser.parsePrice(price);
        if (item.getNotifiedPrice() > 0) {
            display = "#1比通知价格" + getPrice(item.getNotifiedPrice(), newPrice[0], "高", "低");
        } else if (newPrice[0] < item.getNormalPrice()) {
            display = "#2比正常价格" + getPrice(item.getNormalPrice(), newPrice[0], "高", "低");
        }
        if (display.length() == 0 && item.getNormalPrice() == 0 && newPrice[0] < item.getInitialPrice()) {
            display = "#3比收藏时" + getPrice(item.getInitialPrice(), newPrice[0], "上升", "下降");
        } else if (!item.getPrice().equals(price)) {
            int[] itemPrice = TaobaoDataParser.parsePrice(item.getPrice());
            display += "#4比上一次" + getPrice(itemPrice[0], newPrice[0], "上升", "下降");
        }
        item.setDisplay(display);
    }

    private static String getPrice(int targetPrice, int newPrice, String ascendedStr, String descendedStr) {
        int price = newPrice - targetPrice;
        String diffStr = price > 0 ? ascendedStr : descendedStr;
        return String.format(Locale.CHINA, "%s%.2f元", diffStr, Math.abs(price * 1F / 100));
    }

    private static Item check(Item item) {
        if (item == null || LshStringUtils.isEmpty(item.getId())
                || LshStringUtils.isEmpty(item.getTitle()) || LshStringUtils.isEmpty(item.getPrice()))
            return null;
        return item;
    }

    private static ItemHistory check(ItemHistory history) {
        if (history == null || LshStringUtils.isEmpty(history.getId())
                || LshStringUtils.isEmpty(history.getPrice()))
            return null;
        return history;
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
