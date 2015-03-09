package gopherdoc.buttercoin.trader.Buttercoin;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ObjCollectionBalanceCoin implements Parcelable{

    public ObjBalanceCoin BTC;
    public ObjBalanceCoin USD;

    public ObjCollectionBalanceCoin(){
        BTC = new ObjBalanceCoin("Bitcoin", "BTC");
        USD = new ObjBalanceCoin("Dollars", "USD");
    }
    public ObjCollectionBalanceCoin(JSONObject jObj) {
        BTC = new ObjBalanceCoin("Bitcoin", "BTC");
        USD = new ObjBalanceCoin("Dollars", "USD");
        try {
            BTC.setBalance((float)jObj.getDouble("BTC"));
            USD.setBalance((float)jObj.getDouble("USD"));
        } catch (JSONException e) {
            Log.e("ObjCollectionBalanceCoin", "Error parsing JSON");
        }
    }


    //Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeParcelable(BTC,i);
        out.writeParcelable(USD,i);
    }
    public ObjCollectionBalanceCoin(Parcel in){
        this.BTC = in.readParcelable(ObjBalanceCoin.class.getClassLoader());
        this.USD = in.readParcelable(ObjBalanceCoin.class.getClassLoader());
    }

    public static final Parcelable.Creator<ObjCollectionBalanceCoin> CREATOR = new Parcelable.Creator<ObjCollectionBalanceCoin>() {
        @Override
        public ObjCollectionBalanceCoin createFromParcel(Parcel parcel) {
            return new ObjCollectionBalanceCoin(parcel);
        }

        @Override
        public ObjCollectionBalanceCoin[] newArray(int i) {
            return new ObjCollectionBalanceCoin[i];
        }
    };
}
