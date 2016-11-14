package com.nicksbbq.nicksbarbecue;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DisplayCouponActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private int position;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location myLocation;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_coupon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent i = getIntent();
        position = i.getIntExtra("position", -1);

        ImageView image = (ImageView) findViewById(R.id.couponImage);
        TextView couponDescription = (TextView) findViewById(R.id.descriptionTextView);
        TextView couponExpiration = (TextView) findViewById(R.id.expirationTextView);
        image.setImageBitmap(MainActivity.couponImages.get(position));
        couponDescription.setText(MainActivity.couponDescriptions.get(position));
        couponExpiration.setText("Expires " + MainActivity.couponExpirations.get(position));

        setTitle(MainActivity.couponDescriptions.get(position));

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

        mDatabase = FirebaseDatabase.getInstance().getReference();
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
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            myLocation = mLastLocation;
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_coupon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        } else if (id == R.id.locations) {
            Intent i = new Intent(getApplicationContext(), LocationsActivity.class);
            startActivity(i);
        } else if (id == R.id.menu) {
            Intent i = new Intent(getApplicationContext(), DisplayFoodCategory.class);
            startActivity(i);
        } else if (id == R.id.order) {
            Uri uri = Uri.parse("http://www.nicksribs.com/pg4.html");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        finish();
        return true;
    }

    public void redeemCoupon(View view) {
        mDatabase.child("UsedCoupons").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot usedCoupons) {
                boolean couponUsed = false;
                if(usedCoupons != null && usedCoupons.hasChildren()) {
                    for (DataSnapshot ds : usedCoupons.getChildren()) {
                        if (ds.child("couponID").getValue().equals(MainActivity.couponIDs.get(position))) {
                            couponUsed = true;
                        }
                    }
                }

                if (couponUsed) {
                    Toast.makeText(getApplicationContext(), "Coupon already redeemed.", Toast.LENGTH_LONG).show();
                } else {
                    Location burbank = new Location("Burbank");
                    burbank.setLatitude(41.747949);
                    burbank.setLongitude(-87.794007);
                    Location palosHeights = new Location("Palos Heights");
                    palosHeights.setLatitude(41.660979);
                    palosHeights.setLongitude(-87.796735);
                    Location tinleyPark = new Location("Tinley Park");
                    tinleyPark.setLatitude(41.588739);
                    tinleyPark.setLongitude(-87.785334);
                    Location romeoville = new Location("Romeoville");
                    romeoville.setLatitude(41.653091);
                    romeoville.setLongitude(-88.080102);
                    Location homerGlen = new Location("Homer Glen");
                    homerGlen.setLatitude(41.601891);
                    homerGlen.setLongitude(-87.930587);

                    if (myLocation.distanceTo(burbank) < 150.0 || myLocation.distanceTo(palosHeights) < 150.0 ||
                            myLocation.distanceTo(tinleyPark) < 150.0 || myLocation.distanceTo(romeoville) < 150.0 ||
                            myLocation.distanceTo(homerGlen) < 150.0) {
                        new AlertDialog.Builder(DisplayCouponActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Confirm Redeem Coupon")
                                .setMessage("Are you sure you want to use this coupon?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String key = mDatabase.push().getKey();
                                        mDatabase.child("UsedCoupons").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).child("couponID").setValue(MainActivity.couponIDs.get(position));
                                        Toast.makeText(getApplicationContext(), "Coupon Redeemed.  Thank you!", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Coupon can only be redeemed in store.", Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
