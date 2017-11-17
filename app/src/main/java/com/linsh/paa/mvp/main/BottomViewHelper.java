package com.linsh.paa.mvp.main;

import android.app.Activity;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.TextView;

import com.linsh.paa.R;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/11
 *    desc   :
 * </pre>
 */
public class BottomViewHelper {

    private final ViewStub vsBottom;
    private CheckBox cbSelectAll;
    private TextView tvMove;
    private TextView tvDelete;
    private TextView tvDone;
    private View mBottomView;

    public BottomViewHelper(Activity activity) {
        vsBottom = (ViewStub) activity.findViewById(R.id.vs_main_bottom);
    }

    public void showBottom(Activity activity, boolean isRemove) {
        if (mBottomView == null) {
            mBottomView = vsBottom.inflate();
            cbSelectAll = (CheckBox) activity.findViewById(R.id.cb_main_bottom_select_all);
            tvMove = (TextView) activity.findViewById(R.id.tv_main_bottom_move);
            tvDelete = (TextView) activity.findViewById(R.id.tv_main_bottom_delete);
            tvDone = (TextView) activity.findViewById(R.id.tv_main_bottom_done);

            cbSelectAll.setOnClickListener(view -> mViewHelperListener.selectAll(cbSelectAll.isChecked()));
            tvMove.setOnClickListener(view -> mViewHelperListener.move());
            tvDelete.setOnClickListener(view -> mViewHelperListener.delete());
            tvDone.setOnClickListener(view -> {
                mBottomView.setVisibility(View.GONE);
                cbSelectAll.setChecked(false);
                mViewHelperListener.done();
            });
        }
        tvDelete.setText(isRemove ? "取消关注" : "删除");
        mBottomView.setVisibility(View.VISIBLE);
    }

    private ViewHelperListener mViewHelperListener;

    public void setViewHelperListener(ViewHelperListener listener) {
        mViewHelperListener = listener;
    }

    public void resetSelectAll() {
        if (cbSelectAll != null) {
            cbSelectAll.setChecked(false);
        }
    }

    public interface ViewHelperListener {
        void delete();

        void move();

        void selectAll(boolean selected);

        void done();
    }
}
