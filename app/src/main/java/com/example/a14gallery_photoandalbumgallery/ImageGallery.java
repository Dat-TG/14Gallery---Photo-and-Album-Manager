package com.example.a14gallery_photoandalbumgallery;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

// Singleton Pattern
public class ImageGallery {
    boolean loaded = false;
    public List<String> images;
    private static ImageGallery INSTANCE;

    // Constructor
    private ImageGallery() { }

    public static ImageGallery getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImageGallery();
        }
        return INSTANCE;
    }

    public void load(Context context)  {
        if (!loaded) {
            images = listOfImages(context);
            loaded = true;
        }
    }

    public void update(Context context) {
        if (!loaded)
            load(context);
        else {
            images = listOfImages(context);
        }
    }

    private static ArrayList<String> listOfImages(Context context) {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        // int column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection,
                null, null,  orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        // Get folder name
        // column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }

        cursor.close();

        return listOfAllImages;
    }
}
