package gopherdoc.buttercoin.trader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentSplashInvalidKeys extends Fragment {
    private OnInvalidKeyBtnClickListener listener;
    private TextView settings;
    private TextView signup;

    static FragmentSplashInvalidKeys init() { return new FragmentSplashInvalidKeys(); }

    public interface OnInvalidKeyBtnClickListener { public void onBtnClicked(String action);}

    public FragmentSplashInvalidKeys(){

    }

    //Methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_splash_invalidkeys, null);
        settings = (TextView)rootView.findViewById(R.id.splash_btnsettings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBtnClicked("Settings");
            }
        });
        signup = (TextView)rootView.findViewById(R.id.splash_btnsignup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBtnClicked("Signup");
            }
        });
        return rootView;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnInvalidKeyBtnClickListener) {
            listener = (OnInvalidKeyBtnClickListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement FragmentSplashInvalidKeys.OnInvalidKeyBtnClickListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }
}
