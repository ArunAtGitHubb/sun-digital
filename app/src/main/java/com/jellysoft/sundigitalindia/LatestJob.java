package com.jellysoft.sundigitalindia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.JobAdapter;
import com.example.item.ItemJob;
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

public class LatestJob extends AppCompatActivity {
    public RecyclerView recyclerView;
    ArrayList<ItemJob> mListItem;
    JobAdapter adapter;
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
        toolbar.setTitle("புதிய வேலை பட்டியல்");
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


        if (NetworkUtils.isConnected(LatestJob.this)) {
            getLatestOrRecent();
        } else {
            Toast.makeText(LatestJob.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
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
                } else {
                    adapter.hideHeader();
                }
            }
        });
    }
    private void getLatestOrRecent() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", isLatest ? "get_latest_job" : "get_recent_job");
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
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            if (objJson.has(Constant.STATUS)) {
                                lyt_not_found.setVisibility(View.VISIBLE);
                            } else {
                                ItemJob objItem = new ItemJob();
                                objItem.setJobLogo(objJson.getString(Constant.JOB_LOGO));
                                objItem.setId(objJson.getString(Constant.JOB_ID));
                                objItem.setJobType(objJson.getString(Constant.JOB_TYPE));
                                objItem.setJobName(objJson.getString(Constant.JOB_NAME));
                                objItem.setJobCompanyName(objJson.getString(Constant.JOB_COMPANY_NAME));
                                objItem.setJobCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                                objItem.setCity(objJson.getString(Constant.CITY_NAME));
                                objItem.setJobPhoneNumber(objJson.getString(Constant.JOB_PHONE_NO));
                                objItem.setJobVacancy(objJson.getString(Constant.JOB_VACANCY));
                                objItem.setJobDate(objJson.getString(Constant.JOB_DATE));
                                objItem.setJobSalary(objJson.getString(Constant.JOB_SALARY));
                                objItem.setJobTime(objJson.getString(Constant.JOB_TIME));
                                objItem.setpLate(objJson.getString("job_date1"));
                                objItem.setViews(objJson.getString("views"));
                                objItem.setJobFavourite(objJson.getBoolean(Constant.JOB_FAVOURITE));
                                mListItem.add(objItem);
                            }
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
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {

            lyt_not_found.setVisibility(View.GONE);
            if (isFirst) {
                isFirst = false;
                adapter = new JobAdapter(LatestJob.this, mListItem);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            adapter.setOnItemClickListener(position -> {
                String jobId = mListItem.get(position).getId();
                Intent intent = new Intent(LatestJob.this, JobDetailsActivity.class);
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
                mListItem.get(i).setJobFavourite(saveJob.isSave());
                adapter.notifyItemChanged(i);
            }
        }
    }
}