package com.example.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.item.ItemProduct;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.jellysoft.sundigitalindia.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList dataList;
    private Context mContext;
    private RvOnClickListener clickListener;

    public HomeProductAdapter(Context context, ArrayList dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_item, parent, false);
        return new ItemRowHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;

        final ItemProduct singleItem = (ItemProduct) dataList.get(position);
        holder.jobTitle.setText(singleItem.getProductName());
        holder.jobid.setText("PD" + singleItem.getId());
        holder.city.setText(singleItem.getCity());
        holder.area.setText(singleItem.getProductArea());
        holder.productPrice.setText("Rs. " + singleItem.getProductPrice().replace(".00", "") + "/-");
        holder.productSellingPrice.setText("Rs. " + singleItem.getProductSellingPrice().replace(".00", "") + "/-");
        holder.productDoc.setText(Objects.equals(singleItem.getProductDoc(), "YES") ? "YES" : "NO");

        Picasso.get().load(singleItem.getProductLogo()).placeholder(R.drawable.placeholder).into(holder.jobImage);
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
            }
        });

        holder.btnApplyJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
            }
        });

        holder.share.setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    singleItem.getProductName() + "\n" +
                            mContext.getString(R.string.job_company_lbl) + singleItem.getProductCompanyName() + "\n" +
                            mContext.getString(R.string.job_designation_lbl) + singleItem.getProductDesignation() + "\n" +
                            mContext.getString(R.string.job_phone_lbl) + singleItem.getProductPhoneNumber() + "\n" +
                            mContext.getString(R.string.job_address_lbl) + singleItem.getCity() + "\n\n" +
                            "Download Application here https://play.google.com/store/apps/details?id=com.jellysoft.sundigitalindia");
            sendIntent.setType("text/plain");
            mContext.startActivity(sendIntent);
        });
        holder.call.setOnClickListener(view -> {
            String phone = singleItem.getProductPhoneNumber();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            mContext.startActivity(intent);
        });

        holder.whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = singleItem.getProductPhoneNumber();
                String url = "https://api.whatsapp.com/send?phone=" + phone.substring(1);
                PackageManager pm = mContext.getPackageManager();
                try {
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, productPrice, productSellingPrice, productDoc,
                jobid, city, call, whatsapp, area;
        LinearLayout lyt_parent;
        Button btnApplyJob;
        CardView cardViewType;
        CircleImageView jobImage;
        ImageView share;


        @RequiresApi(api = Build.VERSION_CODES.O)
        ItemRowHolder(View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.text_job_title);
            city = itemView.findViewById(R.id.city);
            area = itemView.findViewById(R.id.area);
            productPrice = itemView.findViewById(R.id.product_price);
            productSellingPrice = itemView.findViewById(R.id.product_selling_price);
            productDoc = itemView.findViewById(R.id.product_document);

            jobid =itemView.findViewById(R.id.text_job_id);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            cardViewType = itemView.findViewById(R.id.cardJobType);
            jobImage = itemView.findViewById(R.id.image_job);
            share = itemView.findViewById(R.id.share);
            btnApplyJob = itemView.findViewById(R.id.btn_apply_job);
            call = itemView.findViewById(R.id.call);
            whatsapp = itemView.findViewById(R.id.whatsapp);
        }
    }
}
