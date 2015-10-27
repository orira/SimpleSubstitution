package nz.co.nativemobile.simplesubstitution.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import nz.co.nativemobile.simplesubstitution.MainActivity;
import nz.co.nativemobile.simplesubstitution.R;
import nz.co.nativemobile.simplesubstitution.domain.Player;
import nz.co.nativemobile.simplesubstitution.util.AnimationUtil;
import nz.co.nativemobile.simplesubstitution.util.GalleryUtil;
import nz.co.nativemobile.simplesubstitution.util.KeyboardUtil;
import rx.functions.Action1;

/**
 * Created by wadereweti on 9/24/15.
 */
public class PlayerCreateFragment extends BaseFragment {

    private static final String TAG = PlayerCreateFragment.class.getSimpleName();

    private Uri mSelectedImageUri;
    private DefaultActionMode mActionModeCallBack;
    private String mCurrentPlayerName;
    private int mTeamId;
    private int mRequestCode;
    private Intent mData;

    @Bind(R.id.edit_text_create_player_name) EditText mInputPlayerTeam;
    @Bind(R.id.image_view_create_player_placeholder) ImageView mPlayerImageView;
    @Bind(R.id.container_create_player_input_detail) RelativeLayout mInputDetailView;
    @Bind(R.id.label_create_player) TextView mSavingLabel;
    @Bind(R.id.container_create_player_creating) RelativeLayout mCreatingView;
    @Bind(R.id.image_view_player_created) RoundedImageView mPlayerCreatedImageView;
    @Bind(R.id.progress_bar_create_player) ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTeamId = getArguments().getInt(MainActivity.TEAM_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_create, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setTitle(R.string.title_create_player);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mParentActivity.getSupportFragmentManager().popBackStack();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.image_view_create_player_placeholder)
    public void onPlayerImageClicked() {
        startActivityForResult(GalleryUtil.getImageChooserIntent(mParentActivity), GalleryUtil.SELECT_PICTURE_REQUEST_CODE);
    }

    @OnTextChanged(R.id.edit_text_create_player_name)
    public void onPlayerNameChanged(CharSequence input) {
        if (input.length() > 0) {
            boolean startActionMode = false;
            if (mActionModeCallBack == null) {
                mActionModeCallBack = new DefaultActionMode();
                mActionModeCallBack.setEnabled(true);
                startActionMode = true;
            } else if (!mActionModeCallBack.isEnabled()) {
                mActionModeCallBack.setEnabled(true);
                startActionMode = true;
            }

            if (startActionMode) {
                mParentActivity.startActionMode(mActionModeCallBack);
            }
        } else if (mActionModeCallBack != null && input.length() == 0) {
            mActionModeCallBack.setEnabled(false);
            mParentActivity.startActionMode(mActionModeCallBack);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final int RESULT_OK = -1;
        if (resultCode == RESULT_OK) {
            mSelectedImageUri = GalleryUtil.getImageUriSyncronous(requestCode, data);
            Picasso.with(mParentActivity)
                    .load(mSelectedImageUri)
                    .fit()
                    .centerCrop()
                    .into(mPlayerImageView);
            mRequestCode = requestCode;
            mData = data;
        }
    }

    private class DefaultActionMode implements ActionMode.Callback {

        boolean mEnabled;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            boolean create = false;

            if (mEnabled) {
                mode.getMenuInflater().inflate(R.menu.contextual_menu_create_team, menu);
                create = true;
            }

            return create;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_create_team) {
                createTeam();
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (mInputPlayerTeam.getText().toString().length() > 0) {
                mCurrentPlayerName = mInputPlayerTeam.getText().toString();
                mInputPlayerTeam.setText("");
                mSavingLabel.requestFocus();
            }
        }

        public void setEnabled(boolean enabled) {
            mEnabled = enabled;
        }

        public boolean isEnabled() {
            return mEnabled;
        }
    }

    //*** ActionMode.Callback methods ***//
    private void createTeam() {
        mInputDetailView.animate().alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (mInputDetailView != null) {
                    mInputDetailView.setVisibility(View.INVISIBLE);
                }
            }
        });

        KeyboardUtil.dimissKeyboard(mInputPlayerTeam);
        mCreatingView.animate().alpha(1);
        mActionModeCallBack.setEnabled(false);
        mParentActivity.startActionMode(mActionModeCallBack);

        Player.create(mCurrentPlayerName, mSelectedImageUri, mTeamId, mRequestCode, mData);

        runCompletionAnimation();
    }

    private void runCompletionAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressBar.animate().alpha(0);
                mSavingLabel.animate().alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mPlayerCreatedImageView.animate().setDuration(AnimationUtil.ANIMATION_LENGTH_MEDIUM).scaleX(1).scaleY(1).rotationX(1).withStartAction(new Runnable() {
                            @Override
                            public void run() {
                                mPlayerCreatedImageView.startAnimation(AnimationUtil.getSpinRotation());
                                mSavingLabel.setTranslationX(AnimationUtil.LABEL_TRANSLATION_X_OFFSET);
                                mSavingLabel.setText(R.string.message_player_created);
                                mSavingLabel.animate().setDuration(AnimationUtil.ANIMATION_LENGTH_MEDIUM).alpha(1).translationX(1);
                            }
                        }).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                mPlayerCreatedImageView.setAnimation(null);
                                mNavigation.onPlayerCreated();
                                //put this back in after video
                                //mNavigation.onTeamCreated();
                            }
                        });
                    }
                });
            }
        }, 1000);
    }
}
