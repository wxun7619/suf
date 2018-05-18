package com.lonntec.domainservice.adminportal.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lonntec.domainservice.adminportal.proxy.DeploySystemProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import team.benchem.framework.annotation.RequestTokenValidate;

@CrossOrigin
@RequestMapping("/deploy")
@RestController
public class DeployManagerController {

    @Autowired
    DeploySystemProxy deploySystemProxy;
    /**
     * 获取开通申请列表
     */
    @RequestTokenValidate
    @RequestMapping("/getlist")
    public JSONArray getList(
            @RequestParam @Nullable String keyword,
            @RequestParam @Nullable Integer page,
            @RequestParam @Nullable Integer size){

       return deploySystemProxy.getList(keyword,page,size);
    }

    /**
     * 获取开通申请数量
     */
    @RequestTokenValidate
    @RequestMapping("/getlistcount")
    public Integer getListCount(String keyword){
        return deploySystemProxy.getListCount(keyword);
    }

    /**
     * 递交开通申请
     */
    @RequestTokenValidate
    @RequestMapping("/apply")
    public JSONObject apply(@RequestBody JSONObject applyInfo) {
        return deploySystemProxy.apply(applyInfo);
    }
    /**
     *
     * 审核开通申请
     */
    @RequestTokenValidate
    @RequestMapping("/auditapply")
    public JSONObject auditApply(@RequestBody JSONObject postBody){
       return deploySystemProxy.auditApply(postBody);
    }

}
