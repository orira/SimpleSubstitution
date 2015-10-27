package nz.co.nativemobile.simplesubstitution.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import nz.co.nativemobile.simplesubstitution.SimpleSubstitutionApplication;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by wadereweti on 10/15/15.
 */
public class GalleryUtil {
    private static final String TAG = GalleryUtil.class.getSimpleName();

    private static Uri mOutputFileUri;

    public static final int SELECT_PICTURE_REQUEST_CODE = 111;

    public static Intent getImageChooserIntent(Context context) {
        // Determine Uri of camera image to save
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        mOutputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        return chooserIntent;
    }

    public static Observable<Uri> getImageUri(int requestCode, Intent data) {
        Uri selectedImageUri = null;

        if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
            if (isCamera(data)) {
                selectedImageUri = mOutputFileUri;
            } else {
                return convertGalleryUriToLocalFile(data.getData()).compose(RxUtil.<Uri>applySchedulers());
            }
        }

        return Observable.just(selectedImageUri).compose(RxUtil.<Uri>applySchedulers());
    }

    public static Uri getImageUriSyncronous(int requestCode, Intent data) {
        if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
            return isCamera(data) ? mOutputFileUri : data.getData();
        }

        return null;
    }

    private static Observable<Uri> convertGalleryUriToLocalFile(final Uri data) {
        return Observable.create(new Observable.OnSubscribe<Uri>() {
            @Override
            public void call(Subscriber<? super Uri> subscriber) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(SimpleSubstitutionApplication.getInstance().getContentResolver(), data);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                }

                byte[] byteArray = stream.toByteArray();
                Uri persistedUri = saveFile(byteArray);
                subscriber.onNext(persistedUri);
            }
        });
    }

    private static boolean isCamera(Intent data) {
        boolean camera;

        if (data == null) {
            camera = true;
        } else {
            final String action = data.getAction();
            camera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        return camera;
    }

    private static Uri saveFile(byte[] byteArray) {
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);

        String destinationFilename = sdImageMainDirectory.getPath();
        File destinationFile = new File(destinationFilename);

        try {
            OutputStream out = new FileOutputStream(destinationFile);
            out.write(byteArray);
            out.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        return Uri.fromFile(destinationFile);
    }
}
