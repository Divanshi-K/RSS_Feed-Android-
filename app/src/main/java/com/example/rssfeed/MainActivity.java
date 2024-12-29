package com.example.rssfeed;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends ListActivity
{
    List hdlin;
    List lnk;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        new myTask().execute();
    }
    public class myTask extends AsyncTask<Object,Void, ArrayAdapter>
    {
        protected ArrayAdapter doInBackground(Object... objects)
        {
            hdlin = new ArrayList();
            lnk = new ArrayList();
            try
            {
                URL url = new URL("https://feeds.bbci.co.uk/news/rss.xml");

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url),"UTF_8");

                boolean insideItem = false;
                int eventType = xpp.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT)
                {
                    if (eventType == XmlPullParser.START_TAG)
                    {
                        if(xpp.getName().equalsIgnoreCase("item"))
                        {
                            insideItem = true;
                        }
                        else if(xpp.getName().equalsIgnoreCase("title"))
                        {
                            if(insideItem)
                                hdlin.add(xpp.nextText());
                        }
                        else if(xpp.getName().equalsIgnoreCase("Link"))
                        {
                            if(insideItem)
                                lnk.add(xpp.nextText());
                        }
                    }
                    else if(eventType==XmlPullParser.END_TAG &&
                            xpp.getName().equalsIgnoreCase("item"))
                    {
                        insideItem=false;
                    }
                    eventType = xpp.next();
                }
            }
            catch(MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (XmlPullParserException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(ArrayAdapter adapter)
        {
            adapter = new ArrayAdapter(MainActivity.this,
                    android.R.layout.simple_list_item_1,hdlin);
            setListAdapter(adapter);
        }
    }
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        Uri uri = Uri.parse((lnk.get(position)).toString());
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }
    public InputStream getInputStream(URL url)
    {
        try
        {
            return url.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            return null;
        }
    }
}
