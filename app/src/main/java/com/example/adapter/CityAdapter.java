package com.example.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jellysoft.sundigitalindia.R;
import com.example.item.ItemCity;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemCity> dataList;
    private Context mContext;
    private final int VIEW_TYPE_LOADINGs = 0;
    private final int VIEW_TYPE_ITEMs = 1;
    private RvOnClickListener nclickListener;

    public CityAdapter(Context context, ArrayList<ItemCity> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_city, parent, false);
            return new ItemRowHolder(v);
//        } else {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
//            return new ProgressViewHolder(v);
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == VIEW_TYPE_ITEMs) {
            final ItemRowHolder holder = (ItemRowHolder) viewHolder;
            final ItemCity singleItem = dataList.get(position);

            holder.text.setText(singleItem.getCityName());
            holder.count.setText("("+singleItem.getCounting()+")");
            Picasso.get().load(singleItem.getCityImage()).placeholder(R.drawable.placeholder).into(holder.image);
            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), nclickListener);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size()  : 0);
    }

    public void hideHeader() {
//        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADINGs : VIEW_TYPE_ITEMs;
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.nclickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;
        TextView count;
        CardView lyt_parent;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            count = itemView.findViewById(R.id.count);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
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
