package com.example.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jellysoft.sundigitalindia.R;
import com.example.item.ItemCategory;
import com.example.util.RvOnClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeGridAdapter  extends ArrayAdapter<ItemCategory> {

    private ArrayList<ItemCategory> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;

    public HomeGridAdapter(Context context, ArrayList<ItemCategory> dataList) {
        super(context,0,dataList);
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.row_category, parent, false);
        }
        ImageView  image = listitemView.findViewById(R.id.image);
        TextView  text = listitemView.findViewById(R.id.text);
       ItemCategory cat = getItem(position);
        text.setText(cat.getCategoryName());
        Picasso.get().load(cat.getCategoryImage()).into(image);
        return listitemView;
    }
    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }


}
