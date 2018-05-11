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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
//
//    @Autowired
//    DomainRepository domainRepository;

//    @RequestMapping("/buildmock")
//    public void createMokeList(){
//        User norUser =  userRepository.findById("394d3587-c1eb-4233-9ed8-c4a2e3bddc6b").get();
//        User adminUser = userRepository.findById("af4e2e8b-c8d8-4803-9c48-7004c0a73902").get();
//
//        Integer orderIndex = 1;
//        Random random = new Random();
//        for(int index=0; index < 200; index++,orderIndex++){
//            Domain domainInfo = new Domain();
//            domainInfo.setDomainNumber(String.format("DM%s", padLeft(orderIndex.toString(), 6, '0')));
//            domainInfo.setDomainName(String.format("DN%s", padLeft(orderIndex.toString(), 6, '0')));
//            domainInfo.setDomainShortName(String.format("Ds%s", padLeft(orderIndex.toString(), 6, '0')));
//            domainInfo.setAddress("AAAAAAA");
//            domainInfo.setLinkMan("AAACCCCC");
//            domainInfo.setLinkManMobile(String.format("13955%s", padLeft(orderIndex.toString(), 6, '0')));
//            domainInfo.setBusinessLicense("");
//            domainInfo.setMemo("");
//            domainInfo.setOwnerUser( random.nextInt() % 2 == 0 ? norUser : adminUser);
//            domainService.appendDomain(domainInfo);
//        }
//    }
//
//    /**
//     * @作者 尧
//     * @功能 String右对齐
//     */
//    public static String padLeft(String src, int len, char ch) {
//        int diff = len - src.length();
//        if (diff <= 0) {
//            return src;
//        }
//        char[] charr = new char[len];
//        System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
//        for (int i = 0; i < diff; i++) {
//            charr[i] = ch;
//        }
//        return new String(charr);
//    }

    //创建企业域
    @RequestTokenValidate
    @RequestMapping("/create")
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

        if(ownerId==null||ownerId.replaceAll("\\s*","").equals("")){
            throw new DomainSystemException(DomainSystemStateCode.OwnerUserId_IsEmpty);
        }
        Optional<User> userOptional = userRepository.findById(ownerId);
        if(!userOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.OwnerUserId_IsNotExist);
        }

        Domain domainInfo = new Domain();
        domainInfo.setDomainName(domainName);
        domainInfo.setDomainShortName(domainShortName);
        domainInfo.setAddress(address);
        domainInfo.setLinkMan(linkMan);
        domainInfo.setLinkManMobile(linkManMobile);
        domainInfo.setBusinessLicense(businessLicense);
        domainInfo.setMemo(memo);
        domainInfo.setOwnerUser(userOptional.get());

        //todo:
        domainService.appendDomain(domainInfo);
        return domainInfo;
    }

    //获取企业列表
    @RequestTokenValidate
    @RequestMapping("/list")
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
    @RequestMapping("/modify")
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
    @RequestMapping("/setenable")
    public void setEnable(@RequestBody JSONObject postForm){
        String rowId = postForm.getString("rowId");
        Boolean isEnable = postForm.getBoolean("isEnable");
        domainService.setEnable(rowId,isEnable);
    }

    //企业域实施人员变更
    @RequestTokenValidate
    @RequestMapping("/changeuser")
    public Domain changeUser(@RequestBody JSONObject postData){

        String domainId = postData.getString("domainId");
        String newOwnerId = postData.getString("newOwnerId");

        //todo:
        return domainService.changeUser(domainId, newOwnerId);
    }

    //获取已开通SUF企业列表
    @RequestTokenValidate
    @RequestMapping("/activesuflist")
    public JSONArray findActiveSufListDomains(
            @PathParam("keyword") String keyword,
            @PathParam("page") Integer page,
            @PathParam("size") Integer size
    ){
        List<Domain> domainList = domainService.findActiveSufListDomains(keyword, page, size);
        JSONArray reValue = new JSONArray();
        for(Domain item:domainList){
            JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(item));
            reValue.add(json);
        }
        return reValue;
    }

    //获取未开通SUF企业列表
    @RequestTokenValidate
    @RequestMapping("/unactivesuflist")
    public JSONArray findNotActiveSufListDomains(
            @PathParam("keyword") String keyword,
            @PathParam("page") Integer page,
            @PathParam("size") Integer size
    ){
        List<Domain> domainList = domainService.findNotActiveSufListDomains(keyword, page, size);
        JSONArray reValue = new JSONArray();
        for(Domain item:domainList){
            JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(item));
            reValue.add(json);
        }
        return reValue;
    }
}
