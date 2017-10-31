package com.linsh.paa.view;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.linsh.paa.R;
import com.linsh.paa.tools.BeanHelper;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {

    private TextView tvContent;

    public MyMarkerView(Context context) {
        super(context, R.layout.view_marker);
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(BeanHelper.getPriceStr(ce.getHigh()));
        } else {

            tvContent.setText(BeanHelper.getPriceStr(e.getY()));
        }
        tvContent.setTag(e.getData());
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

    public String getEntryData() {
        Object tag = tvContent.getTag();
        if (tag instanceof String) {
            return (String) tag;
        }
        return null;
    }
}
