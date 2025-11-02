package com.example.eecs4443groupprojectgroup1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageUtil {

    /**
     * Converts a Bitmap to a Base64-encoded String (compressed).
     * @param bitmap The bitmap to encode.
     * @param maxWidth Maximum allowed width (432).
     * @param maxHeight Maximum allowed height (432).
     * @param quality JPEG compression quality (0–100).
     * @return Base64 string representation of the image.
     */
    public static String encodeToBase64(Bitmap bitmap, int maxWidth, int maxHeight, int quality) {
        if (bitmap == null) return null;

        Bitmap resizedBitmap = resizeBitmap(bitmap, maxWidth, maxHeight);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Simple version: Converts a Bitmap to Base64 (without resizing or compression).
     */
    public static String encodeToBase64(Bitmap bitmap) {
        return encodeToBase64(bitmap, 432, 432, 80);
    }

    /**
     * Converts a Base64-encoded String back into a Bitmap.
     */
    public static Bitmap decodeFromBase64(String base64String) {
        if (base64String == null || base64String.isEmpty()) return null;

        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Resizes a Bitmap while maintaining aspect ratio.
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratioBitmap = (float) width / height;
        float ratioMax = (float) maxWidth / maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;

        if (ratioMax > ratioBitmap) {
            finalWidth = (int) (maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) (maxWidth / ratioBitmap);
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
    }
}