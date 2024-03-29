package com.mirea.bykonyaia.mireaproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mirea.bykonyaia.mireaproject.databinding.ActivityMainBinding;
import com.mirea.bykonyaia.mireaproject.databinding.ActivityShowPointOfInterestInfoBinding;

public class ShowPointOfInterestInfoActivity extends AppCompatActivity {
    public static final String KEY_TITLE = "ShowPointOfInterestInfoActivity.Keys.Title";
    public static final String KEY_DESCRIPTION = "ShowPointOfInterestInfoActivity.Keys.Description";
    public static final String KEY_LATITUDE = "ShowPointOfInterestInfoActivity.Keys.Latitude";
    public static final String KEY_LONGITUDE = "ShowPointOfInterestInfoActivity.Keys.Longitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityShowPointOfInterestInfoBinding binding = ActivityShowPointOfInterestInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Bundle pointInfoBundle = getIntent().getExtras();
        if(pointInfoBundle == null) {
            binding.pointTitleView.setText("--");
            binding.pointLatitudeView.setText("--");
            binding.pointLongitudeView.setText("--");
            binding.pointDescriptionView.setText("There is not data....Go back ;)");
        } else {
            binding.pointTitleView.setText(pointInfoBundle.getString(KEY_TITLE, "No title..."));
            binding.pointDescriptionView.setText(pointInfoBundle.getString(KEY_DESCRIPTION, "No description..."));
            binding.pointLatitudeView.setText(String.format("%.4f", pointInfoBundle.getDouble(KEY_LATITUDE, 99999999f)));
            binding.pointLongitudeView.setText(String.format("%.4f", pointInfoBundle.getDouble(KEY_LONGITUDE, 99999999f)));
        }
    }
}