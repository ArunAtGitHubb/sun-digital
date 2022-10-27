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
import com.example.util.Constant;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.jellysoft.sundigitalindia.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeJobAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList dataList;
    private Context mContext;
    private RvOnClickListener clickListener;

    public HomeJobAdapter(Context context, ArrayList dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_job_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;

        final ItemJob singleItem = (ItemJob) dataList.get(position);
        holder.jobTitle.setText(singleItem.getJobName());
        holder.jobid.setText("SDI00" + singleItem.getId());
        holder.text_job_time.setText(singleItem.getJobTime());
        holder.company.setText(singleItem.getJobCompanyName());
        holder.vacancy.setText(singleItem.getJobVacancy());
        holder.city.setText(singleItem.getCity());
        holder.salary.setText(singleItem.getJobSalary());
        holder.jobType.setText(singleItem.getJobType());

        Picasso.get().load(singleItem.getJobLogo()).placeholder(R.drawable.placeholder).into(holder.jobImage);

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
                    singleItem.getJobName() + "\n" +
                            mContext.getString(R.string.job_company_lbl) + singleItem.getJobCompanyName() + "\n" +
                            mContext.getString(R.string.job_designation_lbl) + singleItem.getJobDesignation() + "\n" +
                            mContext.getString(R.string.job_phone_lbl) + singleItem.getJobPhoneNumber() + "\n" +
                            mContext.getString(R.string.job_address_lbl) + singleItem.getCity() + "\n\n" +
                            "Download Application here https://play.google.com/store/apps/details?id=com.jellysoft.sundigitalindia");
            sendIntent.setType("text/plain");
            mContext.startActivity(sendIntent);
        });
        holder.call.setOnClickListener(view -> {
            Log.d("bvm",String.valueOf(singleItem.getJobPhoneNumber()));
            String phone = singleItem.getJobPhoneNumber();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+phone));
            mContext.startActivity(intent);
        });

        holder.whatsapp.setOnClickListener(view -> {
            String phone = singleItem.getJobPhoneNumber();
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
        });


        switch (singleItem.getJobType()) {
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
        TextView jobTitle, jobType, company, vacancy,
                jobid, city, salary, call, whatsapp, text_job_time;
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
            text_job_time = itemView.findViewById(R.id.text_time);
            vacancy = itemView.findViewById(R.id.text_vacancy);
            company = itemView.findViewById(R.id.company);
            jobid = itemView.findViewById(R.id.text_job_id);
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
