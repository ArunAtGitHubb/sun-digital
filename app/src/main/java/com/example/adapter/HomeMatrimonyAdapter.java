package com.example.adapter;

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

import com.example.item.ItemMatrimony;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.jellysoft.sundigitalindia.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeMatrimonyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList dataList;
    private Context mContext;
    private RvOnClickListener clickListener;

    public HomeMatrimonyAdapter(Context context, ArrayList dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_matrimony_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;

        final ItemMatrimony singleItem = (ItemMatrimony) dataList.get(position);

        holder.matrimonyName.setText(singleItem.getMatrimonyName());
        holder.matrimonyAge.setText(singleItem.getMatrimonyAge());
        holder.matrimonyJob.setText(singleItem.getMatrimonyCareer());
        holder.matrimonyCity.setText(singleItem.getCity());
        holder.matrimonyGender.setText(singleItem.getMatrimonyGender());
        holder.matrimonyReligion.setText(singleItem.getMatrimonyReligion());
        holder.text_caste.setText(singleItem.getCategoryName());
        holder.text_area.setText(singleItem.getMatrimonyArea());
        holder.text_matrimony_id.setText("MM" + singleItem.getId());

        Picasso.get().load(singleItem.getMatrimonyLogo()).placeholder(R.drawable.placeholder).into(holder.matrimonyImage);
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
                    singleItem.getMatrimonyName() + "\n" +
                            mContext.getString(R.string.job_phone_lbl) + singleItem.getMatrimonyPhoneNumber() + "\n" +
                            mContext.getString(R.string.job_address_lbl) + singleItem.getCity() + "\n\n" +
                            "Download Application here https://play.google.com/store/apps/details?id=com.jellysoft.sundigitalindia");
            sendIntent.setType("text/plain");
            mContext.startActivity(sendIntent);
        });
        holder.call.setOnClickListener(view -> {
            String phone = singleItem.getMatrimonyPhoneNumber();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+phone));
            mContext.startActivity(intent);
        });

        holder.whatsapp.setOnClickListener(view -> {
            String phone = "91" + singleItem.getMatrimonyPhoneNumber().replace("+91", "");
            String url = "https://api.whatsapp.com/send?phone=" + phone;
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
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView call, whatsapp;
        TextView matrimonyJob, matrimonyName, text_area, text_caste, text_matrimony_id,
                matrimonyAge, matrimonyCity, matrimonyGender, matrimonyReligion;
        LinearLayout lyt_parent;
        Button btnApplyJob;
        CardView cardViewType;
        CircleImageView matrimonyImage;
        ImageView share;


        @RequiresApi(api = Build.VERSION_CODES.O)
        ItemRowHolder(View itemView) {
            super(itemView);

            matrimonyName = itemView.findViewById(R.id.text_name);
            matrimonyAge = itemView.findViewById(R.id.text_age);
            matrimonyJob = itemView.findViewById(R.id.job);
            matrimonyCity = itemView.findViewById(R.id.city);
            text_area = itemView.findViewById(R.id.area);
            text_caste = itemView.findViewById(R.id.text_caste);
            matrimonyGender = itemView.findViewById(R.id.text_matrimony_religion);
            matrimonyReligion = itemView.findViewById(R.id.text_religion);
            text_matrimony_id = itemView.findViewById(R.id.text_matrimony_id);

            lyt_parent = itemView.findViewById(R.id.rootLayout);
            cardViewType = itemView.findViewById(R.id.cardJobType);
            matrimonyImage = itemView.findViewById(R.id.image_job);
            share = itemView.findViewById(R.id.share);
            btnApplyJob = itemView.findViewById(R.id.btn_apply_job);
            call = itemView.findViewById(R.id.call);
            whatsapp = itemView.findViewById(R.id.whatsapp);
        }
    }
}
