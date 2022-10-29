package com.example.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
            holder.text_job_title.setText(singleItem.getJobName());
            holder.city.setText(singleItem.getCity());
            holder.salary.setText(singleItem.getJobSalary());
            holder.vacancy.setText(singleItem.getJobVacancy());
            holder.company.setText(singleItem.getJobCompanyName());
            holder.text_time.setText(singleItem.getJobTime());
            holder.jobid.setText("JD" + singleItem.getId());
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
                String phone = singleItem.getJobPhoneNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phone));
                mContext.startActivity(intent);
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
        TextView jobid, call, vacancy;
        TextView text_job_title, jobType, salary, text_time, city, company;
        LinearLayout lyt_parent;
        Button btnApplyJob;
        CardView cardViewType;
        CircleImageView jobImage;
        ImageView share;

        ItemRowHolder(View itemView) {
            super(itemView);
            text_job_title = itemView.findViewById(R.id.text_job_title);
            text_time = itemView.findViewById(R.id.text_time);
            vacancy = itemView.findViewById(R.id.text_vacancy);
            jobType = itemView.findViewById(R.id.text_job_type);
            city = itemView.findViewById(R.id.city);
            salary = itemView.findViewById(R.id.salary);
            company = itemView.findViewById(R.id.company);

            call = itemView.findViewById(R.id.call);
            jobid =itemView.findViewById(R.id.text_job_id);

            lyt_parent = itemView.findViewById(R.id.rootLayout);
            cardViewType = itemView.findViewById(R.id.cardJobType);
            jobImage = itemView.findViewById(R.id.image_job);
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
