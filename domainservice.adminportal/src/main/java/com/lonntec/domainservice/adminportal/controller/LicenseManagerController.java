package com.lonntec.domainservice.adminportal.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lonntec.domainservice.adminportal.proxy.DeploySystemProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.benchem.framework.annotation.RequestTokenValidate;

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
    public JSONArray getList(String keyword, Integer page, Integer size
    ){
        return deploySystemProxy.getlicenselist(keyword,page,size);
    }

    /**
     * 获取开通申请数量
     */
    @RequestTokenValidate
    @RequestMapping("/getlistcount")
    public Integer getListCount(String keyword){
        return deploySystemProxy.getlicenseListCount(keyword);
    }

    /**
     * 递交开通申请
     */
    @RequestTokenValidate
    @RequestMapping("/apply")
    public JSONObject apply(@RequestBody JSONObject applyInfo) {
        return deploySystemProxy.licenseapply(applyInfo);
    }
    /**
     *
     * 审核开通申请
     */
    @RequestTokenValidate
    @RequestMapping("/auditapply")
    public void auditApply(String applyId,Boolean isPass,String auditMemo
    ){
        deploySystemProxy.licenseauditApply(applyId,isPass,auditMemo);
    }
}
