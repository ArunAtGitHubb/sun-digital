package com.jellysoft.sundigitalindia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.example.util.IsRTL;

import org.w3c.dom.Text;

public class about extends AppCompatActivity {
TextView about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("எங்களை பற்றி");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        about = findViewById(R.id.about);
        about.setText("   சன் டிஜிட்டல் இந்தியா - வேலை வாய்ப்பு நிறுவனம் இந்த ஆண்ட்ராய்டு செயலி, வேலை தேடும் மக்களுக்கு மற்றும் வேலை அளிக்கும் நிறுவனங்களுக்கும் பயன்படும் நோக்கத்தில் வடிவமைத்துளோம். இந்த செயலியின் மூலம் அநேக மக்கள் பயனடைவதே எங்களின் பிரதான நோக்கம். வேலை தேடும் அணைத்து மக்களும் இந்த ஆண்ட்ராய்டு செயலியை சரிவர பயன்படுத்தி பயன்பெற்று மற்றவர்களுக்கும் இதை அறிமுகம் செய்து அவர்களையும் பயன்பெறச்செய்து இந்த செயலிக்கு ஆதரவு அளிக்க வேடுகிறோம்.");
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}