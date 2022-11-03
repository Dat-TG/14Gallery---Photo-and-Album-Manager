package com.example.a14gallery_photoandalbumgallery.album;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.a14gallery_photoandalbumgallery.Image;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AlbumGallery {
    private static AlbumGallery INSTANCE = null;
    private boolean loaded = false;
    public List<Album> albums;

    public static AlbumGallery getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AlbumGallery();
        }
        return INSTANCE;
    }

    public void load(Context context) {
        if (!loaded) {
            albums = getPhoneAlbums(context);
            loaded = true;
        }
    }

    public void update(Context context) {
        if (!loaded)
            load(context);
        else {
            albums = getPhoneAlbums(context);
        }
    }

    public static List<Album> getPhoneAlbums(Context context) {
        List<Album> albums = new ArrayList<>();
        List<String> albumsNames = new ArrayList<>();

        // which image properties are we querying
        String[] projection = new String[]{
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_TAKEN
        };
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor cur = context.getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        if (cur != null && cur.getCount() > 0) {
            if (cur.moveToFirst()) {
                String bucketName;
                String data;
                String imageId;
                long dateTaken;
                int bucketNameColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                int imageUriColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.DATA);

                int imageIdColumn = cur.getColumnIndex(
                        MediaStore.Images.Media._ID);

                int dateTakenColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.DATE_TAKEN);
                do {
                    Calendar myCal = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
                    // Get the field values
                    bucketName = cur.getString(bucketNameColumn);
                    data = cur.getString(imageUriColumn);
                    imageId = cur.getString(imageIdColumn);
                    dateTaken = cur.getLong(dateTakenColumn);
                    myCal.setTimeInMillis(dateTaken);
                    String dateText = formatter.format(myCal.getTime());

                    if (bucketName.equals("FavoriteAlbum") || bucketName.equals("PrivateAlbum") || bucketName.equals("RecycleBin")) {
                        continue;
                    }

                    Image image = new Image();
                    image.setAlbumName(bucketName);
                    image.setPath(data);
                    image.setId(Integer.parseInt(imageId));
                    image.setDateTaken(dateText);



                    if (albumsNames.contains(bucketName)) {
                        for (Album album : albums) {
                            if (album.getName().equals(bucketName)) {
                                album.getAlbumImages().add(image);
                                break;
                            }
                        }
                    } else {
                        Album album = new Album();
                        album.setId(image.getId());
                        album.setName(bucketName);
                        album.setCoverUri(image.getPath());
                        album.getAlbumImages().add(image);

                        albums.add(album);
                        albumsNames.add(bucketName);
                    }

                } while (cur.moveToNext());
            }

            cur.close();
        }
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/14Gallery/");
        File[] allFiles = folder.listFiles();
        if (folder.exists()) {
            for (File allFile : allFiles) {
                File[] content = allFile.listFiles();
                if (allFile.getName().equals("FavoriteAlbum") || allFile.getName().equals("PrivateAlbum") || allFile.getName().equals("RecycleBin")) {
                    continue;
                }
                if (content.length == 0) {
                    Album album = new Album();
                    //album.setId(image.getId());
                    album.setName(allFile.getName());
                    //album.setCoverUri(image.getimageUri());
                    //album.getAlbumimages().add(image);
                    albums.add(album);
                    albumsNames.add(allFile.getName());
                }
            }
        }
        return albums;
    }
}
