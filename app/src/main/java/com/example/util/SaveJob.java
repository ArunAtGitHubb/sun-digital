package com.example.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.jellysoft.sundigitalindia.AddJobActivity;
import com.jellysoft.sundigitalindia.MyApplication;
import com.jellysoft.sundigitalindia.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SaveJob {

    private ProgressDialog pDialog;
    private Context mContext;

    public SaveJob(Context context) {
        this.mContext = context;
        pDialog = new ProgressDialog(mContext);
    }

    public void userSave(final String jobId) {
//        Log.d("jobId",jobId);
//        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
//        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
//        jsObj.addProperty("method_name", "saved_job_add");
//        jsObj.addProperty("saved_job_id", jobId);
//        params.put("data", API.toBase64(jsObj.toString()));
//        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                super.onStart();
//                showProgressDialog();
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                Log.d("jobId", String.valueOf(headers));
//                dismissProgressDialog();
//
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Log.d("jobId","fail");
//                dismissProgressDialog();
//            }
//
//        });
    }

    private void showProgressDialog() {
        pDialog.setMessage(mContext.getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }
}
