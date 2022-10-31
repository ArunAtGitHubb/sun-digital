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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.db.DatabaseHelper;
import com.example.fragment.MatrimonyDetailsFragment;
import com.example.item.ItemMatrimony;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MatrimonyDetailsActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    ItemMatrimony objBean;
    TextView matrimonyName, matrimonyDate, matrimonySalary,
            matrimonyPhone, matrimonyReligion, matrimonyPhone2,
            serviceMail, matrimonyJob, matrimonyAge,
            text_city, last_date, whatsapp_num, mail_id, text_area,
            matrimonyCaste, text_matrimony_id, text_gender;
    ImageSlider image;
    String Id;
    DatabaseHelper databaseHelper;
    Button btnSave;
    LinearLayout mAdViewLayout;
    Button btnApplyJob, btn_whats;
    MyApplication MyApp;
    boolean isFromNotification = false;
    CoordinatorLayout lytParent;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_matrimony);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        objBean = new ItemMatrimony();
        Intent i = getIntent();
        Id = i.getStringExtra("Id");
        if (i.hasExtra("isNotification")) {
            isFromNotification = true;
        }

        setTitle(getString(R.string.tool_matrimony_details));
        databaseHelper = new DatabaseHelper(getApplicationContext());
        MyApp = MyApplication.getInstance();
        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        mAdViewLayout = findViewById(R.id.adView);

        matrimonyName = findViewById(R.id.text_name);
        matrimonyReligion = findViewById(R.id.text_matrimony_religion);
        text_gender = findViewById(R.id.text_gender);
        text_city  = findViewById(R.id.text_city);
        text_area = findViewById(R.id.text_area);
        matrimonySalary = findViewById(R.id.text_salary);
        matrimonyJob = findViewById(R.id.text_job);
        matrimonyCaste = findViewById(R.id.text_caste);
        matrimonyAge = findViewById(R.id.text_age);
        matrimonyDate = findViewById(R.id.text_job_date);
        text_matrimony_id = findViewById(R.id.text_matrimony_id);
        last_date  = findViewById(R.id.last_date);

        btn_whats = findViewById(R.id.btn_whats);
        image = findViewById(R.id.image_slider);
        matrimonyPhone = findViewById(R.id.text_phone);
        matrimonyPhone2 = findViewById(R.id.text_phone2);
        serviceMail = findViewById(R.id.text_email);
        lytParent = findViewById(R.id.lytParent);
        btnApplyJob = findViewById(R.id.btn_apply_job);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        whatsapp_num  = findViewById(R.id.whatsapp_num);
        mail_id  = findViewById(R.id.mail_id);
        if (NetworkUtils.isConnected(MatrimonyDetailsActivity.this)) {
            getDetails();
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        matrimonyPhone.setOnClickListener(v -> dialNumber(objBean.getMatrimonyPhoneNumber()));
        matrimonyPhone2.setOnClickListener(v -> dialNumber(objBean.getMatrimonyPhoneNumber2()));
        btnApplyJob.setOnClickListener(v -> dialNumber(objBean.getMatrimonyPhoneNumber()));
    }

    private void getDetails() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_single_matrimony");
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
                Log.d("result", result + " " + Id);
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
                                ArrayList<SlideModel> images = new ArrayList<>();
                                objBean.setId(objJson.getString(Constant.SERVICE_ID));
                                objBean.setMatrimonyName(objJson.getString(Constant.MATRIMONY_NAME));
                                objBean.setMatrimonyGender(objJson.getString(Constant.MATRIMONY_GENDER));
                                objBean.setMatrimonyReligion(objJson.getString(Constant.MATRIMONY_RELIGION));
                                objBean.setMatrimonyArea(objJson.getString(Constant.MATRIMONY_AREA));
                                objBean.setMatrimonyMaritalStatus(objJson.getString(Constant.MATRIMONY_MARITAL_STATUS));
                                objBean.setMatrimonyDob(objJson.getString(Constant.MATRIMONY_DOB));
                                objBean.setMatrimonyAge(objJson.getString(Constant.MATRIMONY_AGE));
                                objBean.setMatrimonyEducation(objJson.getString(Constant.MATRIMONY_EDUCATION));
                                objBean.setMatrimonyCareer(objJson.getString(Constant.MATRIMONY_CAREER));
                                objBean.setMatrimonySalary(objJson.getString(Constant.MATRIMONY_SALARY));
                                objBean.setMatrimonyDesc(objJson.getString(Constant.MATRIMONY_DESC));
                                objBean.setMatrimonyPartnerExpect(objJson.getString(Constant.MATRIMONY_PARTNER_EXPECT));
                                objBean.setMatrimonyPersonName(objJson.getString(Constant.MATRIMONY_PERSON_NAME));
                                objBean.setMatrimonyPhoneNumber(objJson.getString(Constant.MATRIMONY_PHONE_NUMBER));
                                objBean.setMatrimonyPhoneNumber2(objJson.getString(Constant.MATRIMONY_PHONE_NUMBER2));
                                objBean.setMatrimonyImage(objJson.getString(Constant.MATRIMONY_IMAGE));
                                objBean.setMatrimonyLogo(objJson.getString(Constant.MATRIMONY_LOGO));
                                objBean.setMatrimonyImage2(objJson.getString(Constant.MATRIMONY_IMAGE2));
                                objBean.setMatrimonyImage3(objJson.getString(Constant.MATRIMONY_IMAGE3));
                                objBean.setMatrimonyImage4(objJson.getString(Constant.MATRIMONY_IMAGE4));
                                objBean.setMatrimonySDate(objJson.getString(Constant.MATRIMONY_START_DATE));
                                objBean.setMatrimonyEDate(objJson.getString(Constant.MATRIMONY_END_DATE));
                                objBean.setUrl(objJson.getString("url"));
                                objBean.setCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                                objBean.setCity(objJson.getString(Constant.CITY_NAME));
                                objBean.setCid(objJson.getString(Constant.CATEGORY_CID));

                                images.add(new SlideModel(objJson.getString(Constant.MATRIMONY_IMAGE), ScaleTypes.FIT));
                                images.add(new SlideModel(objJson.getString(Constant.MATRIMONY_IMAGE2), ScaleTypes.FIT));
                                images.add(new SlideModel(objJson.getString(Constant.MATRIMONY_IMAGE3), ScaleTypes.FIT));
                                images.add(new SlideModel(objJson.getString(Constant.MATRIMONY_IMAGE4), ScaleTypes.FIT));
                                objBean.setMatrimonyBanner(images);
                                setResult();
                            }
                        }
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

    @SuppressLint("SetTextI18n")
    private void setResult() {
        firstFavourite();

        matrimonyName.setText(objBean.getMatrimonyName());
        matrimonyCaste.setText(objBean.getCategoryName());
        text_gender.setText(objBean.getMatrimonyGender());
        matrimonyAge.setText(objBean.getMatrimonyAge());
        matrimonyReligion.setText(objBean.getMatrimonyReligion());
        text_city.setText(objBean.getCity());
        text_area.setText(objBean.getMatrimonyArea());
        matrimonySalary.setText(objBean.getMatrimonySalary());
        matrimonyJob.setText(objBean.getMatrimonyCareer());
        matrimonyDate.setText(objBean.getMatrimonySDate());
        last_date.setText(objBean.getMatrimonyEDate());
        text_matrimony_id.setText("MM" + objBean.getId());

        SpannableString content = new SpannableString(objBean.getMatrimonyPhoneNumber());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        matrimonyPhone.setText(content);

        whatsapp_num.setText(content);

        SpannableString content2 = new SpannableString(objBean.getMatrimonyPhoneNumber2());
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        matrimonyPhone2.setText(content2);

//        SpannableString content2 = new SpannableString(objBean.getMatrimonyMail());
//        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
//        mail_id.setText(content2);

        image.setImageList(objBean.getMatrimonyBanner(), ScaleTypes.FIT);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        btn_whats.setOnClickListener(view -> {
            String phone = "91" + objBean.getMatrimonyPhoneNumber().replace("+91", "");
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
            String phone = "91" + objBean.getMatrimonyPhoneNumber().replace("+91", "");
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
        adapter.addFragment(MatrimonyDetailsFragment.newInstance(objBean), getString(R.string.tab_matrimony_details));
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
        Toast.makeText(MatrimonyDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                        objBean.getMatrimonyName() + "\n" +
                                getString(R.string.job_phone_lbl) + objBean.getMatrimonyPhoneNumber() + "\n" +
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
//        startActivity(new Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse(addHttp(objBean.getWebsiteLink()))));
    }

    private void openEmail() {
//        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//                "mailto", objBean.getMatrimonyMail(), null));
//        emailIntent
//                .putExtra(Intent.EXTRA_SUBJECT, "Apply for the post " + objBean.getMatrimonyDesignation());
//        startActivity(Intent.createChooser(emailIntent, "Send suggestion..."));
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
        if (isFromNotification) {
            Intent intent = new Intent(MatrimonyDetailsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
