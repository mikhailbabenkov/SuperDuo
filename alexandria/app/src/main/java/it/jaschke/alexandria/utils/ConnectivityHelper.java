package it.jaschke.alexandria.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by michaelbabenkov on 24/07/15.
 */
public enum ConnectivityHelper {
    INSTANCE;
    private Context mContext;


    public ConnectivityHelper setContext(Context context) {
        mContext = context;
        return this;
    }

    public boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

}
