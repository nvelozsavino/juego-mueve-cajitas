package com.pocotopocopo.juego;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by nico on 23/01/15.
 */
public class ConnectedPiece{
    private Set<ConnectedPiece> leftDirectConnections=new HashSet<>();
    private Set<ConnectedPiece> rightDirectConnections=new HashSet<>();
    private Set<ConnectedPiece> topDirectConnections=new HashSet<>();
    private Set<ConnectedPiece> bottomDirectConnections=new HashSet<>();

    private Map<ConnectedPiece,Set<ConnectedPiece>> leftInDirectConnections=new HashMap<>();
    private Map<ConnectedPiece,Set<ConnectedPiece>> rightInDirectConnections=new HashMap<>();
    private Map<ConnectedPiece,Set<ConnectedPiece>> topInDirectConnections=new HashMap<>();
    private Map<ConnectedPiece,Set<ConnectedPiece>> bottomInDirectConnections=new HashMap<>();
    private Piece origin;

    public ConnectedPiece(Piece piece){
        this.origin=piece;
    }

    public void add(ConnectedPiece pieceToAdd,ConnectedPiece throughPiece, Direction direction){
        for (ConnectedPiece piece: getConnections(direction)){
            piece.addToThis(pieceToAdd,throughPiece,direction);
        }
        pieceToAdd.getDirectConnections(direction.reverse()).add(throughPiece);
        Set<ConnectedPiece> reverseIndirect;
        for (ConnectedPiece piece: throughPiece.getDirectConnections(direction.reverse())){
            reverseIndirect=new HashSet<>();
            reverseIndirect.add(throughPiece);
            pieceToAdd.getIndirectConnections(direction.reverse()).put(piece,reverseIndirect);
        }
        pieceToAdd.getIndirectConnections(direction.reverse()).putAll(throughPiece.getIndirectConnections(direction.reverse()));



    }

    private void addToThis(ConnectedPiece pieceToAdd, ConnectedPiece throughPiece, Direction direction){
        Set<ConnectedPiece> directConnection = getDirectConnections(direction);
        Map<ConnectedPiece,Set<ConnectedPiece>> indirectConnection = getIndirectConnections(direction);
        if (throughPiece==this){
            directConnection.add(pieceToAdd);
        } else {
            if (directConnection.contains(throughPiece)|| indirectConnection.containsKey(throughPiece)){
                if (indirectConnection.containsKey(pieceToAdd)){
                    indirectConnection.get(pieceToAdd).add(throughPiece);
                } else {
                    Set<ConnectedPiece> newSet = new HashSet<>();
                    newSet.add(throughPiece);
                    indirectConnection.put(pieceToAdd,newSet);
                }

            } else {
                throw new RuntimeException("Inconsistency found");
                //Set<ConnectedPiece> newSet = new HashSet<>();
                //indirectConnection.put(pieceToAdd,newSet);
            }

        }

    }

    private void checkInconsistencies(Direction direction){
        Map<ConnectedPiece,Set<ConnectedPiece>> indirectConnection = getIndirectConnections(direction);
        for (Set<ConnectedPiece> set:indirectConnection.values()){
            if (set.size()==0){
                throw new RuntimeException("Inconsistency found");
            }
        }
    }

    private Set<ConnectedPiece> getDirectConnections(Direction direction){
        switch (direction){
            case TOP:
                return topDirectConnections;
            case LEFT:
                return leftDirectConnections;
            case BOTTOM:
                return bottomDirectConnections;
            case RIGHT:
                return rightDirectConnections;
            default:
                return null;
        }
    }

    public Set<ConnectedPiece> getConnections(Direction direction){
        Set<ConnectedPiece> connectedPieces = new HashSet<>();
        connectedPieces.add(this);
        connectedPieces.addAll(getDirectConnections(direction));
        connectedPieces.addAll(getIndirectConnections(direction).keySet());
        return connectedPieces;
    }





    private Map<ConnectedPiece,Set<ConnectedPiece>> getIndirectConnections(Direction direction){
        switch (direction){
            case TOP:
                return topInDirectConnections;
            case LEFT:
                return leftInDirectConnections;
            case BOTTOM:
                return bottomInDirectConnections;
            case RIGHT:
                return rightInDirectConnections;
            default:
                return null;
        }
    }


    public void clearConnections(Direction direction){
        Set<ConnectedPiece> connectedPieceSet=getConnections(direction);
        for (ConnectedPiece piece: connectedPieceSet){
            piece.getConnections(direction.reverse()).remove(this);
        }
        getConnections(direction).clear();
    }

    public void clearConnections(){
        clearConnections(Direction.TOP);
        clearConnections(Direction.RIGHT);
        clearConnections(Direction.LEFT);
        clearConnections(Direction.BOTTOM);
    }

    public void addConnection(ConnectedPiece piece, Direction direction) {
        Set<ConnectedPiece> thisSet = getConnections(direction);
        Set<ConnectedPiece> otherSet = getConnections(direction.reverse());
        thisSet.add(piece);
        otherSet.add(this);
    }

    public void removeConnection(ConnectedPiece piece, Direction direction){
        Set<ConnectedPiece> thisSet = getConnections(direction);
        Set<ConnectedPiece> otherSet = getConnections(direction.reverse());
        thisSet.remove(piece);
        otherSet.remove(this);
    }

    public Piece getPiece(){
        return origin;
    }


}
