package gopherdoc.buttercoin.trader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import gopherdoc.buttercoin.trader.Buttercoin.ObjBalanceCoin;

public class PagerBalanceSection extends Fragment {
    private ObjBalanceCoin bitcoin;
    private TextView btc_balance;
    private TextView btc_address;
    private ImageView addr_qr;
    static PagerBalanceSection init() {return new PagerBalanceSection();}
    public PagerBalanceSection(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pager_deposit,null);
        assert rootView != null;
        Bundle args = getArguments();
        if (args != null) {
            bitcoin = args.getParcelable("Bitcoin");
        } else {
            bitcoin = new ObjBalanceCoin();
        }
        if (savedInstanceState != null){
            if (savedInstanceState.getParcelable("bitcoin") != null){
                bitcoin = savedInstanceState.getParcelable("bitcoin");
            }
        }
        btc_balance = (TextView) rootView.findViewById(R.id.deposit_curr_balance);
        btc_address = (TextView) rootView.findViewById(R.id.str_depositaddress);
        addr_qr = (ImageView) rootView.findViewById(R.id.img_deposit_qr);
        updateScreen();
        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelable("bitcoin", bitcoin);
    }
    private void updateScreen(){
        if (bitcoin.address.equals("") || bitcoin.address == null) {
            btc_address.setText("Retrieving address...");
            addr_qr.setVisibility(View.INVISIBLE);
        } else {
            addr_qr.setVisibility(View.VISIBLE);
            addr_qr.setImageBitmap(bitcoin.getQR());
            btc_address.setText(bitcoin.address);
        }
        btc_balance.setText(bitcoin.getBalanceString());
    }
    public void updateCoinInfo(ObjBalanceCoin coin){
        bitcoin.setBalance(coin.getBalance());
        btc_balance.setText(coin.getBalanceString());
        if (!bitcoin.address.equals(coin.address)) {
            bitcoin.setAddress(coin.address);
            updateScreen();
        }
    }

}
