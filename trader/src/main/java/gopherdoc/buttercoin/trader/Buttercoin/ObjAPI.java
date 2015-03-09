package gopherdoc.buttercoin.trader.Buttercoin;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.json.simple.JSONObject;
import org.apache.commons.codec.binary.Base64;
import gopherdoc.buttercoin.trader.R;
import gopherdoc.buttercoin.trader.SecurePreferences;


public class ObjAPI implements Parcelable {
    //Private Fields
    public String apikey;
    public String apisecret;
    public int refreshInterval;
    public int depthRange;
    public ObjBalanceCoin BTC;
    public ObjBalanceCoin USD;
    //TODO Change here for sandboxing
    public final static String BASEURL = "https://sandbox.buttercoin.com/v1/";
    //public final static String BASEURL = "https://api.buttercoin.com/v1/";
    private boolean keyChanged;
    private SecurePreferences mSecurePrefs;
    public SecretKey key;

    //Constructors
    public ObjAPI(Context con, SecretKey secKey){
        USD = new ObjBalanceCoin("Dollars","USD");
        BTC = new ObjBalanceCoin("Bitcoin", "BTC");
        key = secKey;
        mSecurePrefs = new SecurePreferences(con,con.getString(R.string.preference_file_key), secKey.toString(), true);
    }

    //Methods
    public void update(Context con){
        String primary = mSecurePrefs.getString(con.getString(R.string.prefapikey));
        String secret = mSecurePrefs.getString(con.getString(R.string.prefapisecret));
        keyChanged = (!primary.equals(apikey) || !secret.equals(apisecret));
        this.apikey = primary;
        this.apisecret = secret;
        if (keyChanged){
            mSecurePrefs.put(con.getString(R.string.prefapikey), primary);
            mSecurePrefs.put(con.getString(R.string.prefapisecret), secret);
            mSecurePrefs.put(con.getString(R.string.prefbtcaddress), "");
        }
    }

    public static Boolean validateKeys(Context con, SecretKey secKey) {
        SecurePreferences mSecurePrefs = new SecurePreferences(con,con.getString(R.string.preference_file_key), secKey.toString(), true);
        String primary = mSecurePrefs.getString(con.getString(R.string.prefapikey));
        String secret = mSecurePrefs.getString(con.getString(R.string.prefapisecret));
        return primary.length() == 32 && secret.length() == 32;
    }
    public static void initPrefs(final Context con, SecretKey secKey){
        SecurePreferences mSecurePrefs = new SecurePreferences(con,con.getString(R.string.preference_file_key), secKey.toString(), true);

        //API keys
        if (!mSecurePrefs.containsKey(con.getString(R.string.prefapikey))){
            mSecurePrefs.put(con.getString(R.string.prefapikey), "");}
        if (!mSecurePrefs.containsKey(con.getString(R.string.prefapisecret))){
            mSecurePrefs.put(con.getString(R.string.prefapisecret), "");}
        if (!mSecurePrefs.containsKey("refreshInterval")){
            mSecurePrefs.put("refreshInterval","10");}
        if (!mSecurePrefs.containsKey("depthRange")){
            mSecurePrefs.put("depthRange","10");}
        //TODO Update here with trade fee changes
        mSecurePrefs.put("tradeFeeOffset", "0.99999");

    }
    public void loadPrefs(final Context con, SecretKey secKey){
        SecurePreferences mSecurePrefs = new SecurePreferences(con,con.getString(R.string.preference_file_key), secKey.toString(), true);
        this.apikey = mSecurePrefs.getString(con.getString(R.string.prefapikey));
        this.apisecret = mSecurePrefs.getString(con.getString(R.string.prefapisecret));
        this.refreshInterval = Integer.parseInt(mSecurePrefs.getString("refreshInterval"));
        this.depthRange = Integer.parseInt(mSecurePrefs.getString("depthRange"));
    }

    private String buildUrl(String method, String path, JSONObject body){
        String url = this.BASEURL + path;
        if (method.equals("GET") && body != null){
            url += URLEncoder.encode(body.toString());
        }
        return url;
    }
    public String getTimestampMillis(){
        int timestamp = (int) (System.currentTimeMillis() / 1000L);
        String time = "" + timestamp + "000";
        return time.trim();
    }
    public String getTimestamp(){
        int timestamp = (int) (System.currentTimeMillis() / 1000L);
        String time = "" + timestamp;
        return time.trim();
    }
    public String getSignature(String method, String url, String body){
        String data = getTimestampMillis() + url;
        if (method.equals("POST") && body != null){
            data += body;
        }
        try {
            SecretKeySpec secret_key = new SecretKeySpec(this.apisecret.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secret_key);
            String b64data = new String(Base64.encodeBase64(data.getBytes()));
            byte[] rawHmac = mac.doFinal(b64data.getBytes());
            return new String(Base64.encodeBase64(rawHmac));
        } catch (InvalidKeyException e){
            Log.e("ObjAPI.getSignature", "Invalid Key exception");
            return "";
        } catch (NoSuchAlgorithmException e1) {
            Log.e("ObjAPI.getSignature", "NoSuchAlgorithm exception");
            return "";
        }
    }

    public float getTradeFee(){ return Float.parseFloat(mSecurePrefs.getString("tradeFeeOffset"));}
    private String getUrl(String path, JSONObject obj){
        return buildUrl("GET",path, obj);
    }
    private String getPostUrl(String path, JSONObject obj) {
        return buildUrl("POST", path, obj);
    }
    public String getTicker() {
        return getUrl("ticker", null);
    }
    public int getRefreshInterval() {return refreshInterval; }
    public int getDepthRange() { return depthRange; }
    public String getOrderBook() {
        return getUrl("orderbook", null);
    }
    public String getRecentTrades(){
        return getUrl("trades",null);
    }
    public String getKeyPermissions() {
        return getUrl("key", null);
    }
    public String getBalances(){
        return getUrl("account/balances", null);
    }
    public String getDepositAddress(){
        return getUrl("account/depositAddress",null);
    }
    public String getOpenOrders(){
        return BASEURL + "orders?status=opened%2Cpartial-filled";
    }
    public String getTradeHistory(){
        return BASEURL + "orders?status=filled%2Cpartial-filled";
    }

    public String addOrder(String side, String price, String amount){
        return getUrl("orders", null);
    }
    public String cancelOrder(String order_id){
        return BASEURL + "orders/" + order_id;
    }


    //Parcelable
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<ObjAPI> CREATOR = new Creator<ObjAPI>() {
        @Override
        public ObjAPI createFromParcel(Parcel parcel) {
            return new ObjAPI(parcel);
        }

        @Override
        public ObjAPI[] newArray(int i) {
            return new ObjAPI[i];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(apikey);
        out.writeString(apisecret);
        out.writeInt(refreshInterval);

    }
    private ObjAPI(Parcel in){
        this.apikey = in.readString();
        this.apisecret = in.readString();
        this.refreshInterval = in.readInt();
    }
}
