package com.jellysoft.sundigitalindia;

import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_CITY_ID;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_FOOD_ID;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_FOOD_PRICE;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_FOOD_QTY;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_RES_ID;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_RES_NAME;
import static com.example.util.CartReaderContract.CartEntry.TABLE_FOOD;
import static com.example.util.CartReaderContract.SQL_CREATE_TABLE_CART;
import static com.example.util.CartReaderContract.SQL_CREATE_TABLE_FOOD;
import static com.example.util.CartReaderContract.SQL_DELETE_TABLE_CART;
import static com.example.util.CartReaderContract.SQL_DELETE_TABLE_FOOD;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.FoodsAdapter;
import com.example.db.CartDbHelper;
import com.example.item.ItemFood;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.UserUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class RestaurantDetailsActivity extends AppCompatActivity {

    RecyclerView rvFoods;
    ItemFood food;
    static TextView foodsTotalAmount;
    FoodsAdapter adapter;
    ArrayList<ItemFood> mList;
    String Id;
    static CardView viewCart;
    MyApplication MyApp;
    boolean isFromNotification = false;

    CartDbHelper dbHelper;
    static SQLiteDatabase db;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("state2", "create");
        setContentView(R.layout.activity_details_restaurant);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setTitle("Restaurant Menu");

        dbHelper = new CartDbHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        db.execSQL(SQL_DELETE_TABLE_CART);
        db.execSQL(SQL_CREATE_TABLE_CART);
        db.execSQL(SQL_DELETE_TABLE_FOOD);
        db.execSQL(SQL_CREATE_TABLE_FOOD);

        Intent i = getIntent();
        Id = i.getStringExtra("categoryId");
        if (i.hasExtra("isNotification")) {
            isFromNotification = true;
        }

        MyApp = MyApplication.getInstance();
        mList = new ArrayList<>();

        viewCart = findViewById(R.id.viewCart);
        viewCart.setVisibility(View.GONE);

        viewCart.setOnClickListener(view -> {
            Intent intent = new Intent(this, CartDetailsActivity.class);
            intent.putExtra("res_name", mList.get(0).getCategory());
            startActivity(intent);
        });

        rvFoods = findViewById(R.id.rvFoods);
        rvFoods.setHasFixedSize(false);
        rvFoods.setNestedScrollingEnabled(false);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        rvFoods.setLayoutManager(layoutManager);

        foodsTotalAmount = findViewById(R.id.foodsTotalAmount);

        if (NetworkUtils.isConnected(RestaurantDetailsActivity.this)) {
            getDetails();
        } else {
            showToast(getString(R.string.conne_msg1));
        }
    }

    private void getDetails() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_food_by_restaurant_id");
        jsObj.addProperty("job_id", Id);
        jsObj.addProperty("user_id", UserUtils.getUserId());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                Log.d("result22", result + Id);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    if (jsonArray.length() > 0) {
                        JSONObject objJson;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            if (objJson.has(Constant.STATUS)) {
                            } else {
                                food = new ItemFood();
                                food.setId(objJson.getString(Constant.id));
                                food.setName(objJson.getString(Constant.FOOD_NAME));
                                food.setPrice(objJson.getString(Constant.FOOD_PRICE));
                                food.setType(objJson.getString(Constant.FOOD_TYPE));
                                food.setCityId(objJson.getString(Constant.city_id));
                                food.setViews(objJson.getString("views"));
                                food.setImageUrl(objJson.getString(Constant.FOOD_LOGO));
                                food.setCatId(objJson.getString(Constant.cat_id));
                                food.setCategory(objJson.getString(Constant.CATEGORY_NAME));
                                food.setCategoryImage(objJson.getString(Constant.CATEGORY_IMAGE));
                                mList.add(food);
                            }
                        }
                        setResult();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setResult() {

        adapter = new FoodsAdapter(this, mList, dbHelper);
        rvFoods.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mList.forEach(food -> {
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_FOOD_ID, food.getId());
                values.put(COLUMN_NAME_FOOD_PRICE, food.getPrice());
                values.put(COLUMN_NAME_RES_ID, food.getCatId());
                values.put(COLUMN_NAME_CITY_ID, food.getCityId());
                values.put(COLUMN_NAME_RES_NAME, food.getCategory());

                db.insert(TABLE_FOOD, null, values);
            });
        }
    }

    public void showToast(String msg) {
        Toast.makeText(RestaurantDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
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
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    public static void showCart() {
        viewCart.setVisibility(View.VISIBLE);
    }

    public static void updatePrice() {
        Cursor cursor = db.rawQuery("SELECT SUM(food_price * food_qty) as total FROM cart", null);
        cursor.moveToFirst();
        foodsTotalAmount.setText("Rs. " + cursor.getInt(0) + "/-");
        cursor.close();
    }

    public static void hideCart() {
        viewCart.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.execSQL(SQL_DELETE_TABLE_CART);
        db.execSQL(SQL_DELETE_TABLE_FOOD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for(ItemFood food: mList) {
            Cursor cursor = db.rawQuery("SELECT food_qty FROM cart WHERE food_id = " + food.getId(), null);
            cursor.moveToNext();
            try {
                food.setCount(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_FOOD_QTY))));
            }catch (CursorIndexOutOfBoundsException e){
                food.setCount(0);
            }
            cursor.close();
        }
        setResult();
        Log.d("state2", "resume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        updatePrice();
        Log.d("state2", "start");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("state2", "pause");
    }

    @Override
    public void onBackPressed() {
        finish();
        hideCart();
        Intent intent = new Intent(RestaurantDetailsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
