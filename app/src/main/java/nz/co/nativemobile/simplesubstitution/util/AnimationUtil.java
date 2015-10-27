package nz.co.nativemobile.simplesubstitution.util;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

/**
 * Created by wadereweti on 10/1/15.
 */
public class AnimationUtil {
    public static final int ANIMATION_LENGTH_MEDIUM = 450;

    public static final int LABEL_TRANSLATION_X_OFFSET = 150;

    public static RotateAnimation getSpinRotation() {
        final RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setDuration(ANIMATION_LENGTH_MEDIUM);

        return rotateAnimation;
    }
}
