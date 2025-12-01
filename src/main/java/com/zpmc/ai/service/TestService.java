package com.zpmc.ai.service;

import org.springframework.stereotype.Service;

/**
 * @author songqiang
 * @date 2025-11-27 8:38
 */
@Service
public class TestService {

    public String sayHello(String message){
        return message +"你好呀！";
    }
}
