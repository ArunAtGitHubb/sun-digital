package com.jellysoft.sundigitalindia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.adapter.MyAdapter;
import com.example.fragment.AppliedJobFragment;
import com.example.fragment.CategoryFragment;
import com.example.fragment.FilterSearchFragment;
import com.example.fragment.HomeFragment;
import com.example.fragment.LatestFragment;
import com.example.fragment.SavedJobFragment;
import com.example.fragment.SettingFragment;
import com.example.item.ItemAdmin;
import com.example.util.API;
import com.example.util.BannerAds;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ixidev.gdpr.GDPRChecker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    NavigationView navigationView;
    Toolbar toolbar;
    MyApplication MyApp;
    TextView phone,phone1,viewer;
    boolean doubleBackToExitPressedOnce = false;
    String versionName;
    TabLayout tabLayout;
    ViewPager viewPager;
    MyAdapter adapter;
    String vi;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IsRTL.ifSupported(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MyApp = MyApplication.getInstance();
        fragmentManager = getSupportFragmentManager();
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        phone = findViewById(R.id.phone);
        phone1 = findViewById(R.id.phone1);

        getAdmin();

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phone.getText().toString()));
                startActivity(intent);
            }
        });
        phone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone1.getText().toString()));
                startActivity(intent);
            }
        });
        try {
            versionName = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        LinearLayout mAdViewLayout = findViewById(R.id.adView);
//        BannerAds.ShowBannerAds(this, mAdViewLayout);
        tabLayout= findViewById(R.id.tabLayout);
        viewPager= findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.menu_home)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.menu_two)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.menu_one)));
        tabLayout.addTab(tabLayout.newTab().setText("Products Categories"));
        tabLayout.addTab(tabLayout.newTab().setText("Products City"));
        tabLayout.addTab(tabLayout.newTab().setText("சேவை வகைகள்"));
        tabLayout.addTab(tabLayout.newTab().setText("சேவை ஊர்"));
        tabLayout.addTab(tabLayout.newTab().setText("சாதி"));
        tabLayout.addTab(tabLayout.newTab().setText("ஊர்"));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("ttt", String.valueOf(tab.getPosition()));
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        HomeFragment homeFragment = new HomeFragment();
//        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.menu_go_home:
                        viewPager.setCurrentItem(0);
                        return false;
                    case R.id.menu_go_latest:
                        Intent intent1 = new Intent(MainActivity.this, LatestJob.class);
//                intent.putExtra("categoryId", spCategory.getSelectedItemPosition() == 0 ? "" : String.valueOf(mListCategory.get(spCategory.getSelectedItemPosition() - 1).getCategoryId()));
//                intent.putExtra("cityId", spCity.getSelectedItemPosition() == 0 ? "" : mListCity.get(spCity.getSelectedItemPosition() - 1).getCityId());
                        intent1.putExtra("isLatest", true);
                        startActivity(intent1);
                        return true;
                    case R.id.menu_go_category:
                        viewPager.setCurrentItem(1);
                        return false;
                    case R.id.menu_go_city:
                        viewPager.setCurrentItem(2);
                        return false;
                    case R.id.menu_go_contact:
                        Intent intent2 = new Intent(MainActivity.this,contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.menu_go_setting:
                        Intent intent5 = new Intent(MainActivity.this,devcontact.class);
                        startActivity(intent5);
                        return true;
                    case R.id.menu_go_privacy:
                        Intent intent = new Intent(MainActivity.this,privacy.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_go_about:
                        Intent intent7 = new Intent(MainActivity.this,about.class);
                        startActivity(intent7);
                        return true;
                    case R.id.menu_go_logout:
                        onBackPressed();
                        finish();
                        return true;
                    case R.id.menu_go_enquiry:
                        Intent intent8 = new Intent(MainActivity.this,provider.class);
                        startActivity(intent8);
                        return true;
                    case R.id.menu_go_share:
                        String text =
                                "சன் டிஜிட்டல் இந்தியா வேலை வாய்ப்பு" + "\n" +
                                        "வேலை வாய்ப்புகளை பயன்படுத்திக்கொள்ள இப்போதே\n" +
                                        "அப்பை டவுன்லோட் செய்யுங்கள்  https://play.google.com/store/apps/details?id=com.jellysoft.sundigitalindia";
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                text);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        return true;
                    case R.id.menu_go_search:
                        FilterSearchFragment filterSearchFragment = new FilterSearchFragment();
                        filterSearchFragment.show(fragmentManager, "Square");
                        break;
                }
                return false;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                viewer = drawerView.findViewById(R.id.views);
                viewer.setText(vi);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_side_nav);

//        if (!versionName.equals(Constant.appUpdateVersion) && Constant.isAppUpdate) {
//            newUpdateDialog();
//        }
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.Container, f1, name);
        ft.commit();
        setToolbarTitle(name);
    }

    public void setToolbarTitle(String Title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Title);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.search:
                FilterSearchFragment filterSearchFragment = new FilterSearchFragment();
                filterSearchFragment.show(fragmentManager, "Square");
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }


    private void Logout() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.menu_logout))
                .setMessage(getString(R.string.logout_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MyApp.saveIsLogin(false);
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                //  .setIcon(R.drawable.ic_logout)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (navigationView != null) {
            setHeader();
        }
    }

    private void setHeader() {
        if (MyApp.getIsLogin()) {
            View header = navigationView.getHeaderView(0);

            final CircleImageView imageUser = header.findViewById(R.id.header_image);

            Log.e("image", MyApp.getUserImage());
            if (!MyApp.getUserImage().isEmpty()) {
                Picasso.get().load(MyApp.getUserImage()).into(imageUser);
            }
        } else {
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() != 0) {
            String tag = fragmentManager.getFragments().get(fragmentManager.getBackStackEntryCount() - 1).getTag();
            setToolbarTitle(tag);
            super.onBackPressed();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.back_key), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
    private void getAdmin() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_admin");
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
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ItemAdmin objItem = new ItemAdmin();
                        objJson = jsonArray.getJSONObject(i);
                        objItem.setPhone(objJson.getString("phone"));
                        objItem.setPhone1(objJson.getString("phone1"));
                        objItem.setAddress(objJson.getString("address"));
                        phone.setText("+91 " + objJson.getString("phone"));
                        phone1.setText("+91 " + objJson.getString("phone1"));
                        vi = objJson.getString("views");
                    }
                } catch (JSONException e) {
                    Log.d("raky","catch");
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("raky","fail");
            }

        });



    }
}
