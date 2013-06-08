package com.snda.inote.exception;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 11-2-18
 * Time: 上午9:01
 */
public class NotConnectException extends IOException{
    public NotConnectException(String s) {
        super(s);
    }
}
