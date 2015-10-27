package nz.co.nativemobile.simplesubstitution;

import android.os.Bundle;

/**
 * Created by wadereweti on 9/24/15.
 */
public interface Navigation {
    void onCreateTeamSelected();
    void onTeamCreated();

    void onTeamSelected(int teamId);
    void onEditTeamSelected(Bundle args);

    void onCreatePlayerClicked(int teamId);
    void onPlayerCreated();
}
