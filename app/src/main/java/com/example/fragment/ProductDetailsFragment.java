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

import com.example.item.ItemJob;
import com.example.item.ItemProduct;
import com.jellysoft.sundigitalindia.R;

import java.util.ArrayList;

public class ProductDetailsFragment extends Fragment {

    WebView webView;
    TextView text_negotiate, text_desc, text_website;
    ItemProduct itemJob;

    Button btn_call, btn_whatsapp,btn_back;
    ArrayList<String> mSkills;

    public static ProductDetailsFragment newInstance(ItemProduct itemProduct) {
        ProductDetailsFragment f = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("itemJob", itemProduct);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_details, container, false);
        if (getArguments() != null) {
            itemJob = (ItemProduct) getArguments().getSerializable("itemJob");
        }
        mSkills = new ArrayList<>();

        text_negotiate = rootView.findViewById(R.id.text_negotiate);
        text_desc = rootView.findViewById(R.id.text_desc);
        text_website = rootView.findViewById(R.id.text_website);

        btn_call = rootView.findViewById(R.id.btn_call);
        btn_whatsapp = rootView.findViewById(R.id.btn_whats);
        btn_back = rootView.findViewById(R.id.btn_back);


        btn_back.setOnClickListener(view -> getActivity().onBackPressed());
        btn_call.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", itemJob.getProductPhoneNumber(), null));
            startActivity(intent);
        });
        btn_whatsapp.setOnClickListener(view -> {
            PackageManager pm= getActivity().getPackageManager();
            try {
                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("text/plain");
                String text =
                        itemJob.getProductName() + "\n" +
                                getString(R.string.job_company_lbl) + itemJob.getProductCompanyName() + "\n" +
                                getString(R.string.job_designation_lbl) + itemJob.getProductDesignation() + "\n" +
                                getString(R.string.job_phone_lbl) + itemJob.getProductPhoneNumber() + "\n" +
                                getString(R.string.job_address_lbl) + itemJob.getCity() + "\n\n" +
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
