package com.example.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jellysoft.sundigitalindia.R;
import com.example.adapter.SkillsAdapter;
import com.example.item.ItemJob;

import java.util.ArrayList;
import java.util.Arrays;

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
        text_website.setText(itemJob.getJobCompanyWebsite());
        textType.setText(itemJob.getJobType());
        textJobQualification.setText(itemJob.getJobQualification());
        marital.setText(itemJob.getMarital());
        textExp.setText(itemJob.getJobExperience());

        text_website.setMovementMethod(LinkMovementMethod.getInstance());

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { getActivity().onBackPressed(); }
        });
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", itemJob.getJobPhoneNumber(), null));
                startActivity(intent);
            }
        });

        btn_download.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Download started...", Toast.LENGTH_LONG).show();
        });

        btn_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm= getActivity().getPackageManager();
                try {
                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String text =
                            itemJob.getJobName() + "\n" +
                                    getString(R.string.job_company_lbl) + itemJob.getJobCompanyName() + "\n" +
                                    getString(R.string.job_designation_lbl) + itemJob.getJobDesignation() + "\n" +
                                    getString(R.string.job_phone_lbl) + itemJob.getJobPhoneNumber() + "\n" +
                                    getString(R.string.job_address_lbl) + itemJob.getCity() + "\n\n" +
                                    "Download Application here https://play.google.com/store/apps/details?id=com.jellysoft.sundigitalindia";
                    PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, "Share with"));
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getActivity(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        return rootView;
    }
}
