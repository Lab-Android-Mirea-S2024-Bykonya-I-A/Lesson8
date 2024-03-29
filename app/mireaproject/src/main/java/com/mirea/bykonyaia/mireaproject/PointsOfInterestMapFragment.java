package com.mirea.bykonyaia.mireaproject;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mirea.bykonyaia.mireaproject.databinding.FragmentPointsOfInterestListBinding;
import com.mirea.bykonyaia.mireaproject.databinding.FragmentPointsOfInterestMapBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PointsOfInterestMapFragment extends Fragment {
    private FragmentPointsOfInterestMapBinding binding = null;
    public OnSelectPointOfInterestListener listener = null;
    private MyLocationNewOverlay overlay = null;
    public PointsOfInterestMapFragment() {}


    public static PointsOfInterestMapFragment newInstance(String param1, String param2) {
        PointsOfInterestMapFragment fragment = new PointsOfInterestMapFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPointsOfInterestMapBinding.inflate(inflater, container, false);

        // ==========================================
        binding.pointsOfInterestMap.setZoomRounding(true);
        binding.pointsOfInterestMap.setMultiTouchControls(true);
        Configuration.getInstance().load(getActivity().getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()));

        overlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity().getApplicationContext()), binding.pointsOfInterestMap);
        overlay.enableMyLocation();
        binding.pointsOfInterestMap.getOverlays().add(overlay);

        CompassOverlay compassOverlay = new CompassOverlay(getActivity().getApplicationContext(),
            new InternalCompassOrientationProvider(getActivity().getApplicationContext()),
            binding.pointsOfInterestMap);
        compassOverlay.enableCompass();
        binding.pointsOfInterestMap.getOverlays().add(compassOverlay);

        final Context context = getActivity().getApplicationContext();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(binding.pointsOfInterestMap);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        binding.pointsOfInterestMap.getOverlays().add(scaleBarOverlay);
        // ==========================================
        return binding.getRoot();
    }
    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(getActivity().getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()));
        binding.pointsOfInterestMap.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        Configuration.getInstance().save(getActivity().getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()));
        binding.pointsOfInterestMap.onPause();
    }

    public void MoveMapToLocation(GeoPoint location) {
        //new GeoPoint(55.794229, 37.700772)
        IMapController mapController = binding.pointsOfInterestMap.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(location);
    }
    public void SetPointsOfInterestModel(List<PointsOfInterestDto> points) {
        List<Marker> point_of_interest_markers = new ArrayList<>();
        for(final PointsOfInterestDto point: points) {
            Marker marker = new Marker(binding.pointsOfInterestMap);
            marker.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    if(listener != null) {
                        listener.onPointOfInterestSelect(point);
                    }
                    return true;
                }
            });
            marker.setPosition(point.location);
            marker.setTitle("Title");
            point_of_interest_markers.add(marker);
        }

        binding.pointsOfInterestMap.getOverlays().clear();
        binding.pointsOfInterestMap.getOverlays().addAll(point_of_interest_markers);
    }
}
