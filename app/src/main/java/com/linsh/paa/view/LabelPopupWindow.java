package com.linsh.paa.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.linsh.lshutils.utils.LshScreenUtils;
import com.linsh.paa.R;

import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/11
 *    desc   :
 * </pre>
 */
public class LabelPopupWindow extends PopupWindow {
    private Context context;
    private FlexboxLayout mFlLabel;

    public LabelPopupWindow(Context context) {
        this(View.inflate(context, R.layout.popup_main_label, null));
        this.context = context;
    }

    private LabelPopupWindow(View contentView) {
        super(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mFlLabel = (FlexboxLayout) contentView.findViewById(R.id.fl_popup_main_label);
        contentView.setOnClickListener(view -> dismiss());
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public LabelPopupWindow addLabels(String... labels) {
        return addLabels(Arrays.asList(labels));
    }

    public LabelPopupWindow addLabels(String[] labels, int selectedIndex) {
        return addLabels(Arrays.asList(labels), selectedIndex);
    }

    public LabelPopupWindow addLabels(List<String> labels) {
        return addLabels(labels, -1);
    }

    public LabelPopupWindow addLabels(List<String> labels, int selectedIndex) {
        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            View inflate = View.inflate(context, R.layout.item_label, null);
            TextView textView = (TextView) inflate.findViewById(R.id.tv_item_label_tag);
            textView.setOnClickListener(view -> {
                int index = 0;
                for (int j = 0; j < mFlLabel.getChildCount(); j++) {
                    View childAt = mFlLabel.getChildAt(j);
                    if (childAt == view) {
                        index = j;
                    } else if (childAt.isSelected()) {
                        childAt.setSelected(false);
                    }
                }
                view.setSelected(!view.isSelected());
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(LabelPopupWindow.this, index, view.isSelected());
                }
            });
            textView.setText(label);
            if (selectedIndex >= 0 && selectedIndex == i) {
                textView.setSelected(true);
            }
            mFlLabel.addView(textView);
        }
        return this;
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        // 修复 Android 7.0 以上 showAsDropDown 和 MATCH_PARENT (导致全屏而不是 DropDown) 冲突的问题
        if (Build.VERSION.SDK_INT >= 24) {
            int y = LshScreenUtils.getLocationYOnScreen(anchor);
            int height = anchor.getHeight();
            setHeight(LshScreenUtils.getScreenHeight() - height - y);
        }
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    public LabelPopupWindow removeAllViews() {
        mFlLabel.removeAllViews();
        return this;
    }

    private OnItemClickListener mOnItemClickListener;

    public LabelPopupWindow setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        return this;
    }

    public interface OnItemClickListener {
        void onItemClick(LabelPopupWindow popupWindow, int index, boolean isSelected);
    }
}
