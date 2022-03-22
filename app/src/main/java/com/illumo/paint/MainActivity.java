package com.illumo.paint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.slider.RangeSlider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawView paint;
    private ImageButton save, undo, stroke, addObj, btnObj;
    private RangeSlider rangeSlider;

    private LinearLayout objects;


    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private ListView listOfItems;
    public static final List<ObjCanvas> listObjects = new ArrayList<ObjCanvas>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paint = findViewById(R.id.draw_view);
        save = findViewById(R.id.btn_save);
        undo = findViewById(R.id.btn_undo);
        stroke = findViewById(R.id.btn_stroke);
        addObj = findViewById(R.id.addObjBut);
        btnObj = findViewById(R.id.btn_obj);
        rangeSlider = findViewById(R.id.strokeWidth);
        listOfItems = (ListView) findViewById(R.id.listOfItems);
        objects = findViewById(R.id.objlayout);

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paint.undo();
            }
        });

        btnObj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(objects.getVisibility() == View.VISIBLE)
                {
                    objects.setVisibility(View.GONE);
                    btnObj.setImageResource(R.drawable.ic_open);
                }
                else
                {
                    objects.setVisibility(View.VISIBLE);
                    btnObj.setImageResource(R.drawable.ic_close);
                }

            }
        });

        addObj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Bitmap gettedBitmap = paint.save();
                    Bitmap bitmap = Bitmap.createScaledBitmap(
                            gettedBitmap, 100, 100, false);
                    Bitmap savedObjBitmap = Bitmap.createScaledBitmap(
                            gettedBitmap, 1080, 1920, false);
                    ObjCanvas obj = new ObjCanvas();
                    obj.setBitmap(bitmap);
                    listObjects.add(obj);
                    bitmaps.add(savedObjBitmap);
                    ListAdapter adapter = new ObjCanvasAdapter(MainActivity.this, R.layout.list_item, listObjects);
                    listOfItems.setAdapter(adapter);
                } catch (Exception e)
                {
                    Toast.makeText(MainActivity.this,"Error", Toast.LENGTH_LONG).show();
                }

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveGif();
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_LONG).show();

//                Bitmap bmp = paint.save();
//                OutputStream imageOutStream = null;
//
//                ContentValues cv = new ContentValues();
//                cv.put(MediaStore.Images.Media.DISPLAY_NAME, "drawing.png");
//                cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
//                cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
//
//                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//                try {
//                    imageOutStream = getContentResolver().openOutputStream(uri);
//                    bmp.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);
//                    imageOutStream.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });

        stroke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rangeSlider.getVisibility() == View.VISIBLE)
                    rangeSlider.setVisibility(View.GONE);
                else
                    rangeSlider.setVisibility(View.VISIBLE);
            }
        });

        rangeSlider.setValueFrom(0.0f);
        rangeSlider.setValueTo(100.0f);

        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                paint.setStrokeWidth((int) value);
            }
        });

        ViewTreeObserver vto = paint.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                paint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = paint.getMeasuredWidth();
                int height = paint.getMeasuredHeight();
                paint.init(height, width);
            }
        });
    }

    public byte[] generateGIF() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(bos);
//
//        for (ObjCanvas obj : listObjects) {
//            bitmaps.add(obj.getBitmap());
//        }

        for (Bitmap bitmap : bitmaps) {
            encoder.addFrame(bitmap);
            bitmap.recycle();
        }
        encoder.finish();
        return bos.toByteArray();
    }

    public void saveGif() {
        try {
            //File filePath = new File("/sdcard", "sample.gif");
            FileOutputStream outStream = null;

            ContentValues cv = new ContentValues();
            cv.put(MediaStore.Images.Media.DISPLAY_NAME, "drawing.gif");
            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/gif");
            cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

            try {
                outStream = (FileOutputStream) getContentResolver().openOutputStream(uri);
                outStream.write(generateGIF());
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
