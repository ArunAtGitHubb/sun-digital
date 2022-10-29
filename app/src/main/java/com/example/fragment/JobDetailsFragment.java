package com.example.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.item.ItemJob;
import com.jellysoft.sundigitalindia.R;

import java.util.ArrayList;

public class JobDetailsFragment extends Fragment {

//    WebView webView;
    TextView textJobQualification, textWorkDay, textWorkTime, textExp, textType,marital, text_desc, textSkills, text_website;
    ItemJob itemJob;

    Button btn_call, btn_whatsapp, btn_back, btn_download;
    ArrayList<String> mSkills;

    public static JobDetailsFragment newInstance(ItemJob itemJob) {
        JobDetailsFragment f = new JobDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("itemJob", itemJob);
        f.setArguments(args);
        return f;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job_details, container, false);
        if (getArguments() != null) {
            itemJob = (ItemJob) getArguments().getSerializable("itemJob");
        }

        mSkills = new ArrayList<>();

        text_desc= rootView.findViewById(R.id.text_desc);
        textJobQualification = rootView.findViewById(R.id.text_job_qualification);
        textWorkDay = rootView.findViewById(R.id.text_job_work_day);
        textWorkTime = rootView.findViewById(R.id.text_job_work_time);
        textExp = rootView.findViewById(R.id.text_job_exp);
        textType = rootView.findViewById(R.id.text_job_type);
        textSkills= rootView.findViewById(R.id.textSkills);
        text_website = rootView.findViewById(R.id.text_website);
        marital = rootView.findViewById(R.id.marital);

        btn_call = rootView.findViewById(R.id.btn_call);
        btn_whatsapp = rootView.findViewById(R.id.btn_whats);
        btn_back = rootView.findViewById(R.id.btn_back);
        btn_download = rootView.findViewById(R.id.btn_download);

        textSkills.setText(itemJob.getJobSkill());
        text_desc.setText(itemJob.getJobDesc());
        textWorkDay.setText(itemJob.getAge());
        textWorkTime.setText(itemJob.getSex());
        textType.setText(itemJob.getJobType());
        textJobQualification.setText(itemJob.getJobQualification());
        marital.setText(itemJob.getMarital());
        textExp.setText(itemJob.getJobExperience());

        text_website.setText(itemJob.getJobWebsite());
        text_website.setTextColor(Color.BLUE);

        text_website.setOnClickListener(view -> {
            try {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(text_website.getText().toString()));
                startActivity(myIntent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Make sure url is correct",  Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });


        btn_back.setOnClickListener(view -> getActivity().onBackPressed());
        btn_call.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL,
                    Uri.fromParts("tel", itemJob.getJobPhoneNumber(), null));
            startActivity(intent);
        });

        btn_download.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Download started...", Toast.LENGTH_LONG).show();
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(itemJob.getJobPdf()));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(false)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, itemJob.getJobName() + ".pdf");
            DownloadManager downloadManager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadID = downloadManager.enqueue(request);
        });

        btn_whatsapp.setOnClickListener(view -> {
            String phone = itemJob.getJobPhoneNumber();
            String url = "https://api.whatsapp.com/send?phone=" + phone.replace("+91", "");
            PackageManager pm = requireActivity().getPackageManager();
            try {
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        return rootView;
    }
}
