package com.softlink.wall.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.softlink.wall.Account.LogInActivity;
import static com.softlink.wall.R.*;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.softlink.wall.R;

import java.util.Objects;


public class Setting extends PreferenceFragmentCompat {
    private FirebaseAuth mAuth;
    //String email,name;
    Preference logout,feed,username,update,moreApps;
    private InterstitialAd interstitial;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(xml.root_preferences, rootKey);


        MobileAds.initialize(getContext(), getString(R.string.ADMOB_APP_ID));
        AdRequest adIRequest = new AdRequest.Builder().build();
        interstitial = new InterstitialAd(getContext());
        interstitial.setAdUnitId(getString(R.string.Interstitial));
        interstitial.loadAd(adIRequest);
        interstitial.setAdListener(new AdListener()
        {
            public void onAdLoaded()
            {
                // Call displayInterstitial() function when the Ad loads
                displayInterstitial();
            }
        });





        mAuth= FirebaseAuth.getInstance();

        logout = findPreference(getString(string.key_logout));
        update = findPreference(getString(string.key_update));
        moreApps = findPreference(getString(string.key_moreapps));
        username = findPreference(getString(string.key_user));
        feed = findPreference(getString(string.key_send_feedback));

        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();

        username.setTitle(User.getDisplayName());
        username.setSummary(User.getEmail());

        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mAuth.signOut();

                LoginManager.getInstance().logOut();
                Intent intent= new Intent(getActivity(), LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }


        });

        feed.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                sendFeedback(getContext());
                return true;
            }
        });

        moreApps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                moreApp();
                return true;
            }
        });
        update.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                updateApp();
                return true;
            }
        });
    }



    private void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();

        }
        else {
            Toast.makeText(getContext(), "Network Problem", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"dipanjanpanja6@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from Wallpaper store app");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(string.choose_email_client)));
    }



    public void updateApp () {
            try {
                 startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse( getString(R.string.url_market_details)
                                + getContext().getPackageName())));
            } catch (android.content.ActivityNotFoundException anfe) {
                try {
                     startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse( getString(R.string.url_playstore_app)
                                    + getContext().getPackageName())));
                } catch (Exception e) {
                    Toast.makeText( getActivity(),
                            R.string.install_google_play_store,
                            Toast.LENGTH_SHORT).show();
                }
            }
    }




    public void moreApp() {
        Uri uri = Uri.parse("market://dev?id=6523914020726661209");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/dev?id=6523914020726661209")));
        }


    }

}
