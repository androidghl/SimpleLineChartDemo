package com.example.ghl.simplelinechartdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghl on 2017/5/23.
 */

public class SimpleLineChartView extends View {

    private int flag = 0;
    private int maxMileage = 0;
    private Paint dottedPaint;
    private Paint axisPaint;
    private Paint pointPaint;
    private Paint linePaint;
    private Paint p1;
    private Paint p2;

    //View 的宽和高
    private int mWidth, mHeight;
    //X轴偏移量
    private static final int XOFFSET = 50;
    //Y轴字体的大小
    private static final int YAXISFONTSIZE = 36;
    //X轴坐标字体的大小
    private static final int XAXISFONTSIZE = 36;
    //点的半径
    private static final float POINTRADIUS = 5;
    //线条的宽度
    private static final float STROKEWIDTH = 4.0f;
    //线的颜色
    private int mLineColor = Color.parseColor("#68cffa");
    //点的集合
    private List<String> mPointMap;
    //X轴的文字
    private List<String> mXAxis = new ArrayList<>();
    //Y轴的文字
    private List<String> mYAxis = new ArrayList<>();
    //没有数据的时候的内容
    private String mNoDataMsg = "没有数据！";
    //标准线数值
    private int levelLineNum = 0;

    private List<Point> scorePoints = new ArrayList<>();

    public SimpleLineChartView(Context context) {
        this(context, null);
        init();
    }

    public SimpleLineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public SimpleLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //画虚线
        dottedPaint = new Paint();
        dottedPaint.reset();
        dottedPaint.setStyle(Paint.Style.STROKE);
        dottedPaint.setColor(Color.parseColor("#c7c7c8"));
        dottedPaint.setStrokeWidth(2);
        PathEffect effects = new DashPathEffect(new float[]{10, 20}, 1);
        dottedPaint.setAntiAlias(true);
        dottedPaint.setPathEffect(effects);

        //画坐标线的轴
        axisPaint = new Paint();
        axisPaint.setTextSize(YAXISFONTSIZE);
        axisPaint.setColor(Color.parseColor("#c7c7c8"));//坐标轴上文字的颜色
        axisPaint.setStrokeWidth(4);

        //画点
        pointPaint = new Paint();
        pointPaint.setColor(mLineColor);
        pointPaint.setStyle(Paint.Style.FILL);

        //折线
        linePaint = new Paint();
        linePaint.setColor(mLineColor);
        linePaint.setAntiAlias(true);
        //设置线条宽度
        linePaint.setStrokeWidth(STROKEWIDTH);

        //写X轴坐标
        p1 = new Paint();
        p1.setTextSize(XAXISFONTSIZE);
        p1.setColor(Color.parseColor("#bdbdbd"));
        p1.setTypeface(Typeface.DEFAULT);
        p1.setAntiAlias(true);//防止边缘锯齿
        p1.setFilterBitmap(true);

        //写字(levelLineNum的值)
        p2 = new Paint();
        p2.setTextSize(XAXISFONTSIZE);
        p2.setColor(Color.parseColor("#bdbdbd"));
        p2.setTypeface(Typeface.DEFAULT);
        p2.setAntiAlias(true);//防止边缘锯齿
        p2.setFilterBitmap(true);
    }

    private void initData() {

        if (mPointMap == null || mPointMap.size() == 0) {
            return;
        }

        for (int j = 0; j < 100; j++) {
            mYAxis.add("");
        }

        //计算x轴 刻度间距
        int xInterval = (mWidth - XOFFSET * 2) / (mXAxis.size() - 1);

        //x轴的刻度集合
        int[] xPoints = new int[mXAxis.size()];
        for (int i = 0; i < mXAxis.size(); i++) {
            xPoints[i] = i * xInterval + XOFFSET;
        }

        scorePoints.clear();
        maxMileage = 0;
        for (int i = 0; i < mPointMap.size(); i++) {
            //找最大公里数及其下标
            if (maxMileage < Integer.parseInt(mPointMap.get(i))) {
                maxMileage = Integer.parseInt(mPointMap.get(i));
            }
        }
        for (int i = 0; i < mPointMap.size(); i++) {
            Point point = new Point();
            point.x = xPoints[i];
            if (maxMileage > levelLineNum) {
                point.y = (mHeight - YAXISFONTSIZE - 20 - mHeight / 10) * (maxMileage - YCoord(mPointMap.get(i))) / maxMileage + mHeight / 10;
            } else {
                point.y = (mHeight - YAXISFONTSIZE - 20 - mHeight / 10) * (levelLineNum - YCoord(mPointMap.get(i))) / levelLineNum + mHeight / 10;
            }
            scorePoints.add(point);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        initData();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPointMap == null || mPointMap.size() == 0) {
            int textLength = (int) axisPaint.measureText(mNoDataMsg);
            canvas.drawText(mNoDataMsg, mWidth / 2 - textLength / 2, mHeight / 2, axisPaint);
        } else {
            drawDottedLine(canvas);
            drawAxisLine(canvas);
            drawText(canvas);
            drawPoint(canvas);
            drawBrokenLine(canvas);
        }
    }

    /**
     * 绘制折线
     *
     * @param canvas
     */
    private void drawBrokenLine(Canvas canvas) {
        for (int i = 0; i < scorePoints.size(); i++) {
            if (i > 0) {
                canvas.drawLine(scorePoints.get(i - 1).x, scorePoints.get(i - 1).y, scorePoints.get(i).x, scorePoints.get(i).y, linePaint);
            }
        }
    }

    /**
     * 画数据点
     *
     * @param canvas
     */
    private void drawPoint(Canvas canvas) {
        for (int i = 0; i < scorePoints.size(); i++) {
            canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, POINTRADIUS, pointPaint);
        }
    }

    /**
     * 画坐标线
     *
     * @param canvas
     */
    private void drawAxisLine(Canvas canvas) {
        canvas.drawLine(36, mHeight - YAXISFONTSIZE - 20, mWidth - 30, mHeight - YAXISFONTSIZE - 20,
                axisPaint);//底部横线  mHeight - YAXISFONTSIZE-20为原点Y轴坐标
    }

    /**
     * 绘制文本
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {

        //画X轴坐标
        if (flag == 7) {
            for (int i = 0; i < mXAxis.size(); i++) {
                canvas.drawText(mXAxis.get(i), scorePoints.get(i).x - XAXISFONTSIZE / 3,
                        mHeight - YAXISFONTSIZE + 30 + 1, p1);
            }
        } else if (flag == 30) {
                canvas.drawText(mXAxis.get(29), scorePoints.get(29).x - XAXISFONTSIZE / 3,
                        mHeight - YAXISFONTSIZE + 30, p1);
                canvas.drawText(mXAxis.get(23), scorePoints.get(23).x - XAXISFONTSIZE / 3,
                        mHeight - YAXISFONTSIZE + 30, p1);
                canvas.drawText(mXAxis.get(17), scorePoints.get(17).x - XAXISFONTSIZE / 3,
                        mHeight - YAXISFONTSIZE + 30, p1);
                canvas.drawText(mXAxis.get(11), scorePoints.get(11).x - XAXISFONTSIZE / 3,
                        mHeight - YAXISFONTSIZE + 30, p1);
                canvas.drawText(mXAxis.get(5), scorePoints.get(5).x - XAXISFONTSIZE / 3,
                        mHeight - YAXISFONTSIZE + 30, p1);
                canvas.drawText(mXAxis.get(0), scorePoints.get(0).x - XAXISFONTSIZE / 3,
                        mHeight - YAXISFONTSIZE + 30, p1);
        }

        //画levelLineNum km文字
        int xuTextY;
        if (maxMileage > levelLineNum) {
            xuTextY = (mHeight - YAXISFONTSIZE - 20 - mHeight / 10) * (maxMileage - levelLineNum) / maxMileage + mHeight / 10 + (int) (p2.getTextSize() / 2);
        } else {
            xuTextY = mHeight / 10 + (int) (p2.getTextSize() / 2);
        }
        canvas.drawText(levelLineNum + "km", mWidth - 120 + 12, xuTextY, p2);

    }

    /**
     * 画虚线
     *
     * @param canvas
     */
    private void drawDottedLine(Canvas canvas) {
        Path path = new Path();
        int xuY;
        if (maxMileage > levelLineNum) {
            xuY = (mHeight - YAXISFONTSIZE - 20 - mHeight / 10) * (maxMileage - levelLineNum) / maxMileage + mHeight / 10;
        } else {
            xuY = mHeight / 10;
        }
        path.moveTo(36, xuY);
        path.lineTo(mWidth - 120 + 12, xuY);
        canvas.drawPath(path, dottedPaint);
    }


    private int YCoord(String y0) { //计算绘制时的Y坐标，无数据时返回-2001
        int y;
        try {
            y = Integer.parseInt(y0);
        } catch (Exception e) {
            return -2001;    //出错则返回-2001
        }
        return y;
    }

    /**
     * 设置数据
     *
     * @param data
     */
    public void setData(List<String> data) {
        mPointMap = data;
        flag = data.size();
        initData();
        invalidate();
    }

    /**
     * 设置X轴文字
     *
     * @param xItem
     */
    public void setXItem(List<String> xItem) {
        mXAxis = xItem;
    }

    /**
     * 设置标准线数值
     *
     * @param levelLineNum
     */
    public void setLevelLineNum(int levelLineNum) {
        this.levelLineNum = levelLineNum;
    }

}
