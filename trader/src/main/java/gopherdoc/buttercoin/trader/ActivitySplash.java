package gopherdoc.buttercoin.trader;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import gopherdoc.buttercoin.trader.Buttercoin.ObjAPI;
import gopherdoc.buttercoin.trader.security.KeyStoreJb43;
import gopherdoc.buttercoin.trader.security.KeyStoreKk;
import gopherdoc.buttercoin.trader.security.KeyStore;
import gopherdoc.buttercoin.trader.security.PRNGFixes;


@SuppressWarnings("WeakerAccess")
public class ActivitySplash extends FragmentActivity implements FragmentSplashInvalidKeys.OnInvalidKeyBtnClickListener{
    private static int SPLASH_TIME_OUT = 2000;
    private static final boolean IS_JB43 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    private static final boolean IS_JB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    private static final boolean IS_KK = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    public static final String OLD_UNLOCK_ACTION = "android.credentials.UNLOCK";

    public static final String UNLOCK_ACTION = "com.android.credentials.UNLOCK";
    public static final String RESET_ACTION = "com.android.credentials.RESET";
    private KeyStore ks;
    private SecretKey key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume(){
        super.onResume();
        PRNGFixes.apply();
        if (IS_KK){
            ks = KeyStoreKk.getInstance();
        } else if (IS_JB43){
            ks = KeyStoreJb43.getInstance();
        } else {
            ks = KeyStore.getInstance();
        }
        if (ks.state() == KeyStore.State.UNLOCKED){
            byte[] keyBytes = ks.get("hashkey");
            boolean success;
            if (keyBytes != null){
                key = new SecretKeySpec(keyBytes, "AES");
                success = true;
            } else {
                try {
                    KeyGenerator kgen = KeyGenerator.getInstance("AES");
                    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                    byte[] keyStart = "startseed".getBytes();
                    sr.setSeed(keyStart);
                    kgen.init(128, sr);
                    key = kgen.generateKey();
                    success = ks.put("hashkey", key.getEncoded());
                } catch (NoSuchAlgorithmException ignored) {
                    success = false;
                    key = null;
                }
            }
            if (success && key != null) {
                ObjAPI.initPrefs(this, key);
                Boolean validAuth = ObjAPI.validateKeys(this, key);
                if (!validAuth){
                    FragmentSplashInvalidKeys invalidKeys = FragmentSplashInvalidKeys.init();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_splashaction,invalidKeys);
                    ft.commit();
                } else {
                    FragmentSplashValidKeys validKeys = FragmentSplashValidKeys.init();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_splashaction, validKeys);
                    ft.commit();
                    new Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    // This method will be executed once the timer is over
                                    // Start your app main activity
                                    Intent i = new Intent(ActivitySplash.this, MainActivity.class);
                                    startActivity(i);
                                    // close this activity
                                    finish();
                                }
                            }, SPLASH_TIME_OUT
                    );
                }
            }
        } else {
            try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    startActivity(new Intent(OLD_UNLOCK_ACTION));
                } else {
                    startActivity(new Intent(UNLOCK_ACTION));
                }
            } catch (ActivityNotFoundException e) {
                Log.e("Buttercoin", "No UNLOCK activity: " + e.getMessage(), e);
                Toast.makeText(this, "No keystore unlock activity found.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }


    @Override
    public void onBtnClicked(String action) {
        if (action.equals("Settings")){
            Intent i = new Intent(new Intent(this, ActivitySettings.class));
            startActivity(i);
            finish();
        } else if (action.equals("Signup")){
            String url = "https://buttercoin.com/setup?ref=5WDEup1Q";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            finish();
        }
    }
}

