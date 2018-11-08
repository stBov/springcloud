package com.yztsoft.service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @classname: controller
 * @description:
 * @author: Shi Shijie
 * @create: 2018-11-08 14:40
 **/
@RestController
public class Controller {
    @RequestMapping(value = {"/service/getText"})
    public String getText(){
        return "Hello Springcloud";
    }
}
