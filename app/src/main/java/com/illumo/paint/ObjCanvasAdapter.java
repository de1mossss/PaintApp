package com.illumo.paint;

import static com.illumo.paint.MainActivity.listObjects;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import java.util.List;

public class ObjCanvasAdapter extends ArrayAdapter<ObjCanvas> {

    public ObjCanvasAdapter(Context context, int res, List<ObjCanvas> obj) {
        super(context, res, obj);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ObjCanvas obj = getItem(position);
        BitmapDrawable drawable = new BitmapDrawable(this.getContext().getResources(), obj.getBitmap());
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item, null);
        }
        ((ImageButton) convertView.findViewById(R.id.imageBut))
                .setBackgroundDrawable(drawable);
        return convertView;
    }
}
