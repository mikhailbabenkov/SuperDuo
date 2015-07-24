package it.jaschke.alexandria;

import android.app.Application;

import it.jaschke.alexandria.utils.ConnectivityHelper;

/**
 * Created by michaelbabenkov on 24/07/15.
 */
public class AlexandriaApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initializing all helpers that need context
        ConnectivityHelper.INSTANCE.setContext(this);
    }
}
