# customRuleView
自定义刻度尺
![](https://github.com/zhangguoning/customRuleView/tree/master/app/src/main/res/mipmap-hdpi/a.jpg)  

用法：
1.布局文件中：
 <cn.zgn.customrule.CustomRule
        android:id="@+id/crule"
        android:layout_marginTop="10dp"
        android:layout_width="match_parent"
        android:layout_height="100dp"
        />
  
  android:layout_height 可以根据需要自己调整

2.代码中：

1）常用方法：
CustomRule crule = (CustomRule) this.findViewById(R.id.crule);
  crule.setSpacingValue(2); //设置左右游标指示值的最小间距为2
  crule.setLeftCursorValue(12.3f);//设置左游标的值
  crule.setHintText("显示文字")// 设置提示文本(一般情况下无需手动设置)
  
  crule.getLeftCursorFloatValue();// 取得左游标的 float 类型的值
  crule.getRightCursorIntValue() ;// 取得右游标的 int 类型的值
  
  2）滑动监听：
  
  public interface OnScaleListener {
  
        void onScaleStart(float min, float max);

        void onScaling(float min, float max);

        void onScaleEnd(float min, float max);
    }
  用法：
   crule.setOnScaleListener(new CustomRule.OnScaleListener() {
            @Override
            public void onScaleStart(float min, float max) {
                Log.i("onScaleStart()", "min = " + min + ",max = " + max);
            }

            @Override
            public void onScaling(float min, float max) {
                Log.i("onScaling()", "min = " + min + ",max = " + max);
            }

            @Override
            public void onScaleEnd(float min, float max) {
                Log.i("onScaleEnd()", "min = " + min + ",max = " + max);
            }
        });
        
  
  
  
  
  
  

