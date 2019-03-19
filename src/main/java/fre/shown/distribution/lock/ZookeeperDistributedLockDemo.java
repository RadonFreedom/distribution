package fre.shown.distribution.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 *
 * 基于Zookeeper的分布式锁的使用。
 *
 * @author Radon Freedom
 * created at 2019.03.16 11:28
 */

public class ZookeeperDistributedLockDemo {

    private static final String LOCK_ROOT_PATH = "/curator/lock";
    private static final String CONNECT_STRING = "";
    private static final RetryPolicy RETRY_POLICY = new ExponentialBackoffRetry(1000, 3);

    public static void main(String[] args) throws Exception {
        //创建zookeeper的客户端
        CuratorFramework client = CuratorFrameworkFactory.newClient(CONNECT_STRING, RETRY_POLICY);
        client.start();

        //在锁的根节点下创建分布式锁
        InterProcessMutex mutex = new InterProcessMutex(client, LOCK_ROOT_PATH);
        mutex.acquire();
        //获得了锁, 进行业务流程
        System.out.println("Enter mutex");
        //完成业务流程, 释放锁
        mutex.release();

        //关闭客户端
        client.close();
    }
}
