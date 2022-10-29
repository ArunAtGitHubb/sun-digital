package com.example.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.item.ItemService;
import com.jellysoft.sundigitalindia.R;

import java.util.ArrayList;

public class ServiceDetailsFragment extends Fragment {

    WebView webView;
    TextView textDesc, textSkills, textWebsite;
    ItemService itemJob;

    Button btn_call, btn_whatsapp,btn_back;
    ArrayList<String> mSkills;

    public static ServiceDetailsFragment newInstance(ItemService itemProduct) {
        ServiceDetailsFragment f = new ServiceDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("itemJob", itemProduct);
        f.setArguments(args);
        return f;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_service_details, container, false);
        if (getArguments() != null) {
            itemJob = (ItemService) getArguments().getSerializable("itemJob");
        }
        mSkills = new ArrayList<>();

        textDesc = rootView.findViewById(R.id.text_desc);
        textSkills = rootView.findViewById(R.id.textSkills);
        textWebsite = rootView.findViewById(R.id.text_website);

        btn_call = rootView.findViewById(R.id.btn_call);
        btn_whatsapp = rootView.findViewById(R.id.btn_whats);
        btn_back = rootView.findViewById(R.id.btn_back);

        textDesc.setText(itemJob.getServiceDesc());
        textSkills.setText(itemJob.getServiceSkill());
        textWebsite.setText(itemJob.getWebsiteLink());
        textWebsite.setTextColor(Color.BLUE);

        textWebsite.setOnClickListener(view -> {
            try {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(textWebsite.getText().toString()));
                startActivity(myIntent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Make sure url is correct",  Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        btn_back.setOnClickListener(view -> getActivity().onBackPressed());
        btn_call.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", itemJob.getServicePhoneNumber(), null));
            startActivity(intent);
        });
        btn_whatsapp.setOnClickListener(view -> {
            String phone = "91" + itemJob.getServicePhoneNumber().replace("+91", "");
            String url = "https://api.whatsapp.com/send?phone=" + phone;
            PackageManager pm = requireContext().getPackageManager();
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
