package com.snda.inote.exception;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 11-2-23
 * Time: 上午9:13
 */
public class ApiRequestErrorException extends Exception{
    public ApiRequestErrorException(String s) {
        super(s);
    }
}
