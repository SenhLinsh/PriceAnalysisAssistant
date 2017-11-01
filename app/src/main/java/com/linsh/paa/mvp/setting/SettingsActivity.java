package com.linsh.paa.mvp.setting;

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
}
