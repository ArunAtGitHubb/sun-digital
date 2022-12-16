package com.example.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.jellysoft.sundigitalindia.FilterSearchResultActivity;
import com.jellysoft.sundigitalindia.MainActivity;
import com.jellysoft.sundigitalindia.R;
import com.example.adapter.NothingSelectedSpinnerAdapter;
import com.example.item.ItemCategory;
import com.example.item.ItemCity;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.NetworkUtils;
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

public class FilterSearchFragment extends DialogFragment {

    ProgressBar progressBar;
    CardView cardView;
    TextView spCategory, spCity, spCompanyName;
    EditText edtText;
    Button btnSubmit;
    TextView textClose;
    ArrayList<ItemCategory> mListCategory;
    ArrayList<ItemCity> mListCity;
    ArrayList<String> mCategoryName, mCityName, mCompanyName;
    RadioGroup radioGroup;
    RadioButton any,rdFullTime,rdHalfTime;
    Dialog dialog;
    String cityid = "", catid = "", jobType = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Translucent);
        setCancelable(false);
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_search_dialog, container, false);
        progressBar = rootView.findViewById(R.id.progressBar);
        spCategory = rootView.findViewById(R.id.spCategory);

        spCity = rootView.findViewById(R.id.spCity);
        cardView = rootView.findViewById(R.id.card_view);
        edtText = rootView.findViewById(R.id.edt_name);
        btnSubmit = rootView.findViewById(R.id.btn_submit);
        textClose = rootView.findViewById(R.id.txtClose);
        radioGroup = rootView.findViewById(R.id.radioGrp);
        any = rootView.findViewById(R.id.any);
        rdHalfTime = rootView.findViewById(R.id.rdHalfTime);
                rdFullTime= rootView.findViewById(R.id.rdFullTime);
        mListCategory = new ArrayList<>();
        mListCity = new ArrayList<>();
        mCategoryName = new ArrayList<>();
        mCityName = new ArrayList<>();
        mCompanyName = new ArrayList<>();

        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        btnSubmit.setOnClickListener(view -> {
            int radioSelected = radioGroup.getCheckedRadioButtonId();
            if (radioSelected != -1) {
                switch (radioSelected) {
                    case R.id.rdFullTime:
                        jobType = "Full Time";
                        break;
                    case R.id.rdHalfTime:
                        jobType = "Part Time";
                        break;
                    case R.id.any:
                        jobType = "";
                        break;
                }
            } else {
                jobType = "";
            }
            dismiss();
            Intent intent = new Intent(requireActivity(), FilterSearchResultActivity.class);
            intent.putExtra("categoryId", catid);
            intent.putExtra("cityId", cityid );
            intent.putExtra("jobType", jobType);
            startActivity(intent);

        });


        if (NetworkUtils.isConnected(getActivity())) {
            getList();
        }

        return rootView;
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
                cardView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                cardView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

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

                    JSONArray companyArray = jobAppJson.getJSONArray("company_list");
                    if (companyArray.length() > 0) {
                        for (int i = 0; i < companyArray.length(); i++) {
                            JSONObject objJson = companyArray.getJSONObject(i);
                            mCompanyName.add(objJson.getString("job_company_name"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                spCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Initialize dialog
                        dialog=new Dialog(getActivity());

                        // set custom dialog
                        dialog.setContentView(R.layout.dialog_searchable_spinner);

                        // set custom height and width
                        dialog.getWindow().setLayout(650,800);

                        // set transparent background
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        // show dialog
                        dialog.show();

                        // Initialize and assign variable
                        EditText editText=dialog.findViewById(R.id.edit_text);
                        ListView listView=dialog.findViewById(R.id.list_view);

                        // Initialize array adapter
                        final ArrayAdapter<String> adapter1 = new ArrayAdapter<>(requireActivity(),
                        android.R.layout.simple_list_item_1, mCategoryName);

                        // set adapter
                        listView.setAdapter(adapter1);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                adapter1.getFilter().filter(s);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // when item selected from list
                                // set selected item on textView
                                spCategory.setText(adapter1.getItem(position));
                                catid = String.valueOf(mListCategory.get(position).getCategoryId());
                                // Dismiss dialog
                                dialog.dismiss();
                            }
                        });
                    }
                });

                spCity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Initialize dialog
                        dialog=new Dialog(getActivity());

                        // set custom dialog
                        dialog.setContentView(R.layout.dialog_searchable_spinner);

                        // set custom height and width
                        dialog.getWindow().setLayout(650,800);

                        // set transparent background
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        // show dialog
                        dialog.show();

                        // Initialize and assign variable
                        EditText editText=dialog.findViewById(R.id.edit_text);
                        ListView listView=dialog.findViewById(R.id.list_view);

                        // Initialize array adapter
                        final ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                                android.R.layout.simple_list_item_1, mCityName);

                        // set adapter
                        listView.setAdapter(adapter);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                adapter.getFilter().filter(s);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // when item selected from list
                                // set selected item on textView
                                Log.d("grap",adapter.getItem(position));
                                spCity.setText(adapter.getItem(position));

                                        cityid = mListCity.get(position).getCityId();


                                // Dismiss dialog
                                dialog.dismiss();
                            }
                        });
                    }
                });

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
    }
}
