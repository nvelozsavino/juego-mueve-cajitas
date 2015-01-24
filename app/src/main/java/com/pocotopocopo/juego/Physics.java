package com.pocotopocopo.juego;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nico on 22/01/15.
 */
public class Physics {

    private final static String TAG="Physics";
    private Map<Piece,LinkedSet<Piece>> pieceSet=new ConcurrentHashMap<>();
    //private Set<PieceConnection> connectionSet;

    public Physics(Collection<Piece> pieces){
        addPieces(pieces);
    }

    public void addPiece(Piece piece){
        pieceSet.put(piece,new LinkedSet<>(piece));
    }


    public Physics(){
    }


    public void addPieces(Collection<Piece> pieces){
        for (Piece piece: pieces) {
            addPiece(piece);
        }
    }

    public void clearPhysics(){
        pieceSet.clear();
    }


    private void pieceNotExistThrow(Piece piece){
        if (!pieceSet.containsKey(piece)){
            throw new GameExceptions.PieceNotExistException("Piece " + piece + " not exist in the piece set");
        }
    }

    private void updatePerpendicularConnections(LinkedSet<Piece> piece, Direction direction){
        Set<LinkedSet<Piece>> connectedPieces;
        connectedPieces = piece.getObjects(direction.rotatedCW());
        for (LinkedSet<Piece> connectedPiece: connectedPieces) {
            if (!checkCollisionDanger(piece, connectedPiece, direction.rotatedCW())) {
                piece.remove(connectedPiece, direction.rotatedCW());
            }
        }
        connectedPieces = piece.getObjects(direction.rotatedCCW());
        for (LinkedSet<Piece> connectedPiece: connectedPieces) {
            if (!checkCollisionDanger(piece,connectedPiece,direction.rotatedCCW())){
                piece.remove(connectedPiece,direction.rotatedCCW());
            }
        }

    }


    private boolean movePieceSet(Set<LinkedSet<Piece>> pieceSet, Direction direction, int delta){
        boolean movable=true;
        for (LinkedSet<Piece> piece:pieceSet){
            movable&=piece.object.isMovable();
        }
        if (movable) {
            int dx,dy;
            if (direction.getOrientation()==Orientation.Y){
                dx=delta;
                dy=0;
            } else {
                dy=delta;
                dx=0;
            }
            for (LinkedSet<Piece> piece : pieceSet) {
                piece.object.move(dx, dy);

            }
        }
        return movable;

    }

    public void movePiece(Piece pieceToMove, Orientation orientation, int delta){

        LinkedSet<Piece> movingPiece = pieceSet.get(pieceToMove);
        if (movingPiece==null){
            throw new GameExceptions.PieceNotExistException("piece not exist");
        }
        int sign=delta>0?1:-1;
        Direction movingDirection = orientation.direction(sign);

        if (Math.abs(delta)>0){
            //movingDist=delta, it may change if collision is detected
            int movingDist=delta;

            //List for store all the pieces and then remove all the connected pieces to movingPiece
            List<LinkedSet<Piece>> pieceList = new ArrayList<>();

            //Add All Pieces
            pieceList.addAll(pieceSet.values());

            //Get all pieces connected to movingPiece in the direction of movement
            Set<LinkedSet<Piece>> connectedPieces = movingPiece.getObjects(movingDirection);

            //Remove all the connected pieces from the list
            pieceList.removeAll(connectedPieces);

            //Sort the pieces from closer to farther

            pieceList=pieceDistanceSorter(pieceList,movingPiece,movingDirection);
            //Collections.sort(pieceList,new PieceDistanceComparator(movingPiece,movingDirection,delta));

            boolean moved=true;
            boolean movedAtLeastOne=false;

            //Check every piece disconnected from the movingPiece
            for (LinkedSet<Piece> freePiece: pieceList){

                //Check for collision (Return a tuple of all the pieces colliding at the minimum distance founded)
                Tuple<Set<LinkedSet<Piece>>,Integer> collision = checkCollision(connectedPieces,freePiece,movingDirection,delta);

                //if the list in the tuple has any element: means that it was a collision
                if (collision.firstArgument.size()>0){

                    //Get the minimum collision distance founded
                    int minDist=collision.secondArgument;

                    //Try to move the pieces
                    moved=movePieceSet(connectedPieces,movingDirection,minDist);

                    //check if the pieces were successfully moved
                    if (moved){
                        movedAtLeastOne=true;
                        //they moved

                        //update the connection between pieces (disconnect all the pieces to the opposite direction and the ones that gets disconnected from other connected pieces)
                        movingPiece.move(movingDirection);

                        //add the connection between the ones who collided
                        for (LinkedSet<Piece> frontMovingPiece:collision.firstArgument){
                            frontMovingPiece.add(freePiece,movingDirection);
                        }

                        //add the Collided piece to the connected ones (because it wont be calculated again)
                        connectedPieces.add(freePiece);

                        //update the next moving distance
                        movingDist-=minDist;
                    } else {
                        //they didn't move

                        //update the movingDist (stop trying to move, it wont move anymore)
                        movingDist=0;

                        //stop checking
                        break;
                    }

                }
            }

            //if there was no collision or there is a moving distance left after all the collisions:
            if (Math.abs(movingDist)>0) {

                //if it is possible to keep moving (no collisions or all the collided pieces are movable)
                if (moved) {
                    //try to move the pieces the distance that left
                    if(movePieceSet(connectedPieces, movingDirection, movingDist)){
                        movedAtLeastOne=true;
                        //if moved:
                        //update the connection between pieces (disconnect all the pieces to the opposite direction and the ones that gets disconnected from other connected pieces)
                        movingPiece.move(movingDirection);
                    }

                }
            }
            if (movedAtLeastOne) {
                for (LinkedSet<Piece> piece : connectedPieces) {
                    updatePerpendicularConnections(piece, movingDirection);
                }
            }
        }
    }

    private Tuple<Set<LinkedSet<Piece>>,Integer> checkCollision (Set<LinkedSet<Piece>> frontPieces, LinkedSet<Piece> freePiece, Direction direction, int delta){

        //Create a tuple to save the collisions results
        List<Tuple<LinkedSet<Piece>,Integer>> collisionList=new ArrayList<>();

        //check if the connectedPieces contains the supposedly freePiece
        if (frontPieces.contains(freePiece)){
            //if exist: throw an error (This shouldn't happen ever)
            throw new GameExceptions.ConnectionExistException(freePiece + " already exist");
        }

        int minDistance = delta;
        //Check for every Piece in the moving front, the collision with freePiece
        for (LinkedSet<Piece> frontPiece: frontPieces){
            //get the result of the collision (Tuple with the status of the collision and the distance of the collision)
            Tuple<Boolean,Integer> collisionDistance = getCollisionDistance(frontPiece,freePiece,direction,delta);

            //check the status of the collision
            if (collisionDistance.firstArgument){
                //if is true => they collided
                //save the distance and the piece that collide with the freePiece
                collisionList.add(new Tuple<>(frontPiece, collisionDistance.secondArgument));
            }
        }

        //After checking all the pieces for collision, sort the List from lower to higher in the collision distance
        collisionList = collisionSorter(collisionList);

        //create a set to return only the first collision and all the other if they are at the same minimum distance
        Set<LinkedSet<Piece>> resultSet = new HashSet<>();

        //first if there were a collision
        if (collisionList.size()>0) {
            //there were at least one collision
            //because they are sorted from lower to higher collision distance, the 0 index must be the first to collide
            Tuple<LinkedSet<Piece>, Integer> firstCollision = collisionList.get(0);

            //Add the collision to the return set
            resultSet.add(firstCollision.firstArgument);

            //Get the minimum distance = collision distance
            minDistance = firstCollision.secondArgument;

            //Check if there are another collisions at the same distance
            for (int i = 1; i < collisionList.size(); i++) { //not checking 0 because it was already obtained

                //get the collision information of the index i
                Tuple<LinkedSet<Piece>, Integer> collision = collisionList.get(i);

                //check if the collision happens at the minimum distance
                if (collision.secondArgument == minDistance) {
                    //if it happens at the minimum distance, add the collision to the return set
                    resultSet.add(collision.firstArgument);

                } else if (collision.secondArgument > minDistance) {
                    //if it happen after, just stop the loop (the next should be greater than this one)
                    break;
                } else {
                    //if it happen before, there must be a problem in the comparator
                    throw new RuntimeException("Bad Sorting: " + collision.secondArgument + " < " + minDistance);
                }
            }

        }
        //finally return the pieces set which collided at the the minimum distance
        return new Tuple<>(resultSet,minDistance);



    }

    private boolean checkCollisionDanger(LinkedSet<Piece> movingPiece, LinkedSet<Piece> otherPiece, Direction direction){
        boolean a, b, c, d;
        Border movingBorder = movingPiece.object.getBorder(direction);
        Border otherBorder = otherPiece.object.getBorder(direction.reverse());

        int movingStart,movingEnd,otherStart,otherEnd;
        movingStart=movingBorder.getStart();
        movingEnd=movingBorder.getEnd();
        if (movingEnd<movingStart){
            int temp=movingEnd;
            movingEnd=movingStart;
            movingStart=temp;
        }

        otherStart=otherBorder.getStart();
        otherEnd=otherBorder.getEnd();
        if (otherEnd<otherStart){
            int temp=otherEnd;
            otherEnd=otherStart;
            otherStart=temp;
        }
        a=movingEnd>=otherEnd;
        b=movingEnd>=otherStart;
        c=movingStart>=otherEnd;
        d=movingStart>=otherStart;

        //return !(a && b && c && d) && (a || b ||c ||d );
        boolean result;
        result  = movingStart > otherStart  && movingStart < otherEnd;
        result |= movingEnd   > otherStart  && movingEnd   < otherEnd;
        result |= otherStart  > movingStart && otherStart  < movingEnd;
        result |= otherEnd    > movingStart && otherEnd    < movingEnd;
        result |= otherStart == movingStart && otherEnd   == movingEnd;
        return result;

    }

    private Tuple<Boolean,Integer> getCollisionDistance(LinkedSet<Piece> movingPiece, LinkedSet<Piece> otherPiece, Direction direction, int dist){
        Tuple<Boolean,Integer> result= new Tuple(false,dist);

        if (checkCollisionDanger(movingPiece,otherPiece,direction)){
            Border movingBorder = movingPiece.object.getBorder(direction);
            Border otherBorder = otherPiece.object.getBorder(direction.reverse());

            int a = movingBorder.getPos();

            int b = movingBorder.getPos() + dist;
            int c = otherBorder.getPos();
            result.firstArgument = ( b==c ||(c >= a && c <= b) || (c >= b && c <= a));
            if (result.firstArgument){
                int sign = dist>0 ? 1:-1;
                result.secondArgument =Math.abs(movingBorder.getPos() - otherBorder.getPos())*sign;
            }

        }
        return result;


    }

    private static class PieceDistanceComparator implements Comparator<LinkedSet<Piece>> {
        private Piece from;
        private Orientation orientation;
        int sign;

        public PieceDistanceComparator(LinkedSet<Piece> from, Direction direction, int dist) {
            this.from = from.object;
            this.orientation = direction.getOrientation();
            this.sign = dist>0? +1:-1;

        }

        private int getDistance(LinkedSet<Piece> piece){
            int fromValue,toValue;

            if (orientation.equals(Orientation.X)) {
                fromValue = from.getTopPos();
                toValue = piece.object.getTopPos();
            } else {
                fromValue=from.getLeftPos();
                toValue=piece.object.getLeftPos();
            }

            int number=(int)(toValue*1000 - fromValue*1000)*sign;
            if (number<0){
                number=Integer.MAX_VALUE;
            }
            return number;
        }

        @Override
        public int compare(LinkedSet<Piece> lhs, LinkedSet<Piece> rhs) {

            return getDistance(lhs)-getDistance(rhs);
        }
    }

    @Override
    public String toString() {
        String str="\n";
        for (LinkedSet<Piece> piece:pieceSet.values()) {
            str +=piece + "\n";
        }
        return str;
    }

    private static List<Tuple<LinkedSet<Piece>, Integer>> collisionSorter(Collection<Tuple<LinkedSet<Piece>, Integer>> set){

        List<Tuple<LinkedSet<Piece>, Integer>> list = new ArrayList<>(set);

        Tuple<LinkedSet<Piece>, Integer> temp;
        int n=list.size()-1;

        for (int i=n;i>0;i--){
            for (int j=0;j<n;j++){
                temp=list.get(j+1);
                if (Math.abs(list.get(j).secondArgument)>Math.abs(temp.secondArgument)){
                    list.set(j+1,list.get(j));
                    list.set(j,temp);
                }
            }

        }
        return list;

    }

    private static  List<LinkedSet<Piece>> pieceDistanceSorter(Collection <LinkedSet<Piece>> set, LinkedSet<Piece> fromPiece, Direction direction){
        int sign= (direction.equals(Direction.TOP) || direction.equals(Direction.LEFT))? -1: +1;
        List<LinkedSet<Piece>>list = new ArrayList<>(set);
        LinkedSet<Piece> temp;

        int fromPos=fromPiece.object.getBorder(direction).getPos();

        Iterator<LinkedSet<Piece>> iterator = list.iterator();
        while(iterator.hasNext()){
            LinkedSet<Piece> piece = iterator.next();
            int pos = piece.object.getBorder(direction.reverse()).getPos();
            if ((pos-fromPos)*sign<0){
                iterator.remove();
            }
        }

        int n=list.size()-1;
        for (int i=n;i>0;i--){
            for (int j=0;j<n;j++){
                temp=list.get(j+1);
                int tempPos=temp.object.getBorder(direction.reverse()).getPos();
                int posJ = list.get(j).object.getBorder(direction.reverse()).getPos();

                if (((posJ-fromPos)*sign)>((tempPos-fromPos)*sign)){
                    list.set(j+1,list.get(j));
                    list.set(j,temp);
                }
            }

        }
        return list;

    }

}
