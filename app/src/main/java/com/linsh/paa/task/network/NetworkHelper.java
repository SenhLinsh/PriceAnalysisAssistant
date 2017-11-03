package com.linsh.paa.task.network;

import com.linsh.paa.model.bean.ItemProvider;
import com.linsh.paa.model.bean.JingdongDetail;
import com.linsh.paa.tools.BeanHelper;
import com.linsh.paa.tools.JingdongDataParser;
import com.linsh.paa.tools.TaobaoDataParser;

import io.reactivex.Flowable;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/27
 *    desc   : 管理网络访问的帮助类, 避免 ApiCreator 的对外频繁操作
 * </pre>
 */
public class NetworkHelper {

    public static Flowable<String> get(String url) {
        return ApiCreator.getCommonApi().get(url);
    }

    public static Flowable<ItemProvider> getItemProvider(String id) {
        return Flowable.just(BeanHelper.getPlatform(id))
                .flatMap(platform -> {
                    switch (platform) {
                        case Taobao:
                            return ApiCreator.getTaobaoApi()
                                    .getDetail(Url.getTaobaoDetailUrl(BeanHelper.getItemId(id)))
                                    .map(TaobaoDataParser::parseGetDetailData);
                        case Jingdong:
                            return ApiCreator.getCommonApi()
                                    .get(Url.getJingdongDetailHtmlUrl(BeanHelper.getItemId(id)))
                                    .map(html -> JingdongDataParser.parseItemDetailHtml(id, html));
                        default:
                            return Flowable.just(new JingdongDetail(false, "无法获取【" + platform.getName() + "】平台的宝贝信息"));
                    }
                });
    }
}
