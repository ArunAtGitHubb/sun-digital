package com.example.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.adapter.CategoryAdapter;
import com.example.adapter.CityAdapter;
import com.example.adapter.HomeJobAdapter;
import com.example.adapter.HomeMatrimonyAdapter;
import com.example.adapter.HomeProductAdapter;
import com.example.adapter.HomeServiceAdapter;
import com.example.item.ItemCategory;
import com.example.item.ItemCity;
import com.example.item.ItemIcon;
import com.example.item.ItemJob;
import com.example.item.ItemMatrimony;
import com.example.item.ItemProduct;
import com.example.item.ItemService;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.Events;
import com.example.util.GlobalBus;
import com.example.util.NetworkUtils;
import com.example.util.SaveJob;
import com.example.util.UserUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jellysoft.sundigitalindia.JobDetailsActivity;
import com.jellysoft.sundigitalindia.LatestFemaleMatrimony;
import com.jellysoft.sundigitalindia.LatestJob;
import com.jellysoft.sundigitalindia.LatestMaleMatrimony;
import com.jellysoft.sundigitalindia.LatestService;
import com.jellysoft.sundigitalindia.MatrimonyDetailsActivity;
import com.jellysoft.sundigitalindia.NewProduct;
import com.jellysoft.sundigitalindia.ProductDetailsActivity;
import com.jellysoft.sundigitalindia.R;
import com.jellysoft.sundigitalindia.RestaurantDetailsActivity;
import com.jellysoft.sundigitalindia.ServiceDetailsActivity;
import com.jellysoft.sundigitalindia.TutyCityRstrnt;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    ImageSlider image_slider;
    ImageView jobImage, productImage, serviceImage, matrimonyImage;
    NestedScrollView nestedScrollView;
    Button latestViewAll, viewAllServices, viewAllMatrimony,
            textProductCategories, textProductCities,
            textServiceCategories, textServiceCities,
            textMatrimonyCities, textGroomsCategory, textBridesCategory,
            textBrideReligion, textGroomReligion, viewAllBrides, viewAllGrooms,
            viewAllNewProduct, viewTutyRestaurant, viewAllCityRestaurant;
    Button call, whatsapp;
    TextView categoryViewAll, textJobCategories, textJobAllCities,
            textJob, textProduct, textWorker, textMatrimony;
//    GridView rvCategory;
    RecyclerView rvLatestJob, rvProducts, rvServices, rvMatrimony;
    TabLayout tabLayout;
    CategoryAdapter adapter;
    CityAdapter adapter1;
    RelativeLayout jobSection, productSection, serviceSection, matrimonySection;

    ArrayList<ItemCategory> categoryList;
    ArrayList<ItemCity> cityList;
    ArrayList<ItemIcon> iconList;
    ArrayList<ItemJob> jobLatestList;
    ArrayList<ItemProduct> productLatestList;
    ArrayList<ItemService> serviceLatestList;
    ArrayList<ItemMatrimony> matrimonyLatestList;

    LinearLayout lytLatest, homeLinear;
    ArrayList<SlideModel> img;
    HomeJobAdapter latestAdapter;
    HomeProductAdapter latestProductAdapter;
    HomeServiceAdapter latestServiceAdapter;
    HomeMatrimonyAdapter latestMatrimonyAdapter;
    RecyclerView vertical_courses_list, restaurantList;
    CardView jobCard, productCard, serviceCard, matrimonyCard;

    ViewPager mviewPager;
    private int pageIndex = 1;
    final int[] position = {0};
    final int[] productsPosition = {0};
    final int[] servicesPosition = {0};
    final int[] matrimonyPosition = {0};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        categoryList = new ArrayList<>();
        cityList = new ArrayList<>();
        iconList = new ArrayList<>();
        jobLatestList = new ArrayList<>();
        productLatestList = new ArrayList<>();
        serviceLatestList = new ArrayList<>();
        matrimonyLatestList = new ArrayList<>();

        tabLayout = getActivity().findViewById(R.id.tabLayout);
        mviewPager = getActivity().findViewById(R.id.viewPager);
        img = new ArrayList<>();

        homeLinear = rootView.findViewById(R.id.homeLinear);

        jobSection = rootView.findViewById(R.id.jobSection);
        productSection = rootView.findViewById(R.id.productSection);
        serviceSection = rootView.findViewById(R.id.serviceSection);
        matrimonySection = rootView.findViewById(R.id.matrimonySection);

        textJob = rootView.findViewById(R.id.textJob);
        textProduct = rootView.findViewById(R.id.textProduct);
        textWorker = rootView.findViewById(R.id.textWorker);
        textMatrimony = rootView.findViewById(R.id.textMatrimony);

        jobImage = rootView.findViewById(R.id.jobImage);
        productImage = rootView.findViewById(R.id.productImage);
        serviceImage = rootView.findViewById(R.id.serviceImage);
        matrimonyImage = rootView.findViewById(R.id.matrimonyImage);

        viewAllNewProduct = rootView.findViewById(R.id.viewAllNewProducts);
        textProductCategories = rootView.findViewById(R.id.textProductCategories);
        textProductCities = rootView.findViewById(R.id.textProductAllCities);
        viewTutyRestaurant = rootView.findViewById(R.id.viewTutyRestaurant);
        viewAllCityRestaurant = rootView.findViewById(R.id.viewAllCityRestaurant);

        viewAllServices = rootView.findViewById(R.id.viewAllServices);
        textServiceCategories = rootView.findViewById(R.id.textServiceCategories);
        textServiceCities = rootView.findViewById(R.id.textServiceAllCities);

        viewAllBrides = rootView.findViewById(R.id.viewAllBrides);
        viewAllGrooms = rootView.findViewById(R.id.viewAllGrooms);
        textGroomsCategory = rootView.findViewById(R.id.textGroomCategories);
        textBridesCategory = rootView.findViewById(R.id.textBrideCategories);
        textGroomReligion = rootView.findViewById(R.id.textGroomReligion);
        textBrideReligion = rootView.findViewById(R.id.textBrideReligion);
//        textMatrimonyCities = rootView.findViewById(R.id.textMatrimonyAllCities);

        vertical_courses_list = rootView.findViewById(R.id.vertical_courses_list);
        restaurantList = rootView.findViewById(R.id.restaurantList);

        image_slider = rootView.findViewById(R.id.image_slider);
        mProgressBar = rootView.findViewById(R.id.progressBar1);
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        nestedScrollView = rootView.findViewById(R.id.nestedScrollView);
        textJobCategories = rootView.findViewById(R.id.textJobCategories);
        textJobAllCities = rootView.findViewById(R.id.textJobAllCities);
        latestViewAll = rootView.findViewById(R.id.textLatestViewAll);

        lytLatest = rootView.findViewById(R.id.lytHomeLatest);

        rvLatestJob = rootView.findViewById(R.id.rv_latest);
        rvProducts = rootView.findViewById(R.id.rv_products);
        rvServices = rootView.findViewById(R.id.rv_service);
        rvMatrimony = rootView.findViewById(R.id.rv_matrimony);

        jobCard = rootView.findViewById(R.id.jobCard);
        productCard = rootView.findViewById(R.id.productCard);
        serviceCard = rootView.findViewById(R.id.serviceCard);
        matrimonyCard = rootView.findViewById(R.id.matrimonyCard);


        jobSection.setOnClickListener(view -> {
            nestedScrollView.smoothScrollTo(0, jobCard.getTop());
        });
        productSection.setOnClickListener(view -> {
            nestedScrollView.smoothScrollTo(0, productCard.getTop());
        });
        serviceSection.setOnClickListener(view -> {
            nestedScrollView.smoothScrollTo(0, serviceCard.getTop());
        });
        matrimonySection.setOnClickListener(view -> {
            nestedScrollView.smoothScrollTo(0, matrimonyCard.getTop());
        });

        restaurantList.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        restaurantList.setLayoutManager(layoutManager);

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

        rvServices.setHasFixedSize(true);
        rvServices.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvServices.setFocusable(false);
        rvServices.setNestedScrollingEnabled(true);
        rvServices.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                servicesPosition[0] = rvServices.computeHorizontalScrollOffset() / 1000;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        rvMatrimony.setHasFixedSize(true);
        rvMatrimony.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvMatrimony.setFocusable(false);
        rvMatrimony.setNestedScrollingEnabled(true);
        rvMatrimony.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                matrimonyPosition[0] = rvMatrimony.computeHorizontalScrollOffset() / 1000;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        textJobCategories.setOnClickListener(view -> mviewPager.setCurrentItem(6));
        textJobAllCities.setOnClickListener(view -> mviewPager.setCurrentItem(7));

        textProductCategories.setOnClickListener(view -> mviewPager.setCurrentItem(8));
        textProductCities.setOnClickListener(view -> mviewPager.setCurrentItem(9));

        textServiceCategories.setOnClickListener(view -> mviewPager.setCurrentItem(10));
        textServiceCities.setOnClickListener(view -> mviewPager.setCurrentItem(11));

        textBridesCategory.setOnClickListener(view -> mviewPager.setCurrentItem(12));
        textGroomsCategory.setOnClickListener(view -> mviewPager.setCurrentItem(13));
        textBrideReligion.setOnClickListener(view -> mviewPager.setCurrentItem(15));
        textGroomReligion.setOnClickListener(view -> mviewPager.setCurrentItem(16));
//        textMatrimonyCities.setOnClickListener(view -> mviewPager.setCurrentItem(9));

        latestViewAll.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), LatestJob.class);
            intent.putExtra("isLatest", true);
            startActivity(intent);
        });

        viewAllNewProduct.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), NewProduct.class);
            intent.putExtra("isLatest", true);
            startActivity(intent);
        });

        viewAllServices.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), LatestService.class);
            intent.putExtra("isLatest", true);
            startActivity(intent);
        });

        viewAllBrides.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), LatestFemaleMatrimony.class);
            intent.putExtra("isLatest", true);
            startActivity(intent);
        });

        viewAllGrooms.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), LatestMaleMatrimony.class);
            intent.putExtra("isLatest", true);
            startActivity(intent);
        });

        viewTutyRestaurant.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), TutyCityRstrnt.class);
            intent.putExtra("isLatest", true);
            startActivity(intent);
        });

        viewAllCityRestaurant.setOnClickListener(view -> {
            mviewPager.setCurrentItem(5);
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

                    JSONArray navIcons = jobAppJson.getJSONArray("icon");
                    for (int i = 0; i < navIcons.length(); i++) {
                        JSONObject jsonObject = navIcons.getJSONObject(i);
                        Log.d("json21", jsonObject.toString());
                        ItemIcon itemIcon = new ItemIcon();
                        itemIcon.setIcon(jsonObject.getString("icon"));
                        itemIcon.setName(jsonObject.getString("name"));
                        itemIcon.setLabel(jsonObject.getString("label"));
                        iconList.add(itemIcon);
                    }

                    JSONArray categoryArray = jobAppJson.getJSONArray("restaurant_list");
                    for (int i = 0; i < categoryArray.length(); i++) {
                        JSONObject jsonObject = categoryArray.getJSONObject(i);
                        ItemCategory itemCategory = new ItemCategory();
                        itemCategory.setCategoryId(jsonObject.getInt("c_id"));
                        itemCategory.setCounting(jsonObject.getString("num"));
                        itemCategory.setCategoryName(jsonObject.getString(Constant.CATEGORY_NAME));
                        itemCategory.setCategoryImage(jsonObject.getString(Constant.CATEGORY_IMAGE));
                        if(i < 6){
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
                    Log.d("result", jobArray1.toString());
                    for (int i = 0; i < jobArray1.length(); i++) {
                        JSONObject jsonObject = jobArray1.getJSONObject(i);
                        img.add(new SlideModel(jsonObject.getString("banner"),ScaleTypes.FIT));
                    }

                    JSONArray jobArrayLatest = jobAppJson.getJSONArray("latest_job");
                    for (int i = 0; i < jobArrayLatest.length(); i++) {
                        JSONObject jsonObject = jobArrayLatest.getJSONObject(i);
                        ItemJob objItem = new ItemJob();
                        objItem.setJobLogo(jsonObject.getString(Constant.JOB_LOGO));
                        objItem.setId(jsonObject.getString(Constant.JOB_ID));
                        objItem.setJobType(jsonObject.getString(Constant.JOB_TYPE));
                        objItem.setJobArea(jsonObject.getString(Constant.JOB_AREA));
                        objItem.setJobDesc(jsonObject.getString(Constant.JOB_DESC));
                        objItem.setJobName(jsonObject.getString(Constant.JOB_NAME));
                        objItem.setJobTime(jsonObject.getString(Constant.JOB_TIME));
                        objItem.setJobWebsite(jsonObject.getString(Constant.WEBSITE_LINK));
                        objItem.setJobPhoneNumber(jsonObject.getString(Constant.JOB_PHONE_NO));
                        objItem.setJobPhoneNumber2(jsonObject.getString(Constant.JOB_PHONE_NO2));
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
                        objItem.setProductArea(jsonObject.getString(Constant.PRODUCT_AREA));
                        objItem.setProductDesc(jsonObject.getString(Constant.PRODUCT_DESC));
                        objItem.setProductPhoneNumber(jsonObject.getString(Constant.PRODUCT_PHONE_NO)) ;
                        objItem.setProductPhoneNumber2(jsonObject.getString(Constant.PRODUCT_PHONE_NO2));
                        objItem.setProductCategoryName(jsonObject.getString(Constant.CATEGORY_NAME));
                        objItem.setProductPrice(jsonObject.getString(Constant.PRODUCT_PRICE));
                        objItem.setProductNegotiable(jsonObject.getString(Constant.PRODUCT_NEGOTIABLE));
                        objItem.setProductDoc(jsonObject.getString(Constant.PRODUCT_DOC));
                        objItem.setWebsiteLink(jsonObject.getString(Constant.WEBSITE_LINK));
                        objItem.setProductSellingPrice(jsonObject.getString(Constant.PRODUCT_SELLING_PRICE));
                        objItem.setCity(jsonObject.getString(Constant.CITY_NAME));
                        objItem.setProductDate(jsonObject.getString(Constant.PRODUCT_START_DATE));
                        objItem.setPLate(jsonObject.getString(Constant.PRODUCT_END_DATE));
                        objItem.setViews(jsonObject.getString("views"));
                        productLatestList.add(objItem);
                    }

                    JSONArray serviceArrayList = jobAppJson.getJSONArray("latest_service");
                    for (int i = 0; i < serviceArrayList.length(); i++) {
                        JSONObject jsonObject = serviceArrayList.getJSONObject(i);
                        ItemService objItem = new ItemService();
                        objItem.setServiceLogo(jsonObject.getString(Constant.SERVICE_LOGO));
                        objItem.setId(jsonObject.getString(Constant.SERVICE_ID));
                        objItem.setServiceName(jsonObject.getString(Constant.SERVICE_NAME));
                        objItem.setServiceArea(jsonObject.getString(Constant.SERVICE_AREA));
                        objItem.setServiceSkill(jsonObject.getString(Constant.SERVICE_SKILL));
                        objItem.setServiceDesc(jsonObject.getString(Constant.SERVICE_DESC));
                        objItem.setServiceTime(jsonObject.getString(Constant.SERVICE_TIME));
                        objItem.setServiceCost(jsonObject.getString(Constant.SERVICE_COST));
                        objItem.setServiceImage(jsonObject.getString(Constant.SERVICE_IMAGE));
                        objItem.setServiceMail(jsonObject.getString(Constant.SERVICE_MAIL));
                        objItem.setWebsiteLink(jsonObject.getString(Constant.WEBSITE_LINK));
                        objItem.setServiceAddress(jsonObject.getString(Constant.SERVICE_ADDRESS));
                        objItem.setServicePhoneNumber(jsonObject.getString(Constant.SERVICE_PHONE_NO));
                        objItem.setServiceCategoryName(jsonObject.getString(Constant.CATEGORY_NAME));
                        objItem.setCity(jsonObject.getString(Constant.CITY_NAME));
                        objItem.setServiceDate(jsonObject.getString(Constant.SERVICE_START_DATE));
                        objItem.setPLate(jsonObject.getString(Constant.SERVICE_END_DATE));
                        objItem.setViews(jsonObject.getString("views"));
                        serviceLatestList.add(objItem);
                    }

                    JSONArray matrimonyArrayList = jobAppJson.getJSONArray("latest_matrimony");
                    for(int i = 0; i < matrimonyArrayList.length(); i++ ) {
                        JSONObject jsonObject = matrimonyArrayList.getJSONObject(i);
                        ItemMatrimony objItem = new ItemMatrimony();
                        objItem.setId(jsonObject.getString(Constant.id));
                        objItem.setCity(jsonObject.getString(Constant.CITY_NAME));
                        objItem.setMatrimonyName(jsonObject.getString(Constant.MATRIMONY_NAME));
                        objItem.setMatrimonyGender(jsonObject.getString(Constant.MATRIMONY_GENDER));
                        objItem.setMatrimonyReligion(jsonObject.getString(Constant.MATRIMONY_RELIGION));
                        objItem.setMatrimonyArea(jsonObject.getString(Constant.MATRIMONY_AREA));
                        objItem.setMatrimonyMaritalStatus(jsonObject.getString(Constant.MATRIMONY_MARITAL_STATUS));
                        objItem.setMatrimonyDob(jsonObject.getString(Constant.MATRIMONY_DOB));
                        objItem.setMatrimonyAge(jsonObject.getString(Constant.MATRIMONY_AGE));
                        objItem.setMatrimonyEducation(jsonObject.getString(Constant.MATRIMONY_EDUCATION));
                        objItem.setMatrimonyCareer(jsonObject.getString(Constant.MATRIMONY_CAREER));
                        objItem.setMatrimonySalary(jsonObject.getString(Constant.MATRIMONY_SALARY));
                        objItem.setMatrimonyDesc(jsonObject.getString(Constant.MATRIMONY_DESC));
                        objItem.setMatrimonyPartnerExpect(jsonObject.getString(Constant.MATRIMONY_PARTNER_EXPECT));
                        objItem.setMatrimonyPersonName(jsonObject.getString(Constant.MATRIMONY_PERSON_NAME));
                        objItem.setMatrimonyPhoneNumber(jsonObject.getString(Constant.MATRIMONY_PHONE_NUMBER));
                        objItem.setMatrimonyPhoneNumber2(jsonObject.getString(Constant.MATRIMONY_PHONE_NUMBER2));
                        objItem.setMatrimonyImage(jsonObject.getString(Constant.MATRIMONY_IMAGE));
                        objItem.setMatrimonyLogo(jsonObject.getString(Constant.MATRIMONY_LOGO));
                        objItem.setMatrimonyImage2(jsonObject.getString(Constant.MATRIMONY_IMAGE2));
                        objItem.setMatrimonyImage3(jsonObject.getString(Constant.MATRIMONY_IMAGE3));
                        objItem.setMatrimonyImage4(jsonObject.getString(Constant.MATRIMONY_IMAGE4));
                        objItem.setMatrimonySDate(jsonObject.getString(Constant.MATRIMONY_START_DATE));
                        objItem.setMatrimonyEDate(jsonObject.getString(Constant.MATRIMONY_END_DATE));
                        objItem.setCategoryName(jsonObject.getString(Constant.CATEGORY_NAME));
                        matrimonyLatestList.add(objItem);
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

        if(!iconList.isEmpty()){
            for(ItemIcon icon: iconList) {
                switch (Objects.requireNonNull(icon.getName())) {
                    case "Job":
                        textJob.setText(icon.getLabel());
                        Picasso.get().load(icon.getIcon()).placeholder(R.drawable.job).into(jobImage);
                        break;
                    case "Product":
                        textProduct.setText(icon.getLabel());
                        Picasso.get().load(icon.getIcon()).placeholder(R.drawable.product).into(productImage);
                        break;
                    case "ServiceMan":
                        textWorker.setText(icon.getLabel());
                        Picasso.get().load(icon.getIcon()).placeholder(R.drawable.workers).into(serviceImage);
                        break;
                    case "Matrimony":
                        textMatrimony.setText(icon.getLabel());
                        Picasso.get().load(icon.getIcon()).placeholder(R.drawable.matrimony).into(matrimonyImage);
                        break;
                }
            }
        }

        if (!categoryList.isEmpty()) {
            adapter = new CategoryAdapter(getActivity(), categoryList);
            restaurantList.setAdapter(adapter);
            adapter.setOnItemClickListener(position -> {
                String categoryName = categoryList.get(position).getCategoryName();
                String categoryId = String.valueOf(categoryList.get(position).getCategoryId());
                Log.d("result2", categoryId);
                Intent intent = new Intent(requireActivity(), RestaurantDetailsActivity.class);
                intent.putExtra("categoryName", categoryName);
                intent.putExtra("categoryId", categoryId);
                startActivity(intent);
            });
        }

        if (!jobLatestList.isEmpty()) {
            latestAdapter = new HomeJobAdapter(getActivity(), jobLatestList);
            rvLatestJob.setAdapter(latestAdapter);
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

            latestAdapter.setOnItemClickListener(position -> {
                String jobId = jobLatestList.get(position).getId();

                new SaveJob(getContext()).userSave(jobId);
                Intent intent = new Intent(getActivity(), JobDetailsActivity.class);
                intent.putExtra("Id", jobId);
                startActivity(intent);
            });
        } else {
            lytLatest.setVisibility(View.GONE);
        }

        if (!productLatestList.isEmpty()) {
            latestProductAdapter = new HomeProductAdapter(getActivity(), productLatestList);
            rvProducts.setAdapter(latestProductAdapter);
            rvProducts.getLayoutManager().scrollToPosition(0);

            new CountDownTimer(Long.MAX_VALUE, 2000) {

            public void onTick(long millisUntilFinished) {
                rvProducts.smoothScrollToPosition(productsPosition[0]);
                productsPosition[0]++;
            }

            public void onFinish() {
                Toast.makeText(getContext(), "All products are loaded", Toast.LENGTH_LONG).show();
            }
        }.start();

        latestProductAdapter.setOnItemClickListener(position -> {
            String jobId = productLatestList.get(position).getId();

            new SaveJob(getContext()).userSave(jobId);
            Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
            intent.putExtra("Id", jobId);
            startActivity(intent);
        });
    } else {
            lytLatest.setVisibility(View.GONE);
        }

        if (!serviceLatestList.isEmpty()) {
        latestServiceAdapter = new HomeServiceAdapter(getActivity(), serviceLatestList);
        rvServices.setAdapter(latestServiceAdapter);
        rvServices.getLayoutManager().scrollToPosition(0);

        new CountDownTimer(Long.MAX_VALUE, 2000) {

            public void onTick(long millisUntilFinished) {
                rvServices.smoothScrollToPosition(servicesPosition[0]);
                servicesPosition[0]++;
            }

            public void onFinish() {
                Toast.makeText(getContext(), "All services are loaded", Toast.LENGTH_LONG).show();
            }
        }.start();

        latestServiceAdapter.setOnItemClickListener(position -> {
            String jobId = serviceLatestList.get(position).getId();

            new SaveJob(getContext()).userSave(jobId);
            Intent intent = new Intent(getActivity(), ServiceDetailsActivity.class);
            intent.putExtra("Id", jobId);
            startActivity(intent);
        });
    } else {
            lytLatest.setVisibility(View.GONE);
        }

        if (!matrimonyLatestList.isEmpty()) {
            latestMatrimonyAdapter = new HomeMatrimonyAdapter(getActivity(), matrimonyLatestList);
            rvMatrimony.setAdapter(latestMatrimonyAdapter);
            rvMatrimony.getLayoutManager().scrollToPosition(0);

            new CountDownTimer(Long.MAX_VALUE, 2000) {

                public void onTick(long millisUntilFinished) {
                    rvMatrimony.smoothScrollToPosition(matrimonyPosition[0]);
                    matrimonyPosition[0]++;
                }

                public void onFinish() {
                    Toast.makeText(getContext(), "All services are loaded", Toast.LENGTH_LONG).show();
                }
            }.start();

            latestMatrimonyAdapter.setOnItemClickListener(position -> {
                String jobId = matrimonyLatestList.get(position).getId();

                new SaveJob(getContext()).userSave(jobId);
                Intent intent = new Intent(getActivity(), MatrimonyDetailsActivity.class);
                intent.putExtra("Id", jobId);
                startActivity(intent);
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
//                latestAdapter.notifyItemChanged(i);
            }
        }
    }
}
