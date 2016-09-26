package com.example.asus.photogallery.Activity;

import android.support.v4.app.Fragment;

import com.example.asus.photogallery.Fragment.PhotoGallleryFargment;

public class Photogallery_activity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment()
    {
        return  new PhotoGallleryFargment();
    }


}
