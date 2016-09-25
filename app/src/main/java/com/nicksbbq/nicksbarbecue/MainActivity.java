package com.nicksbbq.nicksbarbecue;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static ArrayList<String> couponIDs;
    static ArrayList<Bitmap> couponImages;
    static ArrayList<String> couponDescriptions;
    static ArrayList<String> couponExpirations;
    private int position, numberOfCoupons;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }
            }
        };

        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
/*
        String key = mDatabase.push().getKey();
        mDatabase.child("Coupons").child(key).child("couponDescription").setValue("$1.00 off a Italian Beef Sandwich");
        mDatabase.child("Coupons").child(key).child("couponExpiration").setValue("9/29/2016");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.beef);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
        mDatabase.child("Coupons").child(key).child("couponImage").setValue(base64Image);
*/
        couponIDs = new ArrayList<String>();
        couponImages = new ArrayList<Bitmap>();
        couponDescriptions = new ArrayList<String>();
        couponExpirations = new ArrayList<String>();

        createCoupons();

    }

    private void createCoupons() {
        couponIDs.clear();
        couponImages.clear();
        couponDescriptions.clear();
        couponExpirations.clear();
        numberOfCoupons = 0;
        final GridLayout gridLayout = (GridLayout) findViewById(R.id.mainGrid);

        final ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        mDatabase.child("Coupons").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot allCoupons) {
                mDatabase.child("UsedCoupons").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot usedCoupons) {
                        if(usedCoupons != null && !usedCoupons.hasChildren()) {
                            if(allCoupons != null && allCoupons.hasChildren()) {
                                for (DataSnapshot ds: allCoupons.getChildren()) {
                                    Coupon coupon = ds.getValue(Coupon.class);
                                    String exp = coupon.couponExpiration;
                                    Date expDate = null;
                                    try {
                                        expDate = new SimpleDateFormat("MM/dd/yyyy").parse(exp);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if(expDate.after(Calendar.getInstance().getTime())) {
                                        numberOfCoupons++;
                                        couponIDs.add(ds.getKey());
                                        couponDescriptions.add(coupon.couponDescription);
                                        couponExpirations.add(exp);

                                        String base64Image = coupon.couponImage;
                                        byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
                                        Bitmap image = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                                        couponImages.add(image);
                                    }
                                }
                            }
                        } else {
                            if(allCoupons != null && allCoupons.hasChildren()) {
                                for(DataSnapshot ds: allCoupons.getChildren()) {
                                    boolean shouldDisplayCoupon = true;
                                    for(DataSnapshot ds2: usedCoupons.getChildren()) {
                                        if (ds.getKey().equals(ds2.child("couponID").getValue())) {
                                            shouldDisplayCoupon = false;
                                        }
                                    }

                                    if(shouldDisplayCoupon) {
                                        Coupon coupon = ds.getValue(Coupon.class);
                                        String exp = coupon.couponExpiration;
                                        Date expDate = null;
                                        try {
                                            expDate = new SimpleDateFormat("MM/dd/yyyy").parse(exp);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        if (expDate.after(Calendar.getInstance().getTime())) {
                                            numberOfCoupons++;
                                            couponIDs.add(ds.getKey());
                                            couponDescriptions.add(coupon.couponDescription);
                                            couponExpirations.add(exp);

                                            String base64Image = coupon.couponImage;
                                            byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
                                            Bitmap image = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                                            couponImages.add(image);
                                        }
                                    }
                                }
                            }
                        }

                        spinner.setVisibility(View.GONE);
                        if(numberOfCoupons == 0) {
                            Toast.makeText(getApplicationContext(), "No Coupons Found", Toast.LENGTH_LONG).show();
                        }

                        for(position = 0; position < numberOfCoupons; position++) {
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            GridLayout.LayoutParams relativeLP = new GridLayout.LayoutParams();
                            relativeLP.width = metrics.widthPixels/2-30;
                            relativeLP.height = GridLayout.LayoutParams.WRAP_CONTENT; //450

                            final RelativeLayout relativeLayout = new RelativeLayout(MainActivity.this);
                            relativeLayout.setId(position);
                            relativeLayout.setGravity(Gravity.FILL);
                            //DisplayMetrics metrics = new DisplayMetrics();
                            //getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            //relativeLayout.setMinimumWidth(metrics.widthPixels/2-20);
                            Resources r = getResources();
                            float pxLeftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
                            relativeLP.setMargins(Math.round(pxLeftMargin), Math.round(pxLeftMargin), Math.round(pxLeftMargin), Math.round(pxLeftMargin));
                            relativeLayout.setBackgroundColor(Color.WHITE);
                            relativeLayout.setLayoutParams(relativeLP);
                            relativeLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getApplicationContext(), DisplayCouponActivity.class);
                                    intent.putExtra("position", relativeLayout.getId());
                                    startActivity(intent);
                                    finish();
                                }
                            });

                            int dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                            RelativeLayout.LayoutParams imageLP = new RelativeLayout.LayoutParams(dim, dim);
                            ImageView image = new ImageView(MainActivity.this);
                            image.setImageBitmap(couponImages.get(position));
                            image.setId(View.generateViewId());
                            imageLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            relativeLayout.addView(image, imageLP);

                            RelativeLayout.LayoutParams descriptionLP = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            TextView description = new TextView(MainActivity.this);
                            description.setText(couponDescriptions.get(position));
                            description.setTextColor(Color.RED);
                            description.setId(View.generateViewId());
                            descriptionLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            descriptionLP.addRule(RelativeLayout.BELOW, image.getId());
                            relativeLayout.addView(description, descriptionLP);

                            RelativeLayout.LayoutParams expirationLP = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            TextView expiration = new TextView(MainActivity.this);
                            expiration.setText("Expires "+ couponExpirations.get(position));
                            expiration.setTextColor(Color.BLACK);
                            expiration.setId(View.generateViewId());
                            expirationLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            expirationLP.addRule(RelativeLayout.BELOW, description.getId());
                            relativeLayout.addView(expiration, expirationLP);

                            gridLayout.addView(relativeLayout, position);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                }
        });
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
        getMenuInflater().inflate(R.menu.main, menu);
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        finish();
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}

