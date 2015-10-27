package nz.co.nativemobile.simplesubstitution.domain;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Random;

import nz.co.nativemobile.simplesubstitution.util.GalleryUtil;
import rx.functions.Action1;

/**
 * Created by wadereweti on 10/15/15.
 */
@Table(name = "Player")
public class Player extends Model {
    private static final String FIRST_NAME = "FIRST_NAME";
    private static final String FAMILY_NAME = "FAMILY_NAME";
    private static final String IMAGE_PATH = "IMAGE_PATH";

    @Column(name = FIRST_NAME)
    public String firstName;

    @Column(name = FAMILY_NAME)
    public String familyName;

    @Column(name = IMAGE_PATH)
    public String imagePath;

    @Column(name = Team.TEAM)
    public Team team;

    @Column(name = "COLOUR")
    public int colour;

    public static void create(String firstName, Uri imagePath, int teamId, int requestCode, Intent data) {
        final Player player = new Player();
        player.firstName = firstName;

        if (imagePath != null) {
            player.imagePath = imagePath.toString();
        }

        Team playerTeam = Team.findTeamById(teamId);
        player.team = playerTeam;
        player.colour = generateColour();

        player.save();

        GalleryUtil.getImageUri(requestCode, data).subscribe(new Action1<Uri>() {
            @Override
            public void call(Uri uri) {
                player.imagePath = uri.toString();
                player.save();
            }
        });
    }

    private static int generateColour() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(), random.nextInt(), random.nextInt());
    }

    public int getIdAsInt() {
        return getId().intValue();
    }
}
