package com.fusi.fishingalarm.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.utils.UIUtils;

/**
 * Created by dev on 2016/4/11.
 */
public class DashedLine extends View {
    private Paint paint = null;
    private Path path = null;
    private PathEffect pe = null;

    public DashedLine(Context context) {
        this(context, null);
    }

    public DashedLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        //通过R.styleable.dashedline获取我们在attrs.xml中定义的
        // <declare-styleable name="dashedline"> TypedArray
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.dashedline);
        //我们在attrs.xml中<declare-styleable name="dashedline">节点下
        // 增加了<attr name="lineColor" format="color" />
        // 表示这个属性名为lineColor类型为color。当用户在布局文件中对它有设定值时，
        // 可通过TypedArray获得它的值当用户无设置值是采用默认值0XFF00000
        int lineColor = array.getColor(R.styleable.dashedline_lineColor, 0xff000000);
        int dashWidth=array.getInteger(R.styleable.dashedline_dashWidth, 2);
        int dashGap=array.getInteger(R.styleable.dashedline_dashGap,2);
        array.recycle();
        this.paint = new Paint();
        this.path = new Path();
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setColor(lineColor);
        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(UIUtils.dip2px(2));
        float[] arrayOfFloat = new float[4];//控制虚线间距，密度
        arrayOfFloat[0] = (float) UIUtils.dip2px(dashWidth);
        arrayOfFloat[1] = (float) UIUtils.dip2px(dashGap);
        arrayOfFloat[2] = (float) UIUtils.dip2px(dashWidth);
        arrayOfFloat[3] = (float) UIUtils.dip2px(dashGap);
        this.pe = new DashPathEffect(arrayOfFloat, UIUtils.dip2px(1));//设置虚线的间隔和点的长度

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.path.moveTo(0.0f, 0.0f);
        this.path.lineTo(getMeasuredWidth(), 0.0f);
        this.paint.setPathEffect(this.pe);
        canvas.drawPath(this.path, this.paint);
    }
}


