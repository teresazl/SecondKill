package com.teresazl.seckill.exception;

/**
 * 秒杀关闭异常
 * Created by teresa on 2017/2/9.
 */
public class SeckillCloseException extends SeckillException {

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
