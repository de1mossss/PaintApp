package com.illumo.paint;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.appcompat.widget.AppCompatImageButton;


public class ObjCanvas {
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
