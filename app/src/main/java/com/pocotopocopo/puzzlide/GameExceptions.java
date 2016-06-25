package com.pocotopocopo.puzzlide;

/**
 * Created by nico on 22/01/15.
 */
public class GameExceptions {

    public static class SamePieceException extends RuntimeException {
        public SamePieceException(String msg){
            super(msg);
        }
        public SamePieceException(){
            super();
        }
    }

    public static class PieceNotExistException extends RuntimeException {
        public PieceNotExistException(String msg){
            super(msg);
        }
        public PieceNotExistException(){
            super();
        }
    }
    public static class ConnectionExistException extends RuntimeException {
        public ConnectionExistException(String msg){
            super(msg);
        }
        public ConnectionExistException(){
            super();
        }
    }

    public static class PieceNotConnectedException extends Exception {
        public PieceNotConnectedException(String msg){
            super(msg);
        }
        public PieceNotConnectedException(){
            super();
        }
    }

}
