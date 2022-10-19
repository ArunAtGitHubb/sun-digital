package com.jellysoft.sundigitalindia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.db.DatabaseHelper;
import com.example.fragment.JobDetailsFragment;
import com.example.fragment.ProductDetailsFragment;
import com.example.fragment.SimilarJobFragment;
import com.example.item.ItemJob;
import com.example.item.ItemProduct;
import com.example.util.API;
import com.example.util.ApplyJob;
import com.example.util.BannerAds;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.SaveClickListener;
import com.example.util.SaveJob;
import com.example.util.UserUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ProductDetailsActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    ItemProduct objBean;
    TextView jobTitle, companyTitle, jobDate, jobDesignation,salary,text_job_address, jobAddress, jobVacancy, jobPhone, jobMail, jobWebsite,text_job_id,text_job_category,text_city,last_date,whatsapp_num,mail_id;
    ImageView image;
    String Id;
    DatabaseHelper databaseHelper;
    Button btnSave;
    LinearLayout mAdViewLayout;
    Button btnApplyJob,btn_whats;
    MyApplication MyApp;
    boolean isFromNotification = false;
    CoordinatorLayout lytParent;
    TabLayout tabLayout;
    ViewPager viewPager;
    WebView videoView;
    boolean isJobSaved = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_product);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        objBean = new ItemProduct();
        Intent i = getIntent();
        Id = i.getStringExtra("Id");
        if (i.hasExtra("isNotification")) {
            isFromNotification = true;
        }

        setTitle(getString(R.string.tool_product_details));
        databaseHelper = new DatabaseHelper(getApplicationContext());
        MyApp = MyApplication.getInstance();
        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        videoView = findViewById(R.id.videoView);
        mAdViewLayout = findViewById(R.id.adView);

        btn_whats = findViewById(R.id.btn_whats);
        image = findViewById(R.id.image);
        salary = findViewById(R.id.salary);
        jobTitle = findViewById(R.id.text_job_title);
        companyTitle = findViewById(R.id.text_job_company);
        jobDate = findViewById(R.id.text_job_date);
        jobDesignation = findViewById(R.id.text_job_designation);
        jobAddress = findViewById(R.id.text_job_address);
        jobPhone = findViewById(R.id.text_phone);
        jobWebsite = findViewById(R.id.text_website);
        jobMail = findViewById(R.id.text_email);
        jobVacancy = findViewById(R.id.text_vacancy);
        lytParent = findViewById(R.id.lytParent);
        text_job_id  = findViewById(R.id.text_job_id);
        btnApplyJob = findViewById(R.id.btn_apply_job);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        text_job_category  = findViewById(R.id.text_job_category);
        text_city  = findViewById(R.id.text_city);
        last_date  = findViewById(R.id.last_date);
        whatsapp_num  = findViewById(R.id.whatsapp_num);
        mail_id  = findViewById(R.id.mail_id);
        if (NetworkUtils.isConnected(ProductDetailsActivity.this)) {
            getDetails();
        } else {
            showToast(getString(R.string.conne_msg1));
        }


        jobPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialNumber();
            }
        });


        btnApplyJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialNumber();
            }
        });
    }

    private void getDetails() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_single_product");
        jsObj.addProperty("job_id", Id);
        jsObj.addProperty("user_id", UserUtils.getUserId());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                lytParent.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                lytParent.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    if (jsonArray.length() > 0) {
                        JSONObject objJson;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            if (objJson.has(Constant.STATUS)) {
                                lyt_not_found.setVisibility(View.VISIBLE);
                            } else {
                                Log.d("products", "before objBean.toString()");
                                objBean.setId(objJson.getString(Constant.PRODUCT_ID));
                                objBean.setProductName(objJson.getString(Constant.PRODUCT_NAME));
                                objBean.setProductDate(objJson.getString(Constant.PRODUCT_START_DATE));
                                objBean.setProductAddress(objJson.getString(Constant.PRODUCT_ADDRESS));
                                objBean.setProductImage(objJson.getString(Constant.PRODUCT_IMAGE));
                                objBean.setProductPhoneNumber(objJson.getString(Constant.PRODUCT_PHONE_NO));
                                objBean.setProductMail(objJson.getString(Constant.PRODUCT_MAIL));
                                objBean.setProductCompanyWebsite(objJson.getString(Constant.PRODUCT_SITE));
                                objBean.setProductDesc(objJson.getString(Constant.PRODUCT_DESC));
                                objBean.setPLate(objJson.getString(Constant.PRODUCT_END_DATE));
                                objBean.setCity(objJson.getString(Constant.CITY_NAME));
                                objBean.setUrl(objJson.getString("url"));
                                objBean.setProductMail(objJson.getString(Constant.PRODUCT_MAIL));
                                objBean.setProductCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                                objBean.setProductType(objJson.getString(Constant.PRODUCT_TYPE));
                                Log.d("products", "objBean.toString()");
                            }
                        }
                        Log.d("products", objBean.toString());
                        setResult();
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        lytParent.setVisibility(View.GONE);
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                lytParent.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setResult() {
        firstFavourite();

        Log.d("productDetails", objBean.toString());

        jobAddress.setText("objBean.getProductAddress()");
        jobTitle.setText(objBean.getProductName());
        text_job_id.setText("SDI00"+objBean.getId());
        text_job_category.setText(objBean.getProductCategoryName());
        text_city.setText(objBean.getCity());
        companyTitle.setText(objBean.getProductCompanyName());
        jobDate.setText(objBean.getProductDate());
        last_date.setText(objBean.getPLate());
        SpannableString content = new SpannableString(objBean.getProductPhoneNumber());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);


        jobPhone.setText(content);
        whatsapp_num.setText(content);
        SpannableString content2 = new SpannableString(objBean.getProductMail());
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        mail_id.setText(content2);
        Picasso.get().load(objBean.getProductImage()).into(image);
        Log.d("bvm",objBean.getUrl());
//        Uri uri = Uri.parse(objBean.getUrl());
//
//        videoView.setVideoURI(uri);
//        MediaController mediaController = new MediaController(this);
//        mediaController.setAnchorView(videoView);
//        mediaController.setMediaPlayer(videoView);
//        videoView.setMediaController(mediaController);
//        videoView.start();
        if(objBean.getUrl() !=""){
            videoView.getSettings().setJavaScriptEnabled(true);
            videoView.getSettings().setPluginState(WebSettings.PluginState.ON);
            videoView.loadUrl(objBean.getUrl());
            videoView.setWebChromeClient(new WebChromeClient());
        }else{
            videoView.setVisibility(View.GONE);
        }

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        btn_whats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = objBean.getProductPhoneNumber();
                String url = "https://api.whatsapp.com/send?phone=" + phone.substring(1);
                PackageManager pm = getPackageManager();
                try {
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        whatsapp_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = objBean.getProductPhoneNumber();
                String url = "https://api.whatsapp.com/send?phone=" + phone.substring(1);
                PackageManager pm = getPackageManager();
                try {
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        mail_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmail();
            }
        });
    }

    private void setupViewPager(final ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ProductDetailsFragment.newInstance(objBean), getString(R.string.tab_job_details));
//        adapter.addFragment(SimilarJobFragment.newInstance(Id), getString(R.string.tab_job_similar));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return FragmentStatePagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void showToast(String msg) {
        Toast.makeText(ProductDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_edit:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        objBean.getProductName() + "\n" +
                                getString(R.string.job_company_lbl) + objBean.getProductCompanyName() + "\n" +
                                getString(R.string.job_designation_lbl) + objBean.getProductDesignation() + "\n" +
                                getString(R.string.job_phone_lbl) + objBean.getProductPhoneNumber() + "\n" +
                                getString(R.string.job_address_lbl) + objBean.getCity() + "\n\n" +
                                "Download Application here https://play.google.com/store/apps/details?id=com.jellysoft.sundigitalindia");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void openWebsite() {
        startActivity(new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(addHttp(objBean.getProductCompanyWebsite()))));
    }

    private void openEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", objBean.getProductMail(), null));
        emailIntent
                .putExtra(Intent.EXTRA_SUBJECT, "Apply for the post " + objBean.getProductDesignation());
        startActivity(Intent.createChooser(emailIntent, "Send suggestion..."));
    }

    protected String addHttp(String string1) {
        // TODO Auto-generated method stub
        if (string1.startsWith("http://"))
            return String.valueOf(string1);
        else
            return "http://" + String.valueOf(string1);
    }

    private void dialNumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", objBean.getProductPhoneNumber(), null));
        startActivity(intent);
    }

    private void firstFavourite() {

    }

    @Override
    public void onBackPressed() {
        if (isFromNotification) {
            Intent intent = new Intent(ProductDetailsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
