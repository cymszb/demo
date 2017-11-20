package com.zglue.zgluesports.widget;



import com.zglue.zgluesports.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageView;



public class CircleImageView extends AppCompatImageView {

    private Context mContext;
    private int mBorderThickness = 0;
    private int mBorderInsideColor = 0;
    private int mBorderOutsideColor = 0;
    private int mBorderWidth = 0;
    private int mBorderHeight = 0;

    private int defaultColor = 0xFFFFFFFF;


    public CircleImageView(Context context) {
        super(context);
        mContext = context;
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setRoundAttribute(attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        setRoundAttribute(attrs);

    }


    private void setRoundAttribute(AttributeSet attrs){

        TypedArray myAttrs = mContext.obtainStyledAttributes(attrs,
                R.styleable.roundedimageview);
        mBorderInsideColor = myAttrs.getColor(
                R.styleable.roundedimageview_border_inside_color,defaultColor);

        mBorderOutsideColor = myAttrs.getColor(
                R.styleable.roundedimageview_border_outside_color, defaultColor);
        mBorderThickness = myAttrs.getDimensionPixelSize(
                R.styleable.roundedimageview_border_thickness,0);

        myAttrs.recycle();
    }



    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }


        if (getWidth() == 0 || getHeight()==0) {
            return;
        }

        if(drawable.getClass() == NinePatchDrawable.class){
            return;
        }


        this.measure(0, 0);

        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        if (mBorderWidth == 0) {
            mBorderWidth = getWidth();
        }

        if (mBorderHeight == 0) {
            mBorderHeight = getHeight();
        }

        int radius = 0;

        if (mBorderInsideColor != defaultColor
                && mBorderOutsideColor != defaultColor) {
            radius = (mBorderWidth < mBorderHeight ? mBorderWidth
                    : mBorderHeight) / 2 - 2 * mBorderThickness;

            drawCircleBorder(canvas, radius + mBorderThickness / 2,
                    mBorderInsideColor);

            drawCircleBorder(canvas, radius + mBorderThickness
                    + mBorderThickness / 2, mBorderOutsideColor);
        } else if (mBorderInsideColor != defaultColor
                && mBorderOutsideColor == defaultColor) {// 定义画一个边框
            radius = (mBorderWidth < mBorderHeight ? mBorderWidth
                    : mBorderHeight) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2,
                    mBorderInsideColor);
        } else if (mBorderInsideColor == defaultColor
                && mBorderOutsideColor != defaultColor) {// 定义画一个边框
            radius = (mBorderWidth < mBorderHeight ? mBorderWidth
                    : mBorderHeight) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2,
                    mBorderOutsideColor);
        } else {
            radius = (mBorderWidth < mBorderHeight ? mBorderWidth
                    : mBorderHeight) / 2;
        }


        Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);
        canvas.drawBitmap(roundBitmap, mBorderWidth/2 - radius,
                mBorderHeight/2 - radius, null);
    }


    private void drawCircleBorder(Canvas canvas,int radius,int color) {

        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mBorderThickness);
        canvas.drawCircle(mBorderWidth/2, mBorderHeight/2, radius, paint);
    }



    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;

        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;

            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else {
            squareBitmap = bmp;
        }

        if (squareBitmap.getWidth() != diameter
                || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
                    diameter, true);

        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        // bitmap回收(recycle导致在布局文件XML看不到效果)
        // bmp.recycle();
        // squareBitmap.recycle();
        // scaledSrcBmp.recycle();
        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }



}