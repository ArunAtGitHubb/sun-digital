package com.jellysoft.sundigitalindia;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.item.ItemAdmin;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class contact extends AppCompatActivity {
WebView googlemap_webView;
TextView mail,num,num2,what,address;
String phones,phones1,ads,mails;
    ArrayList<ItemAdmin> mListItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mListItem = new ArrayList<>();
        getAdmin();
        googlemap_webView = findViewById(R.id.googlemap_webView);
        mail = findViewById(R.id.mail);
        address = findViewById(R.id.address);

        num = findViewById(R.id.num);

        num2 = findViewById(R.id.num2);

        what = findViewById(R.id.what);

        toolbar.setTitle("தொடர்பு");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        String iframe = "<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d126179.21636271877!2d78.14601744999999!3d8.776613800000002!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3b03ee67b4ad764f%3A0x2443e6dc90ee7d3!2sThoothukudi%2C%20Tamil%20Nadu!5e0!3m2!1sen!2sin!4v1658603666038!5m2!1sen!2sin\" width=\"300\" height=\"150\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\" referrerpolicy=\"no-referrer-when-downgrade\"></iframe>";
        googlemap_webView.setVisibility(View.GONE);

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mail.getText().toString() });
                startActivity(Intent.createChooser(intent, ""));
            }
        });
        num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+num.getText().toString()));
                startActivity(intent);
            }
        });
        num2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+num2.getText().toString()));
                startActivity(intent);
            }
        });
        what.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String contact = num.getText().toString(); // use country code with your phone number
                String url = "https://api.whatsapp.com/send?phone=" + contact;
                try {
                    PackageManager pm = getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(contact.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void getAdmin() {
        Log.d("raky","hi");
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_admin");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String result = new String(responseBody);
                try {
                    Log.d("raky","1");
                    JSONObject mainJson = new JSONObject(result);
                    Log.d("raky", String.valueOf(mainJson));
                    Log.d("raky",Constant.ARRAY_NAME);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    Log.d("raky","3");
                    JSONObject objJson;
                    Log.d("raky",String.valueOf(jsonArray.length()));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ItemAdmin objItem = new ItemAdmin();
                        objJson = jsonArray.getJSONObject(i);
                        objItem.setPhone(objJson.getString("phone"));
                        objItem.setPhone1(objJson.getString("phone1"));
                        objItem.setAddress(objJson.getString("address"));
                        phones = objJson.getString("phone");
                        phones1 = objJson.getString("phone1");
                        ads = objJson.getString("address");
                        mails = objJson.getString("email");
                        mListItem.add(objItem);
                        SpannableString content = new SpannableString(mails);
                        UnderlineSpan us=new UnderlineSpan();
                        content.setSpan(us, 0, content.length(), 0);
                        TextPaint tp=new TextPaint();
                        tp.setColor(ContextCompat.getColor(contact.this,R.color.colorPrimaryDark));
                        us.updateDrawState(tp);
                        mail.setText(content);
                        address.setText(ads);
                        num.setText(Html.fromHtml("<u>"+"+91"+phones+"</u>"));
                        num2.setText(Html.fromHtml("<u>"+"+91"+phones1+"</u>"));
                        what.setText(Html.fromHtml("<u>"+"+91"+phones+"</u>"));
                    }
                } catch (JSONException e) {
                    Log.d("raky","catch");
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("raky","fail");
            }

        });



    }
}