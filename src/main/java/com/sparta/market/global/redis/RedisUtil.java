package com.sparta.market.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Service
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    public void setData(String key, String value, Long expiredTime){
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    public String getData(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteData(String key){
        redisTemplate.delete(key);
    }

    public void setDataExpire(String key, String value, long duration) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(duration));
    }
}

//@RequiredArgsConstructor
//@Service
//public class RedisUtil {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    public void setData(String key, String value,Long expiredTime){
//        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
//    }
//
//    public String getData(String key){
//        return (String) redisTemplate.opsForValue().get(key);
//    }
//
//    public void deleteData(String key){
//        redisTemplate.delete(key);
//    }
//
//    public void setDataExpire(String key, String value, long duration) {
//        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
//        Duration expireDuration = Duration.ofSeconds(duration);
//        valueOperations.set(key, value, expireDuration);
//    }
//
//}
