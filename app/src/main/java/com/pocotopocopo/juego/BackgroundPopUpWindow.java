package com.pocotopocopo.juego;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.pocotopocopo.juego.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nico on 21/02/15.
 */
public class BackgroundPopUpWindow {
    private ListPopupWindow popupWindow;
    private List<BackgroundOption> data;

    private View anchor;
    private Context context;

    private AdapterView.OnItemClickListener listener;

    public BackgroundPopUpWindow(Context context, final AdapterView.OnItemClickListener listener){
        this.context=context;

        popupWindow=new ListPopupWindow(context);
        this.listener=listener;
        data=new ArrayList<>();
        data.add(new BackgroundOption(context.getString(R.string.game_background_plain), R.drawable.plain,BackgroundMode.PLAIN));
        data.add(new BackgroundOption(context.getString(R.string.game_background_fixed_image), R.drawable.picture,BackgroundMode.IMAGE));
        data.add(new BackgroundOption(context.getString(R.string.game_background_video), R.drawable.video, BackgroundMode.VIDEO));

        BackgroundAdapter adapter = new BackgroundAdapter(context,android.R.layout.simple_list_item_1,data);

        popupWindow.setAdapter(adapter);
        popupWindow.setModal(true);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onItemClick(parent,view,position,id);
                popupWindow.dismiss();
            }
        }); // the callback for when a list item is selected
    }

    public void show(View anchor){
        this.anchor=anchor;
        popupWindow.setAnchorView(anchor);
        popupWindow.show();
    }



    public ListPopupWindow getPopupWindow() {
        return popupWindow;
    }


    public List<BackgroundOption> getData() {
        return data;
    }

    public View getAnchor() {
        return anchor;
    }

    public void setAnchor(View anchor) {
        this.anchor = anchor;
    }

    public AdapterView.OnItemClickListener getListener() {
        return listener;
    }

    public Context getContext() {
        return context;
    }



    public static class BackgroundOption{
        private String title;
        private int iconResource;
        BackgroundMode mode;

        public BackgroundOption(String title, int iconResource, BackgroundMode mode){
            this.title=title;
            this.iconResource=iconResource;
            this.mode=mode;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getIconResource() {
            return iconResource;
        }

        public void setIconResource(int iconResource) {
            this.iconResource = iconResource;
        }

        public BackgroundMode getMode() {
            return mode;
        }

        public void setMode(BackgroundMode mode) {
            this.mode = mode;
        }
    }

    private static class BackgroundAdapter extends ArrayAdapter<BackgroundOption>{

        private List<BackgroundOption> items;
        public BackgroundAdapter(Context context, int resource, List<BackgroundOption> items) {
            super(context, resource, items);
            this.items=items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.title_button_layout, null);
            }
            BackgroundOption backgroundOption = items.get(position);
            if (backgroundOption != null) {
                ImageView iconImage=(ImageView)v.findViewById(R.id.iconImage);
                TextView titleTextView=(TextView)v.findViewById(R.id.titleText);
                TextView additionalTextView=(TextView)v.findViewById(R.id.additionalText);
                additionalTextView.setVisibility(View.GONE);
                if (iconImage != null) {
                    iconImage.setImageResource(backgroundOption.getIconResource());
                }
                if(titleTextView != null){
                    titleTextView.setText(backgroundOption.getTitle());
                }
            }
            return v;
        }


    }

}
