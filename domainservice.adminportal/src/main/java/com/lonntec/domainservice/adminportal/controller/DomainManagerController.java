package com.lonntec.domainservice.adminportal.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lonntec.domainservice.adminportal.proxy.DomainServiceProxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.benchem.framework.annotation.RequestTokenValidate;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/domain")
public class DomainManagerController {

    @Autowired
    DomainServiceProxy domainService;


    //创建企业域
    @RequestTokenValidate
    @RequestMapping("/create")
    public JSONObject createDomain(@RequestBody JSONObject postData){
       return domainService.createDomain(postData);
    }

    //获取企业列表
    @RequestTokenValidate
    @RequestMapping("/list")
    public JSONArray findDomains(
            @PathParam("keyword") String keyword,
            @PathParam("page") Integer page,
            @PathParam("size") Integer size){
        return domainService.findDomains(keyword, page, size);
    }

    //获取用户数量
    @RequestTokenValidate
    @RequestMapping("/listcount")
    public Integer getListCount(@PathParam("keyword") String keyword){
        return domainService.findDomainsCount(keyword);
    }

    //修改企业域
    @RequestTokenValidate
    @RequestMapping("/modify")
    public JSONObject modify(@RequestBody JSONObject postData){

        return  domainService.modifyDomain(postData);


    }

    //启用/禁用企业域
    @RequestTokenValidate
    @RequestMapping("/setenable")
    public void setEnable(@RequestBody JSONObject postForm){

        domainService.setEnable(postForm);
    }

    //企业域实施人员变更
    @RequestTokenValidate
    @RequestMapping("/changeuser")
    public JSONObject changeUser(@RequestBody JSONObject postData){


        return domainService.changeUser(postData);
    }

    //获取已开通SUF企业列表
    @RequestTokenValidate
    @RequestMapping("/activesuflist")
    public JSONArray findActiveSufListDomains(
            @PathParam("keyword") String keyword,
            @PathParam("page") Integer page,
            @PathParam("size") Integer size
    ){
        JSONObject postData = new JSONObject();
        postData.put("keyword", keyword);
        postData.put("page", page);
        postData.put("size", size);
        return domainService.findActiveSufListDomains(postData);
    }

    //获取未开通SUF企业列表
    @RequestTokenValidate
    @RequestMapping("/unactivesuflist")
    public JSONArray findNotActiveSufListDomains(
            @PathParam("keyword") String keyword,
            @PathParam("page") Integer page,
            @PathParam("size") Integer size
    ){
        JSONObject postData = new JSONObject();
        postData.put("keyword", keyword);
        postData.put("page", page);
        postData.put("size", size);
        return domainService.findNotActiveSufListDomains(postData);
    }
}
