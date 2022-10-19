package com.example.util;

import com.jellysoft.sundigitalindia.MyApplication;

public class UserUtils {
    public static String getUserId() {
        return MyApplication.getInstance().getIsLogin() ? MyApplication.getInstance().getUserId() : "";
    }
}
