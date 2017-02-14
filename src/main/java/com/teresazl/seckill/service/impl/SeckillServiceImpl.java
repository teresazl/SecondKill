package com.teresazl.seckill.service.impl;

import com.teresazl.seckill.dao.SeckillDao;
import com.teresazl.seckill.dao.SuccessKilledDao;
import com.teresazl.seckill.dto.Exposer;
import com.teresazl.seckill.dto.SeckillExecution;
import com.teresazl.seckill.entity.Seckill;
import com.teresazl.seckill.entity.SuccessKilled;
import com.teresazl.seckill.enums.SeckillStateEnum;
import com.teresazl.seckill.exception.RepeatKillException;
import com.teresazl.seckill.exception.SeckillCloseException;
import com.teresazl.seckill.exception.SeckillException;
import com.teresazl.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by teresa on 2017/2/9.
 */
// @Component @Service @Dao @Controller
@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    // 盐值
    private final String salt = "asd;jjashd!*&%^&@sadf";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 10);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);

        if (seckill == null) {
            return new Exposer(false, seckillId);
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        // 系统当前时间
        Date nowTime = new Date();

        if (nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }

        // 转化特定字符串的过程
        String md5 = getMD5(seckillId);

        return new Exposer(true, seckillId, md5);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    // 使用注解控制事物方法的优点
    // 1.开发团队达成一致的约定，明确标注事物方法的编程风格
    // 2.保证事物方法的执行时间尽可能短，不要穿插其他的网络操作RPC/HTTP请求或者剥离到事物方法外部
    // 3.不是所有的方法都需要事物，如只有一条修改操作，只读操作不需要事物控制
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {

        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }

        // 执行秒杀逻辑：减库存 + 记录购买行为
        Date nowTime = new Date();

        try {
            // 减库存
            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0) {
                // 没有更新到记录 秒杀结束了
                throw new SeckillCloseException("seckill closed");
            } else {
                // 记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);

                // 唯一：seckillId、userPhone
                if (insertCount <= 0) {
                    // 重复秒杀
                    throw new RepeatKillException("seckill repeated");
                } else {
                    // 秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e) {
            throw e;
        } catch (RepeatKillException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 所有编译期异常转化为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }

    }
}
