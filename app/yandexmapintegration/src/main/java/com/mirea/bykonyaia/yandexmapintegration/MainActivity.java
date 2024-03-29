package com.mirea.bykonyaia.yandexmapintegration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mirea.bykonyaia.yandexmapintegration.databinding.ActivityMainBinding;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

public class MainActivity extends AppCompatActivity implements UserLocationObjectListener {
    static final private int REQUEST_CODE_PERMISSION = 200;
    private boolean is_permissions_granted = false;
    private UserLocationLayer userLocationLayer = null;
    private ActivityMainBinding binding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MapKitFactory.initialize(this);
        binding.customMapView.getMap().move(
            new CameraPosition(new Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
            new Animation(Animation.Type.SMOOTH, 1),
            null
        );
    }
    @Override
    protected void onStart() {
        super.onStart();
        binding.customMapView.onStart();
        MapKitFactory.getInstance().onStart();
        MakePermissionsRequest();
    }
    @Override
    protected void onStop() {
        super.onStop();
        binding.customMapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {
        Toast.makeText(this, "HE_HE_HE_HE", Toast.LENGTH_SHORT).show();
        userLocationLayer.setAnchor(
                new PointF((float)(binding.customMapView.getWidth() * 0.5),
                        (float)(binding.customMapView.getHeight() * 0.5)),
                new PointF((float)(binding.customMapView.getWidth() * 0.5),
                        (float)(binding.customMapView.getHeight() * 0.83)));
        // При определении направления движения устанавливается следующая иконка
        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                this, android.R.drawable.arrow_up_float));

        // При получении координат местоположения устанавливается следующая иконка
        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();
        pinIcon.setIcon("pin",
            ImageProvider.fromResource(this, R.drawable.ic_launcher_foreground),
            new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(0.5f)
        );

        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);
    }
    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }
    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {

    }

    private void loadUserLocationLayer(){
        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();

        userLocationLayer = mapKit.createUserLocationLayer(binding.customMapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);
    }



    private void MakePermissionsRequest() {
        final boolean background_location_enabled = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        final boolean coarse_location_enabled = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        final boolean fine_location_enabled = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        final boolean internet_enabled = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        is_permissions_granted = background_location_enabled && coarse_location_enabled && fine_location_enabled && internet_enabled;
        if(is_permissions_granted) {
            loadUserLocationLayer();
        } else {
            ActivityCompat.requestPermissions(this,
                new	String[] { android.Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                },	REQUEST_CODE_PERMISSION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("", "onRequestPermissionsResult: " + String.valueOf(requestCode));
        if(requestCode == REQUEST_CODE_PERMISSION) {
            Log.i("HE_HE", permissions.toString());
            Log.i("HE_HE", grantResults.toString());
            is_permissions_granted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
            if(is_permissions_granted) {
                loadUserLocationLayer();
            }
        } else {
            finish();
        }
    }
}