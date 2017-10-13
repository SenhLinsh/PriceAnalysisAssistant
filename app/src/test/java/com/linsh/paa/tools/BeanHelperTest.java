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
    public void getItemId() throws Exception {
        String itemId = BeanHelper.getItemId("54673849583");
        Truth.assertThat(itemId).isEqualTo("54673849583");
        itemId = BeanHelper.getItemId("https://detail.tmall.com/item.htm?id=554244546975" +
                "&spm=a21bo.50862.201875.1.6ea2684qYpsyR&scm=1007.12493.69999.100200300000001");
        Truth.assertThat(itemId).isEqualTo("554244546975");
        itemId = BeanHelper.getItemId("【【天猫超市】三只松鼠 俏蛮腰麻花112g零食特产天津风味小麻花】http://v.cvz5.com/h.Gfsjzt " +
                "点击链接，再选择浏览器打开；或复制这条信息￥7mat0UlsPvn￥后打开\uD83D\uDC49手机淘宝\uD83D\uDC48");
        Truth.assertThat(itemId).isEqualTo("http://v.cvz5.com/h.Gfsjzt");
    }

    @Test
    public void getItemIdFromTKL() throws Exception {
        String itemId = BeanHelper.getItemIdFromTKL(HTML_DATA_TKL);
        Truth.assertThat(itemId).isEqualTo("20496648200");
    }

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

    public static String HTML_DATA_TKL = "<html>\n" +
            "    <head>\n" +
            "        <meta charset=\"UTF-8\"/>\n" +
            "        <meta name=\"viewport\" content=\"initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no\">\n" +
            "        <meta name=\"format-detection\" content=\"telephone=no, email=no\">\n" +
            "        <title></title>\n" +
            "        <script src=\"//g.alicdn.com/mtb/lib-callapp/1.4.8/??combo.js,callapp.js\"></script>\n" +
            "        <style>\n" +
            "        body{\n" +
            "            background-repeat:no-repeat;\n" +
            "            background-position: left top;\n" +
            "            background-color: #efeff4;\n" +
            "            background-size: 100%;\n" +
            "        \tpadding: 0;\n" +
            "            margin: 0; \n" +
            "        }\n" +
            "    </style>\n" +
            "        <script>\n" +
            "       //业务点code\n" +
            "      \tvar bizCode=\"tbshare\";\n" +
            "    \t//短地址\n" +
            "    \tvar shortName = \"h.Gfnp5f\";\n" +
            "    \t//目标地址\n" +
            "    \tvar url = 'https://a.m.taobao.com/i20496648200.htm?price=369&sourceType=item&sourceType=item&suid=14e62cd7-dc82-41c9-b50e-78d78950e7cd&ut_sk=1.V1UEJP6RumQDADZ6ehyJm7zX_21646297_1507881237099.Copy.1&cpp=1&shareurl=true&spm=a313p.22.1ji.72321924216&short_name=h.Gfnp5f';\n" +
            "    \t//短地址有问题时跳转的地址\n" +
            "    \tvar invalidUrl = \"http://m.tb.cn/scanError.htm\";\n" +
            "    \t//黄金令箭埋点值\n" +
            "    \tvar ecode=\"ecode.2.1\";\n" +
            "    \t//短地址状态\n" +
            "    \tvar status = \"true\";\n" +
            "\t\t\t//是否是阿里系APP访问\n" +
            "\t\t\tvar isAliApp = \"false\";\n" +
            "\t\t\t//是否强制在body里插入img节点（tbshare有类似需求）\n" +
            "\t\t\tvar mustImg = \"true\";\n" +
            "\t\t\n" +
            "     //客户端应用信息，见App.java定义,这些变量在页面上已经用不到了，全部在java内部判断用了\n" +
            "     var app= \"chrome\";//如果是微信，需要特殊处理\n" +
            "     //客户端系统信息，见OS.java定义\n" +
            "     var os = \"mac\";//访问的系统\n" +
            "\t\t\n" +
            "\t\t//客户端打开的方式，见xcode里Constant.java中定义\n" +
            "\t\tvar appMethod=\"replace\";\n" +
            "\t\t//selfview的模板不用在这里输出，不然会js变量冲突\n" +
            "\t\t\t\t\tvar methodString=\"1\";\n" +
            "\t\t\n" +
            "\t\t//创建码时，指定码的extraStr的json字符内容，用于自定义模板页面填坑位用\n" +
            "\t\t\t\t\tvar extraData = {\"pic\":\"http://img.alicdn.com/imgextra/i4/1046707508/TB2BIdUdyAKL1JjSZFoXXagCFXa_!!1046707508.jpg\",\"priceH\":\"\",\"priceL\":\"369\",\"title\":\"这个#聚划算团购#宝贝不错:CONVERSE 匡威 经典款 休闲男女帆布鞋  情侣鞋101001(分享自@手机淘宝android客户端)\"};\n" +
            "\t\t\t\t\n" +
            "    </script>\n" +
            "    </head>\n" +
            "    <body>\n" +
            "        <script>\n" +
            "with(document)with(body)with(insertBefore(createElement(\"script\"),firstChild))setAttribute(\"exparams\",\"category=&userid=&aplus&yunid=&&trid=0bb374bb15078841748746091e&asid=AQAAAACOfOBZZbebDwAAAACpywgXFYvvAw==\",id=\"tb-beacon-aplus\",src=(location>\"https\"?\"//g\":\"//g\")+\".alicdn.com/alilog/mlog/aplus_v2.js\")\n" +
            "</script>\n" +
            "    </body>\n" +
            "    <script type=\"text/javascript\" src=\"/assets/scan.js?t=201705231430\"></script>\n" +
            "</html>";

}