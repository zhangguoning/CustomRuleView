package cn.zgn.customrule;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ning on 2016/8/1.
 * Summary : 自定义刻度尺
 */
public class CustomRule extends View {

    private int mWidth, mHeight;

    private int sharp = 7;
    private int markTextOffsetY = 6;
    private int hintTextWidth = 140;
    private int cursorWidth;

    private int hintTextHeight = 35;
    private int ruleHeight = 40;
    private int cursorHeight = 30;

    private String hintText;


    private int backGroundColor = 0xffffffff;

    private int ruleStartX, ruleEndX, ruleStartY, ruleEndY;

    private float leftCursorMiddleX = 0, rightCursorMiddleX = 999999;

    private float spacingValue = 2;//最小间距

    private int leftCursorEndX = -1;
    private int rightCursorStartX = -1;
    private float markLineStartX;
    private float markLineEndX;
    private float unitWidth;
    Rect rectCursor; // 游标部分
    Rect rectHintText; // 提示文本部分

    private OnScaleListener onScaleListener;

    public interface OnScaleListener {
        void onScaleStart(float min, float max);

        void onScaling(float min, float max);

        void onScaleEnd(float min, float max);
    }

    public CustomRule(Context context) {
        super(context);

    }

    public CustomRule(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRule(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        float sum = hintTextHeight + ruleHeight + cursorHeight;

        hintTextHeight = (int) (hintTextHeight / sum * height);
        ruleHeight = (int) (ruleHeight / sum * height);
        cursorHeight = (int) (cursorHeight / sum * height);

        cursorWidth = (int) (cursorHeight * 2 / 3.0f);

        rectHintText = new Rect(getLeft(), 0, getRight(), hintTextHeight); // 游标部分
        rectCursor = new Rect(getLeft(), hintTextHeight + ruleHeight, getRight(), hintTextHeight + ruleHeight + cursorHeight); // 游标部分
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
        canvas.drawColor(backGroundColor);
        drawRule(canvas);
        drawLeftCursor(canvas, leftCursorMiddleX);
        drawRightCursor(canvas, rightCursorMiddleX);
        drawCheckedLine(canvas);
        drawHintText(canvas, hintText);

    }

    private void drawHintText(Canvas canvas, String hintText) {

        //绘制多边形区域
        Paint paint = new Paint();
        paint.setColor(0xffe3f5f7);
        paint.setStrokeWidth((float) 1);
        Path path = new Path();

        int x = (int) (rightCursorMiddleX + leftCursorMiddleX) / 2;
        path.moveTo(x, hintTextHeight);// 最低点位置 A
        path.lineTo(x - sharp, hintTextHeight - sharp); // B
        path.lineTo(x - hintTextWidth / 2.0f, hintTextHeight - sharp); // 左下角位置 C
        path.lineTo(x - hintTextWidth / 2.0f, 0);//左上角 D
        path.lineTo(x + hintTextWidth / 2.0f, 0); //右上角 E
        path.lineTo(x + hintTextWidth / 2.0f, hintTextHeight - sharp); //右下角 F
        path.lineTo(x + sharp, hintTextHeight - sharp); //I
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, paint);

        //绘制多边形描边
        Paint paint2 = new Paint();
        paint2.setColor(0xff17abc1);
        canvas.drawLine(x, hintTextHeight, x - sharp, hintTextHeight - sharp, paint2);//AB
        canvas.drawLine(x - sharp, hintTextHeight - sharp, x - hintTextWidth / 2, hintTextHeight - sharp, paint2);//BC
        canvas.drawLine(x - hintTextWidth / 2, hintTextHeight - sharp, x - hintTextWidth / 2, 0, paint2);//CD
        canvas.drawLine(x - hintTextWidth / 2, hintTextHeight - sharp, x - hintTextWidth / 2, 0, paint2);//DE
        canvas.drawLine(x - hintTextWidth / 2, 0, x + hintTextWidth / 2, 0, paint2);//DE
        canvas.drawLine(x + hintTextWidth / 2, 0, x + hintTextWidth / 2, hintTextHeight - sharp, paint2);//EF
        canvas.drawLine(x + hintTextWidth / 2, hintTextHeight - sharp, x + sharp, hintTextHeight - sharp, paint2);//FI
        canvas.drawLine(x + sharp, hintTextHeight - sharp, x, hintTextHeight, paint2);//IA
        String text = "";
        //绘制文字
        if (TextUtils.isEmpty(hintText)) {
            int low = getLeftCursorIntValue();
            int high = getRightCursorIntValue();
            String low_s, high_s = "";

            if (high > 100) {
                high_s = "100+";
            } else {
                high_s = high + "";
            }

            text = low + "~" + high_s ;
//            text+="万";
            if (low > 100) {
                text = "100+";
//                text+="万";
            }
        } else {
            text = hintText;
        }


        int textSize = 20;
        paint2.setTextSize(textSize);
        Rect rect = new Rect();
        paint2.getTextBounds(text, 0, text.length(), rect);
        int textWidth = rect.width();
        float startX = x - textWidth / 2;
        float startY = hintTextHeight / 2 + sharp / 2;
        canvas.drawText(text, startX, startY, paint2);

    }

    private void drawRule(Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(0xff999999);
        paint.setStrokeWidth((float) 3);
        int startX = hintTextWidth / 2;
        int endX = mWidth - hintTextWidth / 2;
        int startY = hintTextHeight;

        int textHight = ruleHeight / 2;
        int markLineHight = ruleHeight / 2;

        //绘制刻度长横线
        ruleStartX = startX;
        ruleStartY = startY + textHight + markLineHight;
        ruleEndX = endX;
        ruleEndY = startY + textHight + markLineHight;
        canvas.drawLine(ruleStartX, ruleStartY, ruleEndX, ruleEndY, paint);
        //绘制刻度竖短线
        Paint markLinePaint = new Paint();
        markLinePaint.setColor(0xff999999);
        markLinePaint.setStrokeWidth((float) 1);
        markLinePaint.setTextSize(20);
        markLineStartX = ruleStartX + 30;
        markLineEndX = ruleEndX - 30;
        float markLineStartX_temp = markLineStartX;
        int longLineHight = markLineHight, shortLineHight = markLineHight * 3 / 4;
        unitWidth = (markLineEndX - markLineStartX) / 45.0f;

        for (int i = 0; i <= 45; i++) {
            if (i % 5 == 0) {
                canvas.drawLine(markLineStartX_temp, ruleStartY, markLineStartX_temp, ruleStartY - longLineHight, markLinePaint);
                float x = markLineStartX_temp;
                float y = ruleStartY - longLineHight;
                if (i <= 30) {//刻度5 -35 位置
                    String markText = (i + 5) + "";
                    Rect rect = new Rect();
                    markLinePaint.getTextBounds(markText, 0, markText.length(), rect);
                    int textWidth = rect.width();
                    canvas.drawText(markText, x - textWidth / 2, y - markTextOffsetY, markLinePaint);
                }
                if (i == 35) { //刻度50位置
                    String markText = "50";
                    Rect rect = new Rect();
                    markLinePaint.getTextBounds(markText, 0, markText.length(), rect);
                    int textWidth = rect.width();
                    canvas.drawText(markText, x - textWidth / 2, y - markTextOffsetY, markLinePaint);
                }
                if (i == 40) { //刻度70位置
                    String markText = "70";
                    Rect rect = new Rect();
                    markLinePaint.getTextBounds(markText, 0, markText.length(), rect);
                    int textWidth = rect.width();
                    canvas.drawText(markText, x - textWidth / 2, y - markTextOffsetY, markLinePaint);
                }
                if (i == 45) { //刻度100位置
                    String markText = "100";
                    Rect rect = new Rect();
                    markLinePaint.getTextBounds(markText, 0, markText.length(), rect);
                    int textWidth = rect.width();
                    canvas.drawText(markText, x - textWidth / 2, y - markTextOffsetY, markLinePaint);
                }
            } else {
                canvas.drawLine(markLineStartX_temp, ruleStartY, markLineStartX_temp, ruleStartY - shortLineHight, markLinePaint);
            }
            markLineStartX_temp += unitWidth;
        }

    }

    private void drawLeftCursor(Canvas canvas, float x) {

        //不许和右游标重合
//            if(x>rightCursorMiddleX){
//                float leftValue = getRightCursorFloatValue() - spacingValue;
//                x = getLeftCursorPositionByValue(leftValue);
//            }

        //不许脱离刻度尺
        if (x < ruleStartX) {
            x = ruleStartX;
        }
        if (x > ruleEndX) {
            x = ruleEndX;
        }
        leftCursorMiddleX = x;
        Paint paint = new Paint();
        paint.setColor(0xff6ed6dc);
        paint.setStrokeWidth((float) 1);
        Path path = new Path();
        path.moveTo(x, ruleStartY);// 最高点位置 A
        path.lineTo(x - cursorWidth / 2, ruleStartY + cursorWidth / 2);//左肩位置 B
        path.lineTo(x - cursorWidth / 2, ruleStartY + cursorHeight);//左下位置 C
        path.lineTo(x + cursorWidth / 2, ruleStartY + cursorHeight);//右下位置 D
        path.lineTo(x + cursorWidth / 2, ruleStartY + cursorWidth / 2);//右肩位置 E
        path.close();
        canvas.drawPath(path, paint);

        //描边
        paint.setColor(0xff17abc1);
        canvas.drawLine(x, ruleStartY, x - cursorWidth / 2, ruleStartY + cursorWidth / 2, paint);//AB
        canvas.drawLine(x - cursorWidth / 2, ruleStartY + cursorWidth / 2, x - cursorWidth / 2, ruleStartY + cursorHeight, paint);//BC
        canvas.drawLine(x - cursorWidth / 2, ruleStartY + cursorHeight, x + cursorWidth / 2, ruleStartY + cursorHeight, paint);//CD
        canvas.drawLine(x + cursorWidth / 2, ruleStartY + cursorHeight, x + cursorWidth / 2, ruleStartY + cursorWidth / 2, paint);//DE
        canvas.drawLine(x + cursorWidth / 2, ruleStartY + cursorWidth / 2, x, ruleStartY, paint);//EA

        leftCursorEndX = (int) (x + cursorWidth / 2);
    }

    private void drawRightCursor(Canvas canvas, float x) {
        //不许脱离刻度尺
        if (x < ruleStartX) {
            x = ruleStartX;
        }
        if (x > ruleEndX) {
            x = ruleEndX;
        }
        rightCursorMiddleX = x;
        Paint paint = new Paint();
        paint.setColor(0xff6ed6dc);
        paint.setStrokeWidth((float) 1);
        Path path = new Path();
        path.moveTo(x, ruleStartY);// 最高点位置 A
        path.lineTo(x - cursorWidth / 2, ruleStartY + cursorWidth / 2);//左肩位置 B
        path.lineTo(x - cursorWidth / 2, ruleStartY + cursorHeight);//左下位置 C
        path.lineTo(x + cursorWidth / 2, ruleStartY + cursorHeight);//右下位置 D
        path.lineTo(x + cursorWidth / 2, ruleStartY + cursorWidth / 2);//右肩位置 E
        path.close();
        canvas.drawPath(path, paint);
        //描边
        paint.setColor(0xff17abc1);
        canvas.drawLine(x, ruleStartY, x - cursorWidth / 2, ruleStartY + cursorWidth / 2, paint);//AB
        canvas.drawLine(x - cursorWidth / 2, ruleStartY + cursorWidth / 2, x - cursorWidth / 2, ruleStartY + cursorHeight, paint);//BC
        canvas.drawLine(x - cursorWidth / 2, ruleStartY + cursorHeight, x + cursorWidth / 2, ruleStartY + cursorHeight, paint);//CD
        canvas.drawLine(x + cursorWidth / 2, ruleStartY + cursorHeight, x + cursorWidth / 2, ruleStartY + cursorWidth / 2, paint);//DE
        canvas.drawLine(x + cursorWidth / 2, ruleStartY + cursorWidth / 2, x, ruleStartY, paint);//EA

        rightCursorStartX = (int) (x + cursorWidth / 2);
    }

    public float getLeftCursorFloatValue() {
//
        if (leftCursorMiddleX < ruleStartX) {
            return 0;
        }
        if (leftCursorMiddleX > markLineEndX) {
            return 999;
        }
        float checkedLineLength = leftCursorMiddleX - markLineStartX;
        float markSum = checkedLineLength / unitWidth;
//        Log.w("leftCursorMiddleX =" ,leftCursorMiddleX+"");
//        Log.w("markLineStartX =" ,markLineStartX+"");
        if (leftCursorMiddleX <= markLineStartX) {
            checkedLineLength = leftCursorMiddleX - ruleStartX;
            float unitWidth = (markLineStartX - ruleStartX) / 5.0f;
            markSum = Math.abs(checkedLineLength / unitWidth);//重新计算 markSum
//            Log.i("LeftCursor小于5",markSum+"");
            return markSum;
        } else if (markSum <= 30) {
//            Log.i("LeftCursor5-35之间",markSum+"");
            return markSum + 5;
        } else if (markSum > 30 && markSum <= 35) {//35-50之间
//            Log.i("LeftCursor35-50之间",markSum+"");
            return ((markSum - 30) * 3) + 35;
        } else if (markSum > 35 && markSum <= 40) {//50 -70 之间
//            Log.i("LeftCursor50 -70 之间",markSum+"");
            return (markSum - 35) * 4 + 50;
        } else if (markSum > 40 && markSum <= 45) {//70 -100 之间
//            Log.i("LeftCursor70 -100 之间",markSum+"");
            return (markSum - 40) * 6 + 70;
        }
        return 0;
    }

    public int getRightCursorIntValue() {
        return Math.round(getRightCursorFloatValue());
    }

    public float getRightCursorFloatValue() {

        if (rightCursorMiddleX < ruleStartX) {

            return 0;
        }
        if (rightCursorMiddleX > markLineEndX) {
            return 999;
        }
        float checkedLineLength = rightCursorMiddleX - markLineStartX;
        float markSum = checkedLineLength / unitWidth;
//        Log.i("markSum = ",markSum+"");
        if (rightCursorMiddleX <= markLineStartX) {
//            Log.i("RightCursor小于5",markSum+"");
            checkedLineLength = rightCursorMiddleX - ruleStartX;
            float unitWidth = (markLineStartX - ruleStartX) / 5.0f;
            markSum = Math.abs(checkedLineLength / unitWidth);//重新计算 markSum
            return markSum;

        } else if (markSum <= 30) {
            return markSum + 5;
        } else if (markSum > 30 && markSum <= 35) {//35-50之间
//            Log.i("RightCursor35-50之间",markSum+"");
            return ((markSum - 30) * 3) + 35;
        } else if (markSum > 35 && markSum <= 40) {//50 -70 之间
//            Log.i("RightCursor50 -70 之间",markSum+"");
            return (markSum - 35) * 4 + 50;
        } else if (markSum > 40 && markSum <= 45) {//70 -100 之间
//            Log.i("RightCursor70 -100 之间",markSum+"");
            return (markSum - 40) * 6 + 70;
        }
        return 0;
    }

    /**
     * 取得左游标的值，四舍五入取整
     *
     * @return
     */
    public int getLeftCursorIntValue() {
        return Math.round(getLeftCursorFloatValue());
    }


    private void drawCheckedLine(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(0xff17abc1);
        paint.setStrokeWidth((float) 3);
        canvas.drawLine(leftCursorMiddleX, ruleStartY, rightCursorMiddleX, ruleStartY, paint);
    }


    float x = 0;
    boolean leftCursorIsMoving = false;
    boolean rightCursorIsMoving = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (onScaleListener != null) {
                    onScaleListener.onScaleStart(getLeftCursorFloatValue(), getRightCursorFloatValue());
                }
                leftCursorIsMoving = false;
                rightCursorIsMoving = false;
                break;

            case MotionEvent.ACTION_MOVE:
                x = event.getX();

                if (x < leftCursorMiddleX) { //交由左游标处理
                    if (!rightCursorIsMoving) {
                        leftCursorMiddleX = x;
                        leftCursorIsMoving = true;
                    }

                }
                if (x > rightCursorMiddleX) { //交由右游标处理
                    if (!leftCursorIsMoving) {
                        rightCursorMiddleX = x;
                        rightCursorIsMoving = true;
                    }


                }
                float distance_left = Math.abs(x - leftCursorMiddleX);
                float distance_right = Math.abs(x - rightCursorMiddleX);

                if (distance_left < distance_right //距左游标更近 ,交由左游标处理
                        && isValidSection(x, true)) {
                    if (!rightCursorIsMoving) {
                        leftCursorMiddleX = x;
                        leftCursorIsMoving = true;
                    }
                }
                if (distance_left > distance_right
                        && isValidSection(x, false)) {
                    if (!leftCursorIsMoving) {
                        rightCursorMiddleX = x;
                        rightCursorIsMoving = true;
                    }
                }


                invalidate();
                if (onScaleListener != null) {
                    onScaleListener.onScaling(getLeftCursorFloatValue(), getRightCursorFloatValue());
                }
                break;

            case MotionEvent.ACTION_UP:
                if (onScaleListener != null) {
                    onScaleListener.onScaleEnd(getLeftCursorFloatValue(), getRightCursorFloatValue());
                }
                leftCursorIsMoving = false;
                rightCursorIsMoving = false;
                break;
        }

        return true;
    }

    /**
     *
     * @param isLeftCursor 当前是否对左游标进行判定
     * @param x
     */
    private boolean isValidSection(float x, boolean isLeftCursor) {
        if (isLeftCursor) {
            float v = getRightCursorFloatValue() - spacingValue;
            float cursorXByValue = getCursorXByValue(v);
            return x <= cursorXByValue;
        } else {
            float v = getLeftCursorFloatValue() + spacingValue;
            float cursorXByValue = getCursorXByValue(v);
            return x >= cursorXByValue;
        }
    }

    public void setOnScaleListener(OnScaleListener onScaleListener) {
        this.onScaleListener = onScaleListener;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
        invalidate(rectHintText);
    }

    public void setBackGroundColor(int backGroundColor) {
        this.backGroundColor = backGroundColor;
    }

    public void reset() {
        leftCursorMiddleX = 0;
        rightCursorMiddleX = 999999;
        invalidate();
    }

    private float getCursorXByValue(float value) {
        float cursorMiddleX = 0.0f;
        if (value < 0) {
            cursorMiddleX = 0;
        }
        if (value >= 0 && value <= 5) {
            float length = markLineStartX - ruleStartX;
            float unitWidth = length / 5.0f;
            cursorMiddleX = unitWidth * value + ruleStartX;
        }
        if (value > 5 && value <= 35) {
            cursorMiddleX = unitWidth * (value - 5) + markLineStartX;
        }
        if (value > 35 && value <= 50) {
            float before35 = unitWidth * 30 + markLineStartX;
            float after35 = (value - 35) * (unitWidth / 3.0f);
            cursorMiddleX = before35 + after35;
        }
        if (value > 50 && value <= 70) {
            float before50 = unitWidth * 35 + markLineStartX;
            float after50 = (value - 50) * (unitWidth / 4.0f);
            cursorMiddleX = before50 + after50;
        }
        if (value > 70 && value <= 100) {
            float before70 = unitWidth * 40 + markLineStartX;
            float after70 = (value - 70) * (unitWidth / 6.0f);
            cursorMiddleX = before70 + after70;
        }
        if (value > 100) {
            cursorMiddleX = 99999;
        }
        return cursorMiddleX;
    }

    public void setLeftCursorValue(float value) {
        if (value > getRightCursorFloatValue() - spacingValue) {
            value = getRightCursorFloatValue() - spacingValue;
        }

        if (value < 0) {
            leftCursorMiddleX = 0;
        }
        if (value >= 0 && value <= 5) {
            float length = markLineStartX - ruleStartX;
            float unitWidth = length / 5.0f;
            leftCursorMiddleX = unitWidth * value + ruleStartX;
        }
        if (value > 5 && value <= 35) {
            leftCursorMiddleX = unitWidth * (value - 5) + markLineStartX;
        }
        if (value > 35 && value <= 50) {
            float before35 = unitWidth * 30 + markLineStartX;
            float after35 = (value - 35) * (unitWidth / 3.0f);
            leftCursorMiddleX = before35 + after35;
        }
        if (value > 50 && value <= 70) {
            float before50 = unitWidth * 35 + markLineStartX;
            float after50 = (value - 50) * (unitWidth / 4.0f);
            leftCursorMiddleX = before50 + after50;
        }
        if (value > 70 && value <= 100) {
            float before70 = unitWidth * 40 + markLineStartX;
            float after70 = (value - 70) * (unitWidth / 6.0f);
            leftCursorMiddleX = before70 + after70;
        }
        if (value > 100) {
            leftCursorMiddleX = 99999;
        }
        invalidate();
        if (onScaleListener != null) {
            onScaleListener.onScaleEnd(getLeftCursorFloatValue(), getRightCursorFloatValue());
        }
    }

    public void setRightCursorValue(float value) {

        if (value < getLeftCursorFloatValue() + spacingValue) {
            value = getLeftCursorFloatValue() + spacingValue;
        }

        if (value < 0) {
            rightCursorMiddleX = 0;
        }
        if (value >= 0 && value <= 5) {
            float length = markLineStartX - ruleStartX;
            float unitWidth = length / 5.0f;
            rightCursorMiddleX = unitWidth * value + ruleStartX;
        }
        if (value > 5 && value <= 35) {
            rightCursorMiddleX = unitWidth * (value - 5) + markLineStartX;
        }
        if (value > 35 && value <= 50) {
            float before35 = unitWidth * 30 + markLineStartX;
            float after35 = (value - 35) * (unitWidth / 3.0f);
            rightCursorMiddleX = before35 + after35;
        }
        if (value > 50 && value <= 70) {
            float before50 = unitWidth * 35 + markLineStartX;
            float after50 = (value - 50) * (unitWidth / 4.0f);
            rightCursorMiddleX = before50 + after50;
        }
        if (value > 70 && value <= 100) {
            float before70 = unitWidth * 40 + markLineStartX;
            float after70 = (value - 70) * (unitWidth / 6.0f);
            rightCursorMiddleX = before70 + after70;
        }
        if (value > 100) {
            rightCursorMiddleX = 99999;
        }

        invalidate();
        if (onScaleListener != null) {
            onScaleListener.onScaleEnd(getLeftCursorFloatValue(), getRightCursorFloatValue());
        }
    }

    public float getSpacingValue() {
        return spacingValue;
    }

    public void setSpacingValue(float spacingValue) {
        this.spacingValue = spacingValue;
    }
}