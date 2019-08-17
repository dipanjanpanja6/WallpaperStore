package com.softlink.wall;

import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.view.MenuItem;
import android.view.Window;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pd.chocobar.ChocoBar;
import com.softlink.wall.Account.LogInActivity;
import com.softlink.wall.Fragment.CatagoryFragment;
import com.softlink.wall.Fragment.FaveFragment;
import com.softlink.wall.Fragment.HomeFragment;
import com.softlink.wall.Fragment.Setting;
import io.fabric.sdk.android.Fabric;




public class MainActivity extends AppCompatActivity {

    CatagoryFragment catagoryFragment;
    FaveFragment faveFragment;
    HomeFragment homeFragment;
   public Setting settingsFragment;
  //  private InterstitialAd interstitial;


    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    {
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:

                        getFragment(homeFragment);
                        return true;
                    case R.id.navigation_favorite:
                        getFragment(faveFragment);
                        return true;
                    case R.id.navigation_settings:
                        getFragment(settingsFragment);
                        return true;

                    case R.id.navigation_catagory:
                        getFragment(catagoryFragment);
                        return true;
                }
                return false;
            }


        };
    }


    private void getFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framLayout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
/*
        MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID));
        AdRequest adIRequest = new AdRequest.Builder().build();
        interstitial = new InterstitialAd(MainActivity.this);
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

*/
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
        {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {

            ChocoBar.builder().setActivity(MainActivity.this)
                    .setText("Signed In as "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                    .setDuration(ChocoBar.LENGTH_SHORT)
                    .green().show();
            catagoryFragment = new CatagoryFragment();

            homeFragment = new HomeFragment();
            faveFragment = new FaveFragment();
            settingsFragment = new Setting();

            getSupportFragmentManager().beginTransaction().replace(R.id.framLayout, new HomeFragment()).commit();
        }


    }
    /*private void displayInterstitial() {


            if (interstitial.isLoaded()) {
                interstitial.show();

        }
            else {
                Toast.makeText(this, "Network Problem", Toast.LENGTH_SHORT).show();
            }
    }
*/

}
