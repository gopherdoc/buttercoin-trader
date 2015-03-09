package gopherdoc.buttercoin.trader;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ListItemSettings extends LinearLayout {
    private final TextView mHead;
    private final TextView mSub;

    public ListItemSettings(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.lvitem_settings, this);
        mSub = (TextView) findViewById(R.id.si_sub);
        mHead = (TextView) findViewById(R.id.si_head);
    }

    public void setSub(String text){
        mSub.setText(text);
    }
    public void setHead(String text){
        mHead.setText(text);
    }
    public void hideSub() {setSub("<Hidden>"); }
}
