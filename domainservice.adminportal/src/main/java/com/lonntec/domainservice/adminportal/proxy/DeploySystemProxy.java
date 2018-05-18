package com.lonntec.domainservice.adminportal.proxy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import team.benchem.framework.annotation.MicroServiceMethodProxy;
import team.benchem.framework.lang.MicroServiceException;
import team.benchem.framework.lang.RequestType;
import team.benchem.framework.lang.StateCodeImpl;

@Service
public class DeploySystemProxy {
    /**
     *
     * suf开通管理
     */
    //获取开通申请列表
    @MicroServiceMethodProxy(microserviceKey = "sufService",path="/deploy/getlist")
    public JSONArray getList(String keyword, Integer page, Integer size
    ){
        throw new MicroServiceException(new StateCodeImpl(-2,""));
    }
    //获取开通申请数量
    @MicroServiceMethodProxy(microserviceKey = "sufService",path="/deploy/getlistcount")
    public Integer getListCount(String keyword){
        throw new MicroServiceException(new StateCodeImpl(-2,""));
    }

    //递交开通申请
    @MicroServiceMethodProxy(microserviceKey = "sufService",path="/deploy/apply", type=RequestType.POST)
    public JSONObject apply(JSONObject applyInfo) {
        throw new MicroServiceException(new StateCodeImpl(-2,""));
    }

    //审核开通申请
    @MicroServiceMethodProxy(microserviceKey = "sufService",path="/deploy/auditapply", type=RequestType.POST)
    public JSONObject auditApply(JSONObject postData){
        throw new MicroServiceException(new StateCodeImpl(-2,""));
    }

    /**
     *
     * suf授权管理
     */
    //获取授权申请列表
    @MicroServiceMethodProxy(microserviceKey = "sufService",path="/license/getlist")
    public JSONArray getlicenselist(String keyword, Integer page, Integer size
    ){
        throw new MicroServiceException(new StateCodeImpl(-2,""));
    }
    //获取授权申请数量
    @MicroServiceMethodProxy(microserviceKey = "sufService",path="/license/getlistcount")
    public Integer getlicenseListCount(String keyword){
        throw new MicroServiceException(new StateCodeImpl(-2,""));
    }

    //递交授权申请
    @MicroServiceMethodProxy(microserviceKey = "sufService",path="/license/apply", type=RequestType.POST)
    public JSONObject licenseapply(JSONObject applyInfo) {
        throw new MicroServiceException(new StateCodeImpl(-2,""));
    }

    //审核授权申请
    @MicroServiceMethodProxy(microserviceKey = "sufService",path="/license/auditapply", type=RequestType.POST)
    public JSONObject licenseauditApply(JSONObject postData){
        throw new MicroServiceException(new StateCodeImpl(-2,""));
    }

}