package gopherdoc.buttercoin.trader;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListItemOrderBook extends LinearLayout {
    //Fields
    private final TextView price;
    private final TextView amount;
    private final TextView quoteAmount;

    //Constructors
    public ListItemOrderBook(Context context) {
        super(context, null);
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.lvitem_order, this);
        price = (TextView) findViewById(R.id.orderbookitem_price);
        amount = (TextView) findViewById(R.id.orderbookitem_amount);
        quoteAmount = (TextView) findViewById(R.id.orderbookitem_quoteamount);
    }
    //Setters
    public void setPrice(String text){
        price.setText(text);
    }
    public void setAmount(String text){
        amount.setText(text);
    }
    public void setQuote(String text){
        quoteAmount.setText(text);
    }
}
