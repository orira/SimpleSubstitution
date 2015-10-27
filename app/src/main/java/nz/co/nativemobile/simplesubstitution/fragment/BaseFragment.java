package nz.co.nativemobile.simplesubstitution.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import nz.co.nativemobile.simplesubstitution.MainActivity;
import nz.co.nativemobile.simplesubstitution.Navigation;
import nz.co.nativemobile.simplesubstitution.SimpleSubstitutionApplication;
import nz.co.nativemobile.simplesubstitution.view.DeviceDisplay;

/**
 * Created by wadereweti on 9/29/15.
 */
public class BaseFragment extends Fragment implements DeviceDisplay {

    private static final String TAG = BaseFragment.class.getSimpleName();

    protected Navigation mNavigation;
    protected MainActivity mParentActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SimpleSubstitutionApplication.getBus().register(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof MainActivity) {
            mParentActivity = (MainActivity) activity;
        }

        if (activity instanceof Navigation) {
            mNavigation = (Navigation) activity;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SimpleSubstitutionApplication.getBus().unregister(this);
    }

    protected void setTitle(int titleId) {
        if (mParentActivity.getSupportActionBar() != null) {
            mParentActivity.getSupportActionBar().setTitle(getString(titleId));
        }
    }

    @Override
    public int getDeviceWidth() {
        Display display = mParentActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;

        Point point = new Point();
        display.getSize(point);

        return point.x;
    }

    @Override
    public float getPixelFromDp(int dp) {
        float density  = getResources().getDisplayMetrics().density;
        return dp / density;
    }
}
