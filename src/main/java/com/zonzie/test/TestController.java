package com.zonzie.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zonzie
 * @date 2018/7/13 14:41
 */
@RestController
@RequestMapping(value = "test")
public class TestController {

    @GetMapping(value = "test")
    public String test() {
        return "hello!!!";
    }
}
