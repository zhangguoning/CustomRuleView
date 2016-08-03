package cn.zgn.customrule;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener ,CustomRule.OnScaleListener{

    private CustomRule crule ;
    private Button reset_but ,ok_but ,spacing_but ,text_but;
    private EditText min_et ,max_et ,spacing_et , text_et;
    private TextView text_tv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        crule = (CustomRule) this.findViewById(R.id.crule);
        reset_but = (Button) this.findViewById(R.id.reset_but);
        ok_but = (Button) this.findViewById(R.id.ok_but);
        text_but = (Button) this.findViewById(R.id.text_but);
        spacing_but = (Button) this.findViewById(R.id.spacing_but);
        min_et = (EditText) this.findViewById(R.id.min_et);
        max_et = (EditText) this.findViewById(R.id.max_et);
        spacing_et = (EditText) this.findViewById(R.id.spacing_et);
        text_et = (EditText) this.findViewById(R.id.text_et);
        text_tv = (TextView) this.findViewById(R.id.text_tv);

        crule.setListener(this);
        reset_but.setOnClickListener(this);
        ok_but.setOnClickListener(this);
        spacing_but.setOnClickListener(this);
        text_but.setOnClickListener(this);

        crule.setBackGroundColor(0xffffffff);
    }

    @Override
    public void onClick(View v) {

        try {
            switch (v.getId()) {
                case R.id.ok_but:
                    crule.setLeftCursorValue(Float.parseFloat(min_et.getText().toString()));
                    crule.setRightCursorValue(Float.parseFloat(max_et.getText().toString()));
                    break;

                case R.id.reset_but:
                    crule.reset();
                    break;
                case R.id.spacing_but:

                    crule.setSpacingValue(Float.parseFloat(spacing_et.getText().toString()));

                    break;

                case R.id.text_but:
                    crule.setHintText(text_et.getText().toString());
                    break;
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onScaleStart(float min, float max) {
//        Log.e("onScaleStart()","min = " + min + ",max = " +max);
        text_tv.setText("onScaleStart : min = " + min + ",max = " +max);
    }

    @Override
    public void onScaling(float min, float max) {
//        Log.e("onScaling()","min = " + min + ",max = " +max);
        text_tv.setText("onScaling : min = " + min + ",max = " +max);
    }

    @Override
    public void onScaleEnd(float min, float max) {
//        Log.e("onScaleEnd()","min = " + min + ",max = " +max);
        text_tv.setText("onScaleEnd : min = " + min + ",max = " +max);
    }
}
