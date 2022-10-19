package com.example.util;

import android.content.Context;
import android.os.Bundle;


import com.ixidev.gdpr.GDPRChecker;

public class PopUpAds {
    public static void showInterstitialAds(Context context, final int adapterPosition, final RvOnClickListener clickListener) {

            clickListener.onItemClick(adapterPosition);

    }
}
