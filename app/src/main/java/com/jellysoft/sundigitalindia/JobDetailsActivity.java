package com.jellysoft.sundigitalindia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.item.ItemJob;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
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

public class JobDetailsActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    ItemJob objBean;
    TextView jobTitle, companyTitle, jobDate, jobDesignation,
            salary, work_time, jobAddress, jobVacancy,
            jobPhone, jobMail, jobWebsite, text_job_id, text_job_category,
            text_city, last_date, whatsapp_num, mail_id, text_area, jobPhone2;
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        objBean = new ItemJob();
        Intent i = getIntent();
        Id = i.getStringExtra("Id");
        if (i.hasExtra("isNotification")) {
            isFromNotification = true;
        }

        setTitle(getString(R.string.tool_job_details));
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
        text_area = findViewById(R.id.text_area);
        companyTitle = findViewById(R.id.text_job_company);
        jobDate = findViewById(R.id.text_job_date);
        jobDesignation = findViewById(R.id.text_job_designation);
        work_time = findViewById(R.id.work_time);
        jobAddress = findViewById(R.id.text_job_address);
        jobPhone = findViewById(R.id.text_phone);
        jobPhone2 = findViewById(R.id.text_phone2);
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
        if (NetworkUtils.isConnected(JobDetailsActivity.this)) {
            getDetails();
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        jobPhone.setOnClickListener(v -> dialNumber(objBean.getJobPhoneNumber()));
        jobPhone2.setOnClickListener(v -> dialNumber(objBean.getJobPhoneNumber2()));

        btnApplyJob.setOnClickListener(v -> dialNumber(objBean.getJobPhoneNumber()));
    }

    private void getDetails() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_single_job");
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
                Log.d("result", result);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    isJobSaved = mainJson.getBoolean(Constant.JOB_ALREADY_SAVED);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    if (jsonArray.length() > 0) {
                        JSONObject objJson;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            if (objJson.has(Constant.STATUS)) {
                                lyt_not_found.setVisibility(View.VISIBLE);
                            } else {
                                objBean.setId(objJson.getString(Constant.JOB_ID));
                                objBean.setJobName(objJson.getString(Constant.JOB_NAME));
                                objBean.setJobCompanyName(objJson.getString(Constant.JOB_COMPANY_NAME));
                                objBean.setJobDesignation(objJson.getString(Constant.JOB_DESIGNATION));
                                objBean.setJobAddress(objJson.getString(Constant.JOB_ADDRESS));
                                objBean.setJobImage(objJson.getString(Constant.JOB_IMAGE));
                                objBean.setJobArea(objJson.getString(Constant.JOB_AREA));
                                objBean.setJobVacancy(objJson.getString(Constant.JOB_VACANCY));
                                objBean.setJobPhoneNumber(objJson.getString(Constant.JOB_PHONE_NO));
                                objBean.setJobPhoneNumber2(objJson.getString(Constant.JOB_PHONE_NO2));
                                objBean.setJobMail(objJson.getString(Constant.JOB_MAIL));
                                objBean.setJobCompanyWebsite(objJson.getString(Constant.JOB_SITE));
                                objBean.setJobWebsite(objJson.getString(Constant.WEBSITE_LINK));
                                objBean.setJobDesc(objJson.getString(Constant.JOB_DESC));
                                objBean.setJobSkill(objJson.getString(Constant.JOB_SKILL));
                                objBean.setJobQualification(objJson.getString(Constant.JOB_QUALIFICATION));
                                objBean.setJobSalary(objJson.getString(Constant.JOB_SALARY));
                                objBean.setJobTime(objJson.getString(Constant.JOB_TIME));
                                objBean.setAge(objJson.getString("job_age"));
                                objBean.setSex(objJson.getString("job_sex"));
                                objBean.setJobPdf(objJson.getString(Constant.JOB_PDF));
                                objBean.setJobDate(objJson.getString(Constant.JOB_DATE));
                                objBean.setpLate(objJson.getString("job_date1"));
                                objBean.setMarital(objJson.getString("job_marital"));
                                objBean.setCity(objJson.getString(Constant.CITY_NAME));
                                objBean.setUrl(objJson.getString("url"));
                                objBean.setJobMail(objJson.getString(Constant.JOB_MAIL));
                                objBean.setJobCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                                objBean.setJobExperience(objJson.getString(Constant.JOB_EXP));
                                objBean.setJobType(objJson.getString(Constant.JOB_TYPE));
                            }
                        }
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

        jobAddress.setText(objBean.getJobAddress());
        salary.setText(objBean.getJobSalary());
        jobTitle.setText(objBean.getJobName());
        text_job_id.setText("JD" + objBean.getId());
        work_time.setText(objBean.getJobTime());
        text_job_category.setText(objBean.getJobCategoryName());
        text_city.setText(objBean.getCity());
        text_area.setText(objBean.getJobArea());
        companyTitle.setText(objBean.getJobCompanyName());
        jobDate.setText(objBean.getJobDate());
        last_date.setText(objBean.getpLate());

        SpannableString content = new SpannableString(objBean.getJobPhoneNumber());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        jobPhone.setText(content);

        SpannableString content2 = new SpannableString(objBean.getJobPhoneNumber2());
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        jobPhone2.setText(content2);

        whatsapp_num.setText(content);
        jobDesignation.setText(objBean.getJobDesignation());
        jobVacancy.setText(objBean.getJobVacancy());

        SpannableString content3 = new SpannableString(objBean.getJobMail());
        content3.setSpan(new UnderlineSpan(), 0, content3.length(), 0);
        mail_id.setText(content3);


        Picasso.get().load(objBean.getJobImage()).into(image);

        if(objBean.getUrl() != null && !objBean.getUrl().isEmpty()){
            videoView.getSettings().setJavaScriptEnabled(true);
            videoView.getSettings().setPluginState(WebSettings.PluginState.ON);
            videoView.loadUrl(objBean.getUrl());
            videoView.setWebChromeClient(new WebChromeClient());
        }else{
            videoView.setVisibility(View.GONE);
        }

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        btn_whats.setOnClickListener(view -> {
            String phone = "91" + objBean.getJobPhoneNumber().replace("+91", "");
            String url = "https://api.whatsapp.com/send?phone=" + phone;
            PackageManager pm = getPackageManager();
            try {
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        whatsapp_num.setOnClickListener(view -> {
            String phone = "91" + objBean.getJobPhoneNumber().replace("+91", "");
            String url = "https://api.whatsapp.com/send?phone=" + phone;
            PackageManager pm = getPackageManager();
            try {
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        mail_id.setOnClickListener(view -> openEmail());
    }

    private void setupViewPager(final ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(JobDetailsFragment.newInstance(objBean), getString(R.string.tab_job_details));
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
        Toast.makeText(JobDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                        objBean.getJobName() + "\n" +
                                getString(R.string.job_company_lbl) + objBean.getJobCompanyName() + "\n" +
                                getString(R.string.job_designation_lbl) + objBean.getJobDesignation() + "\n" +
                                getString(R.string.job_phone_lbl) + objBean.getJobPhoneNumber() + "\n" +
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
                Uri.parse(addHttp(objBean.getJobCompanyWebsite()))));
    }

    private void openEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", objBean.getJobMail(), null));
        emailIntent
                .putExtra(Intent.EXTRA_SUBJECT, "Apply for the post " + objBean.getJobDesignation());
        startActivity(Intent.createChooser(emailIntent, "Send suggestion..."));
    }

    protected String addHttp(String string1) {
        // TODO Auto-generated method stub
        if (string1.startsWith("http://"))
            return String.valueOf(string1);
        else
            return "http://" + String.valueOf(string1);
    }

    private void dialNumber(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }

    private void firstFavourite() {

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
