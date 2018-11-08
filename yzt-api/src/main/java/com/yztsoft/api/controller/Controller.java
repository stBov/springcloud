package com.yztsoft.api.controller;

import com.yztsoft.api.fegin.ServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    ServiceFeign serviceFeign;

    @RequestMapping(value = {"/",""})
    public String helloboot(){
        return serviceFeign.getText() ;
    }
}
