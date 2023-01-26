package org.example;

import redis.clients.jedis.Jedis;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    private static final int SLEEP = 1000; // 1 секунда
    private static final String redisKey = "Users";

    public static void main(String[] args) throws InterruptedException {
        RedisStorage redisStorage = new RedisStorage();
        Jedis jedis = new Jedis("http://localhost:6379");
        jedis.del(redisKey);
        redisStorage.addUsers(jedis, redisKey);
        int loopNumber = 0;
        int range = 10;
        int minRange = 0;
        int maxRange = 10;
        int randomLoop = 0;
        int redisSize = (int) jedis.llen(redisKey);
        while (true) {
            if (loopNumber % 10 == 0) {
                randomLoop = ThreadLocalRandom.current().nextInt(minRange, maxRange);
                minRange += range;
                maxRange += range;
            }
            if (randomLoop == loopNumber) {
                redisStorage.payRandomVip(jedis, redisSize, redisKey);
                System.out.println("+++++++++++++++++++++++");
            } else {
                redisStorage.defaultQueue(jedis, redisSize, redisKey);
                System.out.println("--------------------------------");
            }
            loopNumber++;
            Thread.sleep(SLEEP);
        }
//        jedis.shutdown(); не доступная строка в данном случае, из-за бесконечного цикла
//        указанна как дань перфекционизму
    }
}