package gopherdoc.buttercoin.trader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import gopherdoc.buttercoin.trader.Buttercoin.ObjCollectionHistoryOrderRecord;
import gopherdoc.buttercoin.trader.Buttercoin.ObjOrderRecord;


public class PagerHistorySection extends Fragment {
    public static PagerHistorySection init() {
        return new PagerHistorySection();
    }
    private ArrayList<ObjOrderRecord> collection;
    private MyAdapter myAdapter;
    private ListView lv;
    public PagerHistorySection(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pager_history, null);
        assert rootView != null;
        lv = (ListView) rootView.findViewById(R.id.lv_tradehistory);
        Bundle args = getArguments();
        if (args != null) {
            ObjCollectionHistoryOrderRecord myTrades = args.getParcelable("TradeList");
            collection = myTrades.collection;
        } else {
            collection = new ArrayList<ObjOrderRecord>();
        }
        if (savedInstanceState != null){
            if (savedInstanceState.getParcelableArrayList("history") != null){
                collection = savedInstanceState.getParcelableArrayList("history");
            }
        }
        myAdapter = new MyAdapter(getActivity(), collection);
        lv.setAdapter(myAdapter);
        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("history",collection);
    }

    private class MyAdapter extends ArrayAdapter<ObjOrderRecord> {
        private final Context context;
        private final ArrayList<ObjOrderRecord> trades;

        public MyAdapter(Context context, ArrayList<ObjOrderRecord> trades) {
            super(context, R.layout.lvitem_history, trades);
            this.context = context;
            this.trades = trades;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListItemHistory rowView = new ListItemHistory(context);
            rowView.setDate(trades.get(position).getTimestamp());
            rowView.setPosition(trades.get(position).getPosition());
            rowView.setPrice(trades.get(position).getPrice());
            rowView.setAmount(trades.get(position).getVolume());
            return rowView;
        }
    }
    public void updateHistory(ObjCollectionHistoryOrderRecord tradeHistory){
        if (myAdapter != null) {
            collection = tradeHistory.collection;
            myAdapter.clear();
            for (ObjOrderRecord order : tradeHistory.collection) {
                myAdapter.add(order);
            }
            myAdapter.notifyDataSetChanged();
        }
    }
    public void clearHistory(){
        updateHistory(new ObjCollectionHistoryOrderRecord());
    }

}
