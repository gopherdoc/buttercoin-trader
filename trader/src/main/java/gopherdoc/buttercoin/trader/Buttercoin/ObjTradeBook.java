package gopherdoc.buttercoin.trader.Buttercoin;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


public class ObjTradeBook implements Parcelable {
    //Fields
    public ArrayList<ObjTradeBookOrderRecord> bids;
    public ArrayList<ObjTradeBookOrderRecord> asks;
    private String priceCurrency;
    private String quantityCurrency;
    public ObjTradeBookChart chart;

    public ObjTradeBook(){
        clearOrderBook();
    }

    //Constructors
    public ObjTradeBook(final JSONObject jObj){
        bids = new ArrayList<ObjTradeBookOrderRecord>();
        asks = new ArrayList<ObjTradeBookOrderRecord>();
        chart = new ObjTradeBookChart();
        try {
            priceCurrency = jObj.getString("priceCurrency");
            quantityCurrency = jObj.getString("quantityCurrency");
            JSONArray b = jObj.getJSONArray("bid");
            for (int i = 0; i < b.length(); i++) {
                ObjTradeBookOrderRecord item = new ObjTradeBookOrderRecord("buy", priceCurrency, quantityCurrency, b.getJSONObject(i));
                addBid(item);
            }
            JSONArray a = jObj.getJSONArray("ask");
            for (int i = 0; i < a.length(); i++) {
                ObjTradeBookOrderRecord item = new ObjTradeBookOrderRecord("sell", priceCurrency, quantityCurrency, a.getJSONObject(i));
                addAsk(item);
            }
        } catch (JSONException e) {
            Log.e("ObjTradeBook","Error parsing JSON");
        }
        Collections.reverse(bids);
        for(int j = 0;j < bids.size(); j++){
            chart.addBid(Float.parseFloat(bids.get(j).getPrice().split(" ")[0]), Float.parseFloat(bids.get(j).getAmount().split(" ")[0]));
        }
        for(int k = 0; k < asks.size(); k++){
            chart.addAsk(Float.parseFloat(asks.get(k).getPrice().split(" ")[0]), Float.parseFloat(asks.get(k).getAmount().split(" ")[0]));
        }

    }

    //Methods
    public void clearOrderBook(){
        this.bids = new ArrayList<ObjTradeBookOrderRecord>();
        this.asks = new ArrayList<ObjTradeBookOrderRecord>();
        this.chart = new ObjTradeBookChart();
        this.priceCurrency = "";
        this.quantityCurrency = "";
    }



    public void addBid(ObjTradeBookOrderRecord item){
        this.bids.add(item);
    }
    public void addAsk(ObjTradeBookOrderRecord item) {
        this.asks.add(item);
    }

    //Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("bids",bids);
        bundle.putParcelableArrayList("asks",asks);
        bundle.putParcelable("chart",chart);
        bundle.putString("priceCurrency", priceCurrency);
        bundle.putString("quantityCurrency", quantityCurrency);
        out.writeBundle(bundle);

    }
    public ObjTradeBook(Parcel in) {
        Bundle b = in.readBundle(ObjTradeBookOrderRecord.class.getClassLoader());
        this.bids = b.getParcelableArrayList("bids");
        this.asks = b.getParcelableArrayList("asks");
        this.chart = b.getParcelable("chart");
        this.priceCurrency = b.getString("priceCurrency");
        this.quantityCurrency = b.getString("quantityCurrency");
    }
    public static final Creator<ObjTradeBook> CREATOR = new Creator<ObjTradeBook>() {
        @Override
        public ObjTradeBook createFromParcel(Parcel parcel) {
            return new ObjTradeBook(parcel);
        }

        @Override
        public ObjTradeBook[] newArray(int i) {
            return new ObjTradeBook[i];
        }
    };
}
