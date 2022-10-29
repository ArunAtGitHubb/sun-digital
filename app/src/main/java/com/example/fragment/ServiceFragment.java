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

import com.example.adapter.HomeServiceAdapter;
import com.example.item.ItemService;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.NetworkUtils;
import com.example.util.UserUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jellysoft.sundigitalindia.LatestService;
import com.jellysoft.sundigitalindia.R;
import com.jellysoft.sundigitalindia.ServiceDetailsActivity;
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
public class ServiceFragment extends Fragment {

    ArrayList<ItemService> mListItem;
    RecyclerView rvLatestService;
    HomeServiceAdapter adapter;
    ViewPager mviewPager;
    Button viewAllServices, textServiceCategories, textServiceAllCities;
    boolean isFirst = true, isOver = false;
    private int pageIndex = 1;
    final int[] position = {0};
    TabLayout tabLayout;
    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_service, container, false);
        Log.d("events", "onCreate");
        mListItem = new ArrayList<>();
        tabLayout = getActivity().findViewById(R.id.tabLayout);
        tabLayout.setVisibility(View.VISIBLE);
        mviewPager = getActivity().findViewById(R.id.viewPager);
        rvLatestService = rootView.findViewById(R.id.rv_service);

        viewAllServices = rootView.findViewById(R.id.viewAllServices);
        textServiceCategories = rootView.findViewById(R.id.textServiceCategories);
        textServiceAllCities = rootView.findViewById(R.id.textServiceAllCities);

        rvLatestService.setHasFixedSize(true);
        rvLatestService.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvLatestService.setFocusable(false);
        rvLatestService.setNestedScrollingEnabled(true);
        rvLatestService.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                position[0] = rvLatestService.computeHorizontalScrollOffset() / 1000;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        viewAllServices.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), LatestService.class);
            intent.putExtra("isLatest", true);
            startActivity(intent);
        });

        textServiceCategories.setOnClickListener(view -> mviewPager.setCurrentItem(9));
        textServiceAllCities.setOnClickListener(view -> mviewPager.setCurrentItem(10));

        if (NetworkUtils.isConnected(getActivity())) {
            getLatestProducts();
        } else {
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    private void getLatestProducts() {
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
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                Log.d("result", result);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
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
                adapter = new HomeServiceAdapter(getContext(), mListItem);
                rvLatestService.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            new CountDownTimer(Long.MAX_VALUE, 2000) {
                public void onTick(long millisUntilFinished) {
                    rvLatestService.smoothScrollToPosition(position[0]);
                    position[0]++;
                }

                public void onFinish() {
                    Toast.makeText(getContext(), "All jobs are loaded", Toast.LENGTH_LONG).show();
                }
            }.start();

            adapter.setOnItemClickListener(position -> {
                String jobId = mListItem.get(position).getId();
                Intent intent = new Intent(getContext(), ServiceDetailsActivity.class);
                intent.putExtra("Id", jobId);
                startActivity(intent);
            });

        }
    }
}
