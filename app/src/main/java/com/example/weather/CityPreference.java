package com.example.weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

@SuppressLint("Registered")
public class CityPreference extends AppCompatActivity {

    SharedPreferences sPref;

    public CityPreference(Activity activity){
        sPref=activity.getPreferences(Activity.MODE_PRIVATE);

    }

    String getCity(){
        return sPref.getString("city","Moscow");
    }

    void setCity(String city){

        sPref.edit().putString("city", city).apply();

    }

    void setActivity(String city, String LUpd, String temper, String details){
        sPref.edit().putString("city", city).apply();
        sPref.edit().putString("LUpd", LUpd).apply();
        sPref.edit().putString("temper", temper).apply();
        sPref.edit().putString("details", details).apply();
    }


}
