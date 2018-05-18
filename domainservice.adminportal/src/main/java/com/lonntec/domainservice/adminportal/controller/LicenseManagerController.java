package com.lonntec.domainservice.adminportal.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lonntec.domainservice.adminportal.proxy.DeploySystemProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import team.benchem.framework.annotation.RequestTokenValidate;
@CrossOrigin
@RequestMapping("/license")
@RestController
public class LicenseManagerController {
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
            @RequestParam @Nullable Integer size
    ){
        return deploySystemProxy.getlicenselist(keyword,page,size);
    }

    /**
     * 获取开通申请数量
     */
    @RequestTokenValidate
    @GetMapping("/getlistcount")
    public Integer getListCount(String keyword){
        return deploySystemProxy.getlicenseListCount(keyword);
    }

    /**
     * 递交开通申请
     */
    @RequestTokenValidate
    @PostMapping("/apply")
    public JSONObject apply(@RequestBody JSONObject applyInfo) {
        return deploySystemProxy.licenseapply(applyInfo);
    }
    /**
     *
     * 审核开通申请
     */
    @RequestTokenValidate
    @PostMapping("/auditapply")
    public JSONObject auditApply(@RequestBody JSONObject postBody){
        return deploySystemProxy.licenseauditApply(postBody);
    }
}
