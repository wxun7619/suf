package com.lonntec.domainservice.adminportal.proxy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import team.benchem.framework.annotation.MicroServiceMethodProxy;
import team.benchem.framework.lang.MicroServiceException;
import team.benchem.framework.lang.RequestType;
import team.benchem.framework.lang.StateCodeImpl;

@Service
public class UserSystemProxy {

    @MicroServiceMethodProxy(microserviceKey = "userSystem", path = "/user/getlist")
    public JSONArray getlist(String keyword, Integer page, Integer size){
        throw new MicroServiceException(new StateCodeImpl(-2, ""));
    }

    @MicroServiceMethodProxy(microserviceKey = "userSystem", path = "/user/create", type = RequestType.POST)
    public JSONObject create(JSONObject userInfo){
        throw new MicroServiceException(new StateCodeImpl(-2, ""));
    }

    @MicroServiceMethodProxy(microserviceKey = "userSystem", path = "/user/modify", type = RequestType.POST)
    public JSONObject modify(JSONObject userInfo) {
        throw new MicroServiceException(new StateCodeImpl(-2, ""));
    }

    @MicroServiceMethodProxy(microserviceKey = "userSystem", path = "/user/remove", type = RequestType.POST)
    public void deleteUser(String rowId) {
        throw new MicroServiceException(new StateCodeImpl(-2, ""));
    }

    @MicroServiceMethodProxy(microserviceKey = "userSystem", path = "/user/setadmin", type = RequestType.POST)
    public void setAdmin(JSONObject postData) {
        throw new MicroServiceException(new StateCodeImpl(-2, ""));
    }

    @MicroServiceMethodProxy(microserviceKey = "userSystem", path = "/user/modifypassword", type = RequestType.POST)
    public void changePassword(JSONObject postData) {
        throw new MicroServiceException(new StateCodeImpl(-2, ""));
    }

    @MicroServiceMethodProxy(microserviceKey = "userSystem", path = "/user/setenable", type = RequestType.POST)
    public void setEnable(JSONObject postData) {
        throw new MicroServiceException(new StateCodeImpl(-2, ""));
    }

    @MicroServiceMethodProxy(microserviceKey = "userSystem", path = "/user/resetpassword", type = RequestType.POST)
    public void resetPassword(JSONObject postData) {
        throw new MicroServiceException(new StateCodeImpl(-2, ""));
    }

    @MicroServiceMethodProxy(microserviceKey = "userSystem", path = "/user/getlistcount")
    public Integer getListCount(String keyword) {
        throw new MicroServiceException(new StateCodeImpl(-2, ""));
    }
}
