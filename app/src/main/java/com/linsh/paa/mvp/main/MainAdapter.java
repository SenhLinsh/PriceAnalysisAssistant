package com.linsh.paa.mvp.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linsh.lshapp.common.tools.ImageTool;
import com.linsh.lshutils.adapter.LshHeaderFooterRcvAdapter;
import com.linsh.lshutils.adapter.LshViewHolder;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.paa.R;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.view.LabelPopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hugo.weaving.DebugLog;

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
    private int curStatusIndex = -1;
    private boolean isSelectMode;
    private boolean[] selectedItems;

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
        ((ItemViewHolder) holder).bindData(item, position);
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

    @Override
    public void setData(List<Item> data) {
        selectedItems = isSelectMode ? new boolean[data.size()] : null;
        setHasFooter(data.size() == 0);
        super.setData(data);
    }

    public void setSelectMode(boolean isSelectable) {
        isSelectMode = isSelectable;
        selectedItems = isSelectMode ? new boolean[getData().size()] : null;
        notifyDataSetChanged();
    }

    public void selectAll(boolean selected) {
        if (selectedItems != null) {
            for (int i = 0; i < selectedItems.length; i++) {
                selectedItems[i] = selected;
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedIds() {
        ArrayList<String> itemIds = new ArrayList<>();
        if (selectedItems != null) {
            for (int i = 0; i < selectedItems.length; i++) {
                if (selectedItems[i]) {
                    itemIds.add(getData().get(i).getId());
                }
            }
        }
        return itemIds;
    }

    class ItemViewHolder extends LshViewHolder {
        private ImageView ivPhoto;
        private TextView tvTitle;
        private TextView tvPrice;
        private TextView tvShopname;
        private TextView tvDisplayDescend;
        private TextView tvDisplayLow;
        private ImageView ivSelect;
        private View flSelect;

        public ItemViewHolder(ViewGroup parent) {
            super(R.layout.item_main, parent);
        }

        @Override
        public void initView(View itemView) {
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_item_main_photo);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_item_main_title);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_item_main_price);
            tvShopname = (TextView) itemView.findViewById(R.id.tv_item_main_shopname);
            tvDisplayDescend = (TextView) itemView.findViewById(R.id.tv_item_main_display_descend);
            tvDisplayLow = (TextView) itemView.findViewById(R.id.tv_item_main_display_low);
            ivSelect = (ImageView) itemView.findViewById(R.id.tv_item_main_select);
            flSelect = itemView.findViewById(R.id.fl_item_main_select);

            flSelect.setOnClickListener(view -> {
                ivSelect.setSelected(!ivSelect.isSelected());
                if (selectedItems != null)
                    selectedItems[getAdapterPosition() - (hasHeader() ? 1 : 0)] = ivSelect.isSelected();
            });

        }

        @DebugLog
        public void bindData(Item item, int position) {
            tvTitle.setText(item.getTitle());
            tvPrice.setText('¥' + item.getPrice());
            tvShopname.setText(item.getShopName());
            flSelect.setVisibility(isSelectMode ? View.VISIBLE : View.GONE);
            ivSelect.setSelected(selectedItems != null && selectedItems[position]);
            String display = LshStringUtils.nullStrToEmpty(item.getDisplay());
            Matcher matcher = Pattern.compile("(#[12]([^#]+))?(#[34]([^#]+))?").matcher(display);
            if (matcher.find()) {
                String lowStr = matcher.group(2);
                String descendStr = matcher.group(4);
                if (LshStringUtils.notEmpty(lowStr)) {
                    tvDisplayLow.setVisibility(View.VISIBLE);
                    tvDisplayLow.setText(lowStr);
                } else {
                    tvDisplayLow.setVisibility(View.GONE);
                }
                if (LshStringUtils.notEmpty(descendStr)) {
                    tvDisplayDescend.setVisibility(View.VISIBLE);
                    tvDisplayDescend.setText(descendStr);
                } else {
                    tvDisplayDescend.setVisibility(View.GONE);
                }
            }
            ImageTool.setImage(ivPhoto, item.getImage());
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
                if (view.isSelected()) {
                    LabelPopupWindow popupWindow = new LabelPopupWindow(view.getContext());
                    popupWindow.addLabels(tags, curLabelIndex)
                            .setOnItemClickListener((popupWindow1, index, isSelected) -> {
                                popupWindow1.dismiss();
                                if (isSelected) {
                                    tvTag.setText(tags.get(index));
                                    curLabelIndex = index;
                                } else {
                                    tvTag.setText("所有");
                                    curLabelIndex = -1;
                                }
                                mOnMainAdapterListener.onTagSelected(isSelected ? tags.get(index) : null);
                            })
                            .showAsDropDown(itemView);
                    popupWindow.setOnDismissListener(() -> tvTag.setSelected(false));
                }
            });
            tvStatus.setOnClickListener(view -> {
                view.setSelected(!view.isSelected());
                if (view.isSelected()) {
                    LabelPopupWindow popupWindow = new LabelPopupWindow(view.getContext());
                    String[] statuses = new String[]{"价格较低", "降价中"};
                    popupWindow.addLabels(statuses, curStatusIndex)
                            .setOnItemClickListener((popupWindow1, index, isSelected) -> {
                                popupWindow1.dismiss();
                                if (isSelected) {
                                    tvStatus.setText(statuses[index]);
                                    curStatusIndex = index;
                                } else {
                                    tvStatus.setText("无");
                                    curStatusIndex = -1;
                                }
                                mOnMainAdapterListener.onStatusSelected(isSelected ? statuses[index] : null);
                            })
                            .showAsDropDown(itemView);
                    popupWindow.setOnDismissListener(() -> tvStatus.setSelected(false));
                }
            });
        }
    }

    class FooterViewHolder extends LshViewHolder {

        public FooterViewHolder(ViewGroup parent) {
            super(R.layout.item_main_footer_empty, parent);
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
        void onItemClick(View itemView, int position);

        void onItemLongClick(View view, int position);

        void onTagSelected(String tag);

        void onStatusSelected(String status);
    }
}
