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

import com.example.adapter.CityAdapter;
import com.example.item.ItemCity;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.EndlessRecyclerViewScrollListener;
import com.example.util.GlobalBus;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.UserUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CityRstrnt extends AppCompatActivity {
    ArrayList<ItemCity> mListItem;
    public RecyclerView recyclerView;
    CityAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    boolean isFirst = true, isOver = false;
    private int pageIndex = 1;
    String categoryId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item);

        categoryId = getIntent().getStringExtra("categoryId");

        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Restaurant List");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mListItem = new ArrayList<>();
        lyt_not_found = findViewById(R.id.lyt_not_found);

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.vertical_courses_list);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(CityRstrnt.this, 3);
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


        if (NetworkUtils.isConnected(this)) {
            getCategoryItem();
        } else {
            Toast.makeText(this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pageIndex++;
                            getCategoryItem();
                        }
                    }, 1000);
                } else {
                    adapter.hideHeader();
                }
            }
        });
    }
    private void getCategoryItem() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_restaurant_by_city_id");
        jsObj.addProperty("cat_id", categoryId);
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
                Log.d("result2", result);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);

                    JSONObject jsonObject;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Log.d("result2", "inside for");
                            jsonObject = jsonArray.getJSONObject(i);
                            ItemCity objItem = new ItemCity();
                            objItem.setCityId(jsonObject.getString("cid"));
                            objItem.setCityName(jsonObject.getString("category_name"));
                            objItem.setCityImage(jsonObject.getString("category_image"));
                            objItem.setCounting(jsonObject.getString("num"));
                            mListItem.add(objItem);
                        }
                    } else {
                        isOver = true;
                        if (adapter != null) { // when there is no data in first time
                            adapter.hideHeader();
                        }
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
            Log.d("result2", "inside emty");
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {

            lyt_not_found.setVisibility(View.GONE);
            if (isFirst) {
                adapter = new CityAdapter(this, mListItem);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            adapter.setOnItemClickListener(position -> {
                String jobId = mListItem.get(position).getCityId();
                Intent intent = new Intent(CityRstrnt.this, RestaurantDetailsActivity.class);
                intent.putExtra("categoryId", jobId);
                startActivity(intent);
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

}