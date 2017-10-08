package com.linsh.paa.mvp.main;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.linsh.lshapp.common.base.BaseViewActivity;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.lshutils.utils.LshClipboardUtils;
import com.linsh.lshutils.view.LshColorDialog;
import com.linsh.paa.R;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.mvp.analysis.AnalysisActivity;
import com.linsh.paa.mvp.display.ItemDisplayActivity;

import java.util.List;

public class MainActivity extends BaseViewActivity<MainContract.Presenter>
        implements MainContract.View {

    private MainAdapter mAdapter;

    @Override
    protected MainContract.Presenter initPresenter() {
        return new MainPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        getSupportActionBar().setTitle("价格分析助手");
        RecyclerView rvContent = (RecyclerView) findViewById(R.id.rv_content);
        rvContent.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new MainAdapter();
        rvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(position ->
                LshActivityUtils.newIntent(AnalysisActivity.class)
                        .putExtra(mAdapter.getData().get(position).getId())
                        .startActivity(getActivity()));
        mAdapter.setOnItemLongClickListener(position ->
                LshActivityUtils.newIntent(ItemDisplayActivity.class)
                        .putExtra(mAdapter.getData().get(position).getId())
                        .startActivity(getActivity()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_main_add_item) {
            String itemId = mPresenter.checkItem(LshClipboardUtils.getText());
            if (itemId != null) {
                showTextDialog("检测到剪贴板中的宝贝(Id:" + itemId + "), 是否添加", "添加", lshColorDialog -> {
                    lshColorDialog.dismiss();
                    mPresenter.addItem(itemId);
                }, null, null);
            } else {
                showAddItemDialog();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private LshColorDialog showAddItemDialog() {
        return new LshColorDialog(this)
                .buildInput()
                .setTitle("添加宝贝")
                .setHint("请输入宝贝id 或者宝贝链接")
                .setPositiveButton(null, (lshColorDialog, text) -> {
                    lshColorDialog.dismiss();
                    String itemId = mPresenter.checkItem(text);
                    if (itemId != null) {
                        mPresenter.addItem(itemId);
                    } else {
                        showTextDialog("无法解析该宝贝, 请传入正确格式");
                    }
                })
                .setNegativeButton(null, null)
                .show();
    }

    @Override
    public void setData(List<Item> items) {
        mAdapter.setData(items);
    }
}
