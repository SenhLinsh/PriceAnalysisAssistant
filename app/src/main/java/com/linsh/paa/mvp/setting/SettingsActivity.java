package com.linsh.paa.mvp.setting;

import android.support.v7.app.AlertDialog;

import com.linsh.lshapp.common.base.BaseToolbarActivity;
import com.linsh.lshutils.utils.LshFragmentUtils;
import com.linsh.paa.R;

/**
 * Created by Senh Linsh on 17/5/2.
 */
public class SettingsActivity extends BaseToolbarActivity<SettingsContract.Presenter>
        implements SettingsContract.View {


    @Override
    protected String getToolbarTitle() {
        return "设置";
    }

    @Override
    protected SettingsPresenter initPresenter() {
        return new SettingsPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initView() {
        LshFragmentUtils.replaceFragment(new SettingsFragment(), R.id.fl_settings_content, getActivity());
    }

    public void checkUpdate() {
        mPresenter.checkUpdate();
    }

    public void importItems() {
        mPresenter.importItems();
    }

    public void exportRealm() {
        mPresenter.exportRealm();
    }

    public void importRealm() {
        mPresenter.importRealm();
    }

    public void setIntervalTime() {
        mPresenter.setIntervalTime();
    }

    @Override
    public void selectIntervalTime(int index, String[] times) {
        final int[] newIndex = {index};
        new AlertDialog.Builder(this)
                .setTitle("选择间隔时间")
                .setSingleChoiceItems(times, index, (dialog, which) -> newIndex[0] = which)
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                    if (newIndex[0] >= 0 && newIndex[0] != index) {
                        mPresenter.saveIntervalTime(times[newIndex[0]]);
                    }
                })
                .show();
    }
}
