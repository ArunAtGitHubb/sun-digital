package com.example.adapter;

import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_CITY_ID;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_FOOD_ID;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_FOOD_PRICE;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_FOOD_QTY;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_RES_ID;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_RES_NAME;
import static com.example.util.CartReaderContract.CartEntry.TABLE_CART;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.db.CartDbHelper;
import com.example.item.ItemFood;
import com.example.util.RvOnClickListener;
import com.jellysoft.sundigitalindia.CartDetailsActivity;
import com.jellysoft.sundigitalindia.R;
import com.jellysoft.sundigitalindia.RestaurantDetailsActivity;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemFood> dataList;
    private Context mContext;
    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private RvOnClickListener clickListener;
    CartDbHelper dbHelper;
    SQLiteDatabase db;

    public CartAdapter(Context context, ArrayList<ItemFood> dataList, CartDbHelper dbHelper) {
        this.dataList = dataList;
        this.mContext = context;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db = dbHelper.getWritableDatabase();

        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
            return new ItemRowHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item1, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @SuppressLint({"Range", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == VIEW_TYPE_ITEM) {
            final ItemRowHolder holder = (ItemRowHolder) viewHolder;

            final ItemFood food = dataList.get(position);
            holder.foodName.setText(food.getName());
            int price = Float.valueOf(food.getPrice()).intValue();
            holder.foodPrice.setText("Rs. " + price + "/-");
            holder.foodCount.setText(String.valueOf(food.getQty()));

            int foodId = Integer.parseInt(food.getId());

            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_FOOD_PRICE, price);
            values.put(COLUMN_NAME_RES_ID, food.getCatId());
            values.put(COLUMN_NAME_CITY_ID, food.getCityId());
            values.put(COLUMN_NAME_RES_NAME, food.getCategory());

            holder.foodPlus.setOnClickListener(view -> {
                RestaurantDetailsActivity.showCart();
                food.setCount(food.getCount() + 1);

                String selection = COLUMN_NAME_FOOD_ID + " = ?";
                String[] selectionArgs = {String.valueOf(foodId)};
                values.put(COLUMN_NAME_FOOD_QTY, food.getCount());
                Cursor cursor = db.query(TABLE_CART, new String[]{COLUMN_NAME_FOOD_ID},
                        COLUMN_NAME_FOOD_ID + "=?" ,new String[]{food.getId()},
                        null, null, null, null);
                if(cursor.getCount() > 0) {
                    db.update(TABLE_CART, values, selection, selectionArgs);
                } else {
                    values.put(COLUMN_NAME_FOOD_ID, food.getId());
                    db.insert(TABLE_CART, null, values);
                }
                cursor.close();
                holder.foodCount.setText(String.valueOf(food.getCount()));
                CartDetailsActivity.updatePrice();
            });

            holder.foodMinus.setOnClickListener(view -> {
                int count = food.getCount() - 1;
                String selection = COLUMN_NAME_FOOD_ID + " = ?";
                String[] selectionArgs = {String.valueOf(foodId)};

                if(count >= 0) {
                    food.setCount(count);
                    values.put(COLUMN_NAME_FOOD_QTY, food.getCount());
                    db.update(TABLE_CART, values, selection, selectionArgs);
                }

                Cursor cartQty = db.rawQuery("SELECT SUM(food_qty) as total FROM cart", null);
                cartQty.moveToFirst();

                if(cartQty.getInt(0) > 0) {
                    RestaurantDetailsActivity.showCart();
                } else {
                    RestaurantDetailsActivity.hideCart();
                }
                cartQty.close();

                holder.foodCount.setText(String.valueOf(food.getCount()));
                CartDetailsActivity.updatePrice();
            });


//            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
//                }
//            });

//            switch (food.getJobType()) {
//                case Constant.JOB_TYPE_HOURLY:
//                    holder.jobType.setTextColor(mContext.getResources().getColor(R.color.hourly_time_text));
//                    holder.cardViewType.setCardBackgroundColor(mContext.getResources().getColor(R.color.hourly_time_bg));
//                    break;
//                case Constant.JOB_TYPE_HALF:
//                    holder.jobType.setTextColor(mContext.getResources().getColor(R.color.half_time_text));
//                    holder.cardViewType.setCardBackgroundColor(mContext.getResources().getColor(R.color.half_time_bg));
//                    break;
//                case Constant.JOB_TYPE_FULL:
//                    holder.jobType.setTextColor(mContext.getResources().getColor(R.color.full_time_text));
//                    holder.cardViewType.setCardBackgroundColor(mContext.getResources().getColor(R.color.full_time_bg));
//                    break;
//            }
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
        TextView foodName, foodPrice, foodCount;
        ImageView foodPlus, foodMinus;
        LinearLayout lyt_parent;
        CardView viewCart;

        ItemRowHolder(View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodCount = itemView.findViewById(R.id.foodCount);

            foodPlus = itemView.findViewById(R.id.btnPlus);
            foodMinus = itemView.findViewById(R.id.btnMinus);

//            lyt_parent = itemView.findViewById(R.id.rootLayout);
//            cardViewType = itemView.findViewById(R.id.cardJobType);
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        static ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }
    }
}
