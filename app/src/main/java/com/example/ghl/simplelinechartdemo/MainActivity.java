package com.example.ghl.simplelinechartdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SwitchButton mSwitchButton;
    private SimpleLineChartView mLineChart;

    private int flag = 30;//7代表周，30代表月；
    private List<String> data = new ArrayList<>();//行驶距离的集合
    private List<String> xItem = new ArrayList<>();//X轴数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwitchButton = (SwitchButton) findViewById(R.id.mSwitchButton);
        mLineChart = (SimpleLineChartView) findViewById(R.id.mLineChart);

        setListener();
        loadData();
    }

    private void loadData() {
        setData();
        mLineChart.setXItem(xItem);
        mLineChart.setData(data);
    }


    private void setListener() {

        mSwitchButton.setOnSwitchListener(new OnSwitchListener() {
            @Override
            public void onSwitchChange() {
                if (mSwitchButton.getCurrentStatus() == SwitchButton.OPEN) {
                    flag = 30;
                    setData();
                    mLineChart.setXItem(xItem);
                    mLineChart.setData(data);

                } else if (mSwitchButton.getCurrentStatus() == SwitchButton.CLOSE) {
                    flag = 7;
                    setData();
                    mLineChart.setXItem(xItem);
                    mLineChart.setData(data);
                }
            }
        });
    }

    private void setData() {
        xItem.clear();
        data.clear();

        for (int i = 0; i < flag; i++) {
            xItem.add("" + (i + 1));
        }

        Random rand = new Random();
        for (int i = 0; i < flag; i++) {
            data.add("" + rand.nextInt(1000));
        }
    }

}
