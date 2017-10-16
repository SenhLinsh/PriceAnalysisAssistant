package com.linsh.paa.mvp.analysis;


import com.github.mikephil.charting.data.Entry;
import com.linsh.lshapp.common.base.BaseContract;
import com.linsh.paa.model.bean.db.Item;

import java.util.ArrayList;

/**
 * Created by Senh Linsh on 17/4/25.
 */

interface AnalysisContract {

    interface View extends BaseContract.BaseView {

        String getItemId();

        void setData(ArrayList<Entry> lowPrices, ArrayList<Entry> highPrices);

        void setData(Item item);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

    }
}
