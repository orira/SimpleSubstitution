package nz.co.nativemobile.simplesubstitution;

import com.activeandroid.app.Application;
import com.squareup.otto.Bus;

/**
 * Created by wadereweti on 9/29/15.
 */
public class SimpleSubstitutionApplication extends Application {

    private static SimpleSubstitutionApplication mInstance;
    private static Bus mBus;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mInstance == null) {
            mInstance = this;
        }
    }

    public static SimpleSubstitutionApplication getInstance() {
        return mInstance;
    }

    public static Bus getBus() {
        if (mBus == null) {
            mBus = new Bus();
        }

        return mBus;
    }
}
