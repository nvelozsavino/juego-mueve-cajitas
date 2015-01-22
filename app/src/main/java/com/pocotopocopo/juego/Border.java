package com.pocotopocopo.juego;

//import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nico on 21/01/15.
 */
enum Orientation {
    HORIZONTAL,VERTICAL;
}

enum Side {
    LEFT,RIGHT,TOP,BOTTOM;
}
public class Border {
    private static final String TAG="Border";
    private float x,y,size;
    private Orientation orientation;

    public Border(Orientation orientation, float x, float y, float size){
        update(orientation,x,y,size);
    }

    public void update(Orientation orientation, float x, float y, float size){
        this.orientation=orientation;
        this.x=x;
        this.y=y;
        this.size=size;
    }
    public void update( float x, float y, float size){
        this.x=x;
        this.y=y;
        this.size=size;
    }
    public void update( float x, float y){
        this.x=x;
        this.y=y;

    }



    private float getStart(){
        if (orientation.equals(Orientation.HORIZONTAL)){
            return x;
        } else {
            return y;
        }
    }

    private float getEnd(){
        return getStart()+size;
    }

    private float getPos(){
        if (orientation.equals(Orientation.HORIZONTAL)){
            return y;
        } else {
            return x;
        }
    }

    private boolean checkOrientation(Orientation orientation) {
        return (orientation.equals(this.orientation));
    }

    public boolean checkDanger(Border border){
        if (checkOrientation(border.orientation)){
            boolean result= ((this.getStart()>=border.getStart() && this.getStart()<=border.getEnd())||this.getEnd()>border.getStart() && this.getEnd()<=border.getEnd());
            result=result || ((border.getStart()>=this.getStart() && border.getStart()<=this.getEnd())||border.getEnd()>this.getStart() && border.getEnd()<=this.getEnd());
            //Log.d(TAG,"danger result: "+result);
            return result;
        } else{
            return false;
        }
    }

    private float getDelta(float dx,float dy){
        if (orientation.equals(Orientation.HORIZONTAL)){
            return dy;
        } else {
            return dx;
        }
    }

    public boolean checkCollision(Border border,float delta){
        float a=this.getPos();

        float b=this.getPos()+delta;
        float c=border.getPos();
        boolean result =(checkDanger(border) && ( (c>=a && c<=b ) || (c>=b && c<=a)));
        //Log.d(TAG,"this.pos="+this.getPos() + " border.pos="+border.getPos() + " delta="+delta + " result="+result);
        //Log.d(TAG,"this=("+this.x + ","+this.y + ") border=("+border.x + ","+border.y+")");
        return result;
    }

    private boolean checkExactCollision(Border border,float delta){
        return  (checkDanger(border) && border.getPos()==this.getPos()+delta);
    }

    public float getDistance(Border border){
        return Math.abs(this.getPos()-border.getPos());
    }

    public List<Collision> collisionDetect(Set<Border> borders,float delta,Orientation orientation){
        List<Collision> collisions = new ArrayList<>();
        if (!checkOrientation(orientation)){
            //Log.d(TAG, "orientation no coincide" );
            return collisions;
        }

        for (Border border:borders){
            //Log.d(TAG, "checking" );
            if (checkCollision(border, delta)){
                collisions.add(new Collision(border,getDistance(border)));
            }
        }
        return collisions;

    }

}


