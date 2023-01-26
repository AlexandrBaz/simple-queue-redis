package org.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.args.ListDirection;
import redis.clients.jedis.args.ListPosition;

import java.util.concurrent.ThreadLocalRandom;


public class RedisStorage {

    public void addUsers(Jedis jedis, String redisKey) {
        String[] user = new String[20];
        for (int i = 1; i <= 20; i++) {
            String str = ("Пользователь:" + i);
            user[i - 1] = str;
        }
        jedis.rpush(redisKey, user);
    }

    //Проверка на присутствие значения
    public static boolean ifPresent(String value, Jedis jedis, String redisKey) {
        for (int i = 0; i < jedis.llen(redisKey); i++) {
            String checksValue = jedis.lindex(redisKey, i);
            if (checksValue.equals(value)) {
                return true;
            }
        }
        return false;
    }

    // очередь с vip оплатой
    public void payRandomVip(Jedis jedis, int redisSize, String redisKey) {
        int randomUser = ThreadLocalRandom.current().nextInt((int) jedis.llen(redisKey));
        int randomLoop = ThreadLocalRandom.current().nextInt(0, (int) jedis.llen(redisKey));
        String vipUserValue = jedis.lindex(redisKey, randomUser);
        String after = jedis.lindex("Users", randomUser - 1);
        long vipIndex = jedis.lpos(redisKey, vipUserValue);
        for (int i = 0; i < redisSize; i++) {
            if (randomLoop == i && i > vipIndex) {
                System.out.println("> Пользователь " + (vipIndex + 1) + " оплатил платную услугу");
                System.out.println("— На главной странице показываем " + vipUserValue);
            } else if (randomLoop == i && i < vipIndex) {
                System.out.println("> Пользователь " + (vipIndex + 1) + " оплатил платную услугу");
                System.out.println("— На главной странице показываем " + vipUserValue);
                jedis.lrem(redisKey, 0, vipUserValue);
                i++;
            } else if (randomLoop == i && i == vipIndex) {
                System.out.println("> Пользователь " + (vipIndex + 1) + " ЛОШАРА оплатил платную услугу, когда пришла его очередь");
                System.out.println("— На главной странице показываем " + vipUserValue);
                jedis.lmove(redisKey, redisKey, ListDirection.LEFT, ListDirection.RIGHT);
                continue;
            }
            System.out.println("— На главной странице показываем " + jedis.lmove(redisKey, redisKey, ListDirection.LEFT, ListDirection.RIGHT));
        }
        System.out.println("================================");
        if (!ifPresent(vipUserValue, jedis, redisKey)) {
            jedis.linsert(redisKey, ListPosition.AFTER, after, vipUserValue);
        }
    }

    // очередь по умолчанию
    public void defaultQueue(Jedis jedis, int redisSize, String redisKey) {
        for (int i = 0; i < redisSize; i++) {
            System.out.println("— На главной странице показываем " + jedis.lmove(redisKey, redisKey, ListDirection.LEFT, ListDirection.RIGHT));
        }
    }
}
