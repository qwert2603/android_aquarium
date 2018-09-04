package com.example.alex.aquarium;

import android.content.res.Resources;
import android.util.TypedValue;

final class Utils {
    static int dp2px(Resources resources, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }
}
