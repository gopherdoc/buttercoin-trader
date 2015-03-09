package gopherdoc.buttercoin.trader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentSplashValidKeys extends Fragment {

    static FragmentSplashValidKeys init() { return new FragmentSplashValidKeys(); }

    public FragmentSplashValidKeys(){

    }

    //Methods
    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_splash_validkeys, null);
        return rootView;
    }
}
