package com.blog.controller;


import com.blog.util.PathUtil;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Paths;

@Api(tags = "检验服务模块")
@RestController
@RequestMapping("/web")
public class WebController {


    @RequestMapping("/check")
    public String check() {
        return "OK";
    }

    @RequestMapping("/path-class")
    public String check2() {
        return Paths.get(PathUtil.getBasePath()).getParent().toString();
    }
    @RequestMapping("/path")
    public String check3() {
        return PathUtil.getBasePath();
    }
}
