package com.pocotopocopo.juego;

import java.util.Comparator;

/**
* Created by nico on 21/01/15.
*/
public class Collision {
    private float dist;
    private Border border;

    public Collision(Border border, float dist){
        this.dist=dist;
        this.border=border;
    }

    public float getDist() {
        return dist;
    }

    public Border getBorder() {
        return border;
    }

    public static class CollisionComparator implements Comparator<Collision>{

        @Override
        public int compare(Collision lhs, Collision rhs) {
            return (int)((lhs.dist-rhs.dist)*100000);
        }
    }
}

