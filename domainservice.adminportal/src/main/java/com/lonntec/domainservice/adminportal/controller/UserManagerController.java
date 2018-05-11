package com.lonntec.domainservice.adminportal.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lonntec.domainservice.adminportal.proxy.UserSystemProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import team.benchem.framework.annotation.RequestTokenValidate;

@CrossOrigin
@RequestMapping("/user")
@RestController
public class UserManagerController {

    @Autowired
    UserSystemProxy userService;

    //获取用户列表
    @RequestTokenValidate
    @RequestMapping("/getlist")
    public JSONArray getUserList(
            @RequestParam @Nullable String keyword,
            @RequestParam @Nullable Integer page,
            @RequestParam @Nullable Integer size)  {
        
        return userService.getlist(keyword, page, size);
    }

    //创建用户
    @RequestTokenValidate
    @RequestMapping("/create")
    public JSONObject createUser(@RequestBody JSONObject userInfo) {
        return userService.create(userInfo);
    }

    //编辑用户
    @RequestTokenValidate
    @RequestMapping("/modify")
    public JSONObject modify(@RequestBody JSONObject userInfo){
        return userService.modify(userInfo);
    }

    //删除用户
    @RequestTokenValidate
    @RequestMapping("/remove")
    public void deleteUser(@RequestBody JSONObject postForm){
        String rowId = postForm.getString("rowId");
        userService.deleteUser(rowId);
    }

    //设置管理员权限
    @RequestTokenValidate
    @RequestMapping("/setadmin")
    public void setAdmin(@RequestBody JSONObject postForm){
        userService.setAdmin(postForm);
    }

    //修改密码
    @RequestTokenValidate
    @RequestMapping("/modifypassword")
    public void changepassword(@RequestBody JSONObject postForm){
        userService.changePassword(postForm);
    }

    //启用/禁用用户
    @RequestTokenValidate
    @RequestMapping("/setenable")
    public void setEnable(@RequestBody JSONObject postForm){
        userService.setEnable(postForm);
    }
    
    //重置密码
    @RequestTokenValidate
    @RequestMapping("/resetpassword")
    public void reSetPassword(@RequestBody JSONObject postForm){
        userService.resetPassword(postForm);
    }

    //获取用户数量
    @RequestTokenValidate
    @RequestMapping("/getlistcount")
    public Integer getListCount(@RequestParam @Nullable String keyword){
        return userService.getListCount(keyword);
    }
}
