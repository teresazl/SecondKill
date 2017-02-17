-- 秒杀存储过程
DELIMITER $$ --  console 换行符 ; 转化为
-- 定于存储过程
-- 参数：in 参数 out 输出参数
-- row_count(): 返回上一条修改类型sql(delete, update, insert)的影响行数
-- row_count(): 0->未修改数据; >0->修改的行数; <0->sql错误/未执行修改sql
-- out参数 r_result 1->成功 -1->重复秒杀 -2->内部异常 0->秒杀结束

CREATE PROCEDURE `seckill`.`execute_seckill`
  (IN v_seckill_id BIGINT, IN v_user_phone BIGINT, IN v_kill_time TIMESTAMP, OUT r_result INT)
  BEGIN
    DECLARE insert_count INT DEFAULT 0;
    START TRANSACTION;
    INSERT IGNORE INTO success_killed (seckill_id, user_phone, create_time) VALUES (v_seckill_id, v_user_phone, v_kill_time);
    SELECT row_count() INTO insert_count;
    IF (insert_count = 0)
    THEN
      ROLLBACK;
      SET r_result = -1;
    ELSEIF (insert_count < 0)
      THEN
        ROLLBACK;
        SET r_result = -2;
    ELSE
      UPDATE seckill
      SET number = number - 1
      WHERE seckill_id = v_seckill_id
            AND start_time < v_kill_time
            AND end_time > v_kill_time
            AND number > 0;
      SELECT row_count() INTO insert_count;
      IF (insert_count = 0)
      THEN
        ROLLBACK;
        SET r_result = 0;
      ELSEIF (insert_count < 0)
        THEN
          ROLLBACK;
          SET r_result = -2;
      ELSE
        COMMIT;
        SET r_result = 1;
      END IF;
    END IF;
  END $$
-- 存储过程定义结束

-- 改回换行符
DELIMITER ;

SET @r_result = -3;

-- 执行存储过程
CALL execute_seckill(1003, 12312341234, now(), @r_result);

-- 获取结果
SELECT @r_result;

-- 存储过程 (只在银行被大量的时候,互联网公司用的很少,但是在秒杀中用)
-- 1.存储过程优化: 事务行级锁持有的时间
-- 2.不要过渡依赖存储过程
-- 3.简单的逻辑可以应用存储过程
-- 4.QPS:一个秒杀但6000/qps
