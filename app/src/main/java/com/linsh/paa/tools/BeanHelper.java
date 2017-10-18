package com.linsh.paa.tools;

import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.bean.json.TaobaoDetail;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   :
 * </pre>
 */
public class BeanHelper {

    /**
     * @param text 从文本中获取 ItemId, 包括 Id \ URL \ 淘口令
     * @return 返回数字格式为 ItemId; 返回 http 格式为 URL
     */
    public static String getItemId(String text) {
        String itemId = null;
        if (LshStringUtils.isEmpty(text)) {
        } else if (text.matches("\\d{8,}")) { // ItemId
            itemId = text;
        } else if (text.matches("https?://.+/(item|detail)\\.htm\\?(.+&)?id=\\d+.*")) { // 宝贝地址
            itemId = text.replaceAll(".+[?&]id=(\\d+).*", "$1");
            itemId = itemId.matches("\\d+") ? itemId : null;
        } else if (text.trim().matches(".+https?://v\\.cvz5\\.com/.+￥.+￥.+")) { // 淘口令
            return text.replaceAll(".+(https?://v\\.cvz5\\.com/[.a-zA-Z0-9]+).+￥.+￥.+", "$1");
        }
        return itemId;
    }

    public static String getItemIdFromTKL(String html) {
        Matcher matcher = Pattern.compile("var.?url.?=.{1,3}https?://[^/]+/i(\\d+).+").matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
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
                        && (!LshStringUtils.isEquals(item.getPrice(), detailPrice)
                        || (item.getDisplay() != null && item.getDisplay().contains("#4")))) {
                    updateItemDisplay(itemCopy, detailPrice);
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

    public static void updateItemDisplay(Item item) {
        String display = null;
        int[] price = TaobaoDataParser.parsePrice(item.getPrice());
        if (item.getNotifiedPrice() > 0) {
            display = "#1比通知价格" + getPrice(item.getNotifiedPrice(), price[0], "高", "低");
        } else if (price[0] < item.getNormalPrice()) {
            display = "#2比正常价格" + getPrice(item.getNormalPrice(), price[0], "高", "低");
        }
        String oldDisplay = item.getDisplay();
        if (oldDisplay != null && (oldDisplay.contains("#3") || oldDisplay.contains("#4"))) {
            if (oldDisplay.startsWith("#1") || oldDisplay.startsWith("#2")) {
                display = display + oldDisplay.substring(oldDisplay.indexOf("#", 3), oldDisplay.length());
            } else {
                display = display + oldDisplay;
            }
        }
        item.setDisplay(display);
    }

    private static void updateItemDisplay(Item item, String newPrice) {
        String display = "";
        int[] newPrices = TaobaoDataParser.parsePrice(newPrice);
        if (item.getNotifiedPrice() > 0) {
            display = "#1比通知价格" + getPrice(item.getNotifiedPrice(), newPrices[0], "高", "低");
        } else if (newPrices[0] < item.getNormalPrice()) {
            display = "#2比正常价格" + getPrice(item.getNormalPrice(), newPrices[0], "高", "低");
        }
        if (display.length() == 0 && item.getNormalPrice() == 0 && newPrices[0] < item.getInitialPrice()) {
            display = "#3比收藏时" + getPrice(item.getInitialPrice(), newPrices[0], "上升", "下降");
        } else if (!item.getPrice().equals(newPrice)) {
            int[] itemPrice = TaobaoDataParser.parsePrice(item.getPrice());
            display += "#4比上一次" + getPrice(itemPrice[0], newPrices[0], "上升", "下降");
        }
        item.setDisplay(display);
    }

    private static String getPrice(int targetPrice, int newPrice, String ascendedStr, String descendedStr) {
        int price = newPrice - targetPrice;
        String diffStr = price > 0 ? ascendedStr : descendedStr;
        return String.format(Locale.CHINA, "%s%.2f元", diffStr, Math.abs(price * 1F / 100));
    }

    public static String getPriceStr(int price) {
        String format;
        if (price % 100 == 0) {
            format = "%.0f";
        } else if (price % 10 == 0) {
            format = "%.1f";
        } else {
            format = "%.2f";
        }
        return String.format(Locale.CHINA, format, Math.abs(price * 1F / 100));
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
