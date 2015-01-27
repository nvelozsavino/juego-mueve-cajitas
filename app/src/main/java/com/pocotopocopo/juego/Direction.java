package com.pocotopocopo.juego;

public enum Direction {
    RIGHT, LEFT, UP, DOWN, NONE;
    public Direction reverse(){
        switch (this){
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            default:
                return null;
        }
    }
    public int getSign(){
        switch (this){
            case LEFT:
                return -1;
            case RIGHT:
                return -1;
            case UP:
                return +1;
            case DOWN:
                return +1;
            default:
                return 0;
        }
    }
    public Direction rotatedCW(){
        switch (this){
            case LEFT:
                return DOWN;
            case RIGHT:
                return UP;
            case UP:
                return LEFT;
            case DOWN:
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
            case UP:
            case DOWN:
                return Orientation.X;
            default:
                return null;
        }
    }
}
