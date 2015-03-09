package gopherdoc.buttercoin.trader.NavDrawer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import gopherdoc.buttercoin.trader.R;

import static gopherdoc.buttercoin.trader.R.color.FaintGray;
import static gopherdoc.buttercoin.trader.R.color.Gray;

public class NavDrawerListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private int current;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        this.current = 0;
        updateSelected(0);
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item,null);
        }
        TextView title = (TextView) convertView.findViewById(R.id.drawer_list_item);
        title.setText(navDrawerItems.get(position).getTitle());
        if (navDrawerItems.get(position).isSelected()){
            convertView.setBackgroundColor(FaintGray);
        } else {
            convertView.setBackgroundColor(Gray);
        }
        return convertView;
    }
    public void updateSelected(int position){
        navDrawerItems.get(current).setSelected(false);
        navDrawerItems.get(position).setSelected(true);
        this.current = position;
    }
    public int getCurrent(){
        return this.current;
    }
}
