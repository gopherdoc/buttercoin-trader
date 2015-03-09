package gopherdoc.buttercoin.trader.Buttercoin;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class ObjTimestamp implements Comparable<ObjTimestamp>,Parcelable {
    //Fields
    private final Integer timestamp;

    //Constructors
    public ObjTimestamp(String stamp){
        this.timestamp = parseTimestampString(stamp);
    }
    public ObjTimestamp(Integer i){
        this.timestamp = i;
    }

    //Methods
    private Integer parseTimestampString(String stamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date dt = sdf.parse(stamp);
            Long mytime = dt.getTime();
            mytime -= 18000;
            return (int)(mytime/1000L);
        } catch (ParseException e) {
            return 0;
        }
    }

    public String getStampstring(){return FormatTimestamp(); }
    public String FormatTimestamp(){
        Date date = new Date(this.timestamp*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        return sdf.format(date);
    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") ObjTimestamp t) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        if (this.timestamp.equals(t.timestamp)) return EQUAL;
        if (this.timestamp < t.timestamp) return BEFORE;
        if (this.timestamp > t.timestamp) return AFTER; else { return 0; }
    }

    //Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(timestamp);

    }
    public ObjTimestamp(Parcel in) {
        this.timestamp = in.readInt();
    }
    public static final Creator<ObjTimestamp> CREATOR = new Creator<ObjTimestamp>() {
        @Override
        public ObjTimestamp createFromParcel(Parcel parcel) {
            return new ObjTimestamp(parcel);
        }

        @Override
        public ObjTimestamp[] newArray(int i) {
            return new ObjTimestamp[i];
        }
    };
}
