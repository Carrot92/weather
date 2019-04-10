package com.example.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.weather.RemoteFetch.city;


public class WeatherFragment extends Fragment {

    ImageView weatherIcon;
    LinearLayout llForecast;
    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    RemoteFetch.ApiInterface api;
    String KEY = "b731a2032b2902716eaa083333f177f2";
    boolean checker;




    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = rootView.findViewById(R.id.city_field);
        updatedField = rootView.findViewById(R.id.updated_field);
        detailsField = rootView.findViewById(R.id.details_field);
        currentTemperatureField = rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = rootView.findViewById(R.id.weather_icon);
        llForecast = rootView.findViewById(R.id.llForecast);

        weatherIcon.setImageResource(R.drawable.img1);
        updatedField.setText(new CityPreference(Objects.requireNonNull(getActivity())).sPref.getString("LUpd", ""));
        cityField.setText(new CityPreference(getActivity()).sPref.getString("city", ""));
        currentTemperatureField.setText(new CityPreference(getActivity()).sPref.getString("temper", ""));
        detailsField.setText(new CityPreference(getActivity()).sPref.getString("details", ""));
        return rootView;


    }


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        api = RemoteFetch.getClient().create(RemoteFetch.ApiInterface.class);
        checker=true;
          updateWeatherData(new CityPreference(Objects.requireNonNull(getActivity())).getCity());

       }



    @Override
    public void onStop(){
        new CityPreference(Objects.requireNonNull(getActivity())).setActivity(String.valueOf(cityField.getText()),
                String.valueOf(updatedField.getText()),
                String.valueOf(currentTemperatureField.getText()),
                String.valueOf(detailsField.getText()));
        checker=false;
        super.onStop();


    }

    private void updateWeatherData(final String city) {

        RemoteFetch.city = city;
        new Thread(){
            public void run(){
                while (checker){
                    try {
                        renderWeather();
                        Thread.sleep(10000);
                    }catch (InterruptedException ex){break;}
                }
            }
        }.start();

    }

    private void renderWeather() {

            // get weather for today
            Call<WeatherDay> callToday = api.getToday(city, KEY);
            callToday.enqueue(new Callback<WeatherDay>() {
                @Override
                public void onResponse(@NonNull Call<WeatherDay> call, @NonNull Response<WeatherDay> response) {
                    WeatherDay data = response.body();


                    if (response.isSuccessful()) {
                        cityField.setText(Objects.requireNonNull(data).getCity());
                        currentTemperatureField.setText(String.format("%s%s", data.getTempWithDegree(), getString(R.string.degree_c)));
                        detailsField.setText(String.format("%s\n%s%s%s\n%s%s%s\n", data.getDesc(), getString(R.string.pressure), data.getPressure(), getString(R.string.pascal), getString(R.string.humidity), data.getHumidity(), getString(R.string.percent)));
                        DateFormat df = DateFormat.getDateTimeInstance();
                        String updatedOn = df.format(data.getDate());
                        updatedField.setText(String.format("Last update: %s", updatedOn));
                        Glide.with(getActivity()).load(data.getIconUrl()).into(weatherIcon);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<WeatherDay> call, @NonNull Throwable t) {

                }
            });

            Call<WeatherForecast> callForecast = api.getForecast(city, KEY);
            callForecast.enqueue(new Callback<WeatherForecast>() {
                @Override
                public void onResponse(@NonNull Call<WeatherForecast> call, @NonNull Response<WeatherForecast> response) {
                    WeatherForecast data = response.body();

                    if (response.isSuccessful()) {
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat formatDayOfWeek = new SimpleDateFormat("E");
                        LinearLayout.LayoutParams paramsTextView = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        LinearLayout.LayoutParams paramsImageView = new LinearLayout.LayoutParams(convertDPtoPX(40, getActivity()),
                                convertDPtoPX(40, getActivity()));

                        int marginRight = convertDPtoPX(35, getActivity());
                        LinearLayout.LayoutParams paramsLinearLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        paramsLinearLayout.setMargins(0, 0, marginRight, 0);

                        llForecast.removeAllViews();

                        for (WeatherDay day : Objects.requireNonNull(data).getItems()) {

                            if (day.getDateTime().getHourOfDay() == 16) {

                                // child view wrapper
                                LinearLayout childLayout = new LinearLayout(getActivity());
                                childLayout.setLayoutParams(paramsLinearLayout);
                                childLayout.setOrientation(LinearLayout.VERTICAL);

                                // show day of week
                                TextView tvDay = new TextView(getActivity());
                                String dayOfWeek = formatDayOfWeek.format(day.getDate().getTime());
                                tvDay.setText(dayOfWeek);
                                tvDay.setLayoutParams(paramsTextView);
                                childLayout.addView(tvDay);

                                // show image
                                ImageView ivIcon = new ImageView(getActivity());
                                ivIcon.setLayoutParams(paramsImageView);
                                Glide.with(getActivity()).load(day.getIconUrl()).into(ivIcon);
                                childLayout.addView(ivIcon);

                                // show temp
                                TextView tvTemp = new TextView(getActivity());
                                tvTemp.setText(day.getTempWithDegree());
                                tvTemp.setLayoutParams(paramsTextView);
                                childLayout.addView(tvTemp);

                                llForecast.addView(childLayout);
                            }

                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<WeatherForecast> call, @NonNull Throwable t) {

                }
            });
    }


    public int convertDPtoPX(int dp, Context ctx) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    public void changeCity(String city) {
        updateWeatherData(city);
    }

}