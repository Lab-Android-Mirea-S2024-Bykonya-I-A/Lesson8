package com.mirea.bykonyaia.mireaproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.LauncherActivity;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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

    public void OnUpdatePointsOfInterestButtonClicked(View v) {
        UpdatePointsOfInterest();
    }
    private void UpdatePointsOfInterest() {
        List<PointsOfInterestDto> points = new ArrayList<>();
        points.add(new PointsOfInterestDto(
                "MIREA title", "Mirea description",
                new GeoPoint(55.794259, 37.701448)
        ));
        points.add(new PointsOfInterestDto(
                "Shop Title", "Shop Description",
                new GeoPoint(55.795667, 37.700810)
        ));
        points.add(new PointsOfInterestDto(
                "Dorm Title", "Dorm Description",
                new GeoPoint(55.801121, 37.805680)
        ));
        list_view.SetPointsOfInterestModel(points);
        map_view.SetPointsOfInterestModel(points);
    }
}