package com.pinterest.secor.uploader.exceptions;

/**
 * Created by master on 19/01/2017.
 */
public class HandleException extends Exception {

    public HandleException(){
        super();
    }

    public HandleException(String message){
        super(message);
    }

    public HandleException(Throwable cause){
        super(cause);
    }

    public HandleException(String message, Throwable cause){
        super(message, cause);
    }
}
