package com.pocotopocopo.juego;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nico on 21/01/15.
 */
public class Piece extends View {
    private static final String TAG = "Piece";
    private int top, left;
    private int width, height;
    private int number;
    private boolean selected=false;
    public List<Piece> contactTop, contactLeft, contactRight, contactBottom;
    private Border borderLeft, borderRight, borderTop, borderBottom;
    private boolean movable=true;
    private boolean numerable=false;
    public boolean border=false;
    private int lastPos;
    private Bitmap bitmap;
    //private Rect rInic;
    private int paddingX=0;
    private int paddingY=0;



    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
        invalidate();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        invalidate();
    }


    public int getPieceWidth() {
        return width;
    }

    public int getPieceHeight() {
        return height;
    }

    public Piece(Context context,int number){
        this(context,0,0,0,0,number);

    }

    public Piece(Context context,boolean border){
        this(context,0,0,0,0,border);

    }

    public Piece(Context context, int top, int left, int width, int height,boolean border) {
        super(context);
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
        this.border=border;
        if (border) {
            borderRight = new Border(Orientation.Y, left, top, height);
            borderLeft = new Border(Orientation.Y, left + width, top, height);
            borderBottom = new Border(Orientation.X, left, top, width);
            borderTop = new Border(Orientation.X, left, top + height, width);
        } else {
            borderLeft = new Border(Orientation.Y, left, top, height);
            borderRight = new Border(Orientation.Y, left + width, top, height);
            borderTop = new Border(Orientation.X, left, top, width);
            borderBottom = new Border(Orientation.X, left, top + height, width);

        }
        contactTop = new ArrayList<>();
        contactRight = new ArrayList<>();
        contactLeft = new ArrayList<>();
        contactBottom = new ArrayList<>();
        numerable=false;

    }
    public Piece(Context context, int top, int left, int width, int height, int number) {
        this(context,top,left,width,height,false);
        numerable=true;
        movable=true;
        this.number=number;


    }

    public void setPadding(int paddingX,int paddingY){
        this.paddingX=paddingX;
        this.paddingY=paddingY;
    }

    public int getPaddingX() {
        return paddingX;
    }

    public int getPaddingY() {
        return paddingY;
    }

    public void updateSize(int width, int height){
        this.width=width;
        this.height=height;
        updateBorders();
        invalidate();
    }

    public void moveAbsolute(int x, int y){
        this.left=x;
        this.top=y;
        updateBorders();
        invalidate();
    }

    private void updateBorders() {
        if (border) {
            borderRight.update(left, top,height);
            borderLeft.update(left + width, top,height);
            borderBottom.update(left, top,width);
            borderTop.update(left, top + height,width);
        } else {
            borderLeft.update(left, top,height);
            borderRight.update(left + width, top,height);
            borderTop.update(left, top,width);
            borderBottom.update(left, top + height,width);

        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

//    public Rect getrInic() {
//        return rInic;
//    }

//    public void setrInic(Rect rInic) {
//        this.rInic = rInic;
//    }

    public int getLastPos() {
        return lastPos;
    }

    public void setLastPos(int lastPos) {
        this.lastPos = lastPos;
    }

    public int getNumber() {
        return number;
    }


    public int getTopPos() {
        return top;
    }

    public int getLeftPos() {
        return left;
    }


//    public int getHeightMeasurement() {
//        return height;
//    }
//
//    public int getWidthMeasurement() {
//        return width;
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect r = new Rect((int)left, (int)top, (int)(left + width), (int)(top + height));
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setStyle(Paint.Style.FILL);

        if (movable) {

            paint.setColor(Color.GREEN);
        } else {
            paint.setColor(Color.GRAY);
        }
        canvas.drawRect(r, paint);
        //Log.d(TAG, "antes de dibujar pieza " + number);

       // try {
        if (bitmap!=null) {
            //Log.d(TAG, "width = " + bitmap.getWidth());
            //Log.d(TAG, "Height = " + bitmap.getHeight());
            //Log.d(TAG,rInic.toString());
            Rect rInic = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
            canvas.drawBitmap(bitmap, rInic, r, paint);
            //Log.d(TAG,"Logre dibujar");
            Log.d(TAG, "width = " + bitmap.getWidth());
            Log.d(TAG, "Height = " + bitmap.getHeight());
        }
       // }catch(RuntimeException e){
       //     Log.d(TAG,e.getMessage());
        //}


       // Log.d(TAG,"despues de dibujar pieza " + number);
        if (numerable) {
            paint.setTextSize(height / 2);
            paint.setColor(Color.BLUE);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(Integer.toString(number), left + (width / 2), top + (height / 2) + (paint.getTextSize() / 2), paint);

        }
        if (selected) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            int stroke=5;
            paint.setStrokeWidth(stroke);

            Rect rBorder = new Rect((int)(left+stroke/2), (int)(top+stroke/2), (int)(left + width-stroke/2), (int)(top + height-stroke/2));
            canvas.drawRect(rBorder, paint);
        }

    }

    public synchronized boolean intersect(int x, int y) {
        if (border){
            return false;
        }

        boolean intersection = ((x > left && x < (left + width)) && (y > top && y < (top + height)));
        //Log.d(TAG, "number=" + number + " square=(" + left + "," + top + "," + (left + width) + "," + (top + height) + ")  (x,y)=(" + x + "," + y + ") intersect=" + intersection);
        return intersection;
    }

    public void move(int dx, int dy) {
        //Log.d(TAG, "Moving " + number);
        left += dx;
        top += dy;
        updateBorders();
        invalidate();
    }


//    public static void addContact(Piece topLeft, Piece bottomRight, Orientation orientation){
//        if (orientation.equals(Orientation.Y)){
//            topLeft.contactRight.add(bottomRight);
//            bottomRight.contactLeft.add(topLeft);
//        } else {
//            topLeft.contactBottom.add(bottomRight);
//            bottomRight.contactTop.add(topLeft);
//        }
//    }

//    public void removeContacts(Orientation orientation, int delta,boolean all){
//        //Log.d(TAG,"Contacts: *********** Removing Contacts ***********");
//        if (orientation.equals(Orientation.Y)){
//            //Log.d(TAG,"Contacts: Vertical");
//            if (delta>0){
//                removeSideContact(Side.LEFT);
//                if (all) {
//                    //removeSideContact(piece,Side.RIGHT,Side.LEFT);
//                    removeSideContact(Side.UP);
//                    removeSideContact(Side.DOWN);
//                }
//
//            } else {
//                //removeSideContact(piece,Side.LEFT,Side.RIGHT);
//                removeSideContact(Side.RIGHT);
//                if (all) {
//                    removeSideContact( Side.UP);
//                    removeSideContact( Side.DOWN);
//                }
//            }
//        } else {
//            //Log.d(TAG, "Contacts: Horizontal");
//            if (delta>0){
//                if (all) {
//                    removeSideContact( Side.LEFT);
//                    removeSideContact( Side.RIGHT);
//                }
//                removeSideContact(Side.UP);
//                //removeSideContact(piece,Side.DOWN,Side.UP);
//
//            } else {
//                if(all) {
//                    removeSideContact( Side.LEFT);
//                    removeSideContact( Side.RIGHT);
//                }
////                removeSideContact(piece,Side.UP,Side.DOWN);
//                removeSideContact(Side.DOWN);
//
//            }
//        }
//    }
//    private void removeSideContact(Side side1){
//        Side side2=Side.UP;
//        switch (side1){
//            case UP:
//                side2=Side.DOWN;
//                break;
//            case DOWN:
//                side2=Side.UP;
//                break;
//            case LEFT:
//                side2=Side.RIGHT;
//                break;
//            case RIGHT:
//                side2=Side.LEFT;
//                break;
//        }
//        //Log.d(TAG,"Contacts: moving Top " + piece.number);
//        for (Piece contactPiece:this.getContacts(side1)){
//            contactPiece.getContacts(side2).remove(this);
//        }
//        this.getContacts(side1).clear();
//    }

//    private void removeSpecificContact(Side side1){
//        Side side2=Side.UP;
//        switch (side1){
//            case UP:
//                side2=Side.DOWN;
//                break;
//            case DOWN:
//                side2=Side.UP;
//                break;
//            case LEFT:
//                side2=Side.RIGHT;
//                break;
//            case RIGHT:
//                side2=Side.LEFT;
//                break;
//        }
//        List<Piece> contactList=this.getContacts(side1);
//        List<Piece> toRemove = new ArrayList<>();
//        for (Piece toCheck:contactList){
//            if (this==toCheck || !this.getBorder(side1).checkDanger(toCheck.getBorder(side2))){
//                toCheck.getContacts(side2).remove(this);
//                toRemove.add(toCheck);
//            }
//        }
//        for (Piece removed:toRemove){
//            contactList.remove(removed);
//        }
//    }

//    private void checkAndRemoveContacts(Orientation orientation){
//        //Log.d(TAG, "Contacts: *********** checking and Removing Contacts ***********");
//        if (orientation.equals(Orientation.Y)){
//            removeSpecificContact(Side.UP);
//            removeSpecificContact(Side.DOWN);
////            for (Piece top:piece.contactTop){
////                if (!piece.borderTop.checkDanger(top.borderBottom)){
////                    top.contactBottom.remove(piece);
////                    piece.contactTop.remove(top);
////                }
////            }
////            for (Piece bottom:piece.contactBottom){
////                if (!piece.borderBottom.checkDanger(bottom.borderTop)){
////                    bottom.contactTop.remove(piece);
////                    piece.contactBottom.remove(bottom);
////                }
////            }
//        } else {
//            removeSpecificContact(Side.LEFT);
//            removeSpecificContact(Side.RIGHT);
////            for (Piece left:piece.contactLeft){
////                //Log.d(TAG,"Contacts: checking danger left with: " + left.number);
////                if (!piece.borderLeft.checkDanger(left.borderRight)){
////                    left.contactRight.remove(piece);
////                    piece.contactLeft.remove(left);
////                }
////            }
////            for (Piece right:piece.contactRight){
////                //Log.d(TAG,"Contacts: checking danger right with: " + right.number);
////                if (!piece.borderRight.checkDanger(right.borderLeft)){
////                    right.contactLeft.remove(piece);
////                    piece.contactRight.remove(right);
////                }
////            }
//
//        }
//    }


//    private boolean move(Set<Piece> connectedPieces,int delta, Orientation orientation) {
//
//            //Log.d(TAG,"Moving " + this.number);
//        Side side;
//        int dx = 0;
//        int dy = 0;
//
//        if (orientation.equals(Orientation.Y)) {
//            dx = delta;
//            if (delta > 0) { //moving right => borderFront right, piece border left
//                side = Side.RIGHT;
//
//            } else { //moving left => borderFront left, piece border right
//                side = Side.LEFT;
//            }
//        } else {
//            dy = delta;
//            if (delta > 0) { //moving down=> borderFront bottom, piece border up
//                side = Side.DOWN;
//
//            } else { //moving up=> borderFront top, piece border down
//                side = Side.UP;
//
//            }
//        }
//        boolean setMovable=true;
//        for (Piece piece : connectedPieces) {
//            setMovable=setMovable && piece.movable;
//            if (!setMovable){
//                return false;
//            }
//        }
//        removeContacts(orientation, delta, false);
//        for (Piece piece : connectedPieces) {
//            Log.d(TAG,"Moving "+piece);
//            piece.move(dx, dy);
//            piece.checkAndRemoveContacts(orientation);
//        }
//
//        return true;
//    }


//    public void checkAllCollisions(List<Piece> pieceList, int delta, Orientation orientation) {
//        //while there is some movement left:
//        while (Math.abs(delta) > 0) {
//            //Log.d(TAG, "Check Recursive piece: " + movingPiece.number + " delta =" + delta + " orientation " + orientation);
//            List<Piece> contactPieces;
//            Side side;
//            int sign = 0;
//
//            // get the parameters depending on the orientation and the direction of movement
//            //The orientation is the orientation of the border, if you are testing on the X axis: then
//            // the orientation is Vertical, otherwise is Horizontal for the Y axis
//
//            if (orientation.equals(Orientation.Y)) {
//                if (delta > 0) { //moving right => borderFront right, piece border left
//                    side = Side.RIGHT;
//                    sign = 1;
//                } else { //moving left => borderFront left, piece border right
//                    side = Side.LEFT;
//                    sign = -1;
//                }
//            } else {
//
//                if (delta > 0) { //moving down=> borderFront bottom, piece border up
//                    side = Side.DOWN;
//                    sign = 1;
//
//                } else { //moving up=> borderFront top, piece border down
//                    side = Side.UP;
//                    sign = -1;
//                }
//            }
//
//            //Get all connected pieces in this direction, including this
//
//            Map<Border, Piece> borderFront = this.getFront(side);
//            //Log.d(TAG, "BorderFront Size: " + borderFront.size());
//            Set<Piece> connectedPieces = new HashSet<>(borderFront.values());
//            //Create an empty set to store all the pieces which not going to test
//            Set<Piece> cleanSet=new HashSet<>();
//
//            //add all pieces
//            cleanSet.addAll(pieceList);
//
//            //then remove all connected pieces from set
//            cleanSet.removeAll(connectedPieces);
//
//            //Convert Set to List in order to Sort
//            List<Piece> sortedCleanList = new ArrayList<>(cleanSet);
//
//            //Sort the list from near to far of this
//            Collections.sort(sortedCleanList, new DistanceComparator(this, orientation, sign));
//
//
//
//            int dist = delta;
//            boolean collision = false;
//            boolean moved = false;
//
//            //for every piece in the sorted list:
//            for (Piece piece : sortedCleanList) {
//
//                if(this==piece){
//                    Log.d(TAG,"iguales 1");
//                }
//                //Check the collision
//                List<CollidedPiece> collidedPieces = this.checkCollision(borderFront,piece, delta, orientation);
//
//                //if collision happen: the list will be greater than 0
//                if (collidedPieces.size() > 0) {
//                    //there was a collision
//                    collision = true;
//                    //get the miniun free distance before collision
//                    dist = Math.abs(collidedPieces.get(0).getDist()) * sign;
//
//                    if (Math.abs(dist)>Math.abs(delta)){
//                        Log.d(TAG,"CollisionCheck error, repairing");
//                        dist=delta;
//                    }
//                    Log.d(TAG, "Collision Moving " + this);
//                    //move this piece (and all connected pieces) to this position
//                    moved=this.move(connectedPieces,dist, orientation);
//
//
//                    //add the connected piece or pieces to the one which collides
//                    for (CollidedPiece collidedPiece : collidedPieces) {
//                        Piece frontPiece = collidedPiece.getMovingPiece();
//                        if (frontPiece!=piece) {
//                            Log.d(TAG, "Colision between " + frontPiece + " and " + piece);
//                            addContact(frontPiece, piece, orientation);
//                        } else {
//                            throw new RuntimeException("Something wrong");
//                        }
//                        //frontPiece.getContacts(side).add(piece);
//                    }
//
//                    // stop the checking
//                    break;
//
//                }
//            }
//            // if there were no collision
//            if (!collision) {
//                //Log.d(TAG, "No Collision. Moving " + dist);
//                //move this piece (and all connected pieces) to the maximum position specified by delta
//                Log.d(TAG, "Collision Free Moving " + this);
//                moved=this.move(connectedPieces,dist, orientation);
//
//            }
//            //if the move was not possible because a immovable piece, then stop the checking
//            if (!moved){
//                dist=delta;
//            }
//
//            //reduce the size of the movement and make the loop again
//            delta = delta- dist;
//        }
//    }

//    public List<CollidedPiece> checkCollision(Map<Border,Piece> borderFront, Piece piece, int delta, Orientation orientation) {
//        //Log.d(TAG, "Checkin Collision between " + movingPiece.number + " and " + piece.number);
//        List<CollidedPiece> collidedPieces = new ArrayList<>();
//        Side side;
//        Border border;
//        if (orientation.equals(Orientation.Y)) {
//            if (delta > 0) { //moving right => borderFront right, piece border left
//                side = Side.RIGHT;
//                border = piece.borderLeft;
//            } else { //moving left => borderFront left, piece border right
//                side = Side.LEFT;
//                border = piece.borderRight;
//            }
//        } else {
//
//            if (delta > 0) { //moving down=> borderFront bottom, piece border top
//                side = Side.DOWN;
//                border = piece.borderTop;
//
//            } else { //moving down=> borderFront bottom, piece border up
//                side = Side.UP;
//                border = piece.borderBottom;
//            }
//        }
//
//        //Log.d(TAG, "BorderFront Size: " + borderFront.size());
//        Set<Border> borderSet = borderFront.keySet();
//        List<Collision> collisions=new ArrayList<>();
//        for (Border frontBorder:borderSet){
//            if (frontBorder!=border && frontBorder.checkCollision(border,delta)){
//                Collision collision = new Collision(frontBorder,frontBorder.getDistance(border));
//                collisions.add(collision);
//            }
//        }
//        Collections.sort(collisions, new Collision.CollisionComparator());
//        if (collisions.size() > 0) {
//            Collision collision = collisions.get(0);
//            Border movingBorder = collision.getBorder();
//            int minDist = collision.getDist();
//            Piece movingPiece = borderFront.get(movingBorder);
//            if (movingPiece!=piece){
//                collidedPieces.add(new CollidedPiece(movingPiece, piece, minDist));
//            } else {
//                Log.d(TAG,"iguales 2");
//            }
//            for (int j = 1; j < collisions.size(); j++) {
//                Collision otherCollision = collisions.get(j);
//                if (otherCollision.getDist() == minDist) {
//                    Border otherMovingBorder = otherCollision.getBorder();
//                    Piece otherMovingPiece = borderFront.get(otherMovingBorder);
//                    if (otherMovingPiece!=piece) {
//                        collidedPieces.add(new CollidedPiece(otherMovingPiece, piece, minDist));
//                    } else {
//                        Log.d(TAG,"iguales 3");
//                    }
//                } else if (otherCollision.getDist() > minDist) {
//                    break;
//
//                } else {
//                    throw new RuntimeException("Algo mal aqui");
//                }
//            }
//        }
//        return collidedPieces;
//
//    }


    public Border getBorder(Direction direction) {
        switch (direction) {
            case LEFT:
                return borderLeft;
            case RIGHT:
                return borderRight;
            case UP:
                return borderTop;
            case DOWN:
                return borderBottom;
        }
        return null;
    }

//    private List<Piece> getContacts(Side side) {
//        switch (side) {
//            case LEFT:
//                return contactLeft;
//            case RIGHT:
//                return contactRight;
//            case UP:
//                return contactTop;
//            case DOWN:
//                return contactBottom;
//        }
//        return null;
//    }


//    private Set<Piece> getAllContactSet(Side side){
//        Set<Piece> contacts = new HashSet<>();
//        contacts.add(this);
//        return getRecursiveContactSet(contacts,side);
//
//    }
//    private Set<Piece> getRecursiveContactSet(Set<Piece> contactSet,Side side){
//
//        /*if (contactSet.contains(this)){
//            return contactSet;
//        } else {
//            contactSet.add(this);
//        }
//        */
//        List<Piece> contacts=this.getContacts(side);
//        //Log.d(TAG,"recursive ContactSet");
//        for (Piece p: contacts){
//
//            if (!contactSet.contains(p)) {
//                contactSet.add(p);
//                contactSet = p.getRecursiveContactSet(contactSet, side);
//
//            }
//        }
//        return contactSet;
//    }

//    private Map<Border, Piece> getFront(Side side) {
//        Map<Border, Piece> front=new HashMap<>();
//        Set<Piece> contacts=this.getAllContactSet(side);
//        for (Piece p : contacts) {
//            front.put(p.getBorder(side), p);
//        }
//        Log.d(TAG,"ConnectedSet "+this.number+ ": "  + contacts);
//
//        return front;
//    }

    @Override
    public String toString() {
        return Integer.toString(number);
    }

//    public static class CollidedPiece {
//        private Piece movingPiece;
//        private Piece collidedPiece;
//
//        private int dist;
//
//        public CollidedPiece(Piece movingPiece, Piece collidedPiece, int dist) {
//            this.movingPiece = movingPiece;
//            this.collidedPiece = collidedPiece;
//            this.dist = dist;
//        }
//
//        public Piece getMovingPiece() {
//            return movingPiece;
//        }
//
//        public Piece getCollidedPiece() {
//            return collidedPiece;
//        }
//
//        public int getDist() {
//            return dist;
//        }
//    }

//    public static class DistanceComparator implements Comparator<Piece> {
//        private Piece from;
//        private Orientation orientation;
//        int sign;
//
//        public DistanceComparator(Piece from, Orientation orientation, int sign) {
//            this.from = from;
//            this.orientation = orientation;
//            this.sign = sign;
//
//        }
//
//        private int getDistance(Piece piece){
//            int fromValue; int toValue;
//
//            if (orientation.equals(Orientation.X)) {
//                fromValue = (int) (1000 * from.top);
//                toValue = (int) (1000 * piece.top);
//            } else {
//                fromValue=(int)(1000*from.left);
//                toValue=(int)(1000*piece.left);
//            }
//
//            int number=(toValue - fromValue)*sign;
//            if (number<0){
//                number=Integer.MAX_VALUE;
//            }
//            return number;
//        }
//
//        @Override
//        public int compare(Piece lhs, Piece rhs) {
//
//            return getDistance(lhs)-getDistance(rhs);
//        }
//    }
}
