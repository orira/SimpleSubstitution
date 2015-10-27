package nz.co.nativemobile.simplesubstitution.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import nz.co.nativemobile.simplesubstitution.R;
import nz.co.nativemobile.simplesubstitution.domain.Player;

/**
 * Created by wadereweti on 10/14/15.
 */
public class TeamDetailAdapter extends ParallaxRecyclerAdapter<Player> implements ParallaxRecyclerAdapter.OnClickEvent {

    private Context mContext;
    private PlayerClickListener mTeamDeatilClickListener;

    public TeamDetailAdapter(List<Player> data, PlayerClickListener clickListener, Context context) {
        super(data);
        mTeamDeatilClickListener = clickListener;
        mContext = context;
    }

    @Override
    public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<Player> parallaxRecyclerAdapter, int position) {
        Player player = parallaxRecyclerAdapter.getData().get(position);

        PlayerViewHolder holder = (PlayerViewHolder) viewHolder;

        Picasso.with(mContext)
                .load(player.imagePath)
                .placeholder(R.drawable.placeholder_player)
                .fit().centerCrop()
                .into(holder.mImageView);

        holder.mImageView.setBorderColor(player.colour);
        holder.mPlayerName.setText(player.firstName);
        holder.mClickListener = mTeamDeatilClickListener;
        holder.mPlayer = player;
    }

    @Override
    public PlayerViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, ParallaxRecyclerAdapter<Player> parallaxRecyclerAdapter, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.player_detail_view, viewGroup, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public int getItemCountImpl(ParallaxRecyclerAdapter<Player> parallaxRecyclerAdapter) {
        return parallaxRecyclerAdapter.getData().size();
    }

    @Override
    public void onClick(View view, int position) {
    }

    public interface PlayerClickListener {
        void onPlayerViewClicked(int playerId);
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private PlayerClickListener mClickListener;
        private Player mPlayer;

        @Bind(R.id.image_view_create_player_placeholder) RoundedImageView mImageView;
        @Bind(R.id.player_name) TextView mPlayerName;

        public PlayerViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onPlayerViewClicked(mPlayer.getIdAsInt());
        }
    }
}