package com.pocotopocopo.puzzlide;

/**
 * Created by nico on 22/01/15.
 */
public class Tuple<T,K> {
    public T firstArgument;
    public K secondArgument;

    public Tuple(T firstArgument, K secondArgument){
        this.firstArgument = firstArgument;
        this.secondArgument = secondArgument;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Tuple){
            return firstArgument.equals(((Tuple) o).firstArgument) && secondArgument.equals(((Tuple) o).secondArgument);

        }
        return false;
    }

    @Override
    public int hashCode() {
        return firstArgument.hashCode() + secondArgument.hashCode();
    }
}
