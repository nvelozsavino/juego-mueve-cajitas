package com.pocotopocopo.juego;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;



/**
 * Created by nico on 15/02/15.
 */
public class TitledButton extends RelativeLayout {


    private boolean hasAdditionalText=false;
    private String additionalText;
    private String titleText;
    private int iconResource;
    private ImageView iconImage;
    private TextView titleTextView;
    private TextView additionalTextView;


    public TitledButton(Context context) {
        super(context);
    }

    public TitledButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray=context.getTheme().obtainStyledAttributes(attrs,R.styleable.TitledButton,0,0);
        try{
            this.titleText = typedArray.getString(R.styleable.TitledButton_titleText);
            this.hasAdditionalText = typedArray.getBoolean(R.styleable.TitledButton_hasAdditionalText,false);

            if (hasAdditionalText) {
                this.additionalText = typedArray.getString(R.styleable.TitledButton_additionalText);
            }
            this.iconResource = typedArray.getResourceId(R.styleable.TitledButton_iconSrc, -1);
        } finally {
            typedArray.recycle();
        }
        inflate(context, R.layout.title_button_layout, this);
        init();
    }




    private void setTitleText(){
        if(titleText!=null && !titleText.equals("")){
            titleTextView.setVisibility(VISIBLE);
            titleTextView.setText(titleText);
        } else {
            titleTextView.setVisibility(GONE);
        }
    }

    private void setAdditionalText(){
        if (hasAdditionalText && additionalText!=null && !additionalText.equals("")){
            additionalTextView.setVisibility(VISIBLE);
            additionalTextView.setText(additionalText);
        } else {
            additionalTextView.setVisibility(GONE);
        }
    }

    private void setIconResource(){
        if (iconResource!=-1){
            iconImage.setImageResource(iconResource);
        }
    }
    private void init() {
        iconImage=(ImageView)findViewById(R.id.iconImage);
        titleTextView=(TextView)findViewById(R.id.titleText);
        additionalTextView=(TextView)findViewById(R.id.additionalText);


        setTitleText();
        setAdditionalText();
        setIconResource();
        refreshDrawableState();
        invalidate();


    }


    public boolean isHasAdditionalText() {
        return hasAdditionalText;
    }

    public void setHasAdditionalText(boolean hasAdditionalText) {
        this.hasAdditionalText = hasAdditionalText;
        setAdditionalText();
    }

    public String getAdditionalText() {
        return additionalText;
    }

    public void setAdditionalText(String additionalText) {
        this.additionalText = additionalText;
        if (additionalText!=null && !additionalText.equals("")){
            hasAdditionalText=true;
        }
        setAdditionalText();
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String title) {
        this.titleText = title;
        setTitleText();
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
        setIconResource();
    }

    public void setIconImage(Bitmap bitmap){
        iconImage.setImageBitmap(bitmap);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }
}
