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

    public void moveHole(int index){
        pieceList.remove(null);
        pieceList.add(index,null);
    }

    public List<Piece> getPieceList(){
        return pieceList;
    }

    public Piece getPiece(int index){
        return pieceList.get(index);
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
        int index=j*columns+i;
        return index;
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
        int i=index%columns;
        int j=index/columns;
        switch (direction){
            case UP:
                for (int y=j-1;y>=0;y--){
                    possibleCollisionPieces.add(pieceList.get(getIndex(i,y)));
                }
                possibleCollisionPieces.add(getBorder(Direction.UP));
                break;
            case DOWN:
                for (int y=j+1;y<rows;y++){
                    possibleCollisionPieces.add(pieceList.get(getIndex(i,y)));
                }
                possibleCollisionPieces.add(getBorder(Direction.DOWN));
                break;
            case LEFT:
                for (int x=i-1;x>=0;x--){
                    possibleCollisionPieces.add(pieceList.get(getIndex(x,j)));
                }
                possibleCollisionPieces.add(getBorder(Direction.LEFT));
                break;
            case RIGHT:
                for (int x=i+1;x<columns;x++){
                    possibleCollisionPieces.add(pieceList.get(getIndex(x,j)));
                }
                possibleCollisionPieces.add(getBorder(Direction.RIGHT));
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
        boolean movable=true;
        private Direction finalAllowedDirection;
        //private int delta;

        /**
         * Constructor of the Movement
         * @param piece the piece which will be moved
         */
        public Movement(Piece piece){
            this.piece=piece;
            Tuple<Direction,Integer> directionAndMovements= getDirectionAndMovements(piece);
            this.finalAllowedDirection =directionAndMovements.firstArgument;
            this.allowedOrientation= finalAllowedDirection.getOrientation();
            if (allowedOrientation==null){
                movable=false;
            }

            movedPieces = new HashMap<>();

        }

        /**
         * Invalidate the movement, after is clear, all the calls to move() will throw an exception
         */
        public void clear(){
            this.piece=null;
            this.allowedOrientation=null;
            this.finalAllowedDirection=null;
            movedPieces=null;
            movable=false;
        }

        /**
         * Returns the piece of the movement
         * @return the piece of the movement
         */
        public Piece getPiece(){
            return piece;
        }


        /**
         * Returns if the movement is valid
         * @return true if is valid, false otherwise
         */
        public boolean isMovable() {
            return movable;
        }

        /**
         * Returns the final allowed movement direction of the piece in the movement
         * @return the final allowed movement direction of the piece in the movement
         */
        public Direction getFinalAllowedDirection() {
            return finalAllowedDirection;
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
            if (piece==null){
                throw new RuntimeException("Movement was cleared or the piece is null");
            }
            int movedDistance = 0;
            if (movable && direction.getOrientation().equals(allowedOrientation)) {
                /**
                 * @frontPiece the piece which will be at the front of the movement,
                 * it will be the first piece to collide
                 * @movingPieces is a list containing all the pieces which moves as a whole
                 * @possibleCollisionPieces is a list containing all the pieces in the
                 * direction of the movement, they have to be in order of appearance
                 * from the moving piece
                 * @movingDistance is the distance that movingPieces will try to move
                 * @movedDistance is the total distance moved
                 */
                List<Piece> movingPieces = new ArrayList<>();

                List<Piece> possibleCollisionPieces = getPossibleCollisionPieces(piece, direction);
                //remove the null piece because it is not a piece, it's a hole
                possibleCollisionPieces.remove(null);
                /*
                 if there is at least one piece to collide (at least the border), try to move.
                 Do nothing otherwise (it should be at least a border.

                 */
                int movingDistance = Math.abs(delta);
                Piece frontPiece = piece;
                movingPieces.add(piece);
                for (int i = 0; i < possibleCollisionPieces.size() && movingDistance > 0; i++) {
                    Piece collisionPiece = possibleCollisionPieces.get(i);

                    //check the collision
                    Tuple<Boolean, Integer> collision = getCollisionDistance(frontPiece, collisionPiece, direction, movingDistance);

                    /*
                    If the collision happens, then move the pieces to just the collision distance
                    update the moving pieces, front piece and the next moving distance.

                    If there was no collision, then move all the moving Distance.
                     */
                    if (collision.firstArgument) { //Collision happens
                        //get the collision distance
                        int collisionDistance = collision.secondArgument;
                        //move the pieces to just the collision distance
                        movePieceList(movingPieces, direction, collisionDistance);
                        //update the moved distance
                        movedDistance += collisionDistance;

                        /*
                        check if the collisionPiece is movable, if it is movable, then it will be
                        moved with all the other pieces, if it is not movable, then the movement
                        ends because no matter how many distance left, the pieces won't move any
                        more
                         */
                        if (collisionPiece.isMovable()) { //collided piece is movable
                            //update the movingPieces with the new collided Piece
                            movingPieces.add(collisionPiece);
                            //update the new frontPiece, now it will be the collisionPiece
                            frontPiece = collisionPiece;
                            //update the distance that left to move
                            movingDistance -= collisionDistance;
                        } else { // collided piece is not movable
                            //stop the movement, the pieces won't move any more
                            movingDistance = 0;
                        }

                    } else { //No collision detected

                        //move the pieces all the desired distance
                        movePieceList(movingPieces, direction, movingDistance);
                        movedDistance += movingDistance;
                        //and break the loop because there is no more distance to move
                        movingDistance = 0;
                    }


                }
            }
            return movedDistance;

        }

        /**
         * Moves all the pieces in the specified direction a distance of delta and update the
         * moved distance of each moved piece (for the snap adjustment)
         * @param pieceList the list of pieces to move
         * @param direction the direction of the moving piece
         * @param delta the wanted distance of the movement
         */
        private void movePieceList(List<Piece> pieceList, Direction direction, int delta){
            if (delta>0) {
                int dx, dy;
                if (direction.getOrientation() == Orientation.Y) {
                    dx = direction.getSign()*delta;
                    dy = 0;
                } else {
                    dy = direction.getSign()*delta;
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
         * @return the map containing the moved Pieces as the keys and the moved distance
         * as the values
         */
        public Map<Piece,Integer> getMovedPieces() {
            return movedPieces;
        }

        /**
         * Returns the allowed movement orientation of the
         * @return the allowed orientation
         */
        public Orientation getAllowedOrientation(){
            return allowedOrientation;
        }

    }

    /**
     * After a movement is finished, the user should call this function to snap the pieces to the
     * grid and to update the index of the grid
     * @param movement the finished movement
     * @return true if there was a movement (a piece change its position in the grid),
     * false otherwise
     */
    public boolean snapMovement(Movement movement) {
        boolean moved=false;
        /*
        If the movement is valid (movable) then snap to grid all the moved pieces withing the
        movement ant then update the index of the pieces that change its position in the grid
         */
        if (movement.movable) { //is movable
            //get the map of the moved pieces
            Map<Piece, Integer> movedPieces = movement.getMovedPieces();
            //get the piece which originated the movement
            Piece piece = movement.getPiece();
            //get the index of the piece which originated the movement
            // and its x, y (i,j) coordinates
            int index = pieceList.indexOf(piece);
            int i = index % columns;
            int j = index / columns;

            //get the index of the hole (null piece)
            // and its x, y (i,j) coordinates
            int nullIndex = pieceList.indexOf(null);
            int nullI = nullIndex % columns;
            int nullJ = nullIndex / columns;

            /*
            if the null piece is not on the same row or column, something went wrong,
            the movement should be invalid and movable should be false
             */
            if (i != nullI && j != nullJ) {
                throw new RuntimeException("Inconsistency in the indexes");
            }

            //create an array to put all the pieces which changes their position in the grid
            List<Piece> snappedPieces = new ArrayList<>();

            /*
            For each piece that has some movement, snap to grid
             */
            for (Map.Entry<Piece, Integer> entry : movedPieces.entrySet()) {
                //get the piece
                Piece pieceToSnap = entry.getKey();
                //get its movement
                int distance = entry.getValue();
                //get the movement sign
                int sign;
                if (distance > 0) {
                    sign = +1; //RIGHT or DOWN
                } else if (distance < 0) {
                    sign = -1; //LEFT or UP
                } else {
                    sign = 0; //No movement
                }
                //a variable to get the piece Width or Height depending on the orientation
                int size;

                int padding;
                //variables to store the movement on x or y
                int dx = 0;
                int dy = 0;

                /*
                check if the movement is in X or in Y, and then check if the movement is bigger than
                the half of the size in that orientation. if it is bigger then snap it to
                the next/previous position in the grid. If is lower then remove the movement
                 */
                /*
                if the movement is perpendicular to X => movement along Y or viceversa
                 */
                if (movement.getAllowedOrientation().equals(Orientation.X)) { //along Y
                    //get the piece's height
                    size = pieceToSnap.getPieceHeight();
                    padding = pieceToSnap.getPaddingY();
                    /*
                    if the distance in module is larger than the size of its height, then
                    the piece snaps to the next position, if it is not larger, then it snaps to the
                    original position
                     */
                    if (Math.abs(distance) >padding+(size / 2)) { //it is larger=> snap to next
                        //snap to next (or previous, depending on the sign)
                        dy = sign * (padding+size - Math.abs(distance));

                        snappedPieces.add(pieceToSnap);
                    } else {
                        //snap to the original position (revert the movement)
                        dy = -distance;
                    }
                } else { //along X

                    //get the piece's height
                    size = pieceToSnap.getPieceWidth();

                    padding = pieceToSnap.getPaddingX();
                    /*
                    if the distance in module is larger than the size of its width, then
                    the piece snaps to the next position, if it is not larger, then it snaps to the
                    original position
                     */
                    if (Math.abs(distance) > padding+(size / 2)) {
                        //snap to next (or previous, depending on the sign)
                        dx = sign * (padding+size - Math.abs(distance));
                        //add the piece to the snapped pieces list
                        snappedPieces.add(pieceToSnap);
                    } else {
                        //snap to the original position (revert the movement)
                        dx = -distance;
                    }
                }
                //move the piece according to the snapped coordinates
                pieceToSnap.move(dx, dy);
            }

            //UPDATING THE INDEXES
            /*
            There are only two possibilities if there was a piece which changes the index:
              1. The moving piece push all the other pieces
              2. The moving piece push all the other pieces and then went back to his original position
            If there were no pieces which changes its position in the grid, then the index
            are the same
             */
            //get the only possible movement direction for the piece that initiate the movement
            Direction direction = movement.getFinalAllowedDirection();
            /*
            if there is at least one snapped piece, update the indexes,
            (the movement's piece push all the other pieces, and it may got back to its position
            or stay in the new position)
             */
            if (snappedPieces.size() > 0) {
                moved=true;
                //the piece pushes all the other pieces
                moveInArray(piece, direction);
                /*
                if the movement's piece was not snapped to a new position, then it went back to its
                original position.
                 */
                if (!snappedPieces.contains(piece)) {
                    //move the piece back to its original index;
                    moveInArray(piece, direction.reverse());
                }
            } else{
                moved=false;
            }
            //the snap should reset the movement
            movement.clear();
        }
        return moved;

    }

    /**
     * Returns a Tuple of the direction and how many pieces are between the selected Piece
     * and the hole (or null) (including the actual piece)
     * @param piece from where the direction points
     * @return a Tuple containing the direction and how many pieces for getting the hole in that direction
     *
     */
    private Tuple<Direction,Integer> getDirectionAndMovements(Piece piece){
        if (!pieceList.contains(piece)){
            throw new GameExceptions.PieceNotExistException();
        }

        int index=pieceList.indexOf(piece);
        int i=index%columns;
        int j=index/columns;

        int ini=-1;
        int inj=-1;
        for (int x=0;x<columns;x++){
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
        for (int y=0;y<rows;y++){
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

        Tuple<Direction,Integer> movement= getDirectionAndMovements(piece);
        int index=pieceList.indexOf(piece);
        int i=index%columns;
        int j=index/columns;
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

            int b = movingBorder.getPos() + dist*direction.getSign();
            int c = otherBorder.getPos();
            result.firstArgument = ( b==c ||(c >= a && c <= b) || (c >= b && c <= a));
            if (result.firstArgument){
                int sign = dist>0 ? 1:-1;
                result.secondArgument =Math.abs(movingBorder.getPos() - otherBorder.getPos())*sign;
            }

        }
        return result;
    }


    public int getPieceIndex(Piece piece){
        return pieceList.indexOf(piece);
    }

}
