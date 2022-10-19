package com.example.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.adapter.CategoryAdapter;
import com.example.adapter.CityAdapter;
import com.example.adapter.HomeGridAdapter;
import com.example.adapter.HomeProductAdapter;
import com.example.item.ItemCity;
import com.example.item.ItemProduct;
import com.example.util.SaveJob;
import com.google.android.material.tabs.TabLayout;
import com.jellysoft.sundigitalindia.CatJob;
import com.jellysoft.sundigitalindia.CityJob;
import com.jellysoft.sundigitalindia.FilterSearchResultActivity;
import com.jellysoft.sundigitalindia.JobDetailsActivity;
import com.jellysoft.sundigitalindia.LatestJob;
import com.jellysoft.sundigitalindia.LatestProduct;
import com.jellysoft.sundigitalindia.MainActivity;
import com.jellysoft.sundigitalindia.ProductDetailsActivity;
import com.jellysoft.sundigitalindia.R;
import com.jellysoft.sundigitalindia.SearchActivity;
import com.example.adapter.HomeCategoryAdapter;
import com.example.adapter.HomeJobAdapter;
import com.example.item.ItemCategory;
import com.example.item.ItemJob;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.Events;
import com.example.util.GlobalBus;
import com.example.util.NetworkUtils;
import com.example.util.RvOnClickListener;
import com.example.util.UserUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    ImageSlider image_slider;
    NestedScrollView nestedScrollView;
    Button textCategoryViewAll, textCategoryViewAll1, latestViewAll, viewAllProducts, textProductCategories, textProductCities;
    Button call, whatsapp;
    TextView categoryViewAll, recentViewAll, textJobCategories, textJobAllCities;
//    GridView rvCategory;
    RecyclerView rvRecentJob, rvLatestJob, rvProducts;
    TabLayout tabLayout;
    CategoryAdapter adapter;
    CityAdapter adapter1;
    ArrayList<ItemCategory> categoryList;
    ArrayList<ItemCity> cityList;

    ArrayList<ItemJob> jobRecentList, jobLatestList;
    ArrayList<ItemProduct> productLatestList;
    LinearLayout lytCategory, lytRecent, lytLatest, lytCategory1;
//    HomeCategoryAdapter categoryAdapter;
//    HomeGridAdapter catadap;
    ArrayList<SlideModel> img;
    HomeJobAdapter recentJobAdapter, latestAdapter;
    HomeProductAdapter latestProductAdapter;
    RecyclerView vertical_courses_list, vertical_courses_list1;

    ViewPager mviewPager;
    private int pageIndex = 1;
    final int[] position = {0};
    final int[] productsPosition = {0};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
//        GlobalBus.getBus().register(this);
        categoryList = new ArrayList<>();
        cityList = new ArrayList<>();
        jobRecentList = new ArrayList<>();
        jobLatestList = new ArrayList<>();
        productLatestList = new ArrayList<>();

        tabLayout = getActivity().findViewById(R.id.tabLayout);
        mviewPager = getActivity().findViewById(R.id.viewPager);
        img = new ArrayList<>();
        textCategoryViewAll = rootView.findViewById(R.id.textCategoryViewAll);
        textCategoryViewAll1 = rootView.findViewById(R.id.textCategoryViewAll1);
        textProductCategories = rootView.findViewById(R.id.textProductCategories);
        textProductCities = rootView.findViewById(R.id.textProductAllCities);
        viewAllProducts = rootView.findViewById(R.id.viewAllProducts);

        vertical_courses_list = rootView.findViewById(R.id.vertical_courses_list);
        vertical_courses_list1 = rootView.findViewById(R.id.vertical_courses_list1);

        image_slider = rootView.findViewById(R.id.image_slider);
        mProgressBar = rootView.findViewById(R.id.progressBar1);
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        nestedScrollView = rootView.findViewById(R.id.nestedScrollView);
//        categoryViewAll = rootView.findViewById(R.id.textCategoryViewAll);
        textJobCategories = rootView.findViewById(R.id.textJobCategories);
        textJobAllCities = rootView.findViewById(R.id.textJobAllCities);
        latestViewAll = rootView.findViewById(R.id.textLatestViewAll);
        recentViewAll = rootView.findViewById(R.id.textRecentViewAll);

        lytCategory = rootView.findViewById(R.id.lytHomeTVCategory);
        lytCategory1 = rootView.findViewById(R.id.lytHomeTVCategory1);
        lytCategory.setVisibility(View.GONE);
        lytCategory1.setVisibility(View.GONE);
        lytRecent = rootView.findViewById(R.id.lytHomeRecent);
        lytLatest = rootView.findViewById(R.id.lytHomeLatest);

//        rvCategory = rootView.findViewById(R.id.rv_category);
        rvRecentJob = rootView.findViewById(R.id.rv_recent);
        rvLatestJob = rootView.findViewById(R.id.rv_latest);
        rvProducts = rootView.findViewById(R.id.rv_products);
//        rvCategory.setHasFixedSize(true);
//        rvCategory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        rvCategory.setFocusable(false);
//        rvCategory.setNestedScrollingEnabled(false);


        vertical_courses_list.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        vertical_courses_list.setLayoutManager(layoutManager);

        vertical_courses_list1.setHasFixedSize(true);
        GridLayoutManager layoutManager1 = new GridLayoutManager(getContext(), 3);
        vertical_courses_list1.setLayoutManager(layoutManager1);


        rvRecentJob.setHasFixedSize(true);
        rvRecentJob.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvRecentJob.setFocusable(false);
        rvRecentJob.setNestedScrollingEnabled(false);

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

        rvProducts.setHasFixedSize(true);
        rvProducts.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvProducts.setFocusable(false);
        rvProducts.setNestedScrollingEnabled(true);

        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                productsPosition[0] = rvProducts.computeHorizontalScrollOffset() / 1000;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


//        categoryViewAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String categoryName = getString(R.string.menu_two);
//                FragmentManager fm = getFragmentManager();
//                CategoryFragment channelFragment = new CategoryFragment();
//                assert fm != null;
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.hide(HomeFragment.this);
//                ft.add(R.id.Container, channelFragment, categoryName);
//                ft.addToBackStack(categoryName);
//                ft.commit();
//                ((MainActivity) requireActivity()).setToolbarTitle(categoryName);
//            }
//        });

        textJobCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mviewPager.setCurrentItem(1);
            }
        });

        textJobAllCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mviewPager.setCurrentItem(2);
            }
        });

        textProductCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mviewPager.setCurrentItem(3);
            }
        });

        textProductCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mviewPager.setCurrentItem(4);
            }
        });

        recentViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
//                String categoryName = getString(R.string.recent);
//                Bundle bundle = new Bundle();
//                bundle.putBoolean("isLatest", false);
//
//                FragmentManager fm = getFragmentManager();
//                LatestFragment latestFragment = new LatestFragment();
//                latestFragment.setArguments(bundle);
//                assert fm != null;
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.hide(HomeFragment.this);
//                ft.add(R.id.Container, latestFragment, categoryName);
//                ft.addToBackStack(categoryName);
//                ft.commit();
//                ((MainActivity) requireActivity()).setToolbarTitle(categoryName);
            }
        });

        latestViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String categoryName = getString(R.string.latest);
//                Bundle bundle = new Bundle();
//                bundle.putBoolean("isLatest", true);
//
//                FragmentManager fm = getFragmentManager();
//                LatestFragment latestFragment = new LatestFragment();
//                latestFragment.setArguments(bundle);
//                assert fm != null;
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.hide(HomeFragment.this);
//                ft.add(R.id.Container, latestFragment, categoryName);
//                ft.addToBackStack(categoryName);
//                ft.commit();
//                ((MainActivity) requireActivity()).setToolbarTitle(categoryName);
                Intent intent = new Intent(requireActivity(), LatestJob.class);
//                intent.putExtra("categoryId", spCategory.getSelectedItemPosition() == 0 ? "" : String.valueOf(mListCategory.get(spCategory.getSelectedItemPosition() - 1).getCategoryId()));
//                intent.putExtra("cityId", spCity.getSelectedItemPosition() == 0 ? "" : mListCity.get(spCity.getSelectedItemPosition() - 1).getCityId());
                intent.putExtra("isLatest", true);
                startActivity(intent);
            }
        });

        viewAllProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), LatestProduct.class);
                intent.putExtra("isLatest", true);
                startActivity(intent);
            }
        });



        if (NetworkUtils.isConnected(getActivity())) {
            getHome();
        } else {
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    private void getHome() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        final JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_home");
        jsObj.addProperty("user_id", UserUtils.getUserId());
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject jobAppJson = mainJson.getJSONObject(Constant.ARRAY_NAME);

                    JSONArray categoryArray = jobAppJson.getJSONArray("cat_list");
                    for (int i = 0; i < categoryArray.length(); i++) {
                        JSONObject jsonObject = categoryArray.getJSONObject(i);
                        ItemCategory itemCategory = new ItemCategory();
                        itemCategory.setCategoryId(jsonObject.getInt(Constant.CATEGORY_CID));
                        itemCategory.setCounting(jsonObject.getString("num"));
                        itemCategory.setCategoryName(jsonObject.getString(Constant.CATEGORY_NAME));
                        itemCategory.setCategoryImage(jsonObject.getString(Constant.CATEGORY_IMAGE));
                        if(i < 3){
                            categoryList.add(itemCategory);
                        }
                    }
                    JSONArray cityArray = jobAppJson.getJSONArray("city_list");
                    for (int i = 0; i < cityArray.length(); i++) {
                        JSONObject jsonObject = cityArray.getJSONObject(i);
                        ItemCity itemCategory = new ItemCity();
                        itemCategory.setCityId(jsonObject.getString(Constant.CITY_ID));
                        itemCategory.setCityName(jsonObject.getString(Constant.CITY_NAME));
                        itemCategory.setCounting(jsonObject.getString("num"));
                        itemCategory.setCityImage(jsonObject.getString("city_image"));
                        if(i < 3){
                            cityList.add(itemCategory);
                        }
                    }
                    JSONArray jobArray1 = jobAppJson.getJSONArray("banner");
                    for (int i = 0; i < jobArray1.length(); i++) {
                        JSONObject jsonObject = jobArray1.getJSONObject(i);
                        Log.d("ggg",jsonObject.getString("banner"));
                        img.add(new SlideModel(jsonObject.getString("banner"),ScaleTypes.FIT));
                    }


                    JSONArray jobArray = jobAppJson.getJSONArray("recent_job");
                    for (int i = 0; i < jobArray.length(); i++) {
                        JSONObject jsonObject = jobArray.getJSONObject(i);
                        ItemJob objItem = new ItemJob();
                        objItem.setJobLogo(jsonObject.getString(Constant.JOB_LOGO));
                        objItem.setId(jsonObject.getString(Constant.JOB_ID));
                        objItem.setJobType(jsonObject.getString(Constant.JOB_TYPE));
                        objItem.setJobName(jsonObject.getString(Constant.JOB_NAME));
                        objItem.setJobCompanyName(jsonObject.getString(Constant.JOB_COMPANY_NAME));
                        objItem.setJobCategoryName(jsonObject.getString(Constant.CATEGORY_NAME));
                        objItem.setJobPhoneNumber(jsonObject.getString(Constant.JOB_PHONE_NO));
                        objItem.setCity(jsonObject.getString(Constant.CITY_NAME));
                        objItem.setJobVacancy(jsonObject.getString(Constant.JOB_VACANCY));
                        objItem.setJobDate(jsonObject.getString(Constant.JOB_DATE));
                        objItem.setJobSalary(jsonObject.getString(Constant.JOB_SALARY));
                        objItem.setViews(jsonObject.getString("views"));
                        objItem.setJobFavourite(jsonObject.getBoolean(Constant.JOB_FAVOURITE));
                        jobRecentList.add(objItem);
                    }

                    JSONArray jobArrayLatest = jobAppJson.getJSONArray("latest_job");
                    for (int i = 0; i < jobArrayLatest.length(); i++) {
                        JSONObject jsonObject = jobArrayLatest.getJSONObject(i);
                        ItemJob objItem = new ItemJob();
                        objItem.setJobLogo(jsonObject.getString(Constant.JOB_LOGO));
                        objItem.setId(jsonObject.getString(Constant.JOB_ID));
                        objItem.setJobType(jsonObject.getString(Constant.JOB_TYPE));
                        objItem.setJobName(jsonObject.getString(Constant.JOB_NAME));
                        objItem.setJobPhoneNumber(jsonObject.getString(Constant.JOB_PHONE_NO));
                        objItem.setJobCompanyName(jsonObject.getString(Constant.JOB_COMPANY_NAME));
                        objItem.setJobCategoryName(jsonObject.getString(Constant.CATEGORY_NAME));
                        objItem.setCity(jsonObject.getString(Constant.CITY_NAME));
                        objItem.setJobVacancy(jsonObject.getString(Constant.JOB_VACANCY));
                        objItem.setJobDate(jsonObject.getString(Constant.JOB_DATE));
                        objItem.setJobSalary(jsonObject.getString(Constant.JOB_SALARY));
                        objItem.setpLate(jsonObject.getString("job_date1"));
                        objItem.setViews(jsonObject.getString("views"));
                        objItem.setJobFavourite(jsonObject.getBoolean(Constant.JOB_FAVOURITE));
                        jobLatestList.add(objItem);
                    }

                    JSONArray productArrayList = jobAppJson.getJSONArray("latest_product");
                    for (int i = 0; i < productArrayList.length(); i++) {
                        JSONObject jsonObject = productArrayList.getJSONObject(i);
                        ItemProduct objItem = new ItemProduct();
                        objItem.setProductLogo(jsonObject.getString(Constant.PRODUCT_LOGO));
                        objItem.setId(jsonObject.getString(Constant.PRODUCT_ID));
                        objItem.setProductType(jsonObject.getString(Constant.PRODUCT_TYPE));
                        objItem.setProductName(jsonObject.getString(Constant.PRODUCT_NAME));
                        objItem.setProductPhoneNumber(jsonObject.getString(Constant.PRODUCT_PHONE_NO));
                        objItem.setProductCategoryName(jsonObject.getString(Constant.CATEGORY_NAME));
                        objItem.setCity(jsonObject.getString(Constant.CITY_NAME));
                        objItem.setProductDate(jsonObject.getString(Constant.PRODUCT_START_DATE));
                        objItem.setPLate(jsonObject.getString(Constant.PRODUCT_END_DATE));
                        objItem.setViews(jsonObject.getString("views"));
                        productLatestList.add(objItem);
                    }

                    displayData();
                } catch (JSONException e) {
                    e.printStackTrace();
//                    nestedScrollView.setVisibility(View.GONE);
//                    lyt_not_found.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }


 @RequiresApi(api = Build.VERSION_CODES.M)
 private void displayData() {
    image_slider.setImageList(img, ScaleTypes.FIT);
    textCategoryViewAll.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        mviewPager.setCurrentItem(1);
    }
});
        textCategoryViewAll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mviewPager.setCurrentItem(2);
            }
        });
        if (!categoryList.isEmpty()) {
            adapter = new CategoryAdapter(getActivity(), categoryList);
            vertical_courses_list.setAdapter(adapter);
//            adapter.hideHeader();
            adapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String categoryName = categoryList.get(position).getCategoryName();
                    String categoryId = String.valueOf(categoryList.get(position).getCategoryId());
                    Intent intent = new Intent(requireActivity(), CatJob.class);
                    intent.putExtra("categoryName",  categoryName);
                    intent.putExtra("categoryId",categoryId);
                    startActivity(intent);
                }
            });
        } else {
            lytCategory.setVisibility(View.GONE);
        }

        if (!cityList.isEmpty()) {

            adapter1 = new CityAdapter(getActivity(), cityList);
            vertical_courses_list1.setAdapter(adapter1);
//            adapter1.hideHeader();
            adapter1.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String categoryName = cityList.get(position).getCityName();
                    String categoryId = String.valueOf(cityList.get(position).getCityId());
                    Intent intent = new Intent(requireActivity(), CityJob.class);
                    intent.putExtra("categoryName",  categoryName);
                    intent.putExtra("categoryId",categoryId);
                    startActivity(intent);

                }
            });
        } else {
//            lytCategory.setVisibility(View.GONE);
        }

        if (!jobRecentList.isEmpty()) {
            recentJobAdapter = new HomeJobAdapter(getActivity(), jobRecentList);
            rvRecentJob.setAdapter(recentJobAdapter);

            recentJobAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String jobId = jobRecentList.get(position).getId();
                    new SaveJob(getContext()).userSave(jobId);
                    Intent intent = new Intent(getActivity(), JobDetailsActivity.class);
                    intent.putExtra("Id", jobId);
                    startActivity(intent);
                }
            });

        } else {
            lytRecent.setVisibility(View.GONE);
        }

        if (!jobLatestList.isEmpty()) {
            latestAdapter = new HomeJobAdapter(getActivity(), jobLatestList);
            rvLatestJob.setAdapter(latestAdapter);
//            rvLatestJob.smoothScrollBy();

            rvLatestJob.getLayoutManager().scrollToPosition(0);

            new CountDownTimer(Long.MAX_VALUE, 2000) {

                public void onTick(long millisUntilFinished) {
                    rvLatestJob.smoothScrollToPosition(position[0]);
                    position[0]++;
                }

                public void onFinish() {
                    Toast.makeText(getContext(), "All jobs are loaded", Toast.LENGTH_LONG).show();
                }
            }.start();


            latestAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String jobId = jobLatestList.get(position).getId();
                    Log.d("jobId","1way : "+jobId);

                    new SaveJob(getContext()).userSave(jobId);
                    Intent intent = new Intent(getActivity(), JobDetailsActivity.class);
                    intent.putExtra("Id", jobId);
                    startActivity(intent);
                }
            });

        } else {
            lytLatest.setVisibility(View.GONE);
        }

     if (!productLatestList.isEmpty()) {
         latestProductAdapter = new HomeProductAdapter(getActivity(), productLatestList);
         rvProducts.setAdapter(latestProductAdapter);
//            rvLatestJob.smoothScrollBy();

         rvProducts.getLayoutManager().scrollToPosition(0);

         new CountDownTimer(Long.MAX_VALUE, 2000) {

             public void onTick(long millisUntilFinished) {
                 rvProducts.smoothScrollToPosition(productsPosition[0]);
                 productsPosition[0]++;
             }

             public void onFinish() {
                 Toast.makeText(getContext(), "All jobs are loaded", Toast.LENGTH_LONG).show();
             }
         }.start();

         latestProductAdapter.setOnItemClickListener(new RvOnClickListener() {
             @Override
             public void onItemClick(int position) {
                 String jobId = productLatestList.get(position).getId();

                 new SaveJob(getContext()).userSave(jobId);
                 Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                 intent.putExtra("Id", jobId);
                 startActivity(intent);
             }
         });
     } else {
         lytLatest.setVisibility(View.GONE);
     }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void getSaveJob(Events.SaveJob saveJob) {
        for (int i = 0; i < jobLatestList.size(); i++) {
            if (jobLatestList.get(i).getId().equals(saveJob.getJobId())) {
                jobLatestList.get(i).setJobFavourite(saveJob.isSave());
                latestAdapter.notifyItemChanged(i);
            }
        }

        for (int i = 0; i < jobRecentList.size(); i++) {
            if (jobRecentList.get(i).getId().equals(saveJob.getJobId())) {
                jobRecentList.get(i).setJobFavourite(saveJob.isSave());
                recentJobAdapter.notifyItemChanged(i);
            }
        }
    }
}
