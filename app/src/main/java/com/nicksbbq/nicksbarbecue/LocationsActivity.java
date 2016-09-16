package com.nicksbbq.nicksbarbecue;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        mMap.clear();

        LatLng burbank = new LatLng(41.747949, -87.794007);

        LatLng palosHeights = new LatLng(41.660979, -87.796735);

        LatLng tinleyPark = new LatLng(41.588739, -87.785334);

        LatLng romeoville = new LatLng(41.653091, -88.080102);

        LatLng homerGlen = new LatLng(41.601891, -87.930587);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mMap.addMarker(new MarkerOptions().position(burbank).title("Nick's BBQ - Burbank\n708-233-7427")).getPosition());
        builder.include(mMap.addMarker(new MarkerOptions().position(palosHeights).title("Nick's BBQ - Palos Heights")).getPosition());
        builder.include(mMap.addMarker(new MarkerOptions().position(tinleyPark).title("Nick's BBQ - Tinley Park")).getPosition());
        builder.include(mMap.addMarker(new MarkerOptions().position(romeoville).title("Nick's BBQ - Romeoville")).getPosition());
        builder.include(mMap.addMarker(new MarkerOptions().position(homerGlen).title("Nick's BBQ - Homer Glen")).getPosition());
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
    }
}
