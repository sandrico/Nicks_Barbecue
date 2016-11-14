package com.nicksbbq.nicksbarbecue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;

public class ItemDetails extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabase;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        spinner = (ProgressBar) findViewById(R.id.progressBarFoodItem);
        spinner.setVisibility(View.VISIBLE);

        Intent i = getIntent();
        final String foodCategory = i.getStringExtra("foodCategory");
        final String foodItem = i.getStringExtra("foodItem");
        setTitle(foodItem);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Menu").child(foodCategory).child(foodItem).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot item) {
                TextView itemName = (TextView) findViewById(R.id.itemNameText);
                TextView itemDescription = (TextView) findViewById(R.id.itemDesciptionText);
                ImageView itemImage = (ImageView) findViewById(R.id.itemImageView);
                TextView itemPrice = (TextView) findViewById(R.id.itemPriceText);
                TextView itemPrice2 = (TextView) findViewById(R.id.itemPriceText2);
                TextView itemPrice3 = (TextView) findViewById(R.id.itemPriceText3);
                TextView size = (TextView) findViewById(R.id.sizeText);
                TextView size2 = (TextView) findViewById(R.id.sizeText2);
                TextView size3 = (TextView) findViewById(R.id.sizeText3);
                final TextView dinnerSides = (TextView) findViewById(R.id.sidesText);

                itemName.setText(foodItem);
                String description = String.valueOf(item.child("foodDescription").getValue());
                if(!description.equals("null")) {
                    itemDescription.setText(description);
                }

                String base64Image = String.valueOf(item.child("foodImage").getValue());
                if(!base64Image.equals("null")) {
                    byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
                    Bitmap image = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    itemImage.setImageBitmap(image);
                }

                String sizeString = String.valueOf(item.child("foodSize").getValue());
                if(!sizeString.equals("null")) {
                    size.setText(sizeString);
                }
                sizeString = String.valueOf(item.child("foodSize2").getValue());
                if(!sizeString.equals("null")) {
                    size2.setText(sizeString);
                }
                sizeString = String.valueOf(item.child("foodSize3").getValue());
                if(!sizeString.equals("null")) {
                    size3.setText(sizeString);
                }

                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                double amt = (double) item.child("foodPrice").getValue();
                itemPrice.setText(formatter.format(amt));
                String amtString = String.valueOf(item.child("foodPrice2").getValue());
                if(!amtString.equals("null")) {
                    itemPrice2.setText(formatter.format(Double.valueOf(amtString)));
                }
                amtString = String.valueOf(item.child("foodPrice3").getValue());
                if(!amtString.equals("null")) {
                    itemPrice3.setText(formatter.format(Double.valueOf(amtString)));
                }
                if(foodCategory.equals("Dinners")) {
                    mDatabase.child("Diner Sides").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot sides) {
                            dinnerSides.setText(String.valueOf(sides.child("foodDescription").getValue()));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }  else {
                    dinnerSides.setText("");
                }
                spinner.setVisibility(View.GONE);
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
        getMenuInflater().inflate(R.menu.item_details, menu);
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
        return true;
    }
}
