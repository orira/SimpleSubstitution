package nz.co.nativemobile.simplesubstitution.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import nz.co.nativemobile.simplesubstitution.R;
import nz.co.nativemobile.simplesubstitution.domain.Team;

/**
 * Created by wadereweti on 10/14/15.
 */
public class TeamViewAdapter extends RecyclerView.Adapter<TeamViewAdapter.ViewHolder> {

    private Context mContext;
    private TeamViewClickListener mTeamViewClickListener;
    private List<Team> mTeams;

    public TeamViewAdapter(Context context, TeamViewClickListener teamViewClickListener) {
        mContext = context;
        mTeams = Team.getTeams();
        mTeamViewClickListener = teamViewClickListener;
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_header_view, parent, false);
        return new ViewHolder(view, mTeamViewClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Team team = mTeams.get(position);

        Picasso.with(mContext)
                .load(team.imagePath)
                .placeholder(R.drawable.placeholder_team)
                .fit().centerCrop()
                .into(holder.mImageView);

        holder.mTeamName.setText(team.name);
        holder.teamId = team.getId();
    }

    @Override
    public int getItemCount() {
        return mTeams.size();
    }

    public interface TeamViewClickListener {
        void onTeamClicked(Long position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TeamViewClickListener mTeamViewClickListener;
        private Long teamId;

        @Bind(R.id.team_image) ImageView mImageView;
        @Bind(R.id.team_name) TextView mTeamName;

        public ViewHolder(View itemView, TeamViewClickListener teamViewClickListener) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mTeamViewClickListener = teamViewClickListener;
        }

        @Override
        public void onClick(View v) {
            mTeamViewClickListener.onTeamClicked(teamId);
        }
    }
}
