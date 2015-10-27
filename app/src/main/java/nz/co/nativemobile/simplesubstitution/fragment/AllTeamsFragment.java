package nz.co.nativemobile.simplesubstitution.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nz.co.nativemobile.simplesubstitution.R;
import nz.co.nativemobile.simplesubstitution.adapter.TeamViewAdapter;
import nz.co.nativemobile.simplesubstitution.view.TeamView;

/**
 * Created by wadereweti on 9/24/15.
 */
public class AllTeamsFragment extends BaseFragment implements TeamViewAdapter.TeamViewClickListener, TeamView {

    private static final String TAG = AllTeamsFragment.class.getSimpleName();

    @Bind(R.id.recycler_view_main) RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.team_view, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        setTitle(R.string.title_view_team);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        mRecyclerView.setAdapter(new TeamViewAdapter(getActivity(), this));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    @OnClick(R.id.floating_action_button_main)
    public void onCreateClick() {
        /*int cx = textView.getWidth();
        int cy = textView.getHeight();

        int finalRadius = Math.max(textView.getWidth(), textView.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(textView, cx, cy, 0, finalRadius);
        textView.setVisibility(View.VISIBLE);
        animator.start();*/
        mNavigation.onCreateTeamSelected();
    }

    @Override
    public void onTeamClicked(Long teamId) {
        Log.e(TAG, "team clicked");
        mNavigation.onTeamSelected(teamId.intValue());
    }

    @Override
    public void updateTeams() {
        mRecyclerView.setAdapter(new TeamViewAdapter(getActivity(), this));
    }
}
