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
import com.linsh.lshapp.common.base.BaseToolbarActivity;
import com.linsh.lshutils.utils.Basic.LshLogUtils;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.lshutils.utils.LshClipboardUtils;
import com.linsh.paa.R;
import com.linsh.paa.task.network.Url;

import hugo.weaving.DebugLog;

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

    @DebugLog
    @Override
    protected void initView() {
        initWebView();
    }

    public void initWebView() {
        String url = LshActivityUtils.getStringExtra(this);
        if (url == null) return;
        if (url.matches("\\d+")) {
            url = Url.getTaobaoDetailHtmlUrl(url);
        } else if (!url.startsWith("http")) {
            return;
        }
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((ViewGroup) findViewById(R.id.fl_item_display_root), new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .defaultProgressBarColor()
                .setWebViewClient(new WebViewClient() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                        LshLogUtils.i("shouldInterceptRequest: url = " + request.getUrl().toString());
                        return super.shouldInterceptRequest(view, request);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        getToolbar().setTitle("正在跳转...");
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        getToolbar().setTitle(view.getTitle());
                    }
                })
                .createAgentWeb()
                .ready()
                .go(url);
    }

    @Override
    public void onBackPressed() {
        if (mAgentWeb == null || !mAgentWeb.back()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display, menu);
        return true;
    }

    @DebugLog
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_display_get_url) {
            String url = mAgentWeb.getWebCreator().get().getUrl();
            LshClipboardUtils.putText(url);
            LshLogUtils.i("复制链接: " + url);
            return true;
        } else if (id == R.id.menu_display_add_item) {
            String url = mAgentWeb.getWebCreator().get().getUrl();
            mPresenter.addCurItem(url);
            Log.i("LshLog", "onOptionsItemSelected: url = " + url);
        }
        return super.onOptionsItemSelected(item);
    }
}
