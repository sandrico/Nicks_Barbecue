package com.nicksbbq.nicksbarbecue;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static ArrayList<String> couponIDs;
    static ArrayList<Bitmap> couponImages;
    static ArrayList<String> couponDescriptions;
    static ArrayList<String> couponExpirations;
    private int position, numberOfCoupons;

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

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if(e == null) {
                    Log.i("Logged In", "Yes");
                } else {
                    Log.i("Logged In", "No");
                }
            }
        });

/*        ParseObject object = new ParseObject("Coupons");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ribs);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        ParseFile file = new ParseFile("image.png", bytes);
        object.put("couponImage", file);
        object.put("couponDescription", "$1.25 off Full\nSlab Rib Dinner");
        object.put("couponExpiration", "8/29/16");

        ParseACL parseACL = new ParseACL();
        parseACL.setPublicWriteAccess(true);
        parseACL.setPublicReadAccess(true);
        object.setACL(parseACL);

        object.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {

                } else {

                }
            }
        });
*/
        createCoupons();
    }

    private void createCoupons() {
        couponIDs = new ArrayList<String>();
        couponImages = new ArrayList<Bitmap>();
        couponDescriptions = new ArrayList<String>();
        couponExpirations = new ArrayList<String>();
        numberOfCoupons = 0;
        GridLayout gridLayout = (GridLayout) findViewById(R.id.mainGrid);

        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Coupons");
            List<ParseObject> objects = query.find();

            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UsedCoupons");
            query2.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());
            List<ParseObject> objects2 = query2.find();

            if(objects2.size() == 0) {
                if (objects.size() > 0) {
                    for (ParseObject object : objects) {
                        String exp = object.getString("couponExpiration");
                        Date expDate = new SimpleDateFormat("MM/dd/yyyy").parse(exp);
                        if(expDate.after(Calendar.getInstance().getTime())) {
                            numberOfCoupons++;
                            couponIDs.add(object.getObjectId());
                            couponDescriptions.add(object.getString("couponDescription"));
                            couponExpirations.add(exp);
                            ParseFile file = (ParseFile) object.get("couponImage");
                            byte[] bytes = file.getData();
                            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            couponImages.add(image);
                        }
                    }
                }
            } else {
                if(objects.size() > 0) {
                    for(ParseObject object : objects) {
                        for(ParseObject object2 : objects2) {
                            if (!object.getObjectId().equals(object2.getString("couponID"))) {
                                String exp = object.getString("couponExpiration");
                                Date expDate = new SimpleDateFormat("MM/dd/yyyy").parse(exp);
                                if(expDate.after(Calendar.getInstance().getTime())) {
                                    numberOfCoupons++;
                                    couponIDs.add(object.getObjectId());
                                    couponDescriptions.add(object.getString("couponDescription"));
                                    couponExpirations.add(exp);
                                    ParseFile file = (ParseFile) object.get("couponImage");
                                    byte[] bytes = file.getData();
                                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    couponImages.add(image);
                                }
                            }
                        }
                    }
                }
            }
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), "No Coupons Found", Toast.LENGTH_LONG).show();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        if(numberOfCoupons == 0) {
            Toast.makeText(getApplicationContext(), "No Coupons Found", Toast.LENGTH_LONG).show();
        }

        for(position = 0; position < numberOfCoupons; position++) {
            GridLayout.LayoutParams relativeLP = new GridLayout.LayoutParams();
            final RelativeLayout relativeLayout = new RelativeLayout(this);
            relativeLayout.setId(position);
            relativeLayout.setGravity(Gravity.FILL);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            relativeLayout.setMinimumWidth(metrics.widthPixels/2-20);
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
                }
            });

            int dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            RelativeLayout.LayoutParams imageLP = new RelativeLayout.LayoutParams(dim, dim);
            ImageView image = new ImageView(this);
            image.setImageBitmap(couponImages.get(position));
            image.setId(View.generateViewId());
            imageLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
            relativeLayout.addView(image, imageLP);

            RelativeLayout.LayoutParams descriptionLP = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextView description = new TextView(this);
            description.setText(couponDescriptions.get(position));
            description.setTextColor(Color.RED);
            description.setId(View.generateViewId());
            descriptionLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
            descriptionLP.addRule(RelativeLayout.BELOW, image.getId());
            relativeLayout.addView(description, descriptionLP);

            RelativeLayout.LayoutParams expirationLP = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextView expiration = new TextView(this);
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
