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

import com.example.adapter.HomeServiceAdapter;
import com.example.item.ItemService;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.EndlessRecyclerViewScrollListener;
import com.example.util.Events;
import com.example.util.GlobalBus;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
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

public class LatestService extends AppCompatActivity {
    public RecyclerView recyclerView;
    ArrayList<ItemService> mListItem;
    HomeServiceAdapter adapter;
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
        toolbar.setTitle("சேவை பட்டியல்");
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


        if (NetworkUtils.isConnected(LatestService.this)) {
            getLatestOrRecent();
        } else {
            Toast.makeText(LatestService.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
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
        jsObj.addProperty("method_name", "get_latest_service");
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
                Log.d("result", result);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            if (objJson.has(Constant.STATUS)) {
                                lyt_not_found.setVisibility(View.VISIBLE);
                            } else {
                                ItemService objItem = new ItemService();
                                objItem.setServiceLogo(objJson.getString(Constant.SERVICE_LOGO));
                                objItem.setId(objJson.getString(Constant.SERVICE_ID));
                                objItem.setServiceName(objJson.getString(Constant.SERVICE_NAME));
                                objItem.setServiceArea(objJson.getString(Constant.SERVICE_AREA));
                                objItem.setServiceTime(objJson.getString(Constant.SERVICE_TIME));
                                objItem.setServiceCost(objJson.getString(Constant.SERVICE_COST));
                                objItem.setServiceImage(objJson.getString(Constant.SERVICE_IMAGE));
                                objItem.setServiceMail(objJson.getString(Constant.SERVICE_MAIL));
                                objItem.setServiceSkill(objJson.getString(Constant.SERVICE_SKILL));
                                objItem.setWebsiteLink(objJson.getString(Constant.WEBSITE_LINK));
                                objItem.setServiceAddress(objJson.getString(Constant.SERVICE_ADDRESS));
                                objItem.setServicePhoneNumber(objJson.getString(Constant.SERVICE_PHONE_NO));
                                objItem.setServiceCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                                objItem.setCity(objJson.getString(Constant.CITY_NAME));
                                objItem.setServiceDate(objJson.getString(Constant.SERVICE_START_DATE));
                                objItem.setPLate(objJson.getString(Constant.SERVICE_END_DATE));
                                objItem.setViews(objJson.getString("views"));
                                mListItem.add(objItem);
                            }
                        }
                    } else {
                        isOver = true;
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
                adapter = new HomeServiceAdapter(LatestService.this, mListItem);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            adapter.setOnItemClickListener(position -> {
                String jobId = mListItem.get(position).getId();
                Intent intent = new Intent(LatestService.this, ServiceDetailsActivity.class);
                intent.putExtra("Id", jobId);
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
                mListItem.get(i).setServiceFavourite(saveJob.isSave());
                adapter.notifyItemChanged(i);
            }
        }
    }
}