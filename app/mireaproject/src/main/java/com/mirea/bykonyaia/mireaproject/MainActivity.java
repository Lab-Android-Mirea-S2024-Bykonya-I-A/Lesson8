package com.mirea.bykonyaia.mireaproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.LauncherActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mirea.bykonyaia.mireaproject.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private PointsOfInterestListFragment list_view = null;
    private PointsOfInterestMapFragment map_view = null;
    private ActivityMainBinding binding = null;

    private static final int REQUEST_CODE_PERMISSION = 200;
    private boolean is_permissions_granted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MakePermissionsRequest();
    }
    @Override
    protected void onStart() {
        super.onStart();
        list_view = (PointsOfInterestListFragment)getSupportFragmentManager().findFragmentById(R.id.points_of_interest_list);
        map_view = (PointsOfInterestMapFragment) getSupportFragmentManager().findFragmentById(R.id.points_of_interest_map);

        list_view.listener = new OnSelectPointOfInterestListener() {
            @Override
            public void onPointOfInterestSelect(PointsOfInterestDto point) {
                map_view.MoveMapToLocation(point.location);
            }
        };
        map_view.listener = new OnSelectPointOfInterestListener() {
            @Override
            public void onPointOfInterestSelect(PointsOfInterestDto point) {
                Intent showPointInfoIntent = new Intent(getApplicationContext(), ShowPointOfInterestInfoActivity.class);
                showPointInfoIntent.putExtra(ShowPointOfInterestInfoActivity.KEY_TITLE, point.title);
                showPointInfoIntent.putExtra(ShowPointOfInterestInfoActivity.KEY_DESCRIPTION, point.description);
                showPointInfoIntent.putExtra(ShowPointOfInterestInfoActivity.KEY_LATITUDE, point.location.getLatitude());
                showPointInfoIntent.putExtra(ShowPointOfInterestInfoActivity.KEY_LONGITUDE, point.location.getLongitude());
                startActivity(showPointInfoIntent);
            }
        };

        UpdatePointsOfInterest();
    }



    private void MakePermissionsRequest() {
        is_permissions_granted =
                PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) &&
                        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE) &&
                        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) &&
                        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) &&
                        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(!is_permissions_granted) {
            ActivityCompat.requestPermissions(this,
                    new	String[] { android.Manifest.permission.INTERNET,
                            android.Manifest.permission.ACCESS_NETWORK_STATE,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },	REQUEST_CODE_PERMISSION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("", "onRequestPermissionsResult: " + String.valueOf(requestCode));
        if(requestCode == REQUEST_CODE_PERMISSION) {
            is_permissions_granted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
        } else {
            finish();
        }
    }
    public void OnUpdatePointsOfInterestButtonClicked(View v) {
        UpdatePointsOfInterest();
    }
    private void UpdatePointsOfInterest() {
        if(is_permissions_granted)
            new RequestPointsOfInterestList().execute();
    }

    private class BaseHttpRequestTask extends AsyncTask<Void, Void, String> {
        private final String address;
        private final String method;
        public BaseHttpRequestTask(String address, String method) {
            this.address = address;
            this.method = method;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return MakeRequest();
            } catch (IOException | RuntimeException e) {
                Log.i("HE_HE", e.toString());
                return null;
            }
        }
        private String MakeRequest() throws IOException, RuntimeException {
            final URL url = new URL(address);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod(method);
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new RuntimeException("Invalid return code");

            Log.i("HE_HE", "MAKE_READ");
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (int read = 0; (read = inputStream.read()) != -1;) {
                bos.write(read);
            }
            final String result = bos.toString();
            connection.disconnect();
            bos.close();
            return result;
        }
    }
    private class RequestPointsOfInterestList extends BaseHttpRequestTask {
        public RequestPointsOfInterestList() {
            super("http://178.208.86.244:8000/points.json", "GET");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result == null)
                return;

            Log.i("HE_HE", result);
            try {
                final List<PointsOfInterestDto> points = new ArrayList<>();
                final JSONArray pointsJson = new JSONArray(result);
                for(int index = 0; index < pointsJson.length(); ++index) {
                    try {
                        final JSONObject pointJson = pointsJson.getJSONObject(index);
                        final JSONObject pointJsonLocation = pointJson.getJSONObject("location");
                        points.add(new PointsOfInterestDto(
                            pointJson.getString("title"),
                            pointJson.getString("description"),
                            new GeoPoint(
                                pointJsonLocation.getDouble("latitude"),
                                pointJsonLocation.getDouble("longitude")
                            )
                        ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                list_view.SetPointsOfInterestModel(points);
                map_view.SetPointsOfInterestModel(points);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}