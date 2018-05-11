package team.benchem.usersystem.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.benchem.framework.annotation.MicroServiceValidatePermission;
import team.benchem.framework.annotation.RequestTokenValidate;
import team.benchem.usersystem.entity.User;
import team.benchem.usersystem.service.UserService;

import javax.websocket.server.PathParam;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;
    //创建用户
    @RequestTokenValidate
    @RequestMapping("/create")
    public User createUser(@RequestBody User userInfo){
        //todo:
        userService.appendUser(userInfo);
        return userInfo;
    }
    //获取用户列表
    @RequestTokenValidate
    @RequestMapping("/getlist")
    public JSONArray findUsers(
            @PathParam("keyword") @Nullable String keyword,
            @PathParam("page") @Nullable Integer page,
            @PathParam("size") @Nullable Integer size) {
        List<User> userList = userService.findUsers(keyword, page, size);
        JSONArray reValue = new JSONArray();
        for(User item:userList){
            JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(item));
            json.remove("passwordHash");
            json.remove("lastLoginTime");
            reValue.add(json);
        }
        return reValue;
    }
    //编辑用户
    @RequestTokenValidate
    @RequestMapping("/modify")
    public JSONObject modify(@RequestBody JSONObject userInfo){
        String rowId = userInfo.getString("rowId");
        String nickname = userInfo.getString("nickname");
        String email = userInfo.getString("email");
        String mobile=userInfo.getString("mobile");

        User modifyUser = new User();
        modifyUser.setRowId(rowId);
        modifyUser.setNickname(nickname);
        modifyUser.setEmail(email);
        modifyUser.setMobile(mobile);
        User user =  userService.modifyUser(modifyUser);

        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(user));
        json.remove("passwordHash");
        json.remove("lastLoginTime");
        return json;
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
        String rowId = postForm.getString("rowId");
        Boolean isAdmin = postForm.getBoolean("isAdmin");
        userService.setAdmin(rowId,isAdmin);
    }
    //修改密码
    @RequestTokenValidate
    @RequestMapping("/modifypassword")
    public void changepassword(@RequestBody JSONObject postData){
        String rowId =postData.getString("rowId");
        String oldPassword = postData.getString("oldpassword");
        String newPassword = postData.getString("newpassword");
        userService.changePassword(rowId,oldPassword,newPassword);
    }
    //启用/禁用用户
    @RequestTokenValidate
    @RequestMapping("/setenable")
    public void setEnable(@RequestBody JSONObject postForm){
        String rowId = postForm.getString("rowId");
        Boolean isEnable = postForm.getBoolean("isEnable");
        userService.setEnable(rowId,isEnable);
    }
    //重置密码
    @RequestTokenValidate
    @RequestMapping("/resetpassword")
    public void reSetPassword(@RequestBody JSONObject postData){
        String rowId = postData.getString("rowId");
        String newPassword = postData.getString("newpassword");
        userService.resetPassword(rowId,newPassword);
    }

    //获取用户数量
    @RequestTokenValidate
    @RequestMapping("/getlistcount")
    public Integer getListCount(@PathParam("keyword") String keyword){
        return userService.getListCount(keyword);
    }
}
