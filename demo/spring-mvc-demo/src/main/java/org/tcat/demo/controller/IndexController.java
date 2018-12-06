package org.tcat.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tcat.admin.health.Health;
import org.tcat.admin.health.HealthIndicator;
import org.tcat.admin.utils.SpringBeanUtils;

import java.util.Map;

/**
 * @author lin
 * @date 2018/12/4
 */
@Controller
@RequestMapping
public class IndexController {

    @RequestMapping()
    @ResponseBody
    public String index() {
        return "index";
    }

    @RequestMapping("health")
    @ResponseBody
    public String health() {
        Map<String, HealthIndicator> data = SpringBeanUtils.getBeansOfType(HealthIndicator.class);
        Multimap<String, Health> multimap = ArrayListMultimap.create();
        if (!data.isEmpty()) {
            for (String key : data.keySet()) {
                if (data.get(key).open()) {
                    multimap.putAll(data.get(key).healths());
                }
            }
        }
        return JSON.toJSONString(multimap, SerializerFeature.DisableCircularReferenceDetect);
    }


}
