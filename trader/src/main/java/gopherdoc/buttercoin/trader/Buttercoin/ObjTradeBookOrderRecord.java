package gopherdoc.buttercoin.trader.Buttercoin;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


public class ObjTradeBookOrderRecord implements Parcelable {
    //Fields
    private String priceCurrency;
    private String quantityCurrency;
    private Float price;
    private Float volume;
    private String tradeside;


    //Constructors

    public ObjTradeBookOrderRecord(String tradeside, String price_currency, String quantity_currency, JSONObject item){
        try {
            this.tradeside = tradeside;
            this.price = Float.parseFloat(item.getString("price"));
            this.volume = Float.parseFloat(item.getString("quantity"));
            this.priceCurrency = price_currency;
            this.quantityCurrency = quantity_currency;
        } catch (JSONException e) {
            Log.e("ObjTradeBookOrderRecord", "Error parsing JSON");
        }
    }

    //Methods
    public String getPrice() {
        return (myQuoteString(this.price) + " " + priceCurrency);
    }
    public String getAmount() {
        return (myBaseString(this.volume) + " " + quantityCurrency);
    }
    public String getQuoteAmount(){
        Float amount = this.price * this.volume;
        return ("(" + myTotalString(amount) + " " + priceCurrency+ ")");
    }
    public String getOrderString() {
        StringBuilder text = new StringBuilder();
        text.append(getAmount());
        text.append(" at ");
        text.append(getPrice());
        return text.toString();
    }
    public String myTotalString(Float flt){
        DecimalFormat df = new DecimalFormat("#.#");
        df.setMaximumFractionDigits(3);
        return df.format(flt);
    }
    public String myQuoteString(Float flt) {
        DecimalFormat df = new DecimalFormat("#.#");
        int decimals = 6;
        df.setMaximumFractionDigits(decimals);
        return df.format(flt);
    }
    public String myBaseString(Float flt) {
        DecimalFormat df = new DecimalFormat("#.#");
        int decimals = 5;
        df.setMaximumFractionDigits(decimals);
        return df.format(flt);
    }

    //Parcelable
    private ObjTradeBookOrderRecord(Parcel in) {
        this.price = in.readFloat();
        this.volume = in.readFloat();
        this.tradeside = in.readString();
        this.priceCurrency = in.readString();
        this.quantityCurrency = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(price);
        parcel.writeFloat(volume);
        parcel.writeString(tradeside);
        parcel.writeString(priceCurrency);
        parcel.writeString(quantityCurrency);
    }
    public static final Creator<ObjTradeBookOrderRecord> CREATOR = new Creator<ObjTradeBookOrderRecord>(){
        @Override
        public ObjTradeBookOrderRecord createFromParcel(Parcel parcel) {
            return new ObjTradeBookOrderRecord(parcel);
        }

        @Override
        public ObjTradeBookOrderRecord[] newArray(int i) {
            return new ObjTradeBookOrderRecord[i];
        }
    };
}
