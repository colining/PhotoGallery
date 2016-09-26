package com.example.asus.photogallery.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.asus.photogallery.R;

/**
 * Created by asus on 2016/7/7.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected  abstract Fragment createFragment();         //定义createFragemnt的抽象函数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        android.support.v4.app.FragmentManager fm=getSupportFragmentManager();
        Fragment fragment =fm.findFragmentById(R.id.fragmentContainer);
        if(fragment==null)
        {
            fragment=createFragment();                                        //抽象函数获得fragment 在子类中便可具体实现
            fm.beginTransaction().add(R.id.fragmentContainer,fragment).commit();
        }
    }
}
