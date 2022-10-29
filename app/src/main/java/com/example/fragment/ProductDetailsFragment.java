package com.example.fragment;

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

import com.example.item.ItemProduct;
import com.jellysoft.sundigitalindia.R;

import java.util.ArrayList;

public class ProductDetailsFragment extends Fragment {

    WebView webView;
    TextView text_negotiate, text_desc, text_website, text_doc;
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
        text_doc = rootView.findViewById(R.id.text_doc);

        btn_call = rootView.findViewById(R.id.btn_call);
        btn_whatsapp = rootView.findViewById(R.id.btn_whats);
        btn_back = rootView.findViewById(R.id.btn_back);

        text_negotiate.setText(itemJob.getProductNegotiable());
        text_desc.setText(itemJob.getProductDesc());
        text_doc.setText(itemJob.getProductDoc());
        text_website.setText(itemJob.getWebsiteLink());

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
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", itemJob.getProductPhoneNumber(), null));
            startActivity(intent);
        });
        btn_whatsapp.setOnClickListener(view -> {
            String phone = "91" + itemJob.getProductPhoneNumber().replace("+91", "");
            String url = "https://api.whatsapp.com/send?phone=" + phone;
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
