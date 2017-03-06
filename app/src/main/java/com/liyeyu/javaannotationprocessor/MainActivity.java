package com.liyeyu.javaannotationprocessor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.liyeyu.viewfinder.ViewFinder;
import com.liyeyu.viewfinder_annotation.BindView;
import com.liyeyu.viewfinder_annotation.OnClick;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv)
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ViewFinder.inject(this);

        tv.setText("JavaAnnotationProcessor");
    }

    @OnClick(R.id.tv)
    public void onTextViewClick(View view){
        ((TextView)view).setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
    }
}
