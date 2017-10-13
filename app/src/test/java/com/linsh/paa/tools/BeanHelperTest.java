package com.linsh.paa.tools;

import com.google.common.truth.Truth;
import com.linsh.paa.model.bean.db.Item;

import org.junit.Test;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/13
 *    desc   :
 * </pre>
 */
public class BeanHelperTest {

    @Test
    public void updateItemDisplay() throws Exception {
        Item item = new Item();
        item.setPrice("110-300");
        BeanHelper.updateItemDisplay(item);
        Truth.assertThat(item.getDisplay()).isEqualTo(null);
        item.setNormalPrice(15000);
        BeanHelper.updateItemDisplay(item);
        Truth.assertThat(item.getDisplay()).isEqualTo("#2比正常价格低40.00元");
        item.setDisplay(item.getDisplay() + "#3比收藏时下降666元");
        item.setNotifiedPrice(13000);
        BeanHelper.updateItemDisplay(item);
        Truth.assertThat(item.getDisplay()).isEqualTo("#1比通知价格低20.00元#3比收藏时下降666元");
    }

}