package com.example.airquality;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTv, AirQualityTv, ConditionTv;
    private TextInputEditText cityEdt;
    private ImageView backIv, searchIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBloading);
        cityNameTv = findViewById(R.id.idTVCityName);
        AirQualityTv = findViewById(R.id.idTVAirQuality);
        ConditionTv = findViewById(R.id.idTVCondition);
        cityEdt = findViewById(R.id.idEDtCity);
        backIv = findViewById(R.id.idIVBlack);
        searchIv = findViewById(R.id.idTvSearch);
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEdt.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void getlatlong(View view){

        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList;
        double doubleLat = 0.00, doublelong = 0.00;
        String phno;

        try {
            addressList = geocoder.getFromLocationName(cityNameTv.getText().toString(), 1);

            if(addressList != null){
                doubleLat = addressList.get(0).getLatitude();
                doublelong = addressList.get(0).getLongitude();
                phno = addressList.get(0).getPhone();


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = "http://api.openweathermap.org/data/2.5/air_pollution?lat="+doubleLat+"&lon="+doublelong+"&appid=42ac6b9c27050792307d5c1e42186c77";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);

                try {
                    String aqi = response.getJSONObject("main").getString("aqi");
                    AirQualityTv.setText(aqi);
                    String conditon = "";
                    if(aqi.equals("1"))
                        conditon="Good";
                    else if(aqi.equals("2"))
                        conditon="Fair";
                    else if(aqi.equals("3"))
                        conditon="Moderate";
                    else if(aqi.equals("4"))
                        conditon="Poor";
                    else
                        conditon="Very Poor";
                    ConditionTv.setText(conditon);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter correct latitude and longitude", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);



    }
}