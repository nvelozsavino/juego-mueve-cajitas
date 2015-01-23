package com.pocotopocopo.juego;

/**
 * Created by nico on 21/01/15.
 */
public enum Orientation {
    X, Y;
    public Orientation perpendicular(){
        if (this.equals(X)){
            return Y;
        } else {
            return X;
        }
    }
    public Side direction(int sign) {
        if (this.equals(X)) {
            if (sign >= 0) {
                return Side.RIGHT;
            } else {
                return Side.LEFT;
            }

        } else {
            if (sign >= 0) {
                return Side.BOTTOM;
            } else {
                return Side.TOP;
            }
        }
    }
}
