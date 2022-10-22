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

import com.example.item.ItemProduct;
import com.example.item.ItemService;
import com.example.util.Constant;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.jellysoft.sundigitalindia.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemService> dataList;
    private Context mContext;
    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private RvOnClickListener clickListener;

    public ServiceAdapter(Context context, ArrayList<ItemService> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_service_item_new, parent, false);
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

            final ItemService singleItem = dataList.get(position);
            holder.jobTitle.setText(singleItem.getServiceName());
            holder.jobid.setText("SDI00" + singleItem.getId());
            holder.jobCat.setText(singleItem.getServiceCategoryName());
            holder.company.setText(singleItem.getServiceCompanyName());
            holder.city.setText(singleItem.getCity());
//        holder.ldate.setText("கடைசி தேதி: "+singleItem.getpLate());
            holder.pdate.setText(singleItem.getServiceDate());
            holder.ldate.setText("     : " + singleItem.getPLate());
            holder.salary.setText(singleItem.getServiceCost());

            Picasso.get().load(singleItem.getServiceLogo()).placeholder(R.drawable.placeholder).into(holder.jobImage);

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
                            singleItem.getServiceName() + "\n" +
                                    mContext.getString(R.string.job_company_lbl) + singleItem.getServiceCompanyName() + "\n" +
                                    mContext.getString(R.string.job_designation_lbl) + singleItem.getServiceDesignation() + "\n" +
                                    mContext.getString(R.string.job_phone_lbl) + singleItem.getServicePhoneNumber() + "\n" +
                                    mContext.getString(R.string.job_address_lbl) + singleItem.getCity() + "\n\n" +
                                    "Download Application here https://play.google.com/store/apps/details?id=com.jellysoft.sundigitalindia");
                    sendIntent.setType("text/plain");
                    mContext.startActivity(sendIntent);
                }
            });
            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone = singleItem.getServicePhoneNumber();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+phone));
                    mContext.startActivity(intent);
                }
            });
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
        TextView jobTitle, jobAddress, pdate, vacancy, company, jobid, jobCat, city, ldate, salary, call;
        LinearLayout lyt_parent;
        Button btnApplyJob;
        CardView cardViewType;
        CircleImageView jobImage;
        ImageView share;

        ItemRowHolder(View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.text_job_title);
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
