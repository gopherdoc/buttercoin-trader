package gopherdoc.buttercoin.trader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gopherdoc.buttercoin.trader.Buttercoin.ObjTicker;

public class FragmentTickerSection extends Fragment {
    private TextView tv;
    private TextView counter;
    static FragmentTickerSection init() { return new FragmentTickerSection(); }

    //Constructor
    public FragmentTickerSection(){

    }
    //Methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ticker, null);
        assert rootView != null;

        tv = (TextView) rootView.findViewById(R.id.tickertext);
        counter = (TextView) rootView.findViewById(R.id.ticker_counter);
        Bundle args = getArguments();
        if (args != null) {
            String tickertext = args.getString("TickerText");
            if (tickertext != null) {
                tv.setText(args.getString("TickerText"));
            } else { tv.setText("");}
        } else {
            tv.setText("");
        }


        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }
    @Override
    public void onDetach(){
        super.onDetach();
    }

    public void updateTicker(ObjTicker ticker){
        if (tv != null)
        {
            tv.setText(ticker.print());
        }
    }
    public void updateCounter(String value) {
        if (counter != null){
            counter.setText(value);
        }
    }

}
