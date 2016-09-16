package com.nicksbbq.nicksbarbecue;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;


public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

    // Add your initialization code here
    Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
            .applicationId("nicksbarbecue1227jo0oeplaoWlAu")
            .clientKey(null)
            .server("https://nicksbarbecue.herokuapp.com/parse/")
    .build()
    );

        ParseUser.enableAutomaticUser();
      ParseACL defaultACL = new ParseACL();
    // Optionally enable public read access.
    // defaultACL.setPublicReadAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);
  }
}
