package nz.co.nativemobile.simplesubstitution.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import nz.co.nativemobile.simplesubstitution.SimpleSubstitutionApplication;

/**
 * Created by wadereweti on 9/30/15.
 */
public class KeyboardUtil {
    public static void dimissKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) SimpleSubstitutionApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
