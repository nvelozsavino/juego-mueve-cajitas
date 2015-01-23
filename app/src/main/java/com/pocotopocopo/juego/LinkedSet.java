package com.pocotopocopo.juego;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nico on 23/01/15.
 */
public class LinkedSet<O>{
    Set<LinkedSet<O>> setsR = new HashSet<LinkedSet<O>>();
    Set<LinkedSet<O>> setsL = new HashSet<LinkedSet<O>>();
    Set<LinkedSet<O>> setsT = new HashSet<LinkedSet<O>>();
    Set<LinkedSet<O>> setsB = new HashSet<LinkedSet<O>>();
    O object;
    public LinkedSet(O object){
        this.object=object;
    }


    private Set<LinkedSet<O>> getSet(Direction dir){
        switch (dir){
            case RIGHT:
                return setsR;
            case LEFT:
                return setsL;
            case TOP:
                return setsT;
            case BOTTOM:
                return setsB;
        }
        return null;
    }
    public O getObject(){
        return object;
    }
    public Set<LinkedSet<O>> getObjects(Direction dir){
        Set<LinkedSet<O>> objects=new HashSet<LinkedSet<O>>();
        return getObjects(objects,dir);
    }

    private Set<LinkedSet<O>> getObjects(Set<LinkedSet<O>> objects,Direction dir){
        objects.add(this);
        for (LinkedSet<O> linkedSet:getSet(dir)){
            objects=linkedSet.getObjects(objects,dir);
        }
        return objects;
    }

    public void add(LinkedSet<O> linkedSet, Direction dir){
        this.getSet(dir).add(linkedSet);
        linkedSet.getSet(dir.reverse()).add(this);
    }

    public void remove(LinkedSet<O> linkedSet, Direction dir){
        this.getSet(dir).remove(linkedSet);
        linkedSet.getSet(dir.reverse()).remove(this);
    }

    public void move(Direction dir){
        Set<LinkedSet<O>> cSet = getObjects(dir);
        for (LinkedSet<O> c : cSet){
            Set<LinkedSet<O>> aSet=c.getSet(dir.reverse());
            for (LinkedSet<O> a: aSet){
                if (!cSet.contains(a)){
                    c.remove(a,dir.reverse());
                }
            }
        }
    }

    @Override
    public String toString() {
        String str="";
        for (Direction dir: Direction.values()) {
            str+= dir + ": " + object.toString() + " : { ";
            for (LinkedSet<O> set : getSet(dir)) {
                str += set.object + ", ";
            }
            str += "} \t";
        }
        return str;
    }
}