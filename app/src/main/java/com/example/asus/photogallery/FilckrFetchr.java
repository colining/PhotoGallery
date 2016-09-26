package com.example.asus.photogallery;

import android.net.Uri;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.XMLFormatter;

/**
 * Created by asus on 2016/9/25.
 */
public class FilckrFetchr {
    public  static  final  String TAG = "FilckrFetch";
    private  static  final  String ENDPOINT = "https://api.flickr.com/services/rest";
    private static  final  String API_KEY = "0f8bca8f56c238c501e8b0fc9517ae3d";
    private  static  final  String METHOD_GET_RESENT ="flickr.photos.getRecent";
    private  static  final  String PARAM_EXTRAS ="extras";
    private  static  final String EXTRA_SMALL_URL = "url_s";

    private  static final String XML_PHOTO ="photo";

    byte [] geturlBytes(String urlspec) throws IOException
    {
        URL url = new URL(urlspec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in =connection .getInputStream();
            if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK)
            {
                return null;
            }
            int bytesead = 0 ;
            byte[] buffer = new byte[1024];
            while ((bytesead=in.read(buffer))>0)
            {
                out.write(buffer,0,bytesead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }
    public String geturl(String urlspec) throws  IOException
    {
        return new String(geturlBytes(urlspec));
    }
    public ArrayList<GalleryItem> fetchItems()
    {
        ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
        try
        {
            String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method",METHOD_GET_RESENT)
                .appendQueryParameter("api_key",API_KEY)
                .appendQueryParameter(PARAM_EXTRAS,EXTRA_SMALL_URL)
                .build().toString();
            String xmlString =geturl(url);
            Log.i(TAG , "Receive xml "+xmlString);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));

            parseItems(items,parser);
        }catch (IOException io)
        {
            Log.e(TAG , "Failed to fetch items",io);
        }catch (XmlPullParserException xml)
        {
            Log.e(TAG ,"failed to parse item", xml);
        }
        return  items;
    }

    void parseItems (ArrayList<GalleryItem> items , XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.next();

        while (eventType!=XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_TAG&&XML_PHOTO.equals(parser.getName()))
            {
               String id = parser .getAttributeValue(null , "id");
                String caption = parser.getAttributeValue(null,"title");
                String smallUrl = parser . getAttributeValue(null,EXTRA_SMALL_URL);
                GalleryItem item = new GalleryItem();
                item.setId(id);
                item.setCaption(caption);
                item.setUrl(smallUrl);
                items.add(item);
            }
            eventType = parser.next();
        }
    }
}
