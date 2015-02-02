package com.pocotopocopo.juego;

//import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Border {
    private static final String TAG = "Juego.Border";
    private int x, y, size;
    private Orientation orientation;

    public Border(Orientation orientation, int x, int y, int size) {
        update(orientation, x, y, size);
    }

    public void update(Orientation orientation, int x, int y, int size) {
        this.orientation = orientation;
        update(x,y,size);
    }

//    public void update(int x, int y, int size) {
//        this.x = x;
//        this.y = y;
//        this.size = size;
//    }

    public void update(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size=size;

    }


    public int getStart() {
        if (orientation.equals(Orientation.X)) {
            return x;
        } else {
            return y;
        }
    }

    public int getEnd() {

        return getStart() + size;
    }


    public int getPos() {
        if (orientation.equals(Orientation.X)) {
            return y;
        } else {
            return x;
        }
    }

//    private boolean checkOrientation(Orientation orientation) {
//        return (orientation.equals(this.orientation));
//    }

//    public boolean checkDanger(Border border) {
//        if (checkOrientation(border.orientation)) {
//            boolean result = ((this.getStart() > border.getStart() && this.getStart() < border.getEnd()) || this.getEnd() > border.getStart() && this.getEnd() < border.getEnd());
//            result = result || ((border.getStart() > this.getStart() && border.getStart() < this.getEnd()) || border.getEnd() > this.getStart() && border.getEnd() < this.getEnd());
//            result = result || border.getStart()==this.getStart() && border.getEnd() == this.getEnd();
//            //Log.d(TAG,"danger result: "+result);
//            return result;
//        } else {
//            return false;
//        }
//    }
//
////    private int getDelta(int dx, int dy) {
////        if (orientation.equals(Orientation.X)) {
////            return dy;
////        } else {
////            return dx;
////        }
////    }
////
////    public boolean checkCollision(Border border, int delta) {
////        int a = this.getPos();
////
////        int b = this.getPos() + delta;
////        int c = border.getPos();
////        boolean result = (checkDanger(border) && ((c >= a && c <= b) || (c >= b && c <= a)));
////        //Log.d(TAG,"this.pos="+this.getPos() + " border.pos="+border.getPos() + " delta="+delta + " result="+result);
////        //Log.d(TAG,"this=("+this.x + ","+this.y + ") border=("+border.x + ","+border.y+")");
////        return result;
////    }
////
////    private boolean checkExactCollision(Border border, int delta) {
////        return (checkDanger(border) && border.getPos() == this.getPos() + delta);
////    }
////
////    public int getDistance(Border border) {
////        return Math.abs(this.getPos() - border.getPos());
////    }
//
////    public List<Collision> collisionDetect(Set<Border> borders, int delta, Orientation orientation) {
////        List<Collision> collisions = new ArrayList<>();
////        if (!checkOrientation(orientation)) {
////            //Log.d(TAG, "orientation no coincide" );
////            return collisions;
////        }
////
////        for (Border border : borders) {
////            //Log.d(TAG, "checking" );
////            if (checkCollision(border, delta)) {
////                collisions.add(new Collision(border, getDistance(border)));
////            }
////        }
////        return collisions;
////
////    }

}


