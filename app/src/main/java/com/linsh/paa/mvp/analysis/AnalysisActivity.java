package com.linsh.paa.mvp.analysis;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.linsh.lshapp.common.base.BaseToolbarActivity;
import com.linsh.lshutils.module.SimpleDate;
import com.linsh.lshutils.utils.Basic.LshLogUtils;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.paa.R;
import com.linsh.paa.view.MyMarkerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AnalysisActivity extends BaseToolbarActivity<AnalysisContract.Presenter>
        implements AnalysisContract.View {

    private LineChart mLineChart;

    @Override
    protected String getToolbarTitle() {
        return "价格分析";
    }

    @Override
    protected AnalysisContract.Presenter initPresenter() {
        return new AnalysisPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_analysis;
    }

    @Override
    protected void initView() {
        mLineChart = (LineChart) findViewById(R.id.lc_analysis_chart);

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
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });
    }

    @Override
    public String getItemId() {
        return LshActivityUtils.getStringExtra(this);
    }

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
                lowSet.setColor(Color.rgb(255, 241, 46));
                lowSet.setCircleColor(Color.rgb(244, 117, 117));
                lowSet.setCircleRadius(3f);
                lowSet.setLineWidth(2f);
                lowSet.setHighLightColor(Color.rgb(244, 117, 117));
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
                lowSet.setFillAlpha(255);
                lowSet.setDrawFilled(true);
                lowSet.setFillColor(Color.WHITE);
                lowSet.setHighLightColor(Color.rgb(244, 117, 117));
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
                highSet.setFillAlpha(255);
                highSet.setDrawFilled(true);
                highSet.setFillColor(Color.WHITE);
                highSet.setHighLightColor(Color.rgb(244, 117, 117));
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
                    highSet.setFillAlpha(255);
                    highSet.setDrawFilled(true);
                    highSet.setFillColor(Color.WHITE);
                    highSet.setHighLightColor(Color.rgb(244, 117, 117));
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
    }
}
