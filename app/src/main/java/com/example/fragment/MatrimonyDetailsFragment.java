package com.example.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.example.item.ItemMatrimony;
import com.jellysoft.sundigitalindia.R;

import java.util.ArrayList;

public class MatrimonyDetailsFragment extends Fragment {

    WebView webView;
    TextView text_matrimony_qualification,
            text_matrimony_dob, marital, text_expectation,
            matrimonyContact, matrimonyDesc;
    ItemMatrimony itemMatrimony;

    Button btn_call, btn_whatsapp, btn_back, btn_download;
    ArrayList<String> mSkills;

    public static MatrimonyDetailsFragment newInstance(ItemMatrimony itemProduct) {
        MatrimonyDetailsFragment f = new MatrimonyDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("itemJob", itemProduct);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_matrimony_details, container, false);
        if (getArguments() != null) {
            itemMatrimony = (ItemMatrimony) getArguments().getSerializable("itemJob");
        }
        mSkills = new ArrayList<>();

        text_matrimony_qualification = rootView.findViewById(R.id.text_matrimony_qualification);
        text_matrimony_dob = rootView.findViewById(R.id.text_dob);
        marital = rootView.findViewById(R.id.marital);
        text_expectation = rootView.findViewById(R.id.text_expectation);
        matrimonyContact = rootView.findViewById(R.id.text_contact_person);
        matrimonyDesc = rootView.findViewById(R.id.text_desc);

        btn_call = rootView.findViewById(R.id.btn_call);
        btn_whatsapp = rootView.findViewById(R.id.btn_whats);
        btn_back = rootView.findViewById(R.id.btn_back);
        btn_download = rootView.findViewById(R.id.btn_download);

        text_matrimony_qualification.setText(itemMatrimony.getMatrimonyEducation());
        text_matrimony_dob.setText(itemMatrimony.getMatrimonyDob());
        marital.setText(itemMatrimony.getMatrimonyMaritalStatus());
        matrimonyContact.setText(itemMatrimony.getMatrimonyPersonName());
        matrimonyDesc.setText(itemMatrimony.getMatrimonyDesc());
        text_expectation.setText(itemMatrimony.getMatrimonyPartnerExpect());

        btn_download.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Download started...", Toast.LENGTH_LONG).show();
        });

        btn_back.setOnClickListener(view -> getActivity().onBackPressed());
        btn_call.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", itemMatrimony.getMatrimonyPhoneNumber(), null));
            startActivity(intent);
        });
        btn_whatsapp.setOnClickListener(view -> {
            String phone = "91" + itemMatrimony.getMatrimonyPhoneNumber().replace("+91", "");
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
