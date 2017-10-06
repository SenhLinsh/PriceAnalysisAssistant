package com.linsh.paa.mvp.main;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.linsh.lshapp.common.base.BaseViewActivity;
import com.linsh.lshutils.adapter.LshRecyclerViewAdapter;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.lshutils.utils.LshClipboardUtils;
import com.linsh.lshutils.view.LshColorDialog;
import com.linsh.paa.R;
import com.linsh.paa.model.action.HttpThrowableConsumer;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.bean.json.TaobaoDetail;
import com.linsh.paa.mvp.analysis.AnalysisActivity;
import com.linsh.paa.task.network.ApiCreator;
import com.linsh.paa.task.network.Url;
import com.linsh.paa.tools.BeanHelper;
import com.linsh.paa.tools.TaobaoDataParser;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
        RecyclerView rvContent = (RecyclerView) findViewById(R.id.rv_content);
        rvContent.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new MainAdapter();
        rvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new LshRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                LshActivityUtils.newIntent(AnalysisActivity.class)
                        .putExtra(mAdapter.getData().get(position).getId())
                        .startActivity(getActivity());
            }
        });
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
            String itemId = checkItem(LshClipboardUtils.getText());
            if (itemId != null) {
                showTextDialog("检测到剪贴板中的宝贝(Id:" + itemId + "), 是否添加", "添加", new LshColorDialog.OnPositiveListener() {
                    @Override
                    public void onClick(LshColorDialog lshColorDialog) {
                        lshColorDialog.dismiss();
                        getItem(itemId);
                    }
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
                    String itemId = checkItem(text);
                    if (itemId != null) {
                        getItem(itemId);
                    } else {
                        showTextDialog("无法解析该宝贝, 请传入正确格式");
                    }
                })
                .setNegativeButton(null, null)
                .show();
    }

    private String checkItem(String item) {
        if (item.matches("\\d{8,}")) {
            return item;
        } else if (item.matches("https?://.+/item\\.htm\\?id=\\d+.+")) {
            String itemId = item.replaceAll(".+\\?id=(\\d+).+", "$1");
            return itemId.matches("\\d+") ? itemId : null;
        }
        return null;
    }

    private void getItem(String itemId) {
        ApiCreator.getTaobaoApi()
                .getDetail(Url.getTaobaoDetailUrl(itemId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new HttpThrowableConsumer())
                .subscribe(data -> {
                    Log.i("LshLog", "getItem: data = " + data);
                    TaobaoDetail detail = TaobaoDataParser.parseGetDetailData(data);
                    Log.i("LshLog", "getItem: detail = " + detail);
                    Object[] toSave = BeanHelper.getItemAndHistiryToSave(null, detail);
                    if (toSave != null) {
                        mPresenter.saveItem((Item) toSave[0], (ItemHistory) toSave[1]);
                    }
                });
    }

    @Override
    public void setData(List<Item> items) {
        mAdapter.setData(items);
    }
}
