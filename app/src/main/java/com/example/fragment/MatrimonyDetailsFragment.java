package com.example.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
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
    TextView text_matrimony_gender, text_matrimony_job,
            text_salary, text_matrimony_qualification,
            text_matrimony_caste, text_matrimony_age,
            marital, text_expectation;
    ItemMatrimony itemMatrimony;

    Button btn_call, btn_whatsapp,btn_back;
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

        text_matrimony_gender = rootView.findViewById(R.id.text_matrimony_gender);
        text_matrimony_job = rootView.findViewById(R.id.text_matrimony_job);
        text_salary = rootView.findViewById(R.id.text_salary);
        text_matrimony_qualification = rootView.findViewById(R.id.text_matrimony_qualification);
        text_matrimony_caste = rootView.findViewById(R.id.text_matrimony_caste);
        text_matrimony_age = rootView.findViewById(R.id.text_matrimony_age);
        marital = rootView.findViewById(R.id.marital);
        text_expectation = rootView.findViewById(R.id.text_expectation);

        btn_call = rootView.findViewById(R.id.btn_call);
        btn_whatsapp = rootView.findViewById(R.id.btn_whats);
        btn_back = rootView.findViewById(R.id.btn_back);


        text_matrimony_gender.setText(itemMatrimony.getMatrimonyGender());
        text_matrimony_job.setText(itemMatrimony.getMatrimonyCareer());
        text_salary.setText(itemMatrimony.getMatrimonySalary());
        text_matrimony_qualification.setText(itemMatrimony.getMatrimonyEducation());
        text_matrimony_caste.setText(itemMatrimony.getCategoryName());
        text_matrimony_age.setText(itemMatrimony.getMatrimonyAge());
        marital.setText(itemMatrimony.getMatrimonyMaritalStatus());
        text_expectation.setText(itemMatrimony.getMatrimonyPartnerExpect());


        btn_back.setOnClickListener(view -> getActivity().onBackPressed());
        btn_call.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", itemMatrimony.getMatrimonyPhoneNumber(), null));
            startActivity(intent);
        });
        btn_whatsapp.setOnClickListener(view -> {
            PackageManager pm= getActivity().getPackageManager();
            try {
                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("text/plain");
                String text =
                        itemMatrimony.getMatrimonyName() + "\n" +
                                getString(R.string.job_phone_lbl) + itemMatrimony.getMatrimonyPhoneNumber() + "\n" +
                                getString(R.string.job_address_lbl) + itemMatrimony.getCity() + "\n\n" +
                                "Download Application here https://play.google.com/store/apps/details?id=com.jellysoft.sundigitalindia";
                PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                //Check if package exists or not. If not then code
                //in catch block will be called
                waIntent.setPackage("com.whatsapp");

                waIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(waIntent, "Share with"));

            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(getActivity(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        return rootView;
    }
}
