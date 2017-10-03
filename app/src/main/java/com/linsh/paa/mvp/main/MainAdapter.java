package com.linsh.paa.mvp.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.linsh.lshapp.common.tools.ImageTool;
import com.linsh.lshutils.adapter.LshRecyclerViewAdapter;
import com.linsh.paa.R;
import com.linsh.paa.model.bean.db.Item;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   :
 * </pre>
 */
class MainAdapter extends LshRecyclerViewAdapter<Item, MainAdapter.MyViewHolder> {

    @Override
    protected int getLayout() {
        return R.layout.item_main;
    }

    @Override
    protected MyViewHolder createViewHolder(View view, int type) {
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(MyViewHolder myViewHolder, Item item, int position) {
        myViewHolder.tvTitle.setText(item.getTitle());
        myViewHolder.tvPrice.setText('¥' + item.getPrice());
        myViewHolder.tvShopname.setText(item.getShopName());
        ImageTool.setImage(myViewHolder.ivPhoto, item.getImage());
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private TextView tvTitle;
        private TextView tvPrice;
        private TextView tvShopname;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_item_main_photo);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_item_main_title);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_item_main_price);
            tvShopname = (TextView) itemView.findViewById(R.id.tv_item_main_shopname);
        }
    }
}
