package com.jellysoft.sundigitalindia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.HomeProductAdapter;
import com.example.item.ItemProduct;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.EndlessRecyclerViewScrollListener;
import com.example.util.Events;
import com.example.util.GlobalBus;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.RvOnClickListener;
import com.example.util.UserUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class LatestProduct extends AppCompatActivity {
    public RecyclerView recyclerView;
    ArrayList<ItemProduct> mListItem;
    HomeProductAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    boolean isFirst = true, isOver = false;
    private int pageIndex = 1;
    boolean isLatest = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item);

        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("பொருட்கள் பட்டியல்");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mListItem = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        isLatest = bundle.getBoolean("isLatest");
        lyt_not_found = findViewById(R.id.lyt_not_found);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.vertical_courses_list);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(layoutManager);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case 0:
                        return 1;
                    default:
                        return 1;
                }
            }
        });


        if (NetworkUtils.isConnected(LatestProduct.this)) {
            getLatestOrRecent();
        } else {
            Toast.makeText(LatestProduct.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }
   recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pageIndex++;
                            getLatestOrRecent();
                        }
                    }, 1000);
                }
            }
        });
    }
    private void getLatestOrRecent() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_latest_product");
        jsObj.addProperty("user_id", UserUtils.getUserId());
        jsObj.addProperty("page", pageIndex);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (isFirst)
                    showProgress(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (isFirst)
                    showProgress(false);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject jsonObject;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.has(Constant.STATUS)) {
                                lyt_not_found.setVisibility(View.VISIBLE);
                            } else {
                                ItemProduct objItem = new ItemProduct();
                                objItem.setProductLogo(jsonObject.getString(Constant.PRODUCT_LOGO));
                                objItem.setId(jsonObject.getString(Constant.PRODUCT_ID));
                                objItem.setProductType(jsonObject.getString(Constant.PRODUCT_TYPE));
                                objItem.setProductName(jsonObject.getString(Constant.PRODUCT_NAME));
                                objItem.setWebsiteLink(jsonObject.getString(Constant.WEBSITE_LINK));
                                objItem.setProductPhoneNumber(jsonObject.getString(Constant.PRODUCT_PHONE_NO));
                                objItem.setProductPhoneNumber2(jsonObject.getString(Constant.PRODUCT_PHONE_NO2));
                                objItem.setProductCategoryName(jsonObject.getString(Constant.CATEGORY_NAME));
                                objItem.setProductPrice(jsonObject.getString(Constant.PRODUCT_PRICE));
                                objItem.setProductSellingPrice(jsonObject.getString(Constant.PRODUCT_SELLING_PRICE));
                                objItem.setCity(jsonObject.getString(Constant.CITY_NAME));
                                objItem.setProductDate(jsonObject.getString(Constant.PRODUCT_START_DATE));
                                objItem.setPLate(jsonObject.getString(Constant.PRODUCT_END_DATE));
                                objItem.setViews(jsonObject.getString("views"));
                                mListItem.add(objItem);
                            }
                        }
                        Log.d("mList", mListItem.toString());
                    } else {
                        isOver = true;
                        // when there is no data in first time
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showProgress(false);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {
        if (mListItem.size() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);
            if (isFirst) {
                isFirst = false;
                adapter = new HomeProductAdapter(LatestProduct.this, mListItem);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            adapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String jobId = mListItem.get(position).getId();
                    Intent intent = new Intent(LatestProduct.this, ProductDetailsActivity.class);
                    intent.putExtra("Id", jobId);
                    startActivity(intent);
                }
            });

        }
    }

    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Subscribe
    public void getSaveJob(Events.SaveJob saveJob) {
        for (int i = 0; i < mListItem.size(); i++) {
            if (mListItem.get(i).getId().equals(saveJob.getJobId())) {
                adapter.notifyItemChanged(i);
            }
        }
    }
}