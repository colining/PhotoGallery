package com.example.asus.photogallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.GpsStatus;
import android.net.sip.SipSession;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 2016/9/26.
 */
public class ThumbnailDownloader<Token> extends HandlerThread {
    private  static  final  String TAG = "ThumbnailDownloader";
    private static  final  int MESSAGE_DOWNLOAD=0;

    Handler handler;
    Map<Token,String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());

    Handler responsehandler;
    ThumbnailDownloader.Listener<Token> mlistener;

    public  interface  Listener<Token>
    {
        void onThumbnailDownloaded(Token token,Bitmap thumbnail);
    }
    public  void  setListener(Listener<Token> listener)
    {
        mlistener=listener;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected  void onLooperPrepared()
    {
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == MESSAGE_DOWNLOAD)
                {
                    @SuppressWarnings("unchecked")
                            Token token =(Token)msg.obj;
                    Log.i(TAG,"got to request for url :"+ requestMap.get(token));
                    handleRequest(token);

                }
            }
        };
    }

    public ThumbnailDownloader(Handler responsehandler)
    {
        super(TAG);
        this.responsehandler=responsehandler;
    }

    public  void  queueThumbnail(Token token ,String url)
    {
        Log.i(TAG,"Got to url :"+url);
        requestMap.put(token,url);
        handler
                .obtainMessage(MESSAGE_DOWNLOAD,token)
                .sendToTarget();

    }
    private  void  handleRequest(final Token token)
    {
        try {
            final String url =requestMap.get(token);
            if(url == null)
            {
                return;
            }
            byte[] bitmapBytes = new FilckrFetchr().geturlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes,0,bitmapBytes.length);
            Log.i(TAG,"Bitmap created");
            responsehandler.post(new Runnable() {

                public void run() {
                    if(requestMap.get(token)!=url)
                        return;

                    requestMap.remove(token);
                    mlistener.onThumbnailDownloaded(token,bitmap);
                }
            });
        }catch (IOException io)
        {
            Log.e(TAG,"error downloading image",io);
        }
    }

    public void cleanQueue()
    {
        handler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }

}
