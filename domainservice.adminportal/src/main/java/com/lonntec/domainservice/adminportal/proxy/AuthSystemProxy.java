package com.lonntec.domainservice.adminportal.proxy;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import team.benchem.framework.annotation.MicroServiceMethodProxy;
import team.benchem.framework.lang.MicroServiceException;
import team.benchem.framework.lang.RequestType;
import team.benchem.framework.lang.StateCodeImpl;

@Service
public class AuthSystemProxy {
    //登陆
    @MicroServiceMethodProxy(microserviceKey = "userSystem",path="/auth/login", type = RequestType.POST)
    public String login(@RequestBody JSONObject formData){
        throw new MicroServiceException(new StateCodeImpl(-2,""));
    }

    //登出
    @MicroServiceMethodProxy(microserviceKey = "userSystem",path="/auth/logout", type = RequestType.POST)
    public void logout(@RequestBody JSONObject formData){
        throw new MicroServiceException(new StateCodeImpl(-2,""));
    }

    //获取当前用户
    @MicroServiceMethodProxy(microserviceKey = "userSystem",path="/auth/getuser")
    public JSONObject getUser(){
        throw new MicroServiceException(new StateCodeImpl(-2,""));
    }
}
