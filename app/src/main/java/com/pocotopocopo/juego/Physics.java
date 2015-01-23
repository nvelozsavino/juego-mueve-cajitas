package com.pocotopocopo.juego;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nico on 22/01/15.
 */
public class Physics {

    private final static String TAG="Physics";
    private Set<Piece> pieceSet;
    private Set<PieceConnection> connectionSet;

    public Physics(Collection<Piece> pieces){
        this.pieceSet.addAll(pieces);
    }

    public Physics(){
        this.pieceSet=new HashSet<>();
        this.connectionSet =new HashSet<>();
    }

    public Set<Piece> getPieceSet() {
        return pieceSet;
    }

    public Set<PieceConnection> getConnectionSet() {
        return connectionSet;
    }

    public void addPieces(Collection<Piece> pieces){
        pieceSet.addAll(pieces);
    }

    public void clearPhysics(){
        pieceSet.clear();
        connectionSet.clear();
    }

    public void addPiece(Piece piece){
        this.pieceSet.add(piece);
    }

    private void addConnection(Piece frontPiece, Piece collidedPiece, Side movingDirection){
        PieceConnection pieceConnection = new PieceConnection(frontPiece,collidedPiece,movingDirection);

        if (connectionSet.contains(pieceConnection)){
            throw new GameExceptions.ConnectionExistException("Connection between piece "+ frontPiece + " and " + collidedPiece + " allready exist in the registered connections");
        }

        connectionSet.add(pieceConnection);
    }

    private void pieceNotExistThrow(Piece piece){
        if (!pieceSet.contains(piece)){
            throw new GameExceptions.PieceNotExistException("Piece " + piece + " not exist in the piece set");
        }
    }

    private void deleteConnections(Piece piece, Side direction){
        List<PieceConnection> connectionsToRemove=new ArrayList<>();
        for (PieceConnection connection: connectionSet){
            try {
                Piece otherPiece =connection.isConnected(piece, direction);
                connectionsToRemove.add(connection);
            } catch (GameExceptions.PieceNotConnectedException exception){
                Log.d(TAG, "Piece " + piece + " is not in this connection");
            }
        }
        connectionSet.removeAll(connectionsToRemove);
    }

    private void updatePerpendicularConnections(Piece piece, Side direction){
        List<PieceConnection> connectionsToRemove=new ArrayList<>();

        for (PieceConnection connection: connectionSet){
            try {
                Piece otherPiece =connection.isConnected(piece, direction.getRotatedCW());
                boolean collisionDanger=checkCollisionDanger(piece,otherPiece,direction.getRotatedCW());
                if (!collisionDanger){
                    connectionsToRemove.add(connection);
                }

            } catch (GameExceptions.PieceNotConnectedException exception){
                Log.d(TAG, "Piece " + piece + " is not in this connection");
            }
        }
        connectionSet.removeAll(connectionsToRemove);

    }

    private Set<Piece> getConnectedPieces(Piece fromPiece,Side movingDirection){
        Set<Piece> connectedPieces = new HashSet<>();
        return getConnectedPieces(fromPiece,movingDirection,connectedPieces);

    }


    private Set<Piece> getConnectedPieces (Piece fromPiece, Side movingDirection, Set<Piece> connectedPieces){
        connectedPieces.add(fromPiece);
        for (PieceConnection connection: connectionSet){
            try {
                Piece otherPiece =connection.isConnected(fromPiece, movingDirection);
                if (!connectedPieces.contains(otherPiece)) {
                    connectedPieces = getConnectedPieces(otherPiece, movingDirection, connectedPieces);
                }
            } catch (GameExceptions.PieceNotConnectedException exception){
                Log.d(TAG, "Piece " + fromPiece + " is not in this connection");
            }
        }
        return connectedPieces;
    }

    private boolean movePieceSet(Set<Piece> pieceSet, Side direction, float delta){
        boolean movable=true;
        for (Piece piece:pieceSet){
            movable&=piece.isMovable();
        }
        if (movable) {
            float dx,dy;
            if (direction.getOrientation()==Orientation.Y){
                dx=delta;
                dy=0;
            } else {
                dy=delta;
                dx=0;
            }
            for (Piece piece : pieceSet) {
                piece.move(dx,dy);
                updatePerpendicularConnections(piece,direction);
            }
        }
        return movable;

    }

    public void movePiece(Piece movingPiece, Orientation orientation, float delta){
        int sign=delta>0?1:-1;
        Side movingDirection = orientation.direction(sign);

        float movingDist=delta;
        while (Math.abs(delta)>0){
            List<Piece> pieceList = new ArrayList<>();
            pieceList.addAll(pieceSet);
            Set<Piece> connectedPieces = getConnectedPieces(movingPiece,movingDirection);
            pieceList.removeAll(connectedPieces);
            Collections.sort(pieceList,new PieceDistanceComparator(movingPiece,movingDirection,delta));
            boolean collided=false;
            boolean moved=true;
            for (Piece freePiece: pieceList){
                Tuple<Set<Piece>,Float> collision = checkCollision(movingPiece,freePiece,movingDirection,delta);
                if (collision.firstArgument.size()>0){
                    movingDist=collision.secondArgument;
                    moved=movePieceSet(connectedPieces,movingDirection,movingDist);
                    if (moved){

                        for (Piece frontMovingPiece:collision.firstArgument){
                            addConnection(frontMovingPiece,freePiece,movingDirection);
                        }
                    }
                    collided=true;
                    break;
                }
            }
            if (!collided){
                moved=movePieceSet(connectedPieces,movingDirection,movingDist);
            }
            if (moved){
                deleteConnections(movingPiece,movingDirection.getReverse());
                delta-=movingDist;
            } else {
                delta=0;
            }
        }
    }

    private Tuple<Set<Piece>,Float> checkCollision (Piece movingPiece, Piece freePiece, Side direction, float delta){
        List<Tuple<Piece,Float>> collisionList=new ArrayList<>();

        Set<Piece> frontPieces = getConnectedPieces(movingPiece,direction);
        if (frontPieces.contains(freePiece)){
            throw new GameExceptions.SamePieceException(freePiece + " is connected to " +movingPiece);
        }
        float minDistance = delta;
        for (Piece frontPiece: frontPieces){
            Tuple<Boolean,Float> collisionDistance = getCollisionDistance(frontPiece,freePiece,direction,delta);
            if (collisionDistance.firstArgument){
                collisionList.add(new Tuple<>(frontPiece, collisionDistance.secondArgument));
            }
        }
        Collections.sort(collisionList,new Comparator<Tuple<Piece, Float>>() {
            @Override
            public int compare(Tuple<Piece, Float> lhs, Tuple<Piece, Float> rhs) {

                return (int)(1000*(lhs.secondArgument - rhs.secondArgument));
            }
        });

        Set <Piece> resultSet = new HashSet<>();
        if (collisionList.size()>0) {
            Tuple<Piece, Float> firstCollision = collisionList.get(0);
            resultSet.add(firstCollision.firstArgument);
            minDistance = firstCollision.secondArgument;
            for (int i = 1; i < collisionList.size(); i++) {
                Tuple<Piece, Float> collision = collisionList.get(i);
                if (collision.secondArgument == minDistance) {
                    resultSet.add(collision.firstArgument);
                } else if (collision.secondArgument > minDistance) {
                    break;
                } else {
                    throw new RuntimeException("Bad Sorting");
                }
            }
        }
        return new Tuple<>(resultSet,minDistance);



    }

    private boolean checkCollisionDanger(Piece movingPiece, Piece otherPiece, Side direction){
        boolean result;
        Border movingBorder = movingPiece.getBorder(direction);
        Border otherBorder = otherPiece.getBorder(direction.getReverse());
        result  = movingBorder.getStart() >  otherBorder.getStart()  && movingBorder.getStart() <  otherBorder.getEnd();
        result |= movingBorder.getEnd()   >  otherBorder.getStart()  && movingBorder.getEnd()   <  otherBorder.getEnd();
        result |= otherBorder.getStart()  >  movingBorder.getStart() && otherBorder.getStart()  <  movingBorder.getEnd();
        result |= otherBorder.getEnd()    >  movingBorder.getStart() && otherBorder.getEnd()    <  movingBorder.getEnd();
        result |= otherBorder.getStart()  == movingBorder.getStart() && otherBorder.getEnd()    == movingBorder.getEnd();
        return result;
    }

    private Tuple<Boolean,Float> getCollisionDistance(Piece movingPiece, Piece otherPiece, Side direction, float dist){
        Tuple<Boolean,Float> result= new Tuple(false,dist);

        if (checkCollisionDanger(movingPiece,otherPiece,direction)){
            Border movingBorder = movingPiece.getBorder(direction);
            Border otherBorder = otherPiece.getBorder(direction.getReverse());

            float a = movingBorder.getPos();

            float b = movingBorder.getPos() + dist;
            float c = otherBorder.getPos();
            result.firstArgument = ((c >= a && c <= b) || (c >= b && c <= a));
            if (result.firstArgument){
                int sign = dist>0 ? 1:-1;
                result.secondArgument =Math.abs(movingBorder.getPos() - otherBorder.getPos())*sign;
            }

        }
        return result;


    }

    private static class PieceDistanceComparator implements Comparator<Piece> {
        private Piece from;
        private Orientation orientation;
        int sign;

        public PieceDistanceComparator(Piece from, Side direction, float dist) {
            this.from = from;
            this.orientation = direction.getOrientation();
            this.sign = dist>0? +1:-1;

        }

        private int getDistance(Piece piece){
            int fromValue; int toValue;

            if (orientation.equals(Orientation.X)) {
                fromValue = (int) (1000 * from.getTopPos());
                toValue = (int) (1000 * piece.getTopPos());
            } else {
                fromValue=(int)(1000*from.getLeftPos());
                toValue=(int)(1000*piece.getLeftPos());
            }

            int number=(toValue - fromValue)*sign;
            if (number<0){
                number=Integer.MAX_VALUE;
            }
            return number;
        }

        @Override
        public int compare(Piece lhs, Piece rhs) {

            return getDistance(lhs)-getDistance(rhs);
        }
    }

}
