package edu.skku.finalproject.studyhelper.utils;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

public class FontBase extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "THEjunggt110.otf"))
                .addBold(Typekit.createFromAsset(this, "THEjunggt130.otf"));
    }
}
