package com.lonntec.domainservice.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lonntec.domainservice.entity.Domain;
import com.lonntec.domainservice.entity.User;
import com.lonntec.domainservice.lang.DomainSystemException;
import com.lonntec.domainservice.lang.DomainSystemStateCode;
import com.lonntec.domainservice.repository.DomainRepository;
import com.lonntec.domainservice.repository.UserRepository;
import com.lonntec.domainservice.service.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import team.benchem.framework.annotation.RequestTokenValidate;
import team.benchem.framework.sdk.UserContext;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@CrossOrigin
@RestController
@RequestMapping("/domain")
public class DomainController {

    @Autowired
    DomainService domainService;

    @Autowired
    UserRepository userRepository;
    //创建企业域
    @RequestTokenValidate
    @PostMapping("/create")
    public Domain createDomain(@RequestBody JSONObject postData){

        UserContext currCtx = UserContext.getCurrentUserContext();

        String ownerId = currCtx.properties.getString("rowid");
        String domainName = postData.getString("domainName");
        String domainShortName = postData.getString("domainShortName");
        String address = postData.getString("address");
        String linkMan = postData.getString("linkMan");
        String linkManMobile = postData.getString("linkManMobile");
        String businessLicense = postData.getString("businessLicense");
        String memo = postData.getString("memo");


        Domain domainInfo = new Domain();
        domainInfo.setDomainName(domainName);
        domainInfo.setDomainShortName(domainShortName);
        domainInfo.setAddress(address);
        domainInfo.setLinkMan(linkMan);
        domainInfo.setLinkManMobile(linkManMobile);
        domainInfo.setBusinessLicense(businessLicense);
        domainInfo.setMemo(memo);
        //domainInfo.setOwnerUser(userOptional.get());

        //todo:
        domainService.appendDomain(domainInfo,ownerId );
        return domainInfo;
    }

    //获取企业列表
    @RequestTokenValidate
    @GetMapping("/list")
    public JSONArray findDomains(
            @PathParam("keyword") String keyword,
            @PathParam("page") Integer page,
            @PathParam("size") Integer size
    ){
        List<Domain> domainList = domainService.findDomains(keyword, page, size);
        JSONArray reValue = new JSONArray();
        for(Domain item:domainList){
            JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(item));
            reValue.add(json);
        }
        return reValue;
    }

    //获取用户数量
    @RequestTokenValidate
    @RequestMapping("/listcount")
    public Integer getListCount(@PathParam("keyword") String keyword){

                return domainService.getListCount(keyword);
    }

    //修改企业域
    @RequestTokenValidate
    @PostMapping("/modify")
    public JSONObject modify(@RequestBody JSONObject postData){
        String domainId = postData.getString("rowId");
        String domainName = postData.getString("domainName");
        String domainShortName = postData.getString("domainShortName");
        String address = postData.getString("address");
        String linkMan = postData.getString("linkMan");
        String linkManMobile = postData.getString("linkManMobile");
        String businessLicense = postData.getString("businessLicense");
        String memo = postData.getString("memo");

        Domain domainInfo = new Domain();
        domainInfo.setRowId(domainId);
        domainInfo.setDomainName(domainName);
        domainInfo.setDomainShortName(domainShortName);
        domainInfo.setAddress(address);
        domainInfo.setLinkMan(linkMan);
        domainInfo.setLinkManMobile(linkManMobile);
        domainInfo.setBusinessLicense(businessLicense);
        domainInfo.setMemo(memo);

        Domain domain = domainService.modifyDomain(domainInfo);

        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(domain));
        return json;
    }

    //启用/禁用企业域
    @RequestTokenValidate
    @PostMapping("/setenable")
    public void setEnable(@RequestBody JSONObject postForm){
        String rowId = postForm.getString("rowId");
        Boolean isEnable = postForm.getBoolean("isEnable");
        domainService.setEnable(rowId,isEnable);
    }

    //企业域实施人员变更
    @RequestTokenValidate
    @PostMapping("/changeuser")
    public Domain changeUser(@RequestBody JSONObject postData){

        String domainId = postData.getString("domainId");
        String newOwnerId = postData.getString("newOwnerId");

        //todo:
        return domainService.changeUser(domainId, newOwnerId);
    }

    //获取已开通SUF企业列表
    @RequestTokenValidate
    @GetMapping("/activesuflist")
    public JSONArray findActiveSufListDomains(
            @PathParam("keyword") String keyword,
            @PathParam("page") Integer page,
            @PathParam("size") Integer size
    ){
        List<Domain> domainList = domainService.findActiveSufListDomains(keyword, page, size,true);
        JSONArray reValue = new JSONArray();
        for(Domain item:domainList){
            JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(item));
            json.remove("domainShortName");
            json.remove("address");
            json.remove("linkMan");
            json.remove("linkManMobile");
            json.remove("businessLicense");
            json.remove("memo");
            json.remove("ownerUser");
            json.remove("domainUsers");
            json.remove("isEnable");
            json.remove("isActiveSuf");
            reValue.add(json);
        }
        return reValue;
    }

    //获取未开通SUF企业列表
    @RequestTokenValidate
    @GetMapping("/unactivesuflist")
    public JSONArray findNotActiveSufListDomains(
            @PathParam("keyword") String keyword,
            @PathParam("page") Integer page,
            @PathParam("size") Integer size
    ){
        List<Domain> domainList = domainService.findActiveSufListDomains(keyword, page, size,false);
        JSONArray reValue = new JSONArray();
        for(Domain item:domainList){
            JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(item));
            json.remove("domainShortName");
            json.remove("address");
            json.remove("userCount");
            json.remove("expireDate");
            json.remove("businessLicense");
            json.remove("memo");
            json.remove("ownerUser");
            json.remove("domainUsers");
            json.remove("isEnable");
            json.remove("isActiveSuf");
            reValue.add(json);
        }
        return reValue;
    }
}
