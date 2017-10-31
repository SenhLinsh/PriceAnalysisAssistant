package com.linsh.paa.tools;

import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.paa.model.bean.ItemProvider;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.bean.db.Platform;

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

    public static Platform getPlatform(String id) {
        return Platform.getPlatform(id.substring(0, 2));
    }

    public static String getId(Platform platform, String itemId) {
        return platform.getCode() + itemId;
    }

    public static String getItemId(String id) {
        return id.substring(2);
    }

    public static String getItemIdOrUrlFromText(String text) {
        String idFromUrl = getIdOrUrlFromText(text);
        if (idFromUrl != null && idFromUrl.matches("[A-Z]{2}\\d+")) {
            return getItemId(idFromUrl);
        }
        return idFromUrl;
    }

    /**
     * @param text 从文本中获取 id, 包括 URL \ 淘口令
     * @return 返回 Code + 数字 格式为 id; 返回 http 格式为 URL
     */
    public static String getIdOrUrlFromText(String text) {
        if (LshStringUtils.isEmpty(text)) return null;

        String itemId = null;
        if (text.matches("https?://.+/(item|detail)\\.htm\\?(.+&)?id=\\d+.*")) { // 淘宝宝贝地址
            itemId = text.replaceAll(".+[?&]id=(\\d+).*", "$1");
            itemId = itemId.matches("\\d+") ? Platform.Taobao.getCode() + itemId : null;
        } else if (text.matches("https?://item\\..*\\.jd\\.com.*/\\d+\\.html.*")) {
            itemId = text.replaceAll(".+/(\\d+)\\.html.*", "$1");
            itemId = itemId.matches("\\d+") ? Platform.Jingdong.getCode() + itemId : null;
        } else if (text.trim().matches(".+https?://v\\.cvz5\\.com/.+￥.+￥.+")) { // 淘口令
            return text.replaceAll(".+(https?://v\\.cvz5\\.com/[.a-zA-Z0-9]+).+￥.+￥.+", "$1");
        }
        return itemId;
    }

    /**
     * 从淘口令的网页数据源中获取 ItemId
     *
     * @param html 访问淘口令中的网址得到的网页数据
     */
    public static String getIdFromTKL(String html) {
        Matcher matcher = Pattern.compile("var.?url.?=.{1,3}https?://[^/]+/i(\\d+).+").matcher(html);
        if (matcher.find()) {
            String itemId = matcher.group(1);
            return getId(Platform.Taobao, itemId);
        }
        return "";
    }


    /**
     * 获取需要存储的 Item 和 ItemHistory
     *
     * @return Object[0] 为需要创建的 Item, 为 null 时表示数据解析失败;
     * <br/> Object[1] 为需要创建的 ItemHistory, 为 null 时表示数据解析失败.
     */
    public static Object[] getItemAndHistoryToSave(ItemProvider detail) {
        if (detail.isSuccess()) {
            ItemHistory history = new ItemHistory(detail.getId());
            Item itemNew = new Item(detail.getId());
            String itemPrice = detail.getItemPrice();
            itemNew.setPrice(itemPrice);
            itemNew.setInitialPrice(TaobaoDataParser.parsePrice(itemPrice)[0]);
            history.setPrice(itemPrice);
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
    public static Object[] getItemAndHistoryToSave(Item item, Item itemCopy, ItemProvider detail) {
        if (detail.isSuccess()) {
            ItemHistory history = null;
            boolean needUpdate = false;
            if (item.getId().equals(detail.getId())) {
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
        String display = "";
        int[] price = TaobaoDataParser.parsePrice(item.getPrice());
        if (price[0] < item.getNotifiedPrice()) {
            display = "#1比通知价格" + getPrice(item.getNotifiedPrice(), price[0], "高", "低");
        } else if (price[0] < item.getNormalPrice()) {
            display = "#2比正常价格" + getPrice(item.getNormalPrice(), price[0], "高", "低");
        }
        String oldDisplay = item.getDisplay();
        if (LshStringUtils.notEmpty(oldDisplay)
                && ((item.getNormalPrice() == 0 && oldDisplay.contains("#3")) || oldDisplay.contains("#4"))) {
            if (oldDisplay.startsWith("#1") || oldDisplay.startsWith("#2")) {
                display = display + oldDisplay.substring(oldDisplay.indexOf("#", 3), oldDisplay.length());
            } else {
                display = display + oldDisplay;
            }
        }
        item.setDisplay(display.length() == 0 ? null : display);
    }

    private static void updateItemDisplay(Item item, String newPrice) {
        String display = "";
        int[] newPrices = TaobaoDataParser.parsePrice(newPrice);
        if (newPrices[0] < item.getNotifiedPrice()) {
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
        item.setDisplay(display.length() == 0 ? null : display);
    }

    private static String getPrice(int targetPrice, int newPrice, String ascendedStr, String descendedStr) {
        int price = newPrice - targetPrice;
        String diffStr = price > 0 ? ascendedStr : descendedStr;
        return String.format("%s%s元", diffStr, BeanHelper.getPriceStr(price));
    }

    /**
     * @param price int 价格为真实价格 * 100
     * @return
     */
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

    /**
     * @param price float 价格为真实价格
     * @return
     */
    public static String getPriceStr(float price) {
        String format;
        if (price - (int) price == 0) {
            format = "%.0f";
        } else {
            format = "%.2f";
        }
        return String.format(Locale.CHINA, format, Math.abs(price));
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
