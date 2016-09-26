package com.example.asus.photogallery.Fragment;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.asus.photogallery.FilckrFetchr;
import com.example.asus.photogallery.GalleryItem;
import com.example.asus.photogallery.R;
import com.example.asus.photogallery.ThumbnailDownloader;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;

/**
 * Created by asus on 2016/9/25.
 */
public class PhotoGallleryFargment extends Fragment {

    GridView gridView;
    private  static  final  String TAG = "photoGalleryFragment";
    ArrayList<GalleryItem> mitems;
   ThumbnailDownloader<ImageView> thumbnailDownloader;

    @Override
    public void onCreate(Bundle s)
    {
        super.onCreate(s);
        setRetainInstance(true);
        new FetchItemsTask().execute();
        thumbnailDownloader = new ThumbnailDownloader<ImageView>(new Handler());
        thumbnailDownloader.setListener(new ThumbnailDownloader.Listener<ImageView>() {
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if(isVisible())
                {
                    imageView.setImageBitmap(thumbnail);
                }
            }
        });

        thumbnailDownloader.start();
        thumbnailDownloader.getLooper();
        Log.i(TAG,"Background thread started");

    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup parent,Bundle s)
    {
        View view = inflater.inflate(R.layout.fragment_photo_gallery,parent,false);
        gridView = (GridView) view.findViewById(R.id.gridview);
       setupAdapter();
        return view;
    }
    private  class  FetchItemsTask extends AsyncTask<Void,Void,ArrayList<GalleryItem>>
    {
        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params)
        {
//            try
//            {
//                String result = new FilckrFetchr().geturl("http://www.baidu.com");
//                Log.i(TAG , "Fetched contents of URL: "+result);
//                Log.d(TAG,"LALALALALALAL");
//            }catch (IOException io)
//            {
//                Log.e(TAG, "Failed to fetch URL "+io);
//            }
            return new FilckrFetchr().fetchItems();

        }
        @Override
        protected void onPostExecute(ArrayList<GalleryItem> items)
        {
            mitems = items;
            setupAdapter();
        }
    }

    void setupAdapter()
    {
        if(getActivity()==null||gridView==null) {

            return;
        }
        if(mitems!=null) {
            //gridView.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(), android.R.layout.simple_gallery_item, mitems));

            gridView.setAdapter(new GalleryItemAdapter(mitems));
           Log.d(TAG, mitems.get(0).toString());
        }
        else
            gridView.setAdapter(null);
    }

    private class  GalleryItemAdapter extends  ArrayAdapter<GalleryItem>
    {
        public  GalleryItemAdapter(ArrayList<GalleryItem> items)
        {
            super(getActivity(),0,items);
        }

        @Override
        public  View getView(int position,View convertView,ViewGroup parent)
        {
            if(convertView == null)
            {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item,parent,false);
            }
            ImageView imageView =(ImageView)convertView.findViewById(R.id.gallery_item_imageView);
            imageView.setImageResource(R.drawable.download);
            GalleryItem item = getItem(position);
            thumbnailDownloader.queueThumbnail(imageView,item.getUrl());
            return  convertView;
        }
    }

    @Override
    public  void onDestroy()
    {
        super.onDestroy();
        thumbnailDownloader.quit();
        Log.i(TAG,"background thread destroy");
    }
    @Override
    public  void onDestroyView()
    {
        super.onDestroyView();
        thumbnailDownloader.cleanQueue();

    }
}
