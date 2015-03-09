package gopherdoc.buttercoin.trader.Buttercoin;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


public class ObjTicker implements Parcelable {
    //Fields
    private Float bid;
    private Float ask;
    private Float lasttradeprice;

    //Constructors
    public ObjTicker(){
        this.bid = 0.0F;
        this.ask = 0.0F;
        this.lasttradeprice = 0.0F;

    }
    public ObjTicker(JSONObject item){
        try {
            this.bid = Float.parseFloat(item.getString("bid"));
            this.ask = Float.parseFloat(item.getString("ask"));
            this.lasttradeprice = Float.parseFloat(item.getString("last"));
        } catch (JSONException e) {
            Log.e("ObjTicker", "Error parsing JSONObject");
        }
    }
    //Methods
    public String print(){
        if (this.bid == null || this.ask == null || this.lasttradeprice == null){
            return "";
        } else {
            return "Bid: " + myFloatString(this.bid) +
                    " Ask: " + myFloatString(this.ask) + "\n" +
                    " Last: " + myFloatString(this.lasttradeprice);
        }
    }
    public String myFloatString(Float myFloat) {
        DecimalFormat df = new DecimalFormat("#.#");
        df.setMaximumFractionDigits(5);
        return df.format(myFloat);
    }

    //Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeFloat(bid);
        out.writeFloat(ask);
        out.writeFloat(lasttradeprice);
    }
    public ObjTicker(Parcel in){
        this.bid = in.readFloat();
        this.ask = in.readFloat();
        this.lasttradeprice = in.readFloat();
    }
    public static final Creator<ObjTicker> CREATOR = new Creator<ObjTicker>() {
        @Override
        public ObjTicker createFromParcel(Parcel parcel) {
            return new ObjTicker(parcel);
        }

        @Override
        public ObjTicker[] newArray(int i) {
            return new ObjTicker[i];
        }
    };
}
