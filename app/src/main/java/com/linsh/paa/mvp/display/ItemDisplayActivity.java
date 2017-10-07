package com.linsh.paa.mvp.display;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.linsh.lshapp.common.base.BaseToolbarActivity;
import com.linsh.lshutils.utils.Basic.LshLogUtils;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.lshutils.utils.LshClipboardUtils;
import com.linsh.paa.R;
import com.linsh.paa.task.network.Url;

public class ItemDisplayActivity extends BaseToolbarActivity<ItemDisplayContract.Presenter>
        implements ItemDisplayContract.View {

    private AgentWeb mAgentWeb;

    @Override
    protected String getToolbarTitle() {
        return "正在跳转...";
    }

    @Override
    protected ItemDisplayContract.Presenter initPresenter() {
        return new ItemDisplayPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_item_display;
    }

    @Override
    protected void initView() {
        initWebView();
    }

    public void initWebView() {
        String itemId = LshActivityUtils.getStringExtra(this);
        if (itemId == null) return;
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((ViewGroup) findViewById(R.id.fl_item_display_root), new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()
                .defaultProgressBarColor()
                .setReceivedTitleCallback(new ChromeClientCallbackManager.ReceivedTitleCallback() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {

                    }
                }) //设置 Web 页面的 title 回调
                .setWebViewClient(new WebViewClient() {

                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                        String url = request.getUrl().toString();
                        LshLogUtils.i("shouldInterceptRequest: request.getUrl() = " + url);
                        return super.shouldInterceptRequest(view, request);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        getSupportActionBar().setTitle("正在跳转...");
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        getSupportActionBar().setTitle(view.getTitle());
                    }
                })
                .createAgentWeb()
                .ready()
                .go(Url.getTaobaoDetailHtmlUrl(itemId));
    }

    @Override
    public void onBackPressed() {
        if (!mAgentWeb.back()) {
            super.onBackPressed();//mAgentWeb.getWebCreator().get().getOriginalUrl()
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_display_get_url) {
            String url = mAgentWeb.getWebCreator().get().getOriginalUrl();
            LshClipboardUtils.putText(url);
            LshLogUtils.i("复制链接: " + url);
            return true;
        } else if (id == R.id.menu_display_add_item) {
            String url = mAgentWeb.getWebCreator().get().getOriginalUrl();
            mPresenter.addCurItem(url);
            Log.i("LshLog", "onOptionsItemSelected: url = " + url);
        }
        return super.onOptionsItemSelected(item);
    }
}
