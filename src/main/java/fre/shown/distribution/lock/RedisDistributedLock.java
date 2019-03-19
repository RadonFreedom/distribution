package fre.shown.distribution.lock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

/**
 * 基于Redis的分布式锁实现。
 *
 * @author Radon Freedom
 * created at 2019.03.16 11:25
 */

public class RedisDistributedLock {

    private static final String LOCK_SUCCESS = "OK";
    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 尝试获取分布式锁。该方法会立即放回，结果为获取到锁或者未获取到锁。<B>该锁不可重入</B>。<br/>
     * 如果获取锁的时间超过expireTimeSeconds，锁会被自动释放。
     * @param jedis Redis客户端
     * @param lockKey 锁名唯一标识
     * @param requestId 请求唯一标识
     * @param expireTimeSeconds 超期时间
     * @return 是否获取成功
     */
    public static boolean tryLock(Jedis jedis, String lockKey, String requestId, int expireTimeSeconds) {

        SetParams params = new SetParams();
        //仅当key不存在时设置key
        params.nx();
        params.ex(expireTimeSeconds);
        String result = jedis.set(lockKey, requestId, params);

        return LOCK_SUCCESS.equals(result);
    }

    /**
     * 释放分布式锁。使用LUA脚本是为了保证释放锁语句的原子性。
     * @param jedis Redis客户端
     * @param lockKey 锁名唯一标识
     * @param requestId 请求唯一标识
     * @return 是否释放成功
     */
    public static boolean unlock(Jedis jedis, String lockKey, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

        return RELEASE_SUCCESS.equals(result);
    }
}
