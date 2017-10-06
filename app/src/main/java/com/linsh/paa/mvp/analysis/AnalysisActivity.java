package com.linsh.paa.mvp.analysis;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.linsh.lshapp.common.base.BaseViewActivity;
import com.linsh.lshutils.module.SimpleDate;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.paa.R;

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

        //获取此图表的x轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(true); // 设置轴启用或禁用 如果禁用以下的设置全部不生效
        xAxis.setDrawAxisLine(true); // 是否绘制轴线
        xAxis.setDrawGridLines(true); // 设置x轴上每个点对应的线
        xAxis.setDrawLabels(true); // 绘制标签  指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置x轴的显示位置
        xAxis.enableGridDashedLine(10f, 10f, 0f); // 设置竖线的显示样式为虚线
        xAxis.setAvoidFirstLastClipping(true); // 图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setLabelRotationAngle(10f);//设置x轴标签的旋转角度
        xAxis.setSpaceMin(1000L * 60 * 60 * 24);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return new SimpleDate((long) value)
                        .getNormalizedString(false);
            }
        });

        //获取右边的轴线
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false); // 设置图表右边的y轴禁用
        //获取左边的轴线
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.enableGridDashedLine(10f, 10f, 0f); // 设置网格线为虚线效果
        leftAxis.setDrawZeroLine(false); // 是否绘制0所在的网格线
        leftAxis.setAxisMinimum(0);
        leftAxis.setSpaceMin(1);
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
//        lineChart.getXAxis().setAxisMinimum(lowPrices.get(0).getX() - 1000L * 60 * 60 * 24);

        LineDataSet lowSet = null;
        LineDataSet highSet = null;
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() == 1) {
            lowSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
        } else if (lineChart.getData() != null && lineChart.getData().getDataSetCount() == 2) {
            lowSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            highSet = (LineDataSet) lineChart.getData().getDataSetByIndex(1);
        }
        if (lowSet == null) {
            lowSet = new LineDataSet(lowPrices, "价格");
            lowSet.setColor(Color.BLACK);
            lowSet.setCircleColor(Color.BLACK);
            lowSet.setLineWidth(1f); // 设置线宽
            lowSet.setCircleRadius(3f); // 设置焦点圆心的大小
            lowSet.enableDashedHighlightLine(10f, 5f, 0f); // 点击后的高亮线的显示样式
            lowSet.setHighlightLineWidth(2f); // 设置点击交点后显示高亮线宽
            lowSet.setHighlightEnabled(true); // 是否禁用点击高亮线
            lowSet.setHighLightColor(Color.RED); // 设置点击交点后显示交高亮线的颜色
            lowSet.setValueTextSize(9f); // 设置显示值的文字大小
            lowSet.setDrawFilled(false); // 设置禁用范围背景填充
            LineData lowData = new LineData();
            lowData.addDataSet(lowSet);
            lineChart.setData(lowData);
        } else {
            lowSet.setValues(lowPrices);
        }
        if (highSet == null && highPrices != null) {
            highSet = new LineDataSet(highPrices, "价格上限");
            highSet.setColor(Color.BLACK);
            highSet.setCircleColor(Color.BLACK);
            highSet.setLineWidth(1f); // 设置线宽
            highSet.setCircleRadius(3f); // 设置焦点圆心的大小
            highSet.enableDashedHighlightLine(10f, 5f, 0f); // 点击后的高亮线的显示样式
            highSet.setHighlightLineWidth(2f); // 设置点击交点后显示高亮线宽
            highSet.setHighlightEnabled(true); // 是否禁用点击高亮线
            highSet.setHighLightColor(Color.RED); // 设置点击交点后显示交高亮线的颜色
            highSet.setValueTextSize(9f); // 设置显示值的文字大小
            highSet.setDrawFilled(false); // 设置禁用范围背景填充
            lineChart.getData().addDataSet(highSet);
        } else if (highSet != null && highPrices != null) {
            highSet.setValues(lowPrices);
        } else if (highSet != null) {
            lineChart.getData().removeDataSet(1);
        }
    }
}
