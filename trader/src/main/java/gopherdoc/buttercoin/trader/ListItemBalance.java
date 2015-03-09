package gopherdoc.buttercoin.trader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import gopherdoc.buttercoin.trader.Buttercoin.ObjBalanceCoin;

public class ListItemBalance extends LinearLayout {
    private ImageView iv;
    private TextView symbol;
    private TextView balance;
    private TextView deposit;
    private ObjBalanceCoin myCoin;
    private OnBalanceItemSelectedListener listener;

    public ListItemBalance(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.lvitem_balances, this);
        balance = (TextView)findViewById(R.id.coinbalance);
        symbol = (TextView)findViewById(R.id.coinid);
        iv = (ImageView)findViewById(R.id.coinidimg);
        deposit = (TextView)findViewById(R.id.coindeposit);
        deposit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bmp = null;
                        if (!myCoin.symbol.equals("USD")) {
                            bmp = myCoin.getQR();
                        }
                        listener.BalanceItemSelected(myCoin, bmp);
                    }
                };
                runnable.run();

            }
        });
    }

    public void setBalance(String bal){
        balance.setText(bal);
    }

    public interface OnBalanceItemSelectedListener {
        public void BalanceItemSelected(ObjBalanceCoin coin, Bitmap bmp); }
}
