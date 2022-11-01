package com.jellysoft.sundigitalindia;

import static javax.mail.Message.RecipientType.TO;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.util.IsRTL;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class provider extends AppCompatActivity {
EditText name,place, title,vacancy,num;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("வேலை வழங்குபவர்");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        name = findViewById(R.id.name);
        place = findViewById(R.id.place);
        title = findViewById(R.id.title);
        vacancy = findViewById(R.id.vacancy);
        num = findViewById(R.id.num);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(view -> {
            String reciever_email ="sundigitalindiatuty@gmail.com";
            String subject="JOB VACANCY";
            String body = "name : "+name.getText().toString()+ " \n"+  "Job Title : "+title.getText().toString()+ " \n"
            +"Phone Number : "+num.getText().toString()+ " \n"+                "Place : "+place.getText().toString()+ " \n"
            +"Vacancy : "+vacancy.getText().toString()+ " \n";

            int myval = sendMail(reciever_email, subject, body);
            if(myval == 1){
                Intent intent8 = new Intent(provider.this,finish.class);
                startActivity(intent8);
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public int sendMail(String reciever_email, String subject, String body) {
        final String username = "sundigitalindiatuty@gmail.com";
        final String password = "sun@04361";
        Properties props = new Properties();
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "25");
        props.put("mail.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.EnableSSL.enable", "true");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallbac k", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    public javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(TO, InternetAddress.parse(reciever_email));
            message.setSubject(subject);
            message.setText(body);
            Multipart multipart = new MimeMultipart("related");
            BodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent("<html>HELLO</html>", "text/html");

            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);
            Transport.send(message);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }
}