package nz.co.nativemobile.simplesubstitution.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import nz.co.nativemobile.simplesubstitution.R;
import nz.co.nativemobile.simplesubstitution.SimpleSubstitutionApplication;
import nz.co.nativemobile.simplesubstitution.domain.Team;
import nz.co.nativemobile.simplesubstitution.event.ImageUpdatedEvent;
import nz.co.nativemobile.simplesubstitution.util.AnimationUtil;
import nz.co.nativemobile.simplesubstitution.util.GalleryUtil;
import nz.co.nativemobile.simplesubstitution.util.KeyboardUtil;

/**
 * Created by wadereweti on 9/24/15.
 */
public class TeamCreateFragment extends BaseFragment {

    public static final int DEFAULT_VALUE = -1;
    private Uri mSelectedImageUri;
    private DefaultActionMode mActionModeCallBack;
    private String mCurrentTeamName = null;
    private int mTeamId = DEFAULT_VALUE;
    private int mRequestCode;
    private Intent mData;
    private boolean mCustomImage = false;

    @Bind(R.id.edit_text_create_team_name) EditText mInputCreateTeam;
    @Bind(R.id.image_view_create_team_placeholder) ImageView mTeamImageView;
    @Bind(R.id.container_create_team_input_detail) RelativeLayout mInputDetailView;
    @Bind(R.id.container_create_team_creating) RelativeLayout mCreatingView;
    @Bind(R.id.label_create_team) TextView mSavingLabel;
    @Bind(R.id.image_view_team_created) RoundedImageView mTeamCreatedImageView;
    @Bind(R.id.progress_bar_create_team) ProgressBar mProgressBar;
    @Bind(R.id.container_create_player) RelativeLayout mCreatePlayerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.team_create, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getArguments() != null) {
            mTeamId = getArguments().getInt(Team.TEAM_ID);
            Team team = Team.findTeamById(mTeamId);

            if (team != null && team.imagePath != null && !mCustomImage) {
                Picasso.with(mParentActivity)
                        .load(team.imagePath)
                        .placeholder(R.drawable.placeholder_team)
                        .fit()
                        .centerCrop()
                        .into(mTeamImageView);
                mInputCreateTeam.setText(team.name);
            }

            mCustomImage = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int titleId = getArguments() != null ? R.string.title_edit_team : R.string.title_create_team;
        setTitle(titleId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mTeamId = DEFAULT_VALUE;
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

    @OnClick(R.id.image_view_create_team_placeholder)
    public void onTeamImageClicked() {
        startActivityForResult(GalleryUtil.getImageChooserIntent(mParentActivity), GalleryUtil.SELECT_PICTURE_REQUEST_CODE);
    }

    @OnTextChanged(R.id.edit_text_create_team_name)
    public void onTeamNameChanged(CharSequence input) {
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
            mCustomImage = true;
            mSelectedImageUri = GalleryUtil.getImageUriSyncronous(requestCode, data);
            Picasso.with(mParentActivity)
                    .load(mSelectedImageUri)
                    .placeholder(R.drawable.placeholder_team)
                    .fit()
                    .centerCrop()
                    .into(mTeamImageView);

            mRequestCode = requestCode;
            mData = data;
            /*GalleryUtil.getImageUri(requestCode, data).subscribe(new Action1<Uri>() {
                @Override
                public void call(Uri uri) {
                    mSelectedImageUri = uri;
                }
            });*/
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
                if (mTeamId != DEFAULT_VALUE) {
                    editTeam();
                } else {
                    createTeam();
                }
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (mInputCreateTeam.getText().toString().length() > 0) {
                mCurrentTeamName = mInputCreateTeam.getText().toString();
                mInputCreateTeam.setText("");
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

    private void createTeam() {
        runInitialAnimation(false);
        mTeamId = (int) Team.create(mCurrentTeamName, mSelectedImageUri, mRequestCode, mData).longValue();
        runCompletionAnimation(true);
    }

    private void editTeam() {
        runInitialAnimation(true);
        Team.updateTeam(mTeamId, mCurrentTeamName, mSelectedImageUri, mRequestCode, mData);
        runCompletionAnimation(true);
    }

    private void runInitialAnimation(boolean edited) {
        final String savingStateMessage = edited ? getString(R.string.label_update_team) : getString(R.string.label_create_team);

        mInputDetailView.animate().alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (mInputDetailView != null) {
                    mInputDetailView.setVisibility(View.INVISIBLE);
                }
            }
        });

        KeyboardUtil.dimissKeyboard(mInputCreateTeam);
        mSavingLabel.setText(savingStateMessage);
        mCreatingView.animate().alpha(1);
        mActionModeCallBack.setEnabled(false);
        mParentActivity.startActionMode(mActionModeCallBack);
    }

    private void runCompletionAnimation(final boolean edited) {
        final String teamStateMessage = edited ? getString(R.string.message_team_updated) : getString(R.string.message_team_created);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressBar.animate().alpha(0);
                mSavingLabel.animate().alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mTeamCreatedImageView.animate().setDuration(AnimationUtil.ANIMATION_LENGTH_MEDIUM).scaleX(1).scaleY(1).rotationX(1).withStartAction(new Runnable() {
                            @Override
                            public void run() {
                                mTeamCreatedImageView.startAnimation(AnimationUtil.getSpinRotation());
                                mSavingLabel.setTranslationX(AnimationUtil.LABEL_TRANSLATION_X_OFFSET);
                                mSavingLabel.setText(teamStateMessage);
                                mSavingLabel.animate().setDuration(AnimationUtil.ANIMATION_LENGTH_MEDIUM).alpha(1).translationX(1);
                            }
                        }).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                mTeamCreatedImageView.setAnimation(null);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        SimpleSubstitutionApplication.getBus().post(new ImageUpdatedEvent());
                                        if (!edited) {
                                            createPlayerPrompt();
                                        } else {
                                            navigateToTeamView();
                                        }
                                    }
                                }, 1000);

                                //put this back in after video
                                //mNavigation.onTeamCreated();
                            }
                        });
                    }
                });
            }
        }, 1000);
    }

    private void createPlayerPrompt() {
        mCreatingView.animate().alpha(0);
        mCreatePlayerView.animate().alpha(1);
    }

    private void navigateToTeamView() {
        mNavigation.onTeamCreated();
    }

    @OnClick(R.id.floating_action_button_create_player)
    public void onCreatePlayerClicked() {
        mNavigation.onCreatePlayerClicked(mTeamId);
    }
}
