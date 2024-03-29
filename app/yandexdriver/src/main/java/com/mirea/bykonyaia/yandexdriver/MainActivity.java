package com.mirea.bykonyaia.yandexdriver;


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

import com.mirea.bykonyaia.yandexdriver.databinding.ActivityMainBinding;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingRouterType;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UserLocationObjectListener, DrivingSession.DrivingRouteListener {
    static final private int REQUEST_CODE_PERMISSION = 200;
    private boolean is_permissions_granted = false;
    private UserLocationLayer userLocationLayer = null;
    private ActivityMainBinding binding = null;

    private final static Point MIREA_LOCATION = new Point(55.794259, 37.701448);
    private static final int[] colors = {0xFFFF0000, 0xFF00FF00, 0x00FFBBBB, 0xFF0000FF};
    private MapObjectCollection mapObjects = null;
    private DrivingSession drivingSession = null;
    private DrivingRouter drivingRouter = null;
    private final float ZOOM_BOUNDARY = 16.4f;
    private MapObjectTapListener listener = null;
    private PlacemarkMapObject marker = null;
    private MapKit mapKit = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MapKitFactory.initialize(this);
        DirectionsFactory.initialize(this);
        binding.customMapView.getMap().setRotateGesturesEnabled(true);
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.ONLINE);
        mapObjects = binding.customMapView.getMap().getMapObjects().addCollection();

        listener = new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                Toast.makeText(getApplication(),"MIREA", Toast.LENGTH_SHORT).show();
                return false;
            }
        };
        marker = mapObjects.addPlacemark(MIREA_LOCATION);
        marker.addTapListener(listener);
        marker.setVisible(true);
        marker.setText("MIREA");
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
        userLocationLayer.setAnchor(
            new PointF((float)(binding.customMapView.getWidth() * 0.5),
                    (float)(binding.customMapView.getHeight() * 0.5)),
            new PointF((float)(binding.customMapView.getWidth() * 0.5),
                    (float)(binding.customMapView.getHeight() * 0.83)));

        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                this, android.R.drawable.arrow_up_float));
        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();
        pinIcon.setIcon("pin",
                ImageProvider.fromResource(this, R.drawable.ic_launcher_foreground),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                    .setRotationType(RotationType.ROTATE)
                    .setZIndex(1f)
                    .setScale(0.5f)
        );
        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);

        // ========================================================

        mapKit = MapKitFactory.getInstance();
        mapKit.createLocationManager().requestSingleUpdate(new LocationListener() {
            @Override
            public void onLocationUpdated(@NonNull Location location) {
//                Toast.makeText(MainActivity.this, "HE_G_E_GE_GGE", Toast.LENGTH_SHORT).show();
//                Log.d("TagCheck", "LocationUpdated " + location.getPosition().getLongitude());
//                Log.d("TagCheck", "LocationUpdated " + location.getPosition().getLatitude());
                binding.customMapView.getMap().move(
                        new CameraPosition(location.getPosition(), 14.0f, 0.0f, 0.0f),
                        new Animation(Animation.Type.SMOOTH, 1),
                    null);
                // ================================================================
                MakeRouteRequest(location.getPosition(), MIREA_LOCATION, "MIREA");
            }
            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {
                Toast.makeText(MainActivity.this, "HE_G_E_GE_GGE", Toast.LENGTH_SHORT).show();
                Log.d("TagCheck", String.format("Location status: %s", locationStatus.toString()));
            }
        });
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
            is_permissions_granted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
            if(is_permissions_granted) {
                loadUserLocationLayer();
            }
        } else {
            finish();
        }
    }

    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> list) {
        for (int i = 0; i < list.size(); i++) {
            mapObjects.addPolyline(list.get(i).getGeometry()).setStrokeColor(colors[i]);
        }
    }
    @Override
    public void onDrivingRoutesError(@NonNull Error error) {
        String errorMessage = "Unknown error...";
        if (error instanceof RemoteError) {
            errorMessage = "Remote server error";
        } else if (error instanceof NetworkError) {
            errorMessage = "Network error";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void MakeRouteRequest(Point from, Point to, String name) {
        DrivingOptions drivingOptions = new DrivingOptions();
        VehicleOptions vehicleOptions = new VehicleOptions();
        drivingOptions.setRoutesCount(4);

        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        requestPoints.add(new RequestPoint(from,
                RequestPointType.WAYPOINT,
                null, null));
        requestPoints.add(new RequestPoint(to,
                RequestPointType.WAYPOINT,
                null, null));

        drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions,
            vehicleOptions, MainActivity.this);
    }
}