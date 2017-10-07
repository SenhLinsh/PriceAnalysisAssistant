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
import com.linsh.lshapp.common.base.BaseViewActivity;
import com.linsh.lshutils.module.SimpleDate;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.paa.R;
import com.linsh.paa.view.MyMarkerView;

import java.util.ArrayList;

public class AnalysisActivity extends BaseViewActivity<AnalysisContract.Presenter>
        implements AnalysisContract.View {

    private LineChart lineChart;

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
        lineChart = (LineChart) findViewById(R.id.lc_analysis_chart);

        lineChart.getDescription().setEnabled(false);
        lineChart.setGridBackgroundColor(Color.argb(150, 51, 181, 229));
        lineChart.setDrawGridBackground(true);
        lineChart.setPinchZoom(false);
        lineChart.setDrawBorders(true);
        lineChart.setBorderColor(Color.argb(50, 0, 0, 0));
        Legend l = lineChart.getLegend();
        l.setEnabled(false);

        MyMarkerView mv = new MyMarkerView(this);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);


        //获取此图表的x轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return new SimpleDate((long) value)
                        .getNormalizedString(false);
            }
        });

        //获取右边的轴线
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        //获取左边的轴线
        YAxis leftAxis = lineChart.getAxisLeft();
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
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() == 1) {
            lowSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
        } else if (lineChart.getData() != null && lineChart.getData().getDataSetCount() == 2) {
            lowSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            highSet = (LineDataSet) lineChart.getData().getDataSetByIndex(1);
        }
        if (lowSet == null) {
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
                    return lineChart.getAxisLeft().getAxisMinimum();
                }
            });
            LineData data = new LineData();
            data.addDataSet(lowSet);
            data.setDrawValues(false);
            lineChart.setData(data);
        } else {
            lowSet.setValues(lowPrices);
        }
        if (highSet == null && highPrices != null) {
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
                    return lineChart.getAxisLeft().getAxisMaximum();
                }
            });
            lineChart.getData().addDataSet(highSet);
            lineChart.getData().setDrawValues(false);
        } else if (highSet != null && highPrices != null) {
            highSet.setValues(lowPrices);
        } else if (highSet != null) {
            lineChart.getData().removeDataSet(1);
        }
    }
}
