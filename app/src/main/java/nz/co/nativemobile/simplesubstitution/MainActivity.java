package nz.co.nativemobile.simplesubstitution;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;

import nz.co.nativemobile.simplesubstitution.fragment.AllTeamsFragment;
import nz.co.nativemobile.simplesubstitution.fragment.BaseFragment;
import nz.co.nativemobile.simplesubstitution.fragment.PlayerCreateFragment;
import nz.co.nativemobile.simplesubstitution.fragment.TeamCreateFragment;
import nz.co.nativemobile.simplesubstitution.fragment.TeamViewFragment;
import nz.co.nativemobile.simplesubstitution.view.TeamView;

public class MainActivity extends AppCompatActivity implements Navigation, FragmentManager.OnBackStackChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String TEAM_ID = "team_id";

    private AllTeamsFragment mAllTeamsFragment;
    private TeamCreateFragment mTeamCreateFragment;
    private TeamViewFragment mTeamViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mAllTeamsFragment = new AllTeamsFragment();
        mTeamCreateFragment = new TeamCreateFragment();

        FragmentTransaction ft = getLocalFragmentManager().beginTransaction();
        ft.add(R.id.main_container, mAllTeamsFragment, AllTeamsFragment.class.getSimpleName());
        ft.commit();
    }

    private FragmentManager getLocalFragmentManager() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        return fragmentManager;
    }

    @Override
    public void onCreateTeamSelected() {
        // Inflate transitions to apply
        /*Transition changeTransform = TransitionInflater.from(this).inflateTransition(android.R.transition.move);
        Transition explodeTransform = TransitionInflater.from(this).inflateTransition(android.R.transition.move);*/

        // Setup exit transition on first fragment
        /*mAllTeamsFragment.setSharedElementReturnTransition(changeTransform);
        mAllTeamsFragment.setExitTransition(explodeTransform);*/

        // Setup enter transition on second fragment
        /*mTeamCreateFragment.setSharedElementEnterTransition(changeTransform);
        mTeamCreateFragment.setEnterTransition(explodeTransform);*/

        // Find the shared element (in Fragment A)
        //FloatingActionButton floatingActionButton = ButterKnife.findById(mAllTeamsFragment.getView(), R.id.floating_action_button_main);

        /*FragmentTransaction ft = getFragmentManager().beginTransaction()
                .add(R.id.main_container, mTeamCreateFragment)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .addToBackStack(mTeamCreateFragment.getClass().getSimpleName())
                .addSharedElement(floatingActionButton, "floatingActionButton");
        ft.commit();*/

        navigateToTeamFragment();
    }

    @Override
    public void onEditTeamSelected(Bundle args) {
        mTeamCreateFragment.setArguments(args);
        navigateToTeamFragment();
    }

    private void navigateToTeamFragment() {
        FragmentTransaction ft = getFragmentTransaction();
        ft.setCustomAnimations(R.animator.slide_up, R.animator.scale_down, R.animator.scale_up, R.animator.slide_down);
        ft.add(R.id.main_container, mTeamCreateFragment, mTeamCreateFragment.getClass().getSimpleName());
        ft.addToBackStack(mTeamCreateFragment.getClass().getSimpleName());
        ft.commit();
    }

    @Override
    public void onTeamCreated() {
        FragmentManager fragmentManager = getLocalFragmentManager();
        fragmentManager.popBackStack();

        TeamView teamView = (TeamView) fragmentManager.findFragmentByTag(AllTeamsFragment.class.getSimpleName());
        teamView.updateTeams();
    }

    @Override
    public void onTeamSelected(int teamId) {
        Transition transition = TransitionInflater.from(this).inflateTransition(android.R.transition.move);
        mAllTeamsFragment.setSharedElementReturnTransition(transition);
        mAllTeamsFragment.setExitTransition(transition);

        if (mTeamViewFragment == null) {
            Bundle args = new Bundle();
            args.putInt(TEAM_ID, teamId);

            mTeamViewFragment = new TeamViewFragment();
            mTeamViewFragment.setArguments(args);
        }

        FragmentTransaction ft = getFragmentTransaction();
        ft.setCustomAnimations(R.animator.slide_up, R.animator.scale_down, R.animator.scale_up, R.animator.slide_down);
        ft.add(R.id.main_container, mTeamViewFragment, mTeamViewFragment.getClass().getSimpleName());
        ft.addToBackStack(mTeamViewFragment.getClass().getSimpleName());
        ft.commit();
    }

    @Override
    public void onCreatePlayerClicked(int teamId) {
        Bundle args = new Bundle();
        args.putInt(TEAM_ID, teamId);
        PlayerCreateFragment playerCreateFragment = new PlayerCreateFragment();
        playerCreateFragment.setArguments(args);

        FragmentTransaction ft = getFragmentTransaction();
        ft.setCustomAnimations(R.animator.slide_up, R.animator.scale_down, R.animator.scale_up, R.animator.slide_down);
        ft.add(R.id.main_container, playerCreateFragment, playerCreateFragment.getClass().getSimpleName());
        ft.addToBackStack(playerCreateFragment.getClass().getSimpleName());
        ft.commit();
    }

    @Override
    public void onPlayerCreated() {
        FragmentManager fragmentManager = getLocalFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.popBackStack();
    }

    @SuppressLint("CommitTransaction")
    private FragmentTransaction getFragmentTransaction() {
        return getLocalFragmentManager().beginTransaction();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getLocalFragmentManager();

        if (fragmentManager.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            fragmentManager.popBackStack();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackStackChanged() {
        boolean eligibleForBack = getFragmentManager().getBackStackEntryCount() > 0;
        if (getSupportActionBar() != null) {
            Log.e(TAG, "enabling home is up");
            getSupportActionBar().setDisplayHomeAsUpEnabled(eligibleForBack);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getFragmentManager().popBackStack();

        return true;
    }
}
