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

import com.example.adapter.HomeMatrimonyAdapter;
import com.example.item.ItemMatrimony;
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

public class LatestFemaleMatrimony extends AppCompatActivity {
    public RecyclerView recyclerView;
    ArrayList<ItemMatrimony> mListItem;
    HomeMatrimonyAdapter adapter;
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
        toolbar.setTitle("மணப்பெண் வரன்கள்");
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


        if (NetworkUtils.isConnected(LatestFemaleMatrimony.this)) {
            getLatestOrRecent();
        } else {
            Toast.makeText(LatestFemaleMatrimony.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
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
        jsObj.addProperty("method_name", "get_latest_matrimony_female");
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
                                ItemMatrimony objItem = new ItemMatrimony();
                                objItem.setId(objJson.getString(Constant.id));
                                objItem.setCity(objJson.getString(Constant.CITY_NAME));
                                objItem.setMatrimonyName(objJson.getString(Constant.MATRIMONY_NAME));
                                objItem.setMatrimonyGender(objJson.getString(Constant.MATRIMONY_GENDER));
                                objItem.setMatrimonyReligion(objJson.getString(Constant.MATRIMONY_RELIGION));
                                objItem.setMatrimonyArea(objJson.getString(Constant.MATRIMONY_AREA));
                                objItem.setMatrimonyMaritalStatus(objJson.getString(Constant.MATRIMONY_MARITAL_STATUS));
                                objItem.setMatrimonyDob(objJson.getString(Constant.MATRIMONY_DOB));
                                objItem.setMatrimonyAge(objJson.getString(Constant.MATRIMONY_AGE));
                                objItem.setMatrimonyEducation(objJson.getString(Constant.MATRIMONY_EDUCATION));
                                objItem.setMatrimonyCareer(objJson.getString(Constant.MATRIMONY_CAREER));
                                objItem.setMatrimonySalary(objJson.getString(Constant.MATRIMONY_SALARY));
                                objItem.setMatrimonyDesc(objJson.getString(Constant.MATRIMONY_DESC));
                                objItem.setMatrimonyPartnerExpect(objJson.getString(Constant.MATRIMONY_PARTNER_EXPECT));
                                objItem.setMatrimonyPersonName(objJson.getString(Constant.MATRIMONY_PERSON_NAME));
                                objItem.setMatrimonyPhoneNumber(objJson.getString(Constant.MATRIMONY_PHONE_NUMBER));
                                objItem.setMatrimonyPhoneNumber2(objJson.getString(Constant.MATRIMONY_PHONE_NUMBER2));
                                objItem.setMatrimonyImage(objJson.getString(Constant.MATRIMONY_IMAGE));
                                objItem.setMatrimonyLogo(objJson.getString(Constant.MATRIMONY_LOGO));
                                objItem.setMatrimonyImage2(objJson.getString(Constant.MATRIMONY_IMAGE2));
                                objItem.setMatrimonyImage3(objJson.getString(Constant.MATRIMONY_IMAGE3));
                                objItem.setMatrimonyImage4(objJson.getString(Constant.MATRIMONY_IMAGE4));
                                objItem.setMatrimonySDate(objJson.getString(Constant.MATRIMONY_START_DATE));
                                objItem.setMatrimonyEDate(objJson.getString(Constant.MATRIMONY_END_DATE));
                                objItem.setCategoryName(objJson.getString(Constant.CATEGORY_NAME));
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
                adapter = new HomeMatrimonyAdapter(LatestFemaleMatrimony.this, mListItem);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            adapter.setOnItemClickListener(position -> {
                String jobId = mListItem.get(position).getId();
                Intent intent = new Intent(LatestFemaleMatrimony.this, MatrimonyDetailsActivity.class);
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
                adapter.notifyItemChanged(i);
            }
        }
    }
}