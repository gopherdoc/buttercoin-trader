package gopherdoc.buttercoin.trader;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListItemHistory extends LinearLayout {
    private final TextView position;
    private final TextView date;
    private final TextView amount;
    private final TextView price;

    //Constructor
    public ListItemHistory(Context context) {
        super(context, null);
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.lvitem_history, this);
        position = (TextView)findViewById(R.id.tradehistory_position);
        date = (TextView)findViewById(R.id.tradehistory_date);
        amount = (TextView)findViewById(R.id.tradehistory_amount);
        price = (TextView) findViewById(R.id.tradehistory_price);
    }

    public void setPosition(String pos){position.setText(pos);}
    public void setDate(String mydate){ date.setText(mydate);}
    public void setAmount(String amt){ amount.setText(amt);}
    public void setPrice(String cost){price.setText(cost);}
}
