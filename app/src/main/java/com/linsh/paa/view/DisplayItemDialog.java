package com.linsh.paa.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.linsh.lshapp.common.tools.ImageTool;
import com.linsh.paa.R;
import com.linsh.paa.model.bean.db.Item;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/13
 *    desc   :
 * </pre>
 */
public class DisplayItemDialog extends Dialog {

    private ImageView ivPhoto;
    private TextView tvTitle;
    private TextView tvShopname;
    private TextView tvPrice;
    private TextView tvAdd;
    private TextView tvCancel;
    private View mView;


    public DisplayItemDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
        init();
    }

    private void init() {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_display_item, null);
        ivPhoto = (ImageView) mView.findViewById(R.id.iv_dialog_display_item_photo);
        tvTitle = (TextView) mView.findViewById(R.id.tv_dialog_display_item_title);
        tvShopname = (TextView) mView.findViewById(R.id.tv_dialog_display_item_shopname);
        tvPrice = (TextView) mView.findViewById(R.id.tv_idialog_display_item_price);
        tvAdd = (TextView) mView.findViewById(R.id.tv_dialog_display_item_add);
        tvCancel = (TextView) mView.findViewById(R.id.tv_dialog_display_item_cancel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mView);
    }

    public DisplayItemDialog setData(Item item) {
        tvTitle.setText(item.getTitle());
        tvPrice.setText('¥' + item.getPrice());
        tvShopname.setText(item.getShopName());
        ImageTool.setImage(ivPhoto, item.getImage());
        return this;
    }

    public DisplayItemDialog setNegativeText(String text) {
        tvCancel.setText(text);
        return this;
    }

    public DisplayItemDialog setOnPositiveClickListener(OnPositiveClickListener listener) {
        tvAdd.setOnClickListener(v -> listener.onPositiveClick(this));
        return this;
    }

    public interface OnPositiveClickListener {
        void onPositiveClick(DisplayItemDialog dialog);
    }

    public DisplayItemDialog setOnNegativeClickListener(OnNegativeClickListener listener) {
        tvCancel.setOnClickListener(v -> listener.onNegativeClick(this));
        return this;
    }

    public interface OnNegativeClickListener {
        void onNegativeClick(DisplayItemDialog dialog);
    }

}
