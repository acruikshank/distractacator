package io.github.acruikshank;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.util.Log;
import java.net.*;
import java.io.*;
import java.util.*;
import android.database.*;
import android.content.*;
import android.net.Uri;
import android.text.format.DateUtils;
import static android.provider.CalendarContract.Instances;

public class Distractacator extends Activity
{
  public static String CLIENT_ID = "102686879623-a7v0d02j1t8icjls48ts3ene3dt0bsmg.apps.googleusercontent.com";
  public static String CLIENT_SECRET = "JmqD_ezK3Y91s-BkUqW5SNqe";
  public static String CALENDAR_SCOPE = "https://www.googleapis.com/auth/calendar.readonly";

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    Log.d("Distractacator", "Created");
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.main);

    Log.d("Distractacator", "Starting webview");
    WebView webview = (WebView) findViewById(R.id.webView);
    webview.getSettings().setJavaScriptEnabled(true);
    webview.clearCache(true);
    webview.setInitialScale(200);
    webview.loadUrl("http://acruikshank.github.io/distractacator/");
    webview.addJavascriptInterface(new DeviceAuth(webview), "deviceAuth");
    WebView.setWebContentsDebuggingEnabled(true);
  }

  class DeviceAuth {
    private WebView webView;

    public DeviceAuth(WebView webView) {
      this.webView = webView;
    }

    @JavascriptInterface
    public String getEvents() {
      Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
      long now = new Date().getTime();
      ContentUris.appendId(builder, now);
      ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);

      ContentResolver contentResolver = getApplicationContext().getContentResolver();
      Cursor cur = contentResolver.query(builder.build(),
        new String[] { "title", "begin", "allDay"}, null,
        null, "startDay ASC, startMinute ASC");

      StringBuilder sb = new StringBuilder("[");
      String prefix = "";
      while (cur.moveToNext()) {
        String title = null;
        long startTime = 0l;
        boolean allDay = false;
          
        // Get the field values
        title = cur.getString(0);
        startTime = cur.getLong(1);
        allDay = !cur.getString(2).equals("0");
                  
        if (! allDay) {
          sb.append(prefix);
          prefix = ",";
          sb.append("{\"name\":\"");
          sb.append( title.replace("\\","\\\\").replace("\"", "\\\"") );
          sb.append("\",\"time\":");
          sb.append(startTime);
          sb.append("}");
        }
      }
      sb.append("]");
      return sb.toString();
    }
  }
}
