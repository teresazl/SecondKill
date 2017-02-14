package com.teresazl.seckill.service;

import com.teresazl.seckill.dto.Exposer;
import com.teresazl.seckill.dto.SeckillExecution;
import com.teresazl.seckill.entity.Seckill;
import com.teresazl.seckill.exception.RepeatKillException;
import com.teresazl.seckill.exception.SeckillCloseException;
import com.teresazl.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口：站在使用者的角度设计接口
 * 三个方面：方法定义粒度、参数、返回类型
 *
 * Created by teresa on 2017/2/9.
 */
public interface SeckillService {

    /**
     * 查询所有的秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址
     * 否则输出系统时间和秒杀时间
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException;

}
