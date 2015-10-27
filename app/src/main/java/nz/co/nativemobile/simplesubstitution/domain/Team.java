package nz.co.nativemobile.simplesubstitution.domain;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

import nz.co.nativemobile.simplesubstitution.util.GalleryUtil;
import rx.functions.Action1;

/**
 * Created by wadereweti on 9/29/15.
 */

@Table(name = "Team")
public class Team extends Model {
    private static final String TAG = Team.class.getSimpleName();
    public static final String TEAM_ID = "TEAM_ID";
    private static final String NAME = "NAME";
    private static final String IMAGE_PATH = "IMAGE_PATH";
    public static final String TEAM = "TEAM";

    @Column(name = NAME)
    public String name;

    @Column(name = IMAGE_PATH)
    public String imagePath;

    public List<Player> players() {
        return getMany(Player.class, TEAM);
    }

    public static Long create(String name, Uri imagePath, int requestCode, Intent data) {
        final Team team = new Team();

        updateImagePathToCorrectUri(imagePath, requestCode, data, team);

        team.name = name;
        team.save();

        return team.getId();
    }

    public static List<Team> getTeams() {
        return new Select().from(Team.class).execute();
    }

    public static Team findTeamByName(String name) {
        return new Select().from(Team.class).where(NAME + " = ?", name).executeSingle();
    }

    public static Team findTeamById(long teamId) {
        Team team = new Select().from(Team.class).where("id = ?", teamId).executeSingle();
        return team;
    }

    public static void updateTeam(long teamId, String name, Uri imagePath, int requestCode, Intent data) {
        Team team = Team.findTeamById(teamId);

        if (name != null) {
            team.name = name;
        }

        updateImagePathToCorrectUri(imagePath, requestCode, data, team);

        team.save();
    }

    public static void deleteTeam(Team team) {
        team.delete();
    }

    private static void updateImagePathToCorrectUri(Uri imagePath, int requestCode, Intent data, final Team team) {
        if (imagePath != null) {
            team.imagePath = imagePath.toString();
            GalleryUtil.getImageUri(requestCode, data).subscribe(new Action1<Uri>() {
                @Override
                public void call(Uri uri) {
                    Log.e(TAG, "uri saved");
                    team.imagePath = uri.toString();
                    team.save();
                }
            });
        }
    }

    private Uri getUriFromImagePath() {
        return Uri.parse(imagePath);
    }
}
