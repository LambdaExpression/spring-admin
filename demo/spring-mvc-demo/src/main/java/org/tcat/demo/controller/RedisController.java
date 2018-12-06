package org.tcat.demo.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lin
 * @date 2018/12/5
 */
@Controller
@RequestMapping
public class RedisController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping("redis")
    @ResponseBody
    public String redis(String key, String value) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        if (!StringUtils.isEmpty(value)) {
            redisTemplate.opsForValue().append(key, value);
        }
        return JSON.toJSONString(redisTemplate.opsForValue().get(key));
    }

}
