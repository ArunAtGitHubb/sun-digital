package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
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

import com.example.item.ItemJob;
import com.example.item.ItemProduct;
import com.example.util.Constant;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.jellysoft.sundigitalindia.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;

        final ItemProduct singleItem = (ItemProduct) dataList.get(position);
        holder.jobTitle.setText(singleItem.getProductName());
        holder.jobid.setText("SDI00"+singleItem.getId());
//        holder.jobCat.setText(singleItem.getJobCategoryName());
        holder.company.setText(singleItem.getProductCompanyName());
        holder.city.setText(singleItem.getCity());
//        holder.vacancy.setText(singleItem.getProductVacancy());
//        holder.ldate.setText("கடைசி தேதி: "+singleItem.getpLate());
//        holder.pdate.setText(singleItem.getProductDate());
//        holder.ldate.setText(singleItem.getpLate());
        holder.salary.setText(singleItem.getProductSalary());

        holder.jobType.setText(singleItem.getProductType());


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

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("bvm",String.valueOf(singleItem.getProductPhoneNumber()));
                String phone = singleItem.getProductPhoneNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phone));
                mContext.startActivity(intent);
            }
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


        switch (singleItem.getProductType()) {
            case Constant.JOB_TYPE_HOURLY:
                holder.jobType.setTextColor(mContext.getResources().getColor(R.color.hourly_time_text));
                holder.cardViewType.setCardBackgroundColor(mContext.getResources().getColor(R.color.hourly_time_bg));
                break;
            case Constant.JOB_TYPE_HALF:
                holder.jobType.setTextColor(mContext.getResources().getColor(R.color.half_time_text));
                holder.cardViewType.setCardBackgroundColor(mContext.getResources().getColor(R.color.half_time_bg));
                break;
            case Constant.JOB_TYPE_FULL:
                holder.jobType.setTextColor(mContext.getResources().getColor(R.color.full_time_text));
                holder.cardViewType.setCardBackgroundColor(mContext.getResources().getColor(R.color.full_time_bg));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, jobAddress, jobType,pdate,vacancy,company,jobid,jobCat,city,salary,ldate,call, whatsapp;
        LinearLayout lyt_parent;
        Button btnApplyJob;
        CardView cardViewType;
        CircleImageView jobImage;
        ImageView share;



        @RequiresApi(api = Build.VERSION_CODES.O)
        ItemRowHolder(View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.text_job_title);
            jobType = itemView.findViewById(R.id.text_job_type);
            city = itemView.findViewById(R.id.city);
            salary= itemView.findViewById(R.id.salary);
//            jobCat = itemView.findViewById(R.id.jobCat);
//            pdate = itemView.findViewById(R.id.pdate);
//            ldate = itemView.findViewById(R.id.ldate);
//            vacancy = itemView.findViewById(R.id.vacancy);
            company =itemView.findViewById(R.id.company);
            jobid =itemView.findViewById(R.id.text_job_id);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            cardViewType = itemView.findViewById(R.id.cardJobType);
            jobImage = itemView.findViewById(R.id.image_job);
            share = itemView.findViewById(R.id.share);
            btnApplyJob = itemView.findViewById(R.id.btn_apply_job);
            call = itemView.findViewById(R.id.call);
            whatsapp = itemView.findViewById(R.id.whatsapp);
//            Typeface typeface = mContext.getResources().getFont(R.font.tamil);
//
//            jobTitle.setTypeface(typeface);
//            jobType.setTypeface(typeface);
//            city.setTypeface(typeface);
//            salary.setTypeface(typeface);
//            jobCat.setTypeface(typeface);
//            pdate.setTypeface(typeface);
//            ldate.setTypeface(typeface);
//            vacancy.setTypeface(typeface);
//            company.setTypeface(typeface);
//            jobid.setTypeface(typeface);
        }
    }
}
