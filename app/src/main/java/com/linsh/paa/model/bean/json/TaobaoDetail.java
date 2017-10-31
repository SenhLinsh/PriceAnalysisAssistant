package com.linsh.paa.model.bean.json;

import com.linsh.paa.model.bean.ItemProvider;
import com.linsh.paa.model.bean.db.Platform;
import com.linsh.paa.tools.BeanHelper;

import java.util.List;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   : 淘宝宝贝详情信息
 * </pre>
 */
public class TaobaoDetail implements ItemProvider {


    /**
     * v : 6.0
     * ret : ["SUCCESS::调用成功"]
     * data : {"apiStack":[{"name":"esi","value":{"item":{"couponUrl":"//h5.m.taobao.com/present/hongbao.html?sellerId=103209408","showShopActivitySize":"2","sellCount":"102"},"trade":{"buyEnable":"true","cartEnable":"true"},"price":{"price":{"priceText":"2888-5651","type":"1"},"extraPrices":[{"priceText":"3610-7063.75","priceTitle":"价格","type":"2","lineThrough":"true"}],"priceTag":[{"text":"火热促销"},{"text":"淘金币抵1%"}]}}}],"item":{"itemId":"541678409963","title":"【大象摄影】国行Sony/索尼 ILCE-A6000L套机 微单电高清数码相机","images":["http://img.alicdn.com/imgextra/i2/103209408/TB2XcoEibsTMeJjy1zeXXcOCVXa_!!103209408.jpg","http://img.alicdn.com/imgextra/i4/103209408/TB2ZIDKcwMPMeJjy1XdXXasrXXa-103209408.jpg","http://img.alicdn.com/imgextra/i1/103209408/TB2xz8eiwoQMeJjy0FoXXcShVXa_!!103209408.jpg","http://img.alicdn.com/imgextra/i4/103209408/TB2dFrpXYsTMeJjy1zcXXXAgXXa-103209408.jpg","http://img.alicdn.com/imgextra/i2/103209408/TB2XHZVvmJjpuFjy0FdXXXmoFXa-103209408.jpg"],"commentCount":"1124","favcount":"7014"}}
     */

    public String v;
    public DataBean data;
    public List<String> ret;

    public static class DataBean {

        public ItemBean item;
        public List<ApiStackBean> apiStack;
        public SellerBean seller;

        public static class ItemBean {

            public String itemId;
            public String title;
            public String commentCount;
            public String favcount;
            public List<String> images;
        }

        public static class ApiStackBean {

            public String name;
            public ValueBean value;

            public static class ValueBean {

                public ItemBeanX item;
                public TradeBean trade;
                public PriceBeanX price;

                public static class ItemBeanX {

                    public String couponUrl;
                    public String showShopActivitySize;
                    public String sellCount;
                }

                public static class TradeBean {

                    public String buyEnable;
                    public String cartEnable;
                }

                public static class PriceBeanX {

                    public PriceBean price;
                    public SubPriceBean subPrice;
                    public List<ExtraPricesBean> extraPrices;
                    public List<PriceTagBean> priceTag;

                    public static class PriceBean {

                        public String priceText;
                        public String priceTitle;
                        public boolean showTitle;
                        public String type;
                    }

                    public static class SubPriceBean {

                        public String priceText;
                        public String priceTitle;
                        public boolean showTitle;
                    }

                    public static class ExtraPricesBean {

                        public String priceText;
                        public String priceTitle;
                        public String type;
                        public String lineThrough;
                    }

                    public static class PriceTagBean {
                        public String text;
                    }
                }
            }
        }

        public static class SellerBean {
            public String shopName;
        }
    }

    public boolean isSuccess() {
        return data != null && (ret.size() == 0 || ret.get(0).contains("SUCCESS"));
    }

    public String getId() {
        try {
            return BeanHelper.getId(Platform.Taobao, data.item.itemId);
        } catch (Exception e) {
            return null;
        }
    }

    public String getItemTitle() {
        try {
            return data.item.title;
        } catch (Exception e) {
            return null;
        }
    }

    public String getItemImage() {
        try {
            return data.item.images.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public String getItemPrice() {
        try {
            if (data.apiStack.get(0).value.price.subPrice != null) {
                return data.apiStack.get(0).value.price.subPrice.priceText;
            }
            return data.apiStack.get(0).value.price.price.priceText;
        } catch (Exception e) {
            return null;
        }
    }

    public String getItemExtraPrice() {
        try {
            return data.apiStack.get(0).value.price.extraPrices.get(0).priceText;
        } catch (Exception e) {
            return null;
        }
    }

    public String getShopName() {
        try {
            return data.seller.shopName;
        } catch (Exception e) {
            return null;
        }
    }
}
