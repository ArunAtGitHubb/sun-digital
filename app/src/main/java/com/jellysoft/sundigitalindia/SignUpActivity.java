package com.jellysoft.sundigitalindia;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



import com.example.adapter.NothingSelectedSpinnerAdapter;
import com.example.item.ItemArea;
import com.example.item.ItemCategory;
import com.example.item.ItemCity;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.sayantan.advancedspinner.MultiSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SignUpActivity extends AppCompatActivity implements Validator.ValidationListener {

    @NotEmpty
    EditText edtFullName;
    @NotEmpty
    @Email
    EditText edtEmail;
    @NotEmpty
    @Password (message = "Invalid password min 6 character needed")
    EditText edtPassword;
    @Length(max = 14, min = 6, message = "Enter valid Phone Number")
    EditText edtMobile;

    @NotEmpty
    EditText edtCompanyName;
    @NotEmpty
    @Email
    EditText edtCompanyEmail;
    @Length(max = 14, min = 6, message = "Enter valid Phone Number")
    EditText edtCompanyPhone;
    @NotEmpty
    EditText edtCompanyAddress;
    @NotEmpty
    EditText edtCompanyDesc;
    @NotEmpty
    EditText edtCompanyWorkDay;
    @NotEmpty
    EditText edtCompanyWorkTime;
    @NotEmpty
    EditText edtCompanyWebsite;

    Button btnSignUp;
    String strFullname, area, strPassword, strMobi, strMessage, cit, fcat, fcit;
    private Validator validator;
    TextView txtLogin;
    RadioButton rbProvider, rbIndividual, rbCompany, rbSeeker;
    ProgressDialog pDialog;
    LinearLayout lytJobProvider, lytCompany;
    RadioGroup rgJobUserType, rgCompanyType;
    Spinner  spin_dis,spin_city;
    MultiSpinner spin_cat, spin_fav_dis;
    ArrayList<ItemCategory> mListCategory;
    ArrayList<ItemCity> mListCity;
    ArrayList<ItemArea> mListArea;
    ArrayList<String> mCategoryName, mCityName, mAreaName;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        IsRTL.ifSupported(this);
        pDialog = new ProgressDialog(this);
        lytJobProvider = findViewById(R.id.lytProvider);
        lytCompany = findViewById(R.id.lytCompany);
        rgJobUserType = findViewById(R.id.rgJobUserType);
        rgCompanyType = findViewById(R.id.radioGrp);
        mCityName = new ArrayList<>();
        mAreaName = new ArrayList<>();
        mCategoryName = new ArrayList<>();
        mListCategory = new ArrayList<>();
        mListCity = new ArrayList<>();
        mListArea = new ArrayList<>();
        edtFullName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtMobile = findViewById(R.id.edt_contact_no);
        spin_cat = findViewById(R.id.spin_cat);
        spin_dis = findViewById(R.id.spin_dis);
        spin_city = findViewById(R.id.spin_city);
        spin_fav_dis = findViewById(R.id.spin_fav_dis);

        btnSignUp = findViewById(R.id.button_sign_up);
        txtLogin = findViewById(R.id.text_sign_in);

        rbProvider = findViewById(R.id.rbJobProvider);
        rbSeeker = findViewById(R.id.rbJobSeeker);
        if (NetworkUtils.isConnected(this)) {
            getList();
        }


        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                validator.validate();
                putSignUp();
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        rgJobUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i != -1) {
                    switch (i) {
                        case R.id.rbJobSeeker:
                            lytJobProvider.setVisibility(View.GONE);
                            break;
                        case R.id.rbJobProvider:
                            lytJobProvider.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        });




        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public void onValidationSucceeded() {
        if (NetworkUtils.isConnected(SignUpActivity.this)) {
            putSignUp();
        } else {
            showToast(getString(R.string.conne_msg1));
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
    private void getList() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_list");
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
                    JSONObject jobAppJson = mainJson.getJSONObject(Constant.ARRAY_NAME);

                    JSONArray cityArray = jobAppJson.getJSONArray("city_list");
                    if (cityArray.length() > 0) {
                        for (int i = 0; i < cityArray.length(); i++) {
                            JSONObject objJson = cityArray.getJSONObject(i);
                            ItemCity objItem = new ItemCity();
                            objItem.setCityId(objJson.getString(Constant.CITY_ID));
                            objItem.setCityName(objJson.getString(Constant.CITY_NAME));
                            mCityName.add(objJson.getString(Constant.CITY_NAME));
                            mListCity.add(objItem);
                        }
                    }
                    JSONArray areaArray = jobAppJson.getJSONArray("area_list");
                    if (areaArray.length() > 0) {
                        for (int i = 0; i < areaArray.length(); i++) {
                            JSONObject objJson = areaArray.getJSONObject(i);
                            ItemArea objItem1 = new ItemArea();
                            objItem1.setAreaId(objJson.getString("a_id"));
                            objItem1.setAreaName(objJson.getString("area_name"));
                            objItem1.setDisId(objJson.getString("c_id"));
//                            mAreaName.add(objJson.getString("area_name"));

                            mListArea.add(objItem1);
                        }
                    }

                    JSONArray categoryArray = jobAppJson.getJSONArray("cat_list");
                    if (categoryArray.length() > 0) {
                        for (int i = 0; i < categoryArray.length(); i++) {
                            JSONObject objJson = categoryArray.getJSONObject(i);
                            ItemCategory objItem = new ItemCategory();
                            objItem.setCategoryId(objJson.getInt(Constant.CATEGORY_CID));
                            objItem.setCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                            objItem.setCategoryImage(objJson.getString(Constant.CATEGORY_IMAGE));
                            mCategoryName.add(objJson.getString(Constant.CATEGORY_NAME));
                            mListCategory.add(objItem);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                spin_cat.setItems(mCategoryName, "Multi Selection With Filter", -1, new MultiSpinnerSearchListener() {
//                    @Override
//                    public void onItemsSelected(List<KeyPairBoolData> items) {
//
//                        for(int i=0; i<items.size(); i++) {
//                            if(items.get(i).isSelected()) {
//                                Log.i("TAG", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
//                            }
//                        }
//                    }
//                });



                final ArrayAdapter<String> cityAdapter1 = new ArrayAdapter<>(SignUpActivity.this,
                        android.R.layout.simple_list_item_1, mAreaName);

                cityAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                final ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(SignUpActivity.this,
                        android.R.layout.simple_list_item_1, mCityName);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_dis.setAdapter(new NothingSelectedSpinnerAdapter(cityAdapter, R.layout.sp_city, SignUpActivity.this));
                // Initialize adapter
                spin_city.setAdapter(new NothingSelectedSpinnerAdapter(cityAdapter1, R.layout.sp_city, SignUpActivity.this));


                // set item selected listener on min spinner
                spin_dis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // clear max list
                        mAreaName.clear();
                        // use for loop
                        for(int i=0 ;i<mListArea.size();i++)
                        {
                            // add values in max list
                            Log.d("goo",mListArea.get(i).getDisId());
                            Log.d("goo",String.valueOf(mListCity.get(position).getCityId()));
                            int val = Integer.valueOf(mListArea.get(i).getDisId());
                            if(Integer.valueOf(mListCity.get(position).getCityId()) == (val-1)){
                                mAreaName.add(mListArea.get(i).getAreaName());
                            }

                        }
                        // notify adapter
                        cityAdapter1.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spin_fav_dis.setSpinnerList(mCityName);
                spin_cat.setSpinnerList(mCategoryName);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
    }
    public void putSignUp() {
        strFullname = edtFullName.getText().toString();
        area = (String) spin_city.getSelectedItem();
        cit = (String) spin_dis.getSelectedItem();
        fcit =spin_fav_dis.getAdapterText();
                fcat = spin_cat.getAdapterText();
        strPassword = edtPassword.getText().toString();
        strMobi = edtMobile.getText().toString();




        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_register");
        jsObj.addProperty("name", strFullname);
        jsObj.addProperty("area", area);
        jsObj.addProperty("fav_city", fcit);
        jsObj.addProperty("fav_cat", fcat);
        jsObj.addProperty("city", cit);
        jsObj.addProperty("password", strPassword);
        jsObj.addProperty("phone", strMobi);
        jsObj.addProperty("user_type", "1");
//        if (strType.equals("1")) {
//            jsObj.addProperty("register_as", Constant.INDIVIDUAL);
//        } else {
//            jsObj.addProperty("register_as", rbIndividual.isChecked() ? Constant.INDIVIDUAL : Constant.COMPANY);
//        }
//        jsObj.addProperty("company_name", companyName);
//        jsObj.addProperty("company_email", companyEmail);
//        jsObj.addProperty("mobile_no", companyPhone);
//        jsObj.addProperty("company_address", companyAddress);
//        jsObj.addProperty("company_desc", companyDesc);
//        jsObj.addProperty("company_work_day", companyWorkDay);
//        jsObj.addProperty("company_work_time", companyWorkTime);
//        jsObj.addProperty("company_website", companyWebsite);

        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissProgressDialog();
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        strMessage = objJson.getString(Constant.MSG);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
            }

        });
    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            edtMobile.setText("");
            edtMobile.requestFocus();
            showToast(strMessage);
        } else {
            showToast(strMessage);
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        pDialog.dismiss();
    }
}
