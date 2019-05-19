package com.xiaosq.lwymvc.demo.controller;

import com.xiaosq.lwymvc.framework.annotation.LwyAutowired;
import com.xiaosq.lwymvc.framework.annotation.LwyController;
import com.xiaosq.lwymvc.framework.annotation.LwyRequestMapping;
import com.xiaosq.lwymvc.framework.annotation.LwyRequestParam;
import com.xiaosq.lwymvc.demo.service.IDemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@LwyController
public class DemoController {

    @LwyAutowired
    private IDemoService demoService;

    @LwyRequestMapping("/doTest")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @LwyRequestParam("param") String param) {
        System.out.println(demoService.queryData());

        System.out.println(param);
        try {
            response.getWriter().write("doTest method success! param:" + param);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @LwyRequestMapping("/doTest2")
    public void add(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.getWriter().println("doTest2 method success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
