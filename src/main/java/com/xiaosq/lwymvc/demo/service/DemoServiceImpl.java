package com.xiaosq.lwymvc.demo.service;

import com.xiaosq.lwymvc.framework.annotation.LwyService;

@LwyService
public class DemoServiceImpl implements IDemoService {

    @Override
    public String queryData() {
        return "Hello Morgan";
    }
}
