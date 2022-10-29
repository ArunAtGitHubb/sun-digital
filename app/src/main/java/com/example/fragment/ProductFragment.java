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

import com.example.adapter.HomeProductAdapter;
import com.example.item.ItemProduct;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.NetworkUtils;
import com.example.util.UserUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jellysoft.sundigitalindia.JobDetailsActivity;
import com.jellysoft.sundigitalindia.NewProduct;
import com.jellysoft.sundigitalindia.R;
import com.jellysoft.sundigitalindia.UsedProduct;
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
public class ProductFragment extends Fragment {

    ArrayList<ItemProduct> mListItem;
    RecyclerView rvLatestProduct;
    HomeProductAdapter adapter;
    ViewPager mviewPager;
    Button viewAllUsedProducts, viewAllNewProducts, textProductCategories, textProductAllCities;
    boolean isFirst = true, isOver = false;
    private int pageIndex = 1;
    final int[] position = {0};
    TabLayout tabLayout;
    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product, container, false);
        Log.d("events", "onCreate");
        mListItem = new ArrayList<>();
        tabLayout = getActivity().findViewById(R.id.tabLayout);
        tabLayout.setVisibility(View.VISIBLE);
        mviewPager = getActivity().findViewById(R.id.viewPager);
        rvLatestProduct = rootView.findViewById(R.id.rv_products);

        viewAllUsedProducts = rootView.findViewById(R.id.viewAllUsedProducts);
        viewAllNewProducts = rootView.findViewById(R.id.viewAllNewProducts);
        textProductCategories = rootView.findViewById(R.id.textProductCategories);
        textProductAllCities = rootView.findViewById(R.id.textProductAllCities);

        rvLatestProduct.setHasFixedSize(true);
        rvLatestProduct.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvLatestProduct.setFocusable(false);
        rvLatestProduct.setNestedScrollingEnabled(true);
        rvLatestProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                position[0] = rvLatestProduct.computeHorizontalScrollOffset() / 1000;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        viewAllUsedProducts.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), UsedProduct.class);
            intent.putExtra("isLatest", true);
            startActivity(intent);
        });

        viewAllNewProducts.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), NewProduct.class);
            intent.putExtra("isLatest", true);
            startActivity(intent);
        });

        textProductCategories.setOnClickListener(view -> mviewPager.setCurrentItem(7));
        textProductAllCities.setOnClickListener(view -> mviewPager.setCurrentItem(8));

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
        jsObj.addProperty("method_name", "get_latest_product");
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
                    JSONObject jsonObject;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
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
                                objItem.setProductArea(jsonObject.getString(Constant.PRODUCT_AREA));
                                objItem.setProductDate(jsonObject.getString(Constant.PRODUCT_START_DATE));
                                objItem.setPLate(jsonObject.getString(Constant.PRODUCT_END_DATE));
                                objItem.setViews(jsonObject.getString("views"));
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
                adapter = new HomeProductAdapter(getContext(), mListItem);
                rvLatestProduct.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            new CountDownTimer(Long.MAX_VALUE, 2000) {
                public void onTick(long millisUntilFinished) {
                    rvLatestProduct.smoothScrollToPosition(position[0]);
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
