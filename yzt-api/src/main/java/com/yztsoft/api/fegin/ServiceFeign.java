package com.yztsoft.api.fegin;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "YZT-SERVICE")
public interface ServiceFeign {
    @RequestMapping(method = RequestMethod.POST, value = "/service/getText")
    String getText() ;
}