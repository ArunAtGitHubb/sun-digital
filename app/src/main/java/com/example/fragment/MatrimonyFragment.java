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

import com.example.adapter.HomeMatrimonyAdapter;
import com.example.item.ItemMatrimony;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.NetworkUtils;
import com.example.util.UserUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jellysoft.sundigitalindia.MatrimonyDetailsActivity;
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
public class MatrimonyFragment extends Fragment {

    ArrayList<ItemMatrimony> mListItem;
    RecyclerView rvLatestMatrimony;
    HomeMatrimonyAdapter adapter;
    ViewPager mviewPager;
    Button textBrideCategories, textGroomCategories, textBrideReligion, textGroomReligion;
    boolean isFirst = true, isOver = false;
    private int pageIndex = 1;
    final int[] position = {0};
    TabLayout tabLayout;
    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_matrimony, container, false);
        Log.d("events", "onCreate");
        mListItem = new ArrayList<>();
        tabLayout = getActivity().findViewById(R.id.tabLayout);
        tabLayout.setVisibility(View.VISIBLE);
        mviewPager = getActivity().findViewById(R.id.viewPager);
        rvLatestMatrimony = rootView.findViewById(R.id.rv_matrimony);

        textBrideCategories = rootView.findViewById(R.id.textBrideCategories);
        textGroomCategories = rootView.findViewById(R.id.textGroomCategories);
        textBrideReligion = rootView.findViewById(R.id.textBrideReligion);
        textGroomReligion = rootView.findViewById(R.id.textGroomReligion);

        rvLatestMatrimony.setHasFixedSize(true);
        rvLatestMatrimony.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvLatestMatrimony.setFocusable(false);
        rvLatestMatrimony.setNestedScrollingEnabled(true);
        rvLatestMatrimony.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                position[0] = rvLatestMatrimony.computeHorizontalScrollOffset() / 1000;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        textBrideCategories.setOnClickListener(view -> mviewPager.setCurrentItem(11));
        textGroomCategories.setOnClickListener(view -> mviewPager.setCurrentItem(12));

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
        jsObj.addProperty("method_name", "get_latest_matrimony");
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
                adapter = new HomeMatrimonyAdapter(getContext(), mListItem);
                rvLatestMatrimony.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            new CountDownTimer(Long.MAX_VALUE, 2000) {
                public void onTick(long millisUntilFinished) {
                    rvLatestMatrimony.smoothScrollToPosition(position[0]);
                    position[0]++;
                }

                public void onFinish() {
                    Toast.makeText(getContext(), "All jobs are loaded", Toast.LENGTH_LONG).show();
                }
            }.start();

            adapter.setOnItemClickListener(position -> {
                String jobId = mListItem.get(position).getId();
                Intent intent = new Intent(getContext(), MatrimonyDetailsActivity.class);
                intent.putExtra("Id", jobId);
                startActivity(intent);
            });

        }
    }
}
