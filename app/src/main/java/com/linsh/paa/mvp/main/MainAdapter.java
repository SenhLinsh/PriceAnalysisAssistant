package com.linsh.paa.mvp.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linsh.lshapp.common.tools.ImageTool;
import com.linsh.lshutils.adapter.LshHeaderFooterRcvAdapter;
import com.linsh.lshutils.adapter.LshViewHolder;
import com.linsh.paa.R;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.view.LabelPopupWindow;

import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   :
 * </pre>
 */
class MainAdapter extends LshHeaderFooterRcvAdapter<Item, RecyclerView.ViewHolder>
        implements LshHeaderFooterRcvAdapter.OnItemClickListener, LshHeaderFooterRcvAdapter.OnItemLongClickListener {

    private List<String> tags = Arrays.asList("所有", "摄影", "我是一个很长长标签", "无标签");
    private int curLabelIndex = -1;

    public MainAdapter() {
        super(true, false);
        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(parent);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HeaderViewHolder(parent);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent) {
        return new FooterViewHolder(parent);
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, Item item, int position) {
        ItemViewHolder myViewHolder = (ItemViewHolder) holder;
        myViewHolder.tvTitle.setText(item.getTitle());
        myViewHolder.tvPrice.setText('¥' + item.getPrice());
        myViewHolder.tvShopname.setText(item.getShopName());
        ImageTool.setImage(myViewHolder.ivPhoto, item.getImage());
    }

    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        ((HeaderViewHolder) holder).setViews();
    }

    @Override
    protected void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {
    }

    public void setTags(List<String> tags) {
        tags.add("无标签");
        this.tags = tags;
    }

    @Override
    public void onItemClick(View itemView, int position) {
        mOnMainAdapterListener.onItemClick(itemView, position);
    }

    @Override
    public void onHeaderClick(View itemView) {

    }

    @Override
    public void onFooterClick(View itemView) {

    }

    @Override
    public void onItemLongClick(View view, int position) {
        mOnMainAdapterListener.onItemLongClick(view, position);
    }

    @Override
    public void onHeaderLongClick(View itemView) {

    }

    @Override
    public void onFooterLongClick(View itemView) {

    }

    class ItemViewHolder extends LshViewHolder {
        private ImageView ivPhoto;
        private TextView tvTitle;
        private TextView tvPrice;
        private TextView tvShopname;

        public ItemViewHolder(ViewGroup parent) {
            super(R.layout.item_main, parent);
        }

        @Override
        public void initView(View itemView) {
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_item_main_photo);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_item_main_title);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_item_main_price);
            tvShopname = (TextView) itemView.findViewById(R.id.tv_item_main_shopname);
        }
    }

    class HeaderViewHolder extends LshViewHolder {
        private TextView tvTag;
        private TextView tvStatus;

        public HeaderViewHolder(ViewGroup parent) {
            super(R.layout.item_main_header, parent);
        }

        @Override
        public void initView(View itemView) {
            tvTag = (TextView) itemView.findViewById(R.id.tv_item_main_header_tag);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_item_main_header_status);
        }

        public void setViews() {
            tvTag.setOnClickListener(view -> {
                view.setSelected(!view.isSelected());
                new LabelPopupWindow(view.getContext())
                        .addLabels(tags, curLabelIndex)
                        .setOnItemClickListener(new LabelPopupWindow.OnItemClickListener() {
                            @Override
                            public void onItemClick(LabelPopupWindow popupWindow, int index, boolean isSelected) {
                                if (isSelected) {
                                    tvTag.setText(tags.get(index));
                                    curLabelIndex = index;
                                } else {
                                    tvTag.setText("所有");
                                    curLabelIndex = -1;
                                }
                                popupWindow.dismiss();
                            }

                            @Override
                            public void onAddLabelClick(LabelPopupWindow popupWindow) {
                                popupWindow.dismiss();
                                if (mOnMainAdapterListener != null) {
                                    mOnMainAdapterListener.onAddLabel();
                                }
                            }
                        })
                        .showAsDropDown(itemView);
            });
            tvStatus.setOnClickListener(view -> view.setSelected(!view.isSelected()));
        }
    }

    class FooterViewHolder extends LshViewHolder {

        public FooterViewHolder(ViewGroup parent) {
            super(R.layout.item_main_header, parent);
        }

        @Override
        public void initView(View itemView) {

        }
    }

    private OnMainAdapterListener mOnMainAdapterListener;

    public void setOnMainAdapterListener(OnMainAdapterListener listener) {
        mOnMainAdapterListener = listener;
    }

    public interface OnMainAdapterListener {
        void onAddLabel();

        void onItemClick(View itemView, int position);

        void onItemLongClick(View view, int position);
    }
}
