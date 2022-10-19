package com.jellysoft.sundigitalindia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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

import com.example.util.IsRTL;

public class devcontact extends AppCompatActivity {
    WebView googlemap_webView;
    TextView mail,num,what;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devcontact);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("டெவலப்பர் தொடர்பு");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        googlemap_webView = findViewById(R.id.googlemap_webView);
        mail = findViewById(R.id.mail);
        SpannableString content = new SpannableString("info@jellysoft.in");
        UnderlineSpan us=new UnderlineSpan();
        content.setSpan(us, 0, content.length(), 0);
        TextPaint tp=new TextPaint();
        tp.setColor(ContextCompat.getColor(devcontact.this,R.color.colorPrimaryDark));
        us.updateDrawState(tp);
        mail.setText(content);
        num = findViewById(R.id.num);
        num.setText(Html.fromHtml("<u>+91 70921 50100</u>"));
        what = findViewById(R.id.what);
        what.setText(Html.fromHtml("<u>+91 70921 50100</u>"));
        String iframe = "<iframe src=\"https://www.google.com/maps/embed?pb=!1m14!1m8!1m3!1d15771.330480659346!2d78.1384479!3d8.8017909!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x4ae980863e2dec69!2sJELLYSOFT!5e0!3m2!1sen!2sin!4v1625619631399!5m2!1sen!2sin\" width=\"300\" height=\"150\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\"></iframe>";

        googlemap_webView.getSettings().setJavaScriptEnabled(true);
        googlemap_webView.loadData(iframe, "text/html", "utf-8");
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "info@jellysoft.in" });
                startActivity(Intent.createChooser(intent, ""));
            }
        });
        num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:917092150100"));
                startActivity(intent);
            }
        });
        what.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String contact = "+917092150100"; // use country code with your phone number
                String url = "https://api.whatsapp.com/send?phone=" + contact;
                try {
                    PackageManager pm = getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(devcontact.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        Log.d("him","hiii");
        onBackPressed();
        return true;
    }
}