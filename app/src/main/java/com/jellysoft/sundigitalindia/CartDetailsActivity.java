package com.jellysoft.sundigitalindia;

import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_CITY_ID;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_FOOD_ID;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_FOOD_NAME;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_FOOD_PRICE;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_FOOD_QTY;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_RES_ID;
import static com.example.util.CartReaderContract.CartEntry.COLUMN_NAME_RES_NAME;
import static com.example.util.CartReaderContract.CartEntry.TABLE_CART;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.CartAdapter;
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
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class CartDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    RecyclerView rvFoods;
    ItemFood food;
    static TextView foodsTotalAmount;
    static int totalQty = 0, totalAmt = 0;
    CartAdapter adapter;
    ArrayList<ItemFood> mList;
    ArrayList<String> cities, cityIds;
    String Id;
    MyApplication MyApp;
    Spinner spinner;
    EditText textName, textPhone, textAddress;
    Button orderBtn;
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
        setContentView(R.layout.activity_details_cart);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        dbHelper = new CartDbHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        Intent i = getIntent();
        Id = i.getStringExtra("categoryId");
        if (i.hasExtra("isNotification")) {
            isFromNotification = true;
        }

        setTitle(i.getStringExtra("res_name"));

        MyApp = MyApplication.getInstance();
        mList = new ArrayList<>();
        cities = new ArrayList<>();
        cityIds = new ArrayList<>();

        foodsTotalAmount = findViewById(R.id.foodsTotalAmount);

        textName = findViewById(R.id.textName);
        textPhone = findViewById(R.id.textPhone);
        textAddress = findViewById(R.id.textAddress);

        orderBtn = findViewById(R.id.orderBtn);

        spinner = findViewById(R.id.spinner);
        if (NetworkUtils.isConnected(CartDetailsActivity.this)) {
            listCities();

            spinner.setOnItemSelectedListener(this);
        } else {
            cities.add("Tuticorin");
            cities.add("Tirunelveli");
        }

        rvFoods = findViewById(R.id.rvFoods);
        rvFoods.setHasFixedSize(false);
        rvFoods.setNestedScrollingEnabled(false);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        rvFoods.setLayoutManager(layoutManager);

        orderBtn.setOnClickListener(view -> {
            String phone = String.valueOf(textPhone.getText());
            String name = String.valueOf(textName.getText());

            if(name.isBlank()) {
                Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
            }
            if(phone.length() != 10){
                Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            }

            if(phone.length() == 10 && !name.isBlank()) {
                makeOrder();
            }
        });

        if (NetworkUtils.isConnected(CartDetailsActivity.this)) {
            getDetails();
        } else {
            showToast(getString(R.string.conne_msg1));
        }
    }

    private void listCities() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_list");
        jsObj.addProperty("user_id", UserUtils.getUserId());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                Log.d("cities", result);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject jobAppJson = mainJson.getJSONObject(Constant.ARRAY_NAME);

                    JSONArray cityArray = jobAppJson.getJSONArray("city_list");
                    Log.d("cities2", cityArray.toString());
                    for (int i = 0; i < cityArray.length(); i++) {
                        JSONObject jsonObject = cityArray.getJSONObject(i);
                        cities.add(jsonObject.getString("city_name"));
                        cityIds.add(jsonObject.getString("c_id"));
                    }

                    String[] city = new String[cities.size()];
                    for(int i = 0; i < cities.size(); i++) {
                        city[i] = cities.get(i);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, city);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } catch (Exception e) {
                    Log.e("err", e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void getDetails() {
        String[] projection = {
                BaseColumns._ID,
                COLUMN_NAME_FOOD_ID,
                COLUMN_NAME_RES_ID,
                COLUMN_NAME_CITY_ID,
                COLUMN_NAME_FOOD_QTY,
                COLUMN_NAME_FOOD_NAME,
                COLUMN_NAME_FOOD_PRICE,
                COLUMN_NAME_RES_NAME
        };

        String selection = COLUMN_NAME_FOOD_QTY + " > ?";
        String[] selectionArgs = { "0" };

        Cursor cursor = db.query(TABLE_CART, projection, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            food = new ItemFood();
            Log.d("dbs", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_RES_NAME)));
            food.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_RES_NAME)));
            food.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_FOOD_ID)));
            food.setCatId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_RES_ID)));
            food.setCityId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_CITY_ID)));
            food.setQty(Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_FOOD_QTY))));
            food.setCount(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_FOOD_QTY))));
            food.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_FOOD_NAME)));
            food.setPrice(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_FOOD_PRICE)));
            mList.add(food);
        }
        cursor.close();
        setResult();
    }

    private  void makeOrder() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        ArrayList<ArrayList<String>> orders = new ArrayList<>();
        String[] projection = {
                BaseColumns._ID,
                COLUMN_NAME_FOOD_ID,
                COLUMN_NAME_RES_ID,
                COLUMN_NAME_CITY_ID,
                COLUMN_NAME_FOOD_QTY,
                COLUMN_NAME_FOOD_NAME,
                COLUMN_NAME_FOOD_PRICE,
                COLUMN_NAME_RES_NAME
        };

        String selection = COLUMN_NAME_FOOD_QTY + " > ?";
        String[] selectionArgs = { "0" };

        Cursor cursor = db.query(TABLE_CART, projection, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            food = new ItemFood();
            ArrayList<String> order = new ArrayList<>();
            order.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_FOOD_ID)));
            order.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_FOOD_QTY)));
            orders.add(order);
        }
        cursor.close();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());

        jsObj.addProperty("method_name", "post_order");
        jsObj.addProperty("customer_name", textName.getText().toString());
        jsObj.addProperty("customer_phone", textPhone.getText().toString());
        jsObj.addProperty("customer_address", textAddress.getText().toString());
        jsObj.addProperty("city_id", cityIds.get(spinner.getSelectedItemPosition()));
        jsObj.addProperty("res_id", mList.get(0).getCatId());
        jsObj.addProperty("totalQty", String.valueOf(totalQty));
        jsObj.addProperty("totalAmt", String.valueOf(totalAmt));
        jsObj.addProperty("orders", orders.toString());
        params.put("data", API.toBase64(jsObj.toString()));

        Log.d("orders", jsObj.toString());

        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jobAppJson = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    Log.d("orders2", result);

                    JSONObject obj = jobAppJson.getJSONObject(0);
                    Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                    finish();
                    Intent intent = new Intent(CartDetailsActivity.this, RestaurantDetailsActivity.class);
                    intent.putExtra("categoryId", mList.get(0).getCatId());
                    startActivity(intent);
                }catch (Exception e) {
                    Log.d("err2", e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    @SuppressLint("SetTextI18n")
    private void setResult() {
        adapter = new CartAdapter(this, mList, dbHelper);
        rvFoods.setAdapter(adapter);
        updatePrice();
    }

    public void showToast(String msg) {
        Toast.makeText(CartDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
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


    @SuppressLint("SetTextI18n")
    public static void updatePrice() {
        Cursor qtyCursor = db.rawQuery("SELECT SUM(food_qty) as total FROM cart", null);
        qtyCursor.moveToFirst();
        totalQty = qtyCursor.getInt(0);
        qtyCursor.close();

        Cursor cursor = db.rawQuery("SELECT SUM(food_price * food_qty) as total FROM cart", null);
        cursor.moveToFirst();
        totalAmt = cursor.getInt(0);
        foodsTotalAmount.setText("GrandTotal: Rs. " + totalAmt + "/-");
        cursor.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
