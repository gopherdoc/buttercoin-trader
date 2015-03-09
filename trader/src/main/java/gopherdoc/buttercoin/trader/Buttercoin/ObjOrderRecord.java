package gopherdoc.buttercoin.trader.Buttercoin;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class ObjOrderRecord implements Parcelable {
    //Fields
    public String tradeid;
    public Float price;
    public Float volume;
    public String tradeside;
    public String status;
    public String priceCurrency;
    public String quantityCurrency;
    private JSONArray events;
    public ObjTimestamp closedate;

    //Constructors

    public ObjOrderRecord(JSONObject item){
        try {
            this.tradeid = item.getString("orderId");
            this.tradeside = item.getString("side");
            this.price = Float.parseFloat(item.getString("price"));
            this.volume = Float.parseFloat(item.getString("quantity"));
            this.priceCurrency = item.getString("priceCurrency");
            this.quantityCurrency = item.getString("quantityCurrency");
            this.status = item.getString("status");
            this.events = item.getJSONArray("events");
        } catch (JSONException e) {
            Log.e("ObjOrderRecord", "Error parsing JSON");
        }
    }
    public ObjOrderRecord(){
        this.tradeid = "";
        this.tradeside = "";
        this.price = 0.0F;
        this.volume = 0.0F;
        this.priceCurrency = "";
        this.quantityCurrency = "";
        this.status = "";
        this.events = new JSONArray();
    }
    public ObjOrderRecord clone(){
        ObjOrderRecord clone = new ObjOrderRecord();
        clone.tradeid = this.tradeid;
        clone.tradeside = this.tradeside;
        clone.priceCurrency = this.priceCurrency;
        clone.quantityCurrency = this.quantityCurrency;
        return clone;
    }

    public ArrayList<ObjOrderRecord> filledOrders(){
        ArrayList<ObjOrderRecord> templist = new ArrayList<ObjOrderRecord>();
        for (int i = 0; i < events.length(); i++){
            try {
                JSONObject record = events.getJSONObject(i);
                if (record.getString("eventType").equals("filled")){
                    ObjOrderRecord temp = this.clone();
                    if (temp.quantityCurrency.equals("BTC") && temp.tradeside.equals("sell")){
                        temp.volume = Float.parseFloat(record.getString("offered"));
                        Float received = Float.parseFloat(record.getString("received"));
                        temp.price = received / temp.volume;
                        temp.status = "filled";
                        temp.closedate = new ObjTimestamp(record.getString("eventDate"));
                    } else {
                        temp.volume = Float.parseFloat(record.getString("received"));
                        Float offered = Float.parseFloat(record.getString("offered"));
                        temp.price = offered / temp.volume;
                        temp.status = "filled";
                        temp.closedate = new ObjTimestamp(record.getString("eventDate"));
                    }
                    templist.add(temp);
                }
            } catch (JSONException e){
                Log.e("ObjOrderRecord.filledOrders", "Error parsing JSON");
            }
        }
        return templist;
    }

    //Methods
    public String getPrice() {
        return (myFloatString(this.price) + " " + this.priceCurrency);
    }
    public String getVolume() {
        return (myFloatString(this.volume) + " " + this.quantityCurrency);
    }
    public String getQuoteAmount(){
        Float amount = this.price * this.volume;
        return ("(" + myFloatString(amount) + " " + this.priceCurrency+ ")");
    }
    public String myFloatString(Float flt) {
        DecimalFormat df = new DecimalFormat("#.#");
        df.setMaximumFractionDigits(5);
        return df.format(flt);
    }

    public String getOrderString() {
        StringBuilder text = new StringBuilder();
        text.append(getActiveOrderPosition());
        text.append(": ");
        text.append(getVolume());
        text.append(" at ");
        text.append(getPrice());
        text.append("\n");
        String quoteamount = getQuoteAmount();
        quoteamount = quoteamount.replace("(","");
        quoteamount = quoteamount.replace(")","");
        text.append("Total: ");
        text.append(quoteamount);
        return text.toString();
    }

    public String getTimestamp(){
        if (closedate != null){
            return closedate.getStampstring();
        } else {
            return "";
        }
    }

    public String getPosition() {
        if (tradeside.equals("buy")) {return "Bought";}
        else {return "Sold";}
    }
    public String getActiveOrderPosition(){
        if (tradeside.equals("buy")) {return "Buy";}
        else {return "Sell";}
    }

    //Parcelable
    private ObjOrderRecord(Parcel in) {
        this.tradeid = in.readString();
        this.price = in.readFloat();
        this.volume = in.readFloat();
        this.tradeside = in.readString();
        this.status = in.readString();
        this.priceCurrency = in.readString();
        this.quantityCurrency = in.readString();
        this.closedate = in.readParcelable(ObjTimestamp.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tradeid);
        parcel.writeFloat(price);
        parcel.writeFloat(volume);
        parcel.writeString(tradeside);
        parcel.writeString(status);
        parcel.writeString(priceCurrency);
        parcel.writeString(quantityCurrency);
        parcel.writeParcelable(closedate,i);
    }
    public static final Creator<ObjOrderRecord> CREATOR = new Creator<ObjOrderRecord>(){
        @Override
        public ObjOrderRecord createFromParcel(Parcel parcel) {
            return new ObjOrderRecord(parcel);
        }

        @Override
        public ObjOrderRecord[] newArray(int i) {
            return new ObjOrderRecord[i];
        }
    };
}
