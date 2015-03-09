package gopherdoc.buttercoin.trader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import gopherdoc.buttercoin.trader.Buttercoin.ObjOrderRecord;

public class ListItemActiveorder extends LinearLayout {
    private Integer position;
    private TextView tv;
    private TextView btn;
    private ObjOrderRecord order;
    private OnCancelItemSelectedListener listener;

    public ListItemActiveorder(final Context context) {
        super(context, null);
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.lvitem_activeorder, this);
        tv = (TextView) findViewById(R.id.tv_activeorder_item);
        tv.setSingleLine(false);
        btn = (TextView) findViewById(R.id.btn_activeorder_cancel);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.CancelItemSelected(position,order);
            }
        });
    }

    public void newOrder(PagerTradeSection pageTrade, ObjOrderRecord newOrder, Integer pos){
        listener = pageTrade;
        order = newOrder;
        position = pos;
        tv.setText(newOrder.getOrderString());
    }
    public interface OnCancelItemSelectedListener {
        public void CancelItemSelected(Integer pos, ObjOrderRecord orderRecord);
    }
}
