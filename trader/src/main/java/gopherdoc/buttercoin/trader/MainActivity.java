package gopherdoc.buttercoin.trader;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import gopherdoc.buttercoin.trader.Buttercoin.ListFragmentAdapter;
import gopherdoc.buttercoin.trader.Buttercoin.ObjAPI;
import gopherdoc.buttercoin.trader.Buttercoin.ObjBalanceCoin;
import gopherdoc.buttercoin.trader.Buttercoin.ObjCollectionActiveOrderRecord;
import gopherdoc.buttercoin.trader.Buttercoin.ObjCollectionBalanceCoin;
import gopherdoc.buttercoin.trader.Buttercoin.ObjCollectionHistoryOrderRecord;
import gopherdoc.buttercoin.trader.Buttercoin.ObjExceptions;
import gopherdoc.buttercoin.trader.Buttercoin.ObjOrderRecord;
import gopherdoc.buttercoin.trader.Buttercoin.ObjTicker;
import gopherdoc.buttercoin.trader.Buttercoin.ObjTradeBook;
import gopherdoc.buttercoin.trader.Buttercoin.ObjTradeBookOrderRecord;
import gopherdoc.buttercoin.trader.NavDrawer.NavDrawerItem;
import gopherdoc.buttercoin.trader.NavDrawer.NavDrawerListAdapter;
import gopherdoc.buttercoin.trader.security.KeyStore;
import gopherdoc.buttercoin.trader.security.KeyStoreJb43;
import gopherdoc.buttercoin.trader.security.KeyStoreKk;
import gopherdoc.buttercoin.trader.security.PRNGFixes;


public class MainActivity extends FragmentActivity implements
        PagerTradeSection.TradePagerListener,Handler.Callback{

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavDrawerListAdapter adapter;
    private ViewPager mViewPager;
    private PagerHistorySection pageHist;
    private PagerTradeSection pageTrade;
    private PagerBalanceSection pageBal;
    private FragmentTickerSection fragTick;

    private ObjAPI API;
    private CustomHandlerThread myCustomHandlerThread;
    private Handler myHandler;
    private ArrayList<Toast> toasts;
    private boolean _doubleBackToExitPressedOnce    = false;
    private Integer currPager;
    private CountDownTimer myCountDownTimer;
    private ArrayList depthchartdata;
    private LinearLayout mybar;
    private ImageView depthIcon;
    private static final boolean IS_JB43 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    private static final boolean IS_JB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    private static final boolean IS_KK = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    public static final String OLD_UNLOCK_ACTION = "android.credentials.UNLOCK";

    public static final String UNLOCK_ACTION = "com.android.credentials.UNLOCK";
    public static final String RESET_ACTION = "com.android.credentials.RESET";
    private KeyStore ks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        PRNGFixes.apply();
        if (IS_KK){
            ks = KeyStoreKk.getInstance();
        } else if (IS_JB43){
            ks = KeyStoreJb43.getInstance();
        } else {
            ks = KeyStore.getInstance();
        }


        //Initiating Fragments
        mViewPager = (ViewPager)findViewById(R.id.pager);
        fragTick = FragmentTickerSection.init();
        ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
        pageTrade = PagerTradeSection.init();
        mFragments.add(pageTrade);
        pageBal = PagerBalanceSection.init();
        mFragments.add(pageBal);
        pageHist = PagerHistorySection.init();
        mFragments.add(pageHist);
        mViewPager.setAdapter(new ListFragmentAdapter(super.getSupportFragmentManager(), mFragments));
        currPager = 0;

        //Creating Nav Drawer
        String[] navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.list_slidermenu);
        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();
        //Trade
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
        //Deposit
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
        //Trade History
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2]));
        //Settings
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));
        //About
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4]));

        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);

        //Enabling action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.app_name,
                R.string.app_name){
            public void onDrawerClosed(View view){
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView){
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        mybar = (LinearLayout) findViewById(R.id.myactionbartitle);
	    if (mybar != null) {
	        mybar.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                openNavDrawer();
	            }
        	});
	    }
        depthIcon = (ImageView)findViewById(R.id.depth_icon);
        if (depthIcon != null) {
		    depthIcon.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                launchDepthDialog();
	            }
   	     });
	    }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onPause(){
        super.onPause();
        try {
            //Stop handlers here
            myCustomHandlerThread.setCallback(null);
            myCustomHandlerThread.quit();
            myHandler.removeCallbacks(myCustomHandlerThread.fetchActiveOrders);
            myHandler.removeCallbacks(myCustomHandlerThread.fetchBalances);
            myHandler.removeCallbacks(myCustomHandlerThread.fetchCompletedTrades);
            myHandler.removeCallbacks(myCustomHandlerThread.fetchDepositAddress);
            myHandler.removeCallbacks(myCustomHandlerThread.fetchOrderBook);
            myHandler.removeCallbacks(myCustomHandlerThread.fetchTicker);
            myHandler.removeCallbacks(myCustomHandlerThread.refreshData);
            myHandler.removeCallbacksAndMessages(null);
            myCountDownTimer.cancel();
            myCustomHandlerThread = null;
            finish();

        } catch (NullPointerException ignored) {}
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item if not currently selected.
            boolean showView;
            if (adapter.getCurrent() != position) {
                adapter.updateSelected(position);
                showView = true;
            } else { showView = false;}
            displayView(position, showView);
        }
    }
    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position, boolean loadView) {
        // update the main content by replacing fragments
        if (loadView) {
            switch (position) {
                case 0:
                    //Trade
                    mViewPager.setCurrentItem(0,true);
                    currPager = 0;
                    String basebal = API.BTC.getBalanceString() +" BTC";
                    String quotebal = API.USD.getBalanceString() + " USD";
                    pageTrade.setBasebal(basebal);
                    pageTrade.setQuotebal(quotebal);
                    pageTrade.setTradeFee(API.getTradeFee());

                    // update selected item and title, then close the drawer
                    mDrawerList.setItemChecked(position, true);
                    mDrawerList.setSelection(position);
                    mDrawerLayout.closeDrawer(mDrawerList);

                    break;
                case 1:
                    //Deposit
                    mViewPager.setCurrentItem(1);
                    currPager = 1;
                    mDrawerList.setItemChecked(position, true);
                    mDrawerList.setSelection(position);
                    mDrawerLayout.closeDrawer(mDrawerList);
                    pageBal.updateCoinInfo(API.BTC);
                    break;
                case 2:
                    //History
                    mViewPager.setCurrentItem(2);
                    currPager = 2;

                    // update selected item and title, then close the drawer
                    mDrawerList.setItemChecked(position, true);
                    mDrawerList.setSelection(position);
                    mDrawerLayout.closeDrawer(mDrawerList);
                    sendToast("Updating Trade History");
                    myHandler.post(myCustomHandlerThread.fetchCompletedTrades);
                    break;
                case 3:
                    //Settings
                    startActivity(new Intent(this, ActivitySettings.class));
                    finish();
                    break;
                case 4:
                    //About
                    startActivity(new Intent(this, ActivityAbout.class));
                    finish();
                    break;
                default:
                    break;
            }
        } else {
            mDrawerLayout.closeDrawer(mDrawerList);
        }


    }

    @Override
    public void onResumeFragments(){
        super.onResumeFragments();
        //Retrieve some info from savedprefs
        if (ks.state() == KeyStore.State.UNLOCKED){
            byte[] keyBytes = ks.get("hashkey");
            boolean success;
            SecretKey key;
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
                API = new ObjAPI(this, key);
                API.loadPrefs(this, key);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                fragTick = FragmentTickerSection.init();
                Bundle args = new Bundle();
                args.putString("TickerText", "");
                fragTick.setArguments(args);
                ft.replace(R.id.fragment_ticker, fragTick, "Ticker");
                ft.commit();
                //Start the thread handlers
                myCustomHandlerThread = new CustomHandlerThread("MyHandlerThread");
                myCustomHandlerThread.start();
                myHandler = new Handler(myCustomHandlerThread.getLooper(),this);
                myCustomHandlerThread.setCallback(myHandler);
                myHandler.post(myCustomHandlerThread.refreshData);
                myHandler.post(myCustomHandlerThread.fetchDepositAddress);
                myCountDownTimer = new CountDownTimer(API.getRefreshInterval()*1000,1000) {
                    public void onTick(long millisUntilFinished){
                        if (fragTick != null) {
                            fragTick.updateCounter(Long.toString(millisUntilFinished / 1000L));
                        }
                    }
                    public void onFinish() {
                    }
                };
                displayView(currPager,true);
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
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        removeToast();
    }

    @Override
    protected void onStop () {
        super.onStop();
        removeToast();
    }

    @Override
    public void onBackPressed() {
        if (adapter.getCurrent() == 0){
            if (_doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finish();
                return;
            }
            this._doubleBackToExitPressedOnce = true;
            sendToast("Press again to quit");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    _doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            adapter.updateSelected(0);
            displayView(0,true);
        }
    }




    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    //Data functions

    public void sendToast(final String message) {
        if (null == toasts) {
            toasts = new ArrayList<Toast>();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
                    toast.show();
                    toasts.add(toast);
                } catch (Exception e) {/* do nothing, just means that the activity doesn't exist anymore*/}
            }
        });
    }


    public void removeToast() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != toasts) {
                    for(Toast toast:toasts) {
                        toast.cancel();
                    }
                    toasts = null;
                }
            }
        });
    }

    //Network functions
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected();
    }

    //Listener methods

    public void openNavDrawer() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    public void launchDepthDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        GraphView graph = new GraphView(this);
        if (depthchartdata != null && depthchartdata.size() == 5 ) {
            LineGraphSeries<DataPoint> bids = (LineGraphSeries<DataPoint>) depthchartdata.get(0);
            bids.setColor(Color.RED);
            bids.setBackgroundColor(Color.RED);
            bids.setDrawBackground(true);
            graph.addSeries(bids);

            LineGraphSeries<DataPoint> asks = (LineGraphSeries<DataPoint>) depthchartdata.get(1);
            asks.setColor(Color.GREEN);
            asks.setBackgroundColor(Color.GREEN);
            asks.setDrawBackground(true);
            graph.addSeries(asks);
            Float yMax = Float.parseFloat(Double.toString((Double)depthchartdata.get(2)));
            Float leftbound = Float.parseFloat(Double.toString((Double)depthchartdata.get(3)));
            Float rightbound = Float.parseFloat(Double.toString((Double)depthchartdata.get(4)));
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumIntegerDigits(1);
            nf.setMaximumFractionDigits(0);
            nf.setRoundingMode(RoundingMode.HALF_EVEN);
            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
            graph.getGridLabelRenderer().setNumHorizontalLabels(4);
            graph.getGridLabelRenderer().setHorizontalLabelsVisible(true);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(leftbound);
            graph.getViewport().setMaxX(rightbound);
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(0.0);
            graph.getViewport().setMaxY(yMax);

            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            graph.setLayoutParams(ll);
            layout.addView(graph);
            alert.setTitle("Market Depth");
            alert.setView(layout);
            alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alert.show();
        }
    }


    @Override
    public void onOrderBookItemSelected(ObjTradeBookOrderRecord item) {
        String[] myString = item.getPrice().split(" ");
        Double doublePrice = Double.parseDouble(myString[0]);
        String stringPrice = String.format("%.4f",doublePrice);
        pageTrade.setOrderPrice(stringPrice);
    }

    @Override
    public void onTradeListener(String tradeSide, Float price, Float amount) {
        Double dPrice = Double.parseDouble(String.format("%.4f", price));
        String myPrice = BigDecimal.valueOf(dPrice).toPlainString();
        myCustomHandlerThread.addOrder(tradeSide, myPrice, String.format("%.4f", amount));
    }

    private class asyncFetchBalances extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            ObjExceptions responseCode;
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(),443));
            HttpParams params = new BasicHttpParams();
            SingleClientConnManager mgr = new SingleClientConnManager(params,schemeRegistry);
            DefaultHttpClient client = new DefaultHttpClient(mgr, params);
            HttpGet httpGet = new HttpGet(urls[0]);
            String sig = API.getSignature("GET", urls[0], null);
            String date = API.getTimestampMillis();
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("X-Buttercoin-Access-Key", API.apikey);
            httpGet.setHeader("X-Buttercoin-Signature", sig);
            httpGet.setHeader("X-Buttercoin-Date", date);
            try {
                HttpResponse execute = client.execute(httpGet);
                responseCode = new ObjExceptions(execute.getStatusLine().getStatusCode());
                if (responseCode.status.equals("OK")) {
                    response = EntityUtils.toString(execute.getEntity());
                } else {
                    Log.e("fetchBalances", responseCode.intcode + ": " + responseCode.msg);
                    Log.e("Error message", execute.getStatusLine().getReasonPhrase());
                    Log.e("URL", urls[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
             return response;
        }

        @Override
        protected void onPostExecute(String result) {
            ObjCollectionBalanceCoin bal;
            try {
                JSONObject jObj = new JSONObject(result);
                bal = new ObjCollectionBalanceCoin(jObj);
                API.BTC.setBalance(bal.BTC.getBalance());
                API.USD.setBalance(bal.USD.getBalance());
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        pageTrade.setBasebal(API.BTC.getBalanceString() + " BTC");
                        pageTrade.setQuotebal(API.USD.getBalanceString() + " USD");
                    }
                };
                runOnUiThread(runnable);
            } catch (JSONException e) {
                Log.e("fetchBalances", "Error parsing JSON balances");
            }
        }
    }
    private class asyncAddOrder extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String response = "";
            String url = params[0];
            String side = params[1];
            String price = params[2];
            String amount = params[3];
            ObjExceptions responseCode;
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(),443));
            HttpParams params1 = new BasicHttpParams();
            SingleClientConnManager mgr = new SingleClientConnManager(params1,schemeRegistry);
            DefaultHttpClient client = new DefaultHttpClient(mgr, params1);
            HttpPost httpPost = new HttpPost(url);
            String httpData = "{\"orderType\":\"" + "limit" +
                    "\",\"side\":\"" + side +
                    "\",\"instrument\":\"" + "BTC_USD" +
                    "\",\"quantity\":\"" + amount +
                    "\",\"price\":\"" + price +"\" }";
            String sig = API.getSignature("POST", url, httpData);
            String date = API.getTimestampMillis();
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("X-Buttercoin-Access-Key", API.apikey);
            httpPost.addHeader("X-Buttercoin-Signature", sig);
            httpPost.addHeader("X-Buttercoin-Date", date);
            try {
                httpPost.setEntity(new StringEntity(httpData));
                HttpResponse execute = client.execute(httpPost);
                responseCode = new ObjExceptions(execute.getStatusLine().getStatusCode());
                if (responseCode.status.equals("OK")) {
                    response = execute.getLastHeader("Location").getValue();
                } else {
                    Log.e("addOrder", responseCode.intcode + ": " + responseCode.msg);
                    Log.e("Error message", execute.getStatusLine().getReasonPhrase());
                    Log.e("URL", url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            String response = result;
            String tradeID = response.replace(ObjAPI.BASEURL + "orders/", "");
            sendToast("Order added with TradeID: " + tradeID);
        }
    }
    private class asyncFetchCompletedTrades extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            ObjExceptions responseCode;
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(),443));
            HttpParams params = new BasicHttpParams();
            SingleClientConnManager mgr = new SingleClientConnManager(params,schemeRegistry);
            DefaultHttpClient client = new DefaultHttpClient(mgr, params);
            HttpGet httpGet = new HttpGet(urls[0]);
            String sig = API.getSignature("GET", urls[0], null);
            String date = API.getTimestampMillis();
            httpGet.setHeader("Content-Type","application/json");
            httpGet.setHeader("X-Buttercoin-Access-Key", API.apikey);
            httpGet.setHeader("X-Buttercoin-Signature", sig);
            httpGet.setHeader("X-Buttercoin-Date", date);
            try {
                HttpResponse execute = client.execute(httpGet);
                responseCode = new ObjExceptions(execute.getStatusLine().getStatusCode());
                if (responseCode.status.equals("OK")) {
                    response = EntityUtils.toString(execute.getEntity());
                } else {
                    Log.e("fetchCompletedTrades", responseCode.intcode + ": " + responseCode.msg);
                    Log.e("Error message", execute.getStatusLine().getReasonPhrase());
                    Log.e("URL", urls[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (!result.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray tlArray = jsonObject.getJSONArray("results");
                    final ObjCollectionHistoryOrderRecord historyOrderRecord = new ObjCollectionHistoryOrderRecord(tlArray);
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            pageHist.updateHistory(historyOrderRecord);
                        }
                    };
                    runOnUiThread(runnable);
                } catch (JSONException e) {
                    Log.e("fetchCompletedTrades", "Error parsing JSONArray");
                    sendToast("Error processing completed trades");
                }
            }
        }
    }
    private class asyncFetchActiveOrders extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            ObjExceptions responseCode;
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(),443));
            HttpParams params = new BasicHttpParams();
            SingleClientConnManager mgr = new SingleClientConnManager(params,schemeRegistry);
            DefaultHttpClient client = new DefaultHttpClient(mgr, params);
            HttpGet httpGet = new HttpGet(urls[0]);
            String sig = API.getSignature("GET", urls[0], null);
            String date = API.getTimestampMillis();
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("X-Buttercoin-Access-Key", API.apikey);
            httpGet.setHeader("X-Buttercoin-Signature", sig);
            httpGet.setHeader("X-Buttercoin-Date", date);
            try {
                HttpResponse execute = client.execute(httpGet);
                responseCode = new ObjExceptions(execute.getStatusLine().getStatusCode());
                if (responseCode.status.equals("OK")) {
                    response = EntityUtils.toString(execute.getEntity());
                } else {
                    Log.e("fetchActiveOrders", responseCode.intcode + ": " + responseCode.msg);
                    Log.e("Error message", execute.getStatusLine().getReasonPhrase());
                    Log.e("URL", urls[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            String response = result;
            try {
                JSONObject objResponse = new JSONObject(response);
                JSONArray obArray = objResponse.getJSONArray("results");
                final ObjCollectionActiveOrderRecord activeOrderRecord = new ObjCollectionActiveOrderRecord(obArray);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pageTrade.updateActiveOrders(activeOrderRecord.collection);
                        }catch (NullPointerException ignored){}
                    }
                };
                runOnUiThread(runnable);
            } catch (JSONException e) {
                Log.e("fetchActiveOrders", "Error parsing JSONArray");
            }
        }
    }
    private class asyncCancelOrder extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            ObjExceptions responseCode;
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(),443));
            HttpParams params = new BasicHttpParams();
            SingleClientConnManager mgr = new SingleClientConnManager(params,schemeRegistry);
            DefaultHttpClient client = new DefaultHttpClient(mgr, params);
            HttpDelete httpDelete = new HttpDelete(urls[0]);
            String sig = API.getSignature("GET", urls[0], null);
            String date = API.getTimestampMillis();
            httpDelete.setHeader("Content-Type", "application/json");
            httpDelete.setHeader("X-Buttercoin-Access-Key", API.apikey);
            httpDelete.setHeader("X-Buttercoin-Signature", sig);
            httpDelete.setHeader("X-Buttercoin-Date", date);
            try {
                HttpResponse execute = client.execute(httpDelete);
                responseCode = new ObjExceptions(execute.getStatusLine().getStatusCode());
                if (responseCode.status.equals("OK")) {
                    sendToast("Order cancelled successfully");
                } else {
                    Log.e("cancelOrder", responseCode.intcode + ": " + responseCode.msg);
                    Log.e("Error message", execute.getStatusLine().getReasonPhrase());
                    Log.e("URL", urls[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }
    private class asyncFetchTicker extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            ObjExceptions responseCode;
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urls[0]);
            String sig = API.getSignature("GET", urls[0], null);
            String date = API.getTimestampMillis();
            try {
                HttpResponse execute = client.execute(httpGet);
                responseCode = new ObjExceptions(execute.getStatusLine().getStatusCode());
                if (responseCode.status.equals("OK")) {
                    response = EntityUtils.toString(execute.getEntity());
                } else {
                    Log.e("fetchTicker", responseCode.intcode + ": " + responseCode.msg);
                    Log.e("Error message", execute.getStatusLine().getReasonPhrase());
                    Log.e("URL", urls[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            String response = result;
            try {
                JSONObject jObj = new JSONObject(response);
                final ObjTicker ticker = new ObjTicker(jObj);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        fragTick.updateTicker(ticker);
                    }
                };
                runOnUiThread(runnable);
            } catch (JSONException e) {
                Log.e("fetchTicker", "Error parsing JSON");
            }
        }
    }
    private class asyncFetchOrderBook extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            ObjExceptions responseCode;
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urls[0]);
            String sig = API.getSignature("GET", urls[0], null);
            String date = API.getTimestampMillis();
            httpGet.setHeader("Content-Type", "application/json");
            try {
                HttpResponse execute = client.execute(httpGet);
                responseCode = new ObjExceptions(execute.getStatusLine().getStatusCode());
                if (responseCode.status.equals("OK")) {
                    response = EntityUtils.toString(execute.getEntity());
                } else {
                    Log.e("fetchOrderBook", responseCode.intcode + ": " + responseCode.msg);
                    Log.e("Error message", execute.getStatusLine().getReasonPhrase());
                    Log.e("URL", urls[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            String response = result;
            try {
                JSONObject jObj = new JSONObject(response);
                final ObjTradeBook orderBook = new ObjTradeBook(jObj);
                depthchartdata = orderBook.chart.buildChart(API.getDepthRange());
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {pageTrade.updateOrderBook(orderBook);}
                };
                runOnUiThread(runnable);
            } catch (JSONException e) {
                Log.e("fetchOrderBook", "Error parsing JSON");
            }
        }
    }
    private class asyncFetchDepositAddress extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            ObjExceptions responseCode;
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(),443));
            HttpParams params = new BasicHttpParams();
            SingleClientConnManager mgr = new SingleClientConnManager(params,schemeRegistry);
            DefaultHttpClient client = new DefaultHttpClient(mgr, params);
            HttpGet httpGet = new HttpGet(urls[0]);
            String sig = API.getSignature("GET", urls[0], null);
            String date = API.getTimestampMillis();
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("X-Buttercoin-Access-Key", API.apikey);
            httpGet.setHeader("X-Buttercoin-Signature", sig);
            httpGet.setHeader("X-Buttercoin-Date", date);
            try {
                HttpResponse execute = client.execute(httpGet);
                responseCode = new ObjExceptions(execute.getStatusLine().getStatusCode());
                if (responseCode.status.equals("OK")) {
                    response = EntityUtils.toString(execute.getEntity());
                } else {
                    Log.e("fetchDepositAddress", responseCode.intcode + ": " + responseCode.msg);
                    Log.e("Error message", execute.getStatusLine().getReasonPhrase());
                    Log.e("URL", urls[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jObj = new JSONObject(result);
                ObjBalanceCoin btc_temp = new ObjBalanceCoin(jObj.getString("address"));
                if (btc_temp.symbol.equals("BTC")) {
                    API.BTC.setAddress(btc_temp.address);
                }
            } catch (JSONException e) {
                Log.e("fetchDepositAddress", "Error parsing JSON");
            }
        }
    }
    @Override
    public void onOrderCancelled(ObjOrderRecord order) {
        myCustomHandlerThread.cancelOrder(order);
    }

    public class CustomHandlerThread extends HandlerThread implements Handler.Callback{
        private Handler callback;

        public CustomHandlerThread(String name) {
            super(name);
        }


        public void setCallback(Handler cb){
            callback = cb;
        }

        @Override
        public boolean handleMessage(Message msg) {

            return true;
        }

        final Runnable fetchBalances = new Runnable() {
            @Override
            public void run() {
                final String url = API.getBalances();
                asyncFetchBalances task = new asyncFetchBalances();
                task.execute(url);
            }
        };

        public void addOrder( final String side,
                              final String price, final String amount){
            final Runnable runOrder = new Runnable() {
                @Override
                public void run() {
                    final String url = API.addOrder(side, price, amount);
                    asyncAddOrder task = new asyncAddOrder();
                    task.execute(url, side, price, amount);
                }
            };
            myHandler.post(runOrder);
        }

        final Runnable fetchActiveOrders = new Runnable() {
            @Override
            public void run() {
                final String url = API.getOpenOrders();
                asyncFetchActiveOrders task = new asyncFetchActiveOrders();
                task.execute(url);
            }
        };

        final Runnable fetchCompletedTrades = new Runnable() {
            @Override
            public void run() {
                final String url = API.getTradeHistory();
                asyncFetchCompletedTrades task = new asyncFetchCompletedTrades();
                task.execute(url);
            }
        };

        public void cancelOrder(final ObjOrderRecord order){
            final Runnable runCancel = new Runnable() {
                @Override
                public void run() {
                    final String url = API.cancelOrder(order.tradeid);
                    asyncCancelOrder task = new asyncCancelOrder();
                    task.execute(url);
                }
            };
            myHandler.post(runCancel);
        }
        public Runnable fetchTicker = new Runnable() {
            @Override
            public void run() {
                final String url = API.getTicker();
                asyncFetchTicker task = new asyncFetchTicker();
                task.execute(url);
            }
        };

        public Runnable fetchOrderBook = new Runnable() {
            @Override
            public void run() {
                final String url = API.getOrderBook();
                asyncFetchOrderBook task = new asyncFetchOrderBook();
                task.execute(url);
            }
        };

        public Runnable fetchDepositAddress = new Runnable() {
            @Override
            public void run() {
                final String url = API.getDepositAddress();
                asyncFetchDepositAddress task = new asyncFetchDepositAddress();
                task.execute(url);
            }
        };

        final Runnable refreshData = new Runnable() {
            public void run() {
                if(!isNetworkAvailable(getApplicationContext())){
                    sendToast("No Internet connection");
                } else {
                    if (myCountDownTimer != null){ myCountDownTimer.cancel(); }
                    myHandler.post(fetchTicker);
                    myHandler.post(fetchOrderBook);
                    myHandler.post(fetchBalances);
                    myHandler.post(fetchActiveOrders);
                    if (myCountDownTimer != null) { myCountDownTimer.start(); }
                }
                myHandler.postDelayed(this, API.getRefreshInterval()*1000L);
            }
        };
    }
}