package com.linsh.paa.mvp.analysis;

import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.linsh.lshapp.common.base.BaseToolbarActivity;
import com.linsh.lshutils.module.SimpleDate;
import com.linsh.lshutils.utils.Basic.LshLogUtils;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.paa.R;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.tools.BeanHelper;
import com.linsh.paa.view.MyMarkerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hugo.weaving.DebugLog;

public class AnalysisActivity extends BaseToolbarActivity<AnalysisContract.Presenter>
        implements AnalysisContract.View {

    private LineChart mLineChart;
    private TextView tvSelectedPoint;

    @Override
    protected String getToolbarTitle() {
        return "价格走势";
    }

    @Override
    protected AnalysisContract.Presenter initPresenter() {
        return new AnalysisPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_analysis;
    }

    @DebugLog
    @Override
    protected void initView() {
        mLineChart = (LineChart) findViewById(R.id.lc_analysis_chart);
        TextView tvTitle = (TextView) findViewById(R.id.tv_analysis_title);
        TextView tvShop = (TextView) findViewById(R.id.tv_analysis_shop);
        tvSelectedPoint = (TextView) findViewById(R.id.tv_analysis_selected_point);

        tvTitle.setText(LshActivityUtils.getStringExtra(this, 1));
        tvShop.setText(LshActivityUtils.getStringExtra(this, 2));

        mLineChart.getDescription().setEnabled(false);
        mLineChart.setGridBackgroundColor(Color.argb(150, 51, 181, 229));
        mLineChart.setDrawGridBackground(true);
        mLineChart.setPinchZoom(false);
        mLineChart.setDrawBorders(true);
        mLineChart.setBorderColor(Color.argb(50, 0, 0, 0));
        Legend l = mLineChart.getLegend();
        l.setEnabled(false);

        MyMarkerView mv = new MyMarkerView(this);
        mv.setChartView(mLineChart);
        mLineChart.setMarker(mv);

        mLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String text;
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date((long) e.getX()));
                float f = e instanceof CandleEntry ? ((CandleEntry) e).getHigh() : e.getY();
                String price = BeanHelper.getPriceStr(f) + "元";
                text = "时间: " + date + "        价格: " + price;
                tvSelectedPoint.setText(text);
            }

            @Override
            public void onNothingSelected() {
                tvSelectedPoint.setText("----");
            }
        });

        //获取此图表的x轴
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                float curRange = mLineChart.getVisibleXRange();
                if (curRange < 1000L * 60 * 60 * 24 * 3) {
                    return new SimpleDateFormat("dd日H时").format(new Date((long) value));
                } else if (curRange < 1000L * 60 * 60 * 24 * 30 * 3) {
                    return new SimpleDate((long) value).getNormalizedString(false);
                } else if (curRange < 1000L * 60 * 60 * 24 * 30 * 12 * 3) {
                    SimpleDate simpleDate = new SimpleDate((long) value);
                    return String.format("%s年%s月", simpleDate.getYear(), simpleDate.getMonth());
                } else {
                    return new SimpleDate((long) value).getYear() + "年";
                }
            }
        });

        //获取右边的轴线
        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setEnabled(false);

        //获取左边的轴线
        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(true);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.analysis, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_analysis_update_item:
                mPresenter.updateItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getId() {
        return LshActivityUtils.getStringExtra(this);
    }

    /**
     * 设置正常价格和通知价格的参考线
     */
    @DebugLog
    @Override
    public void setData(Item item) {
        boolean hasLimitLines = false;
        // 设置参考线
        YAxis yAxis = mLineChart.getAxisLeft();
        yAxis.removeAllLimitLines();
        int notifiedPrice = item.getNotifiedPrice();
        if (notifiedPrice > 0) {
            LimitLine notifiedPriceLine = new LimitLine(notifiedPrice * 1F / 100, "通知价格: " + BeanHelper.getPriceStr(notifiedPrice) + "元");
            notifiedPriceLine.setLineColor(Color.RED);
            notifiedPriceLine.setTextColor(Color.RED);
            yAxis.addLimitLine(notifiedPriceLine);
            hasLimitLines = true;
        }
        int normalPrice = item.getNormalPrice();
        if (normalPrice > 0) {
            LimitLine normalPriceLine = new LimitLine(normalPrice * 1F / 100, "正常价格: " +
                    "" + BeanHelper.getPriceStr(normalPrice) + "元");
            normalPriceLine.setLineColor(Color.BLUE);
            normalPriceLine.setTextColor(Color.BLUE);
            yAxis.addLimitLine(normalPriceLine);
            hasLimitLines = true;
        }
        yRangeFlag[0] = hasLimitLines;
        setYRange();
    }

    /**
     * 设置价格数据, 描述价格曲线
     *
     * @param lowPrices  价格, 或价格范围中的最低价
     * @param highPrices 价格范围中的最高价, 单一价格则为 null
     */
    @DebugLog
    @Override
    public void setData(ArrayList<Entry> lowPrices, ArrayList<Entry> highPrices) {
        LineDataSet lowSet = null;
        LineDataSet highSet = null;
        if (mLineChart.getData() != null && mLineChart.getData().getDataSetCount() == 1) {
            lowSet = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
        } else if (mLineChart.getData() != null && mLineChart.getData().getDataSetCount() == 2) {
            lowSet = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
            highSet = (LineDataSet) mLineChart.getData().getDataSetByIndex(1);
        }

        if (lowPrices == null) {
            LshLogUtils.e("lowPrices == null");
        } else if (highPrices == null) {
            if (highSet != null) {
                mLineChart.getData().removeDataSet(1);
            }
            if (lowSet == null) {
                lowSet = new LineDataSet(lowPrices, "价格");
                lowSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                lowSet.setColor(Color.rgb(255, 241, 46)); // 线条颜色
                lowSet.setCircleColor(Color.rgb(244, 117, 117)); // 描点圆圈颜色
                lowSet.setCircleRadius(3f);
                lowSet.setLineWidth(2f);
                lowSet.setHighLightColor(Color.rgb(244, 117, 117)); // 高光颜色
                LineData data = new LineData();
                data.addDataSet(lowSet);
                data.setDrawValues(false);
                mLineChart.setData(data);
            } else {
                lowSet.setValues(lowPrices);
            }
        } else {
            if (lowSet == null) {
                if (highSet != null) {
                    mLineChart.getData().removeDataSet(1);
                }
                lowSet = new LineDataSet(lowPrices, "价格下限");
                lowSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                lowSet.setColor(Color.rgb(255, 241, 46));
                lowSet.setCircleColor(Color.rgb(244, 117, 117));
                lowSet.setCircleRadius(3f);
                lowSet.setLineWidth(2f);
                lowSet.setHighLightColor(Color.rgb(244, 117, 117));
                lowSet.setDrawFilled(true);
                lowSet.setFillAlpha(100);
                lowSet.setFillColor(Color.WHITE);
                lowSet.setFillFormatter(new IFillFormatter() {
                    @Override
                    public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                        return mLineChart.getAxisLeft().getAxisMinimum();
                    }
                });
                highSet = new LineDataSet(highPrices, "价格上限");
                highSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                highSet.setColor(Color.rgb(255, 241, 46));
                highSet.setCircleColor(Color.rgb(244, 117, 117));
                highSet.setCircleRadius(3f);
                highSet.setLineWidth(2f);
                highSet.setHighLightColor(Color.rgb(244, 117, 117));
                highSet.setDrawFilled(true);
                highSet.setFillAlpha(100);
                highSet.setFillColor(Color.WHITE);
                highSet.setFillFormatter(new IFillFormatter() {
                    @Override
                    public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                        return mLineChart.getAxisLeft().getAxisMaximum();
                    }
                });
                LineData data = new LineData();
                data.addDataSet(lowSet);
                data.addDataSet(highSet);
                data.setDrawValues(false);
                mLineChart.setData(data);
            } else {
                lowSet.setValues(lowPrices);
                if (highSet != null) {
                    highSet.setValues(lowPrices);
                } else {
                    highSet = new LineDataSet(highPrices, "价格上限");
                    highSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    highSet.setColor(Color.rgb(255, 241, 46));
                    highSet.setCircleColor(Color.rgb(244, 117, 117));
                    highSet.setCircleRadius(3f);
                    highSet.setLineWidth(2f);
                    highSet.setHighLightColor(Color.rgb(244, 117, 117));
                    highSet.setFillAlpha(100);
                    highSet.setDrawFilled(true);
                    highSet.setFillColor(Color.WHITE);
                    highSet.setFillFormatter(new IFillFormatter() {
                        @Override
                        public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                            return mLineChart.getAxisLeft().getAxisMaximum();
                        }
                    });
                    mLineChart.getData().addDataSet(highSet);
                }
            }
        }

        mLineChart.getData().setDrawValues(false);
        ViewPortHandler handler = mLineChart.getViewPortHandler();
        float xRange = mLineChart.getXRange();
        float minXRange = 1000L * 60 * 60 * 24;
        handler.setMaximumScaleX(Math.max(xRange / minXRange, 1));
        float yRange = mLineChart.getAxisLeft().mAxisRange;
        handler.setMaximumScaleY(Math.max(yRange / 6, 1));
        handler.setMinimumScaleY(0);

        yRangeFlag[1] = true;
        setYRange();
    }

    private boolean[] yRangeFlag = new boolean[2];

    /**
     * 设置 Y 轴范围, 默认的范围不会包含参考线, 导致参考线无法显示, 需要手动进行计算以合理显示
     */
    private void setYRange() {
        if (!yRangeFlag[0] || !yRangeFlag[1]) return;
        YAxis yAxis = mLineChart.getAxisLeft();
        float min = yAxis.getAxisMinimum();
        float max = yAxis.getAxisMaximum();
        float newMin = min;
        float newMax = max;
        List<LimitLine> limitLines = yAxis.getLimitLines();
        for (LimitLine limitLine : limitLines) {
            float limit = limitLine.getLimit();
            if (limit < newMin) {
                newMin = Math.max(limit - (newMax - limit) / 4, 0);
            } else if (limit > newMax) {
                newMax = limit + (limit - newMin) / 4;
            }
        }
        if (newMin != min || newMax != max) {
            newMin = Math.round(Math.max(Math.min(newMin, min - (newMax - max)), 0));
            newMax = Math.round(Math.max(newMax, max + (min - newMin)));
            yAxis.setAxisMinimum(newMin);
            yAxis.setAxisMaximum(newMax);

            ViewPortHandler handler = mLineChart.getViewPortHandler();
            float yRange = mLineChart.getAxisLeft().mAxisRange;
            handler.setMaximumScaleY(Math.max(yRange / 6, 1));
            handler.setMinimumScaleY(0);
        }
    }
}
