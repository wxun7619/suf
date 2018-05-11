package com.lonntec.domainservice.adminportal.proxy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import team.benchem.framework.annotation.MicroServiceMethodProxy;
import team.benchem.framework.lang.MicroServiceException;
import team.benchem.framework.lang.RequestType;
import team.benchem.framework.lang.StateCodeImpl;

@Service
public class DomainServiceProxy {

    //创建企业域
    @MicroServiceMethodProxy(microserviceKey = "domainService", path = "/domain/create", type = RequestType.POST)
    public JSONObject createDomain(JSONObject postData){
        throw new MicroServiceException(new StateCodeImpl(-3,""));
    }

    //获取企业列表
    @MicroServiceMethodProxy(microserviceKey = "domainService", path = "/domain/list", type = RequestType.GET)
    public JSONArray findDomains(String keyword, Integer page, Integer size){
        throw new MicroServiceException(new StateCodeImpl(-3,""));
    }

    //获取用户数量
    @MicroServiceMethodProxy(microserviceKey = "domainService", path = "/domain/listcount", type = RequestType.GET)
    public Integer findDomainsCount(String keyword){
        throw new MicroServiceException(new StateCodeImpl(-3,""));
    }

    //修改企业域
    @MicroServiceMethodProxy(microserviceKey = "domainService", path = "/domain/modify", type = RequestType.POST)
    public JSONObject modifyDomain(JSONObject postData){
        throw new MicroServiceException(new StateCodeImpl(-3,""));
    }

    //启用/禁用企业域
    @MicroServiceMethodProxy(microserviceKey = "domainService", path = "/domain/setenable", type = RequestType.POST)
    public JSONObject setEnable(JSONObject postData){
        throw new MicroServiceException(new StateCodeImpl(-3,""));
    }

    //企业域实施人员变更
    @MicroServiceMethodProxy(microserviceKey = "domainService", path = "/domain/changeuser", type = RequestType.POST)
    public JSONObject changeUser(JSONObject postData){
        throw new MicroServiceException(new StateCodeImpl(-3,""));
    }

    //获取已开通SUF企业列表
    @MicroServiceMethodProxy(microserviceKey = "domainService", path = "/domain/activesuflist", type = RequestType.GET)
    public JSONArray findActiveSufListDomains(JSONObject postData){
        throw new MicroServiceException(new StateCodeImpl(-3,""));
    }

    //获取未开通SUF企业列表
    @MicroServiceMethodProxy(microserviceKey = "domainService", path = "/domain/unactivesuflist", type = RequestType.GET)
    public JSONArray findNotActiveSufListDomains(JSONObject postData){
        throw new MicroServiceException(new StateCodeImpl(-3,""));
    }
}
