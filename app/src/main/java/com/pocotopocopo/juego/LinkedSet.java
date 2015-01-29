package com.pocotopocopo.juego;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nico on 23/01/15.
 */
public class LinkedSet<K>{
    Set<LinkedSet<K>> setsR = new HashSet<LinkedSet<K>>();
    Set<LinkedSet<K>> setsL = new HashSet<LinkedSet<K>>();
    Set<LinkedSet<K>> setsT = new HashSet<LinkedSet<K>>();
    Set<LinkedSet<K>> setsB = new HashSet<LinkedSet<K>>();
    public K object;
    public LinkedSet(K object){
        this.object=object;
    }


    private Set<LinkedSet<K>> getSet(Direction dir){
        switch (dir){
            case RIGHT:
                return setsR;
            case LEFT:
                return setsL;
            case UP:
                return setsT;
            case DOWN:
                return setsB;
        }
        return null;
    }
    /*public K getObject(){
        return object;
    }*/
    public Set<LinkedSet<K>> getObjects(Direction dir){
        Set<LinkedSet<K>> objects=new HashSet<LinkedSet<K>>();
        return getObjects(objects,dir);
    }

    private Set<LinkedSet<K>> getObjects(Set<LinkedSet<K>> objects,Direction dir){
        objects.add(this);
        for (LinkedSet<K> linkedSet:getSet(dir)){
            objects=linkedSet.getObjects(objects,dir);
        }
        return objects;
    }

    public void add(LinkedSet<K> linkedSet, Direction dir){
        this.getSet(dir).add(linkedSet);
        linkedSet.getSet(dir.reverse()).add(this);
    }

    public void remove(LinkedSet<K> linkedSet, Direction dir){
        this.getSet(dir).remove(linkedSet);
        linkedSet.getSet(dir.reverse()).remove(this);
    }

    public void move(Direction dir){
        Set<LinkedSet<K>> cSet = getObjects(dir);
        List<Tuple<LinkedSet<K>,LinkedSet<K>>> toRemoveList=new ArrayList<>();
        for (LinkedSet<K> c: cSet){
            Set<LinkedSet<K>> aSet=c.getSet(dir.reverse());

            for(LinkedSet<K> a: aSet){
                if (!cSet.contains(a)) {
                    toRemoveList.add(new Tuple<LinkedSet<K>, LinkedSet<K>>(a,c));
                }
            }

        }
        for (Tuple<LinkedSet<K>,LinkedSet<K>> toRemove: toRemoveList){
            toRemove.firstArgument.getSet(dir).remove(toRemove.secondArgument);
            toRemove.secondArgument.getSet(dir.reverse()).remove(toRemove.firstArgument);
        }
       /* Iterator<LinkedSet<K>> cIterator = cSet.iterator();
        while (cIterator.hasNext()){
            LinkedSet<K> c=cIterator.next();

            Set<LinkedSet<K>> aSet=c.getSet(dir.reverse());
            Iterator<LinkedSet<K>> aIterator = aSet.iterator();

            while (aIterator.hasNext()){
                LinkedSet<K> a= aIterator.next();

                if (!cSet.contains(a)){
                    a.getSet(dir).remove()


                    c.remove(a,dir.reverse());
                }
            }
        }*/
    }

    @Override
    public String toString() {
        String str="";
        for (Direction dir: Direction.values()) {
            str+= dir + ": " + object.toString() + " : { ";
            for (LinkedSet<K> set : getSet(dir)) {
                str += set.object + ", ";
            }
            str += "} \t";
        }
        return str;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LinkedSet){
            return object.equals(((LinkedSet) o).object);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return object.hashCode();
    }
}