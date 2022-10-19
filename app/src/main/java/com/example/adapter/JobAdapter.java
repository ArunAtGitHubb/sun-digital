package com.example.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jellysoft.sundigitalindia.MyApplication;
import com.jellysoft.sundigitalindia.R;
import com.jellysoft.sundigitalindia.SignInActivity;
import com.example.item.ItemJob;
import com.example.util.ApplyJob;
import com.example.util.Constant;
import com.example.util.NetworkUtils;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.example.util.SaveClickListener;
import com.example.util.SaveJob;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class JobAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemJob> dataList;
    private Context mContext;
    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private RvOnClickListener clickListener;

    public JobAdapter(Context context, ArrayList<ItemJob> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_job_item_new, parent, false);
            return new ItemRowHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item1, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == VIEW_TYPE_ITEM) {
            final ItemRowHolder holder = (ItemRowHolder) viewHolder;

            final ItemJob singleItem = dataList.get(position);
            holder.jobTitle.setText(singleItem.getJobName());
            holder.jobid.setText("SDI00"+singleItem.getId());
            holder.jobCat.setText(singleItem.getJobCategoryName());
            holder.company.setText(singleItem.getJobCompanyName());
            holder.city.setText(singleItem.getCity());
            holder.vacancy.setText(singleItem.getJobVacancy());
//        holder.ldate.setText("கடைசி தேதி: "+singleItem.getpLate());
            holder.pdate.setText(singleItem.getJobDate());
            holder.ldate.setText("     : "+singleItem.getpLate());
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
                @SuppressLint("MissingPermission")
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
                            singleItem.getJobName() + "\n" +
                                    mContext.getString(R.string.job_company_lbl) + singleItem.getJobCompanyName() + "\n" +
                                    mContext.getString(R.string.job_designation_lbl) + singleItem.getJobDesignation() + "\n" +
                                    mContext.getString(R.string.job_phone_lbl) + singleItem.getJobPhoneNumber() + "\n" +
                                    mContext.getString(R.string.job_address_lbl) + singleItem.getCity() + "\n\n" +
                                    "Download Application here https://play.google.com/store/apps/details?id=com.jellysoft.sundigitalindia");
                    sendIntent.setType("text/plain");
                    mContext.startActivity(sendIntent);
                }
            });
            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone = singleItem.getJobPhoneNumber();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+phone));
                    mContext.startActivity(intent);
                }
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
    }
    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() + 1 : 0);
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, jobAddress, jobType,pdate,vacancy,company,jobid,jobCat,city,ldate,salary,call;
        LinearLayout lyt_parent;
        Button btnApplyJob;
        CardView cardViewType;
        CircleImageView jobImage;
        ImageView share;

        ItemRowHolder(View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.text_job_title);
            jobType = itemView.findViewById(R.id.text_job_type);
            city = itemView.findViewById(R.id.city);
            jobCat = itemView.findViewById(R.id.jobCat);
            pdate = itemView.findViewById(R.id.pdate);
            call = itemView.findViewById(R.id.call);
            vacancy = itemView.findViewById(R.id.vacancy);
            company =itemView.findViewById(R.id.company);
            jobid =itemView.findViewById(R.id.text_job_id);

            lyt_parent = itemView.findViewById(R.id.rootLayout);
            cardViewType = itemView.findViewById(R.id.cardJobType);
            jobImage = itemView.findViewById(R.id.image_job);
            ldate = itemView.findViewById(R.id.ldate);
            salary = itemView.findViewById(R.id.salary);
            share = itemView.findViewById(R.id.share);
            btnApplyJob = itemView.findViewById(R.id.btn_apply_job);
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        static ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }
}
