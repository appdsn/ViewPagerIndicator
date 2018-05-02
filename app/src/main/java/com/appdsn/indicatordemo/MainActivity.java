package com.appdsn.indicatordemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                openActivity(TopTabActivity.class);
                break;
            case R.id.btn2:
                openActivity(ScrollTabActivity.class);
                break;
            case R.id.btn3:
                openActivity(CircleActivity.class);
                break;
            case R.id.btn4:
                openActivity(BottomTabActivity.class);
                break;
            default:
                break;
        }
    }

    protected void openActivity(Class<?> pClass) {
        Intent intent = new Intent();
        intent.setClass(this, pClass);
        super.startActivity(intent);
    }

}
