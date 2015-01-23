package com.pocotopocopo.juego;


public class PieceConnection {
    private Piece piece1;
    private Piece piece2;
    private Side side;

    public PieceConnection(Piece piece1,Piece piece2, Side movingDirection){
        this.piece1=piece1;
        this.piece2=piece2;
        this.side=movingDirection;
        if (piece1.equals(piece2)){
            throw new GameExceptions.SamePieceException("same pieces");
        }
    }

    public Piece getPiece1() {
        return piece1;
    }

    public Piece getPiece2() {
        return piece2;
    }

    public Side getSide() {
        return side;
    }

    public boolean isReverse(PieceConnection connection){
        boolean sideMatch= (connection.side.getReverse().equals(this.side));
        boolean piece1Match= this.piece1.equals(connection.piece2);
        boolean piece2Match= this.piece2.equals(connection.piece1);

        return sideMatch && piece1Match && piece2Match;
    }

    public boolean isSame(PieceConnection connection){
        boolean sideMatch= (connection.side.equals(this.side));
        boolean piece1Match= this.piece1.equals(connection.piece1);
        boolean piece2Match= this.piece2.equals(connection.piece2);

        return sideMatch && piece1Match && piece2Match;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PieceConnection){
            PieceConnection pieceConnection= (PieceConnection)o;
            return this.isSame(pieceConnection) || this.isReverse(pieceConnection);
        }
        return false;
    }

    public Piece isConnected (Piece piece, Side movingDirection) throws GameExceptions.PieceNotConnectedException{
        if (this.side.equals(movingDirection) && this.piece1.equals(piece)) {
            return this.piece2;
        } else if(this.side.getReverse().equals(movingDirection) && this.piece2.equals(piece)){
            return this.piece1;
        } else {
            throw new GameExceptions.PieceNotConnectedException();
        }
    }

    @Override
    public String toString() {
        return piece1 + " " + side + " " +piece2;
    }
}