package com.example.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.adapter.HomeJobAdapter;
import com.example.item.ItemJob;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.NetworkUtils;
import com.example.util.UserUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jellysoft.sundigitalindia.JobDetailsActivity;
import com.jellysoft.sundigitalindia.LatestJob;
import com.jellysoft.sundigitalindia.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Arun.
 */
public class JobOpportunitiesFragment extends Fragment {

    ArrayList<ItemJob> mListItem;
    RecyclerView rvLatestJob;
    HomeJobAdapter adapter;
    ViewPager mviewPager;
    Button textLatestViewAll, textJobCategories, textJobAllCities;
    boolean isFirst = true, isOver = false;
    private int pageIndex = 1;
    final int[] position = {0};
    TabLayout tabLayout;
    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job_opportunities, container, false);
        Log.d("events", "onCreate");
        mListItem = new ArrayList<>();
        tabLayout = getActivity().findViewById(R.id.tabLayout);
        tabLayout.setVisibility(View.VISIBLE);
        mviewPager = getActivity().findViewById(R.id.viewPager);
        rvLatestJob = rootView.findViewById(R.id.rv_latest);

        textLatestViewAll = rootView.findViewById(R.id.textLatestViewAll);
        textJobCategories = rootView.findViewById(R.id.textJobCategories);
        textJobAllCities = rootView.findViewById(R.id.textJobAllCities);

        rvLatestJob.setHasFixedSize(true);
        rvLatestJob.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvLatestJob.setFocusable(false);
        rvLatestJob.setNestedScrollingEnabled(true);
        rvLatestJob.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                position[0] = rvLatestJob.computeHorizontalScrollOffset() / 1000;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        textLatestViewAll.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), LatestJob.class);
            intent.putExtra("isLatest", true);
            startActivity(intent);
        });

        textJobCategories.setOnClickListener(view -> mviewPager.setCurrentItem(5));
        textJobAllCities.setOnClickListener(view -> mviewPager.setCurrentItem(6));

        if (NetworkUtils.isConnected(getActivity())) {
            getLatestJobs();
        } else {
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    private void getLatestJobs() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_latest_job");
        jsObj.addProperty("user_id", UserUtils.getUserId());
        jsObj.addProperty("page", pageIndex);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                                ItemJob objItem = new ItemJob();
                                objItem.setJobLogo(objJson.getString(Constant.JOB_LOGO));
                                objItem.setId(objJson.getString(Constant.JOB_ID));
                                objItem.setJobType(objJson.getString(Constant.JOB_TYPE));
                                objItem.setJobArea(objJson.getString(Constant.JOB_AREA));
                                objItem.setJobName(objJson.getString(Constant.JOB_NAME));
                                objItem.setJobCompanyName(objJson.getString(Constant.JOB_COMPANY_NAME));
                                objItem.setJobCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                                objItem.setCity(objJson.getString(Constant.CITY_NAME));
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }

        });
    }

    private void displayData() {
        if (mListItem.size() == 0) {
        } else {
            if (isFirst) {
                isFirst = false;
                adapter = new HomeJobAdapter(getContext(), mListItem);
                rvLatestJob.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            new CountDownTimer(Long.MAX_VALUE, 2000) {
                public void onTick(long millisUntilFinished) {
                    rvLatestJob.smoothScrollToPosition(position[0]);
                    position[0]++;
                }

                public void onFinish() {
                    Toast.makeText(getContext(), "All jobs are loaded", Toast.LENGTH_LONG).show();
                }
            }.start();

            adapter.setOnItemClickListener(position -> {
                String jobId = mListItem.get(position).getId();
                Intent intent = new Intent(getContext(), JobDetailsActivity.class);
                intent.putExtra("Id", jobId);
                startActivity(intent);
            });

        }
    }
}
