package gopherdoc.buttercoin.trader.Buttercoin;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class ListFragmentAdapter extends FragmentPagerAdapter{
    private List<Fragment> mFragments;

    public ListFragmentAdapter(FragmentManager fm, List<Fragment> fragments ){
        super(fm);
        mFragments = fragments;
    }
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
