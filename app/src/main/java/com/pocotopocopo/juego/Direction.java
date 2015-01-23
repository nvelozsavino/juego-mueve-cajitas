package com.pocotopocopo.juego;

public enum Direction {
    RIGHT, LEFT, TOP, BOTTOM;
    public Direction reverse(){
        switch (this){
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            case TOP:
                return BOTTOM;
            case BOTTOM:
                return TOP;
            default:
                return null;
        }
    }
    public Direction rotatedCW(){
        switch (this){
            case LEFT:
                return BOTTOM;
            case RIGHT:
                return TOP;
            case TOP:
                return LEFT;
            case BOTTOM:
                return RIGHT;
            default:
                return null;
        }
    }
    public Direction rotatedCCW(){
        return this.rotatedCW().reverse();
    }
    public Orientation getOrientation(){
        switch (this){
            case LEFT:
            case RIGHT:
                return Orientation.Y;
            case TOP:
            case BOTTOM:
                return Orientation.X;
            default:
                return null;
        }
    }
}
