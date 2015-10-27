package nz.co.nativemobile.simplesubstitution.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nz.co.nativemobile.simplesubstitution.MainActivity;
import nz.co.nativemobile.simplesubstitution.R;
import nz.co.nativemobile.simplesubstitution.adapter.TeamDetailAdapter;
import nz.co.nativemobile.simplesubstitution.domain.Team;
import nz.co.nativemobile.simplesubstitution.event.ImageUpdatedEvent;

/**
 * Created by wadereweti on 9/24/15.
 */
public class TeamViewFragment extends BaseFragment implements TeamDetailAdapter.PlayerClickListener, View.OnClickListener {

    private static final String TAG = TeamViewFragment.class.getSimpleName();

    private int mTeamId;

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

        setupView();
    }

    private void setupView() {
        mTeamId = getArguments().getInt(MainActivity.TEAM_ID);
        Team team = Team.findTeamById(mTeamId);

        mRecyclerView.setLayoutManager(getGridLayoutManager());

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.team_header_view, mRecyclerView, false);
        ImageView imageView = ButterKnife.findById(headerView, R.id.team_image);
        Picasso.with(getActivity())
                .load(team.imagePath)
                .placeholder(R.drawable.placeholder_team)
                .fit()
                .centerCrop()
                .into(imageView);

        TextView teamName = ButterKnife.findById(headerView, R.id.team_name);
        teamName.setText(team.name);

        TeamDetailAdapter adapter = new TeamDetailAdapter(team.players(), this, getActivity());
        headerView.setOnClickListener(this);
        adapter.setParallaxHeader(headerView, mRecyclerView);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.title_team_detail);
    }

    @NonNull
    private GridLayoutManager getGridLayoutManager() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.gridlayout_column));
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? getResources().getInteger(R.integer.gridlayout_column) : 1;
            }
        });

        return gridLayoutManager;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    @OnClick(R.id.floating_action_button_main)
    public void onAddPlayerClicked() {
        mNavigation.onCreatePlayerClicked(mTeamId);
    }

    @Override
    public void onClick(View v) {
        Bundle args = new Bundle();
        args.putInt(Team.TEAM_ID, mTeamId);
        mNavigation.onEditTeamSelected(args);
        Log.e(TAG, "team view clicked");
    }

    @Override
    public void onPlayerViewClicked(int playerId) {
        Log.e(TAG, "player view clicked with id of " + playerId);
    }

    @Subscribe
    public void onTeamImageUpdated(ImageUpdatedEvent imageUpdatedEvent) {
        Log.e(TAG, "onTeamImageUpdated");
        setupView();
    }
}

