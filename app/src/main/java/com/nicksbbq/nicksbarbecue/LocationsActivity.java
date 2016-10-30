package com.nicksbbq.nicksbarbecue;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LatLng myLocation;

    private boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firstTime = true;

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION }, PERMISSION_ACCESS_COARSE_LOCATION);
        }

        createLocationRequest();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            setUpLocations();
        }

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        setUpLocations();
    }

    private void setUpLocations() {
        mMap.clear();

        LatLng burbank = new LatLng(41.747949, -87.794007);
        LatLng palosHeights = new LatLng(41.660979, -87.796735);
        LatLng tinleyPark = new LatLng(41.588739, -87.785334);
        LatLng romeoville = new LatLng(41.653091, -88.080102);
        LatLng homerGlen = new LatLng(41.601891, -87.930587);

        Marker burbankMarker;
        Marker palosMarker;
        Marker tinleyMarker;
        Marker romeoMarker;
        Marker homerMarker;

        burbankMarker = mMap.addMarker(new MarkerOptions().position(burbank).title("Nick's BBQ - Burbank").icon(BitmapDescriptorFactory.fromResource(R.drawable.minifatboy)));
        palosMarker = mMap.addMarker(new MarkerOptions().position(palosHeights).title("Nick's BBQ - Palos Heights").icon(BitmapDescriptorFactory.fromResource(R.drawable.minifatboy)));
        tinleyMarker = mMap.addMarker(new MarkerOptions().position(tinleyPark).title("Nick's BBQ - Tinley Park").icon(BitmapDescriptorFactory.fromResource(R.drawable.minifatboy)));
        romeoMarker = mMap.addMarker(new MarkerOptions().position(romeoville).title("Nick's BBQ - Romeoville").icon(BitmapDescriptorFactory.fromResource(R.drawable.minifatboy)));
        homerMarker = mMap.addMarker(new MarkerOptions().position(homerGlen).title("Nick's BBQ - Homer Glen").icon(BitmapDescriptorFactory.fromResource(R.drawable.minifatboy)));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(burbankMarker.getPosition());
        builder.include(palosMarker.getPosition());
        builder.include(tinleyMarker.getPosition());
        builder.include(romeoMarker.getPosition());
        builder.include(homerMarker.getPosition());
        builder.include(mMap.addMarker(new MarkerOptions().position(myLocation).title("Your Location").icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))).getPosition());
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen

        if(firstTime) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
            firstTime = false;
        }

        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        TextView phoneText = (TextView) findViewById(R.id.phoneText);

        if(marker.getTitle().equals("Nick's BBQ - Burbank")) {
            phoneText.setText("Nick's BBQ - Burbank\nPhone Number: 708-233-7427\nAddress: 6945 W. 79th St.");
        } else if(marker.getTitle().equals("Nick's BBQ - Palos Heights")) {
            phoneText.setText("Nick's BBQ - Palos Heights\nPhone Number: 708-923-7427\nAddress: 12658 S. Harlem Ave.");
        } else if(marker.getTitle().equals("Nick's BBQ - Tinley Park")) {
            phoneText.setText("Nick's BBQ - Tinley Park\nPhone Number: 708-444-7427\nAddress: 16638 S. Oak Park");
        } else if(marker.getTitle().equals("Nick's BBQ - Romeoville")) {
            phoneText.setText("Nick's BBQ - Romeoville\nPhone Number: 815-372-0578\nAddress: 649 N. Independence Blvd.");
        } else if(marker.getTitle().equals("Nick's BBQ - Homer Glen")) {
            phoneText.setText("Nick's BBQ - Homer Glen\nPhone Number: 708-645-7427\nAddress: 15800 S. Bell Rd.");
        }

        return false;
    }

    public void backToMainActivity(View view) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
}
