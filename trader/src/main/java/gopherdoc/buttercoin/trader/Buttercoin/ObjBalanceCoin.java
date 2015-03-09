package gopherdoc.buttercoin.trader.Buttercoin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

import gopherdoc.buttercoin.trader.QR.Contents;
import gopherdoc.buttercoin.trader.QR.QRCodeEncoder;

public class ObjBalanceCoin implements Parcelable {
    public String name;
    public String symbol;
    public Float balance;
    public String address;


    //Constructors
    public ObjBalanceCoin(String name, String sym){
        this.name = name;
        this.symbol = sym.toUpperCase();
        this.balance = 0.0F;
        this.address = "";

    }
    public ObjBalanceCoin(String address){
        this.name = "Bitcoin";
        this.symbol = "BTC";
        this.balance = 0.0F;
        this.address = address;
    }
    public ObjBalanceCoin(){
        this.name = "Bitcoin";
        this.symbol = "BTC";
        this.balance = 0.0F;
        this.address = "";
    }

    //Getters
    public Float getBalance(){ return balance;}
    public String getBalanceString() {
        DecimalFormat df = new DecimalFormat("#.#");
        int decimals = 4;
        df.setMaximumFractionDigits(decimals);
        return df.format(balance);
    }

    //Setters
    public void setAddress(String addr){
        this.address = (addr == null) ? "" : addr;
    }
    //Methods
    public Bitmap getQR(){
        Bitmap qrcode = null;
            if (!address.equals("")) {
                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(address,
                        null,
                        Contents.Type.TEXT,
                        BarcodeFormat.QR_CODE.toString(),
                        150);
                try {
                    Bitmap original = qrCodeEncoder.encodeAsBitmap();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    original.compress(Bitmap.CompressFormat.PNG, 100, out);
                    qrcode = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

                } catch (WriterException e) {
                    Log.e("ObjBalanceCoin", "Error writing QR bitmap");
                }
            }
        return qrcode;

    }
    public void setBalance(Float bal){ balance = bal;}

    //Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(name);
        out.writeString(symbol);
        out.writeFloat(balance);
        out.writeString(address);

    }
    public ObjBalanceCoin(Parcel in){
        name = in.readString();
        symbol = in.readString();
        balance = in.readFloat();
        address = in.readString();
    }

    public static final Creator<ObjBalanceCoin> CREATOR = new Creator<ObjBalanceCoin>() {
        @Override
        public ObjBalanceCoin createFromParcel(Parcel parcel) {
            return new ObjBalanceCoin(parcel);
        }

        @Override
        public ObjBalanceCoin[] newArray(int i) {
            return new ObjBalanceCoin[i];
        }
    };

}
