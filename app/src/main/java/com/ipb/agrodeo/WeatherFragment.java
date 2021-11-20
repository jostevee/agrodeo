package com.ipb.agrodeo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

// import com.ipb.agrodeo.databinding.ActivityMainBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherFragment##newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // Web URL of the JSON file
    AppLocationService appLocationService;
    LocationManager locationManager;
    double latitude, longitude;
    String mApiKey = "f06d6010911585e2f5e79ccfe54925d2";

    // UI and UX
    TextView city, country, time, temp, forecast, humidity, min_temp, max_temp, sunrises, sunsets;
    SwipeRefreshLayout swLayout;

    // String mCity = "London";
    // String mCountry = "United Kingdom";
    // ImageView search;
    // EditText etCity;
    // Button btnGPSShowLocation;
    // Button btnShowAddress;
    // TextView tvAddress;
    // private ActivityMainFragmentBinding binding;
    // private FragmentWeatherBinding bindings;
    // public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public WeatherFragment() {
        // Required empty public constructor
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        city = view.findViewById(R.id.city);
        country = view.findViewById(R.id.country);
        time = view.findViewById(R.id.time);
        temp = view.findViewById(R.id.temp);
        forecast = view.findViewById(R.id.forecast);
        humidity = view.findViewById(R.id.humidity);
        min_temp = view.findViewById(R.id.min_temp);
        max_temp = view.findViewById(R.id.max_temp);
        sunrises = view.findViewById(R.id.sunrises);
        sunsets = view.findViewById(R.id.sunsets);
        swLayout = view.findViewById(R.id.swipeWeatherLayout);

        appLocationService = new AppLocationService(view.getContext());
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext())
                    .setTitle("Perhatian")
                    .setMessage("Kami akan mengakses lokasi terkini anda untuk menjalankan fitur Cuaca. " +
                            "Untuk itu, kami memerlukan izin anda agar kami dapat mengakses lokasi anda")
                    .setPositiveButton("Diizinkan", (dialogInterface, i) -> {
                        //Prompt the user once explanation has been shown
                        mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION);

                        /*
                        ActivityCompat.requestPermissions(requireActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);

                         */
                    })
                    .setNegativeButton("Tidak diizinkan", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false);
            AlertDialog alertDialogPermission = alertDialog.create();
            alertDialogPermission.show();

            // Getting the view elements
            TextView textView = (TextView) alertDialogPermission.getWindow().findViewById(android.R.id.message);
            TextView alertTitle = (TextView) alertDialogPermission.getWindow().findViewById(R.id.alertTitle);
            Button button1 = (Button) alertDialogPermission.getWindow().findViewById(android.R.id.button1);
            Button button2 = (Button) alertDialogPermission.getWindow().findViewById(android.R.id.button2);
            final Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins);
            final Typeface typefaceSemiBold = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold);

            // Setting font
            textView.setTypeface(typeface);
            alertTitle.setTypeface(typefaceSemiBold);
            button1.setTypeface(typefaceSemiBold);
            button2.setTypeface(typefaceSemiBold);

            /*
            TextView tvMessage = (TextView) alertDialogPermission.findViewById(android.R.id.message);
            final Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins);
            // Typeface face = Typeface.createFromAsset(requireContext().getAssets(), "font/poppins.ttf");
            Objects.requireNonNull(tvMessage).setTypeface(typeface);
             */
        } else {
            getLocationAndWeather();
        }

        // Mengeset properti warna yang berputar pada SwipeRefreshLayout
        // swLayout.setColorSchemeResources(getResources().getColor(R.attr.colorAccent), R.attr.colorPrimary);

        // Mengeset listener yang akan dijalankan saat layar di refresh/swipe
        swLayout.setOnRefreshListener(() -> {
            getLocationAndWeather();

            // Handler 5 secs
            new Handler().postDelayed(() -> swLayout.setRefreshing(false), 5000);
        });


        // CLICK ON SEARCH BUTTON :
        /*
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mCity = etCity.getText().toString();

                new weatherTask().execute();
            }
        });
         */
    }

    private void getLocationAndWeather() {
        Location gpsLocation = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            latitude = gpsLocation.getLatitude();
            longitude = gpsLocation.getLongitude();

            // Execute weather tasks
            new weatherTask().execute();

                /*
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    Log.e("addresses", String.valueOf(addresses));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                 */
        }
    }

    private final ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if(result) {
                    getLocationAndWeather();
                    Log.e("WEATHER", "onActivityResult: PERMISSION GRANTED");
                } else {
                    Log.e("WEATHER", "onActivityResult: PERMISSION DENIED");
                }
            });

    private class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=" + mApiKey);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
                JSONObject sys = jsonObj.getJSONObject("sys");

                // CALL VALUE IN API :
                String city_name = jsonObj.getString("name");
                String countryname = sys.getString("country");
                long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Last Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temperature = main.getString("temp");
                String cast = weather.getString("description");
                String humi_dity = main.getString("humidity");
                String temp_min = main.getString("temp_min");
                String temp_max = main.getString("temp_max");
                long rise = sys.getLong("sunrise");
                String sunrise = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(rise * 1000));
                long set = sys.getLong("sunset");
                String sunset = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(set * 1000));

                // SET ALL VALUES IN TEXTBOX :
                city.setText(city_name);
                country.setText(countryname);
                time.setText(updatedAtText);
                temp.setText(temperature + "°C");
                forecast.setText(cast);
                humidity.setText(humi_dity);
                min_temp.setText(temp_min);
                max_temp.setText(temp_max);
                sunrises.setText(sunrise);
                sunsets.setText(sunset);
            } catch (Exception e) {
                Toast.makeText(requireView().getContext(), "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                requireContext());
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        requireActivity().startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
     */

    /*
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            if (message.what == 1) {
                Bundle bundle = message.getData();
                locationAddress = bundle.getString("address");
            } else {
                locationAddress = null;
            }
            tvAddress.setText(locationAddress);
        }
    }
     */
}

/*
class weatherTask extends AsyncTask<String, Void, String> {
    // Web URL of the JSON file
    public String mApiKey = "f06d6010911585e2f5e79ccfe54925d2";
    public String mCity = "London";
    public String mCountry = "United Kingdom";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(String... args) {
        return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + mCity + "&units=metric&appid=" + mApiKey);
    }
    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONObject main = jsonObj.getJSONObject("main");
            JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
            JSONObject sys = jsonObj.getJSONObject("sys");

            // CALL VALUE IN API :
            String city_name = jsonObj.getString("name");
            String countryname = sys.getString("country");
            Long updatedAt = jsonObj.getLong("dt");
            String updatedAtText = "Last Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
            String temperature = main.getString("temp");
            String cast = weather.getString("description");
            String humi_dity = main.getString("humidity");
            String temp_min = main.getString("temp_min");
            String temp_max = main.getString("temp_max");
            Long rise = sys.getLong("sunrise");
            String sunrise = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(rise * 1000));
            Long set = sys.getLong("sunset");
            String sunset = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(set * 1000));
            
            // SET ALL VALUES IN TEXTBOX :
            city.setText(city_name);
            country.setText(countryname);
            time.setText(updatedAtText);
            temp.setText(temperature + "°C");
            forecast.setText(cast);
            humidity.setText(humi_dity);
            min_temp.setText(temp_min);
            max_temp.setText(temp_max);
            sunrises.setText(sunrise);
            sunsets.setText(sunset);
        } catch (Exception e) {
            Toast.makeText(this.getClass(), "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
 */