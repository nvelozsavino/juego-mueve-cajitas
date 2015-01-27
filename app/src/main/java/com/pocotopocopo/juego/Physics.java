package com.pocotopocopo.juego;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nico on 22/01/15.
 */
public class Physics {

    private final static String TAG="Physics";
    private final int rows;
    private final int columns;
    private List<Piece> pieceList;
    private Piece borderLeft;
    private Piece borderRight;
    private Piece borderDown;
    private Piece borderUp;


    /**
     * Constructor of Physics Class
     * @param rows  number of rows of the grid
     * @param columns number of columns of the grid
     */
    public Physics(int rows,int columns){
        if (rows<0 || columns<0){
            throw new IllegalArgumentException("rows and/or column out of bounds");
        }
        pieceList= new ArrayList<>();
        this.columns=columns;
        this.rows=rows;

        pieceList.add(null);
    }

    /**
     * Add all the pieces in the pieces collection, removing the duplicates and if there is enough
     * space for storing all the new unique pieces
     * @param pieces Piece Collection
     */
    public void addPieces(Collection<Piece> pieces){
        Set<Piece> pieceSet=new HashSet<>(pieceList);
        pieceSet.addAll(pieces);
        if (pieceSet.size()<columns*rows) {
            pieces.removeAll(pieceList);
            pieceList.addAll(pieces);
        }
    }

    /**
     * Add Piece to the pieceList if there are less than rows*columns pieces
     * @param piece
     */
    public void addPiece(Piece piece){
        if (pieceList.size()<columns*rows && !pieceList.contains(piece)) {
            pieceList.add(piece);
        }
    }

    /**
     * Add the borders of the Grid. It doesn't assure the correct position of the border
     * @param piece The piece acting as a border of the grid in the direction
     * @param direction The direction in which the border will stop the movement of the other pieces.
     */
    public void addBorder(Piece piece, Direction direction){
        switch (direction){
            case UP:
                this.borderUp=piece;
                return;
            case DOWN:
                this.borderDown=piece;
                return;
            case LEFT:
                this.borderLeft=piece;
                return;
            case RIGHT:
                this.borderRight=piece;
                return;
        }
    }

    /**
     * Returns the border which will stop the movement in the specified direction
     * @param direction The direction in which the border will stop the movement of the other pieces.
     * @return Returns the border which will stop the movement in the specified direction
     */
    public Piece getBorder(Direction direction){
        switch (direction){
            case UP:
                return borderUp;
            case DOWN:
                return  borderDown;
            case LEFT:
                return borderLeft;

            case RIGHT:
                return borderRight;
        }
        return null;
    }


    /**
     * Get the lineal index given the row and column index
     * @param i row index
     * @param j column index
     * @return  lineal index
     */
    private int getIndex(int i, int j){
        return j*rows + i;
    }


    /**
     * Get the allowed movement orientation of a piece according its position and the position of
     * the null piece
     * @param piece the piece of the wanted orientation
     * @return Returns the allowed movement orientation of a piece according its position and
     * the position of the null piece
     */
    private Orientation getAllowedMovementOrientation(Piece piece){
        return getAllowedMovementDirection(piece).getOrientation();


    }

    /**
     * Get only the direction of movement allowed for the piece
     * @param piece The piece which the direction of the movement will be calculated
     * @return returns the allowed movement direction
     */
    private Direction getAllowedMovementDirection(Piece piece){
        return getMaxMovements(piece).firstArgument;
    }

    /**
     * Get a list of pieces which will be in the way of the moving piece
     * @param movingPiece the piece which will be moving
     * @param direction the direction of the moving piece
     * @return all the possible pieces in the way
     */
    private List<Piece> getPossibleCollisionPieces(Piece movingPiece, Direction direction){
        if (!pieceList.contains(movingPiece)){
            throw new GameExceptions.PieceNotExistException();
        }
        List<Piece> possibleCollisionPieces=new ArrayList<>();
        int index=pieceList.indexOf(movingPiece);
        int i=index%rows;
        int j=index/rows;
        switch (direction){
            case UP:
                for (int y=j-1;y>=0;y--){
                    possibleCollisionPieces.add(pieceList.get(getIndex(i,y)));
                }
                break;
            case DOWN:
                for (int y=j+1;y<rows;y++){
                    possibleCollisionPieces.add(pieceList.get(getIndex(i,y)));
                }
                break;
            case LEFT:
                for (int x=i-1;x>=0;x--){
                    possibleCollisionPieces.add(pieceList.get(getIndex(x,i)));
                }
                break;
            case RIGHT:
                for (int x=i+1;x<columns;x++){
                    possibleCollisionPieces.add(pieceList.get(getIndex(x,i)));
                }
                break;
        }
        return possibleCollisionPieces;
    }


    /**
     * Class to move the pieces
     */
    public class Movement{


        private Map<Piece,Integer> movedPieces;
        //private Direction direction;
        private Orientation allowedOrientation;
        private Piece piece;
        //private int delta;

        /**
         * Constructor of the Movement
         * @param piece the piece which will be moved
         */
        public Movement(Piece piece){
            this.piece=piece;
            this.allowedOrientation= getAllowedMovementOrientation(piece);
            movedPieces = new HashMap<>();

        }

        /**
         * Returns the piece of the movement
         * @return the piece of the movement
         */
        public Piece getPiece(){
            return piece;
        }

        /**
         * Checks if the piece has space to move in the direction. If the list of pieces which
         * could collide contains the hole piece (null) or there is no piece in the way,
         * then, the piece could move
         * @param possibleCollisionPieces A list of the pieces which could colide
         * @param direction the direction of the moving piece
         * @return true if the piece can be moved along direction
         */
        private boolean hasSpaceToMove(List<Piece> possibleCollisionPieces,Direction direction){
            if (!possibleCollisionPieces.isEmpty() && possibleCollisionPieces.contains(null)){
                possibleCollisionPieces.remove(null);
                Piece border=getBorder(direction);
                if (border!=null){
                    possibleCollisionPieces.add(border);
                }
                return true;
            }
            return false;
        }


        /**
         * Move the piece in the direction and a delta distance. if the piece collides with another
         * piece, then they both moved until there is no more distance to move or the group collide
         * with a border or unmovable piece
         * @param direction the direction of the moving piece
         * @param delta the wanted distance of the movement
         * @return the moved distance
         */
        public int move(Direction direction, int delta){
            List<Piece> movingPieces = new ArrayList<>();
            int movedDistance=0;
            List<Piece> possibleCollisionPieces = getPossibleCollisionPieces(piece,direction);
            if (allowedOrientation.equals(direction.getOrientation()) &&delta>0 && piece.isMovable() && hasSpaceToMove(possibleCollisionPieces,direction)){

                Piece movingPiece=piece;

                movingPieces.add(piece);
                int movingDistance=delta;
                for (Piece collisionPiece: possibleCollisionPieces){

                    Tuple<Boolean, Integer> collision = getCollisionDistance(movingPiece, collisionPiece, direction, movingDistance);
                    if (collision.firstArgument) {
                        int collisionDistance = collision.secondArgument;
                        movePieceList(movingPieces, direction, collisionDistance);
                        movingPiece = collisionPiece;
                        if (collisionPiece.isMovable()) {
                            movingPieces.add(collisionPiece);
                            movingDistance -= collisionDistance;
                            movedDistance+=collisionDistance;
                        } else {
                            movingDistance=0;
                            break;
                        }
                    } else{
                        if (movingDistance>0){
                            movePieceList(movingPieces, direction, movingDistance);
                            movedDistance+=movingDistance;
                            movingDistance=0;
                            break;
                        }
                    }

                }
                if (movingDistance>0) {
                    movePieceList(movingPieces, direction, movingDistance);
                }
            }
            return movedDistance;
        }

        /**
         * Moves all the pieces in the specified direction a distance of delta
         * @param pieceList the list of pieces to move
         * @param direction the direction of the moving piece
         * @param delta the wanted distance of the movement
         */
        private void movePieceList(List<Piece> pieceList, Direction direction, int delta){
            if (delta>0) {
                int dx, dy;
                if (direction.getOrientation() == Orientation.Y) {
                    dx = delta;
                    dy = 0;
                } else {
                    dy = delta;
                    dx = 0;
                }
                for (Piece piece : pieceList) {
                    int pieceDist = 0;
                    if (movedPieces.containsKey(piece)) {
                        pieceDist = movedPieces.get(piece);
                    }
                    pieceDist += signDistance(direction, delta);
                    movedPieces.put(piece, pieceDist);

                    piece.move(dx, dy);

                }
            }

        }

        /**
         * Returns the moved distance with the sign:
         * +1 if the direction is RIGHT or DOWN
         * -1 if the direction is LEFT or UP
         *  0 if the direction is NONE
         * @param direction the direction of the moving piece
         * @param movedDistance the moved distance
         * @return  +1 if the direction is RIGHT or DOWN, or -1 if the direction is LEFT or UP,
         * 0 otherwise
         */
        private int signDistance(Direction direction, int movedDistance) {
            if (allowedOrientation.equals(direction.getOrientation())) {
                return direction.getSign()*movedDistance;
            }
            return 0;
        }


        /**
         * Return the map containing the moved Pieces as the keys and the moved distance
         * as the values
         * @return Return the map containing the moved Pieces as the keys and the moved distance
         * as the values
         */
        public Map<Piece,Integer> getMovedPieces() {
            return movedPieces;
        }

        /**
         * Returns the allowed movement orientation of the
         * @return
         */
        public Orientation getAllowedOrientation(){
            return allowedOrientation;
        }

    }

    /**
     * After a movement is finished, the user should call this function to snap the pieces to the
     * grid and to update the index of the grid
     * @param movement the finished movement
     */
    public void snapMovement(Movement movement) {
        Map<Piece,Integer> movedPieces=movement.getMovedPieces();
        Piece piece=movement.getPiece();
        int index=pieceList.indexOf(piece);
        int i=index%rows;
        int j=index/rows;
        int nullIndex=pieceList.indexOf(null);
        int nullI=nullIndex%rows;
        int nullJ=nullIndex/rows;

        /**
         * if the null piece is not on the same row or column, something went wrong
         */
        if (i!=nullI && j!=nullJ){
            throw new RuntimeException("algo mal");
        }


        List<Piece> snappedPieces=new ArrayList<>();

        /**
         * For each piece that has some movement, snap to grid
         */
        for (Map.Entry<Piece,Integer> entry:movedPieces.entrySet()){
            Piece pieceToSnap=entry.getKey();
            int distance=entry.getValue();
            int sign;
            Direction direction;
            if (distance>0){
                sign=+1;
            }else if (distance<0){
                sign=-1;
            } else {
                sign=0;
            }
            direction=movement.getAllowedOrientation().direction(sign);
            int size;
            int dx=0;
            int dy=0;

            /**
             * check if the movement is in X or in Y, and then check if the movement is bigger than
             * the half of the size in that orientation. if it is bigger then snap it to
             * the next/previous position in the grid. If is lower then remove the movement
             */
            if (movement.getAllowedOrientation().equals(Orientation.X)){
                size=pieceToSnap.getWidth();
                if (Math.abs(distance)>size){
                    dx=sign*(size-Math.abs(distance));

                    snappedPieces.add(pieceToSnap);
                } else {
                    dx=-distance;
                }
            } else {
                size=pieceToSnap.getHeight();
                if (Math.abs(distance)>size){
                    dy=sign*(size-Math.abs(distance));

                    snappedPieces.add(pieceToSnap);
                } else {
                    dy=-distance;
                }
            }
            pieceToSnap.move(dx,dy);
        }

        /**
         * There are only two possibilities if there was a piece which changes the index:
         *  1. The moving piece push all the other pieces
         *  2. The moving piece push all the other pieces and then went back to his original position
         */
        Direction direction=getAllowedMovementDirection(piece);
        if (snappedPieces.size()>0) {
            moveInArray(piece, direction);
            if (!snappedPieces.contains(piece)) {
                moveInArray(piece,direction.reverse());
            }
        }


    }

    /**
     * Returns a Tuple of the direction and how many pieces are between the selected Piece
     * and the hole (or null) (including the actual piece)
     * @param piece from where the direction points
     * @return a Tuple containing the direction and how many pieces for getting the hole in that direction
     *
     */
    private Tuple<Direction,Integer> getMaxMovements(Piece piece){
        if (!pieceList.contains(piece)){
            throw new GameExceptions.PieceNotExistException();
        }

        int index=pieceList.indexOf(piece);
        int i=index%rows;
        int j=index/rows;

        int ini=-1;
        int inj=-1;
        for (int x=0;x<rows;x++){
            if (pieceList.get(getIndex(x,j))==null){
                ini=x;
                if (ini<i){
                    return new Tuple<>(Direction.LEFT, i-ini);
                } else if (ini>i){
                    return new Tuple<>(Direction.RIGHT,ini-i);
                } else {
                    throw new RuntimeException("Piece 0 selected");
                }
            }
        }
        for (int y=0;y<columns;y++){
            if (pieceList.get(getIndex(i, y))==null){
                inj=y;
                if (inj<j){
                    return new Tuple<>(Direction.UP,j-inj);
                } else if (inj>j){
                    return new Tuple<>(Direction.DOWN,inj-j);
                } else {
                    throw new RuntimeException("Piece 0 selected");
                }
            }
        }
        return new Tuple<>(Direction.NONE,0);
    }

    /**
     * Move the indexes of the pieces when a selected piece is moved in the specified direction
     * @param piece Piece to move
     * @param direction Direction to move the piece
     */
    private void moveInArray(Piece piece,Direction direction){

        Tuple<Direction,Integer> movement=getMaxMovements(piece);
        int index=pieceList.indexOf(piece);
        int i=index%rows;
        int j=index/rows;
        if (movement.firstArgument.equals(direction)){

            for (int x=0;x<movement.secondArgument;x++){

                switch (direction) {
                    case UP:
                        j--;
                        break;
                    case DOWN:
                        j++;
                        break;
                    case LEFT:
                        i--;
                        break;
                    case RIGHT:
                        i++;
                        break;
                    default:
                        break;
                }
                swap(index, getIndex(i,j));

            }
        }

    }

    /**
     * Swap the Piece on indexA with the Piece on indexB
     * @param indexA
     * @param indexB
     */
    private void swap(int indexA, int indexB){
        Piece temp=pieceList.get(indexA);
        pieceList.set(indexA,pieceList.get(indexB));
        pieceList.set(indexB,temp);
    }


    /**
     * Checks if two pieces are in danger of collision if the movingPiece is moved in the
     * specified direction
     * @param movingPiece is the moving piece
     * @param otherPiece is the piece that could collide with the moving piece
     * @param direction the direction of the moving piece
     * @return Returns true if the danger is real, false if there is no danger.
     */
    private boolean checkCollisionDanger(Piece movingPiece, Piece otherPiece, Direction direction){
        boolean a, b, c, d;
        Border movingBorder = movingPiece.getBorder(direction);
        Border otherBorder = otherPiece.getBorder(direction.reverse());

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

    /**
     * Checks if the movingPiece collide with otherPiece if it is moved along the specified
     * direction and distance. Returns a Tuple containing as the first argument, if the collision
     * is true or false, and as the second argument the collision distance if the collision was
     * true, otherwise, the second argument will be the same as the distance parameter.
     * @param movingPiece is the moving piece
     * @param otherPiece is the piece that could collide with the moving piece
     * @param direction the direction of the moving piece
     * @param dist the distance of the movement
     * @return Returns a Tuple containing as the first argument, if the collision
     * is true or false, and as the second argument the collision distance if the collision was
     * true, otherwise, the second argument will be the same as the distance parameter.
     */
    private Tuple<Boolean,Integer> getCollisionDistance(Piece movingPiece, Piece otherPiece, Direction direction, int dist){
        Tuple<Boolean,Integer> result= new Tuple(false,dist);

        if (checkCollisionDanger(movingPiece,otherPiece,direction)){
            Border movingBorder = movingPiece.getBorder(direction);
            Border otherBorder = otherPiece.getBorder(direction.reverse());

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


}
