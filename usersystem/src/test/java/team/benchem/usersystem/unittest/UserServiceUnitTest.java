package team.benchem.usersystem.unittest;

import com.alibaba.fastjson.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.benchem.framework.lang.MicroServiceException;
import team.benchem.framework.test.UserContextScope;
import team.benchem.usersystem.entity.User;
import team.benchem.usersystem.lang.UserSystemException;
import team.benchem.usersystem.lang.UserSystemStateCode;
import team.benchem.usersystem.repository.UserRepository;
import team.benchem.usersystem.service.UserService;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceUnitTest {
    @Autowired
    UserContextScope userContextScope;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Before
    public  void beforeTest(){

        User u=new User();
        u.setRowId("u123");
        u.setUsername("admin");
        u.setPassword("123");
        u.setNickname("dandy");
        u.setMobile("666");
        u.setEmail("abc@666.com");
        u.setIsAdmin(true);
        u.setIsEnable(true);
        userService.appendUser(u);

        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);

        for(int i=0; i<201; i++){
            User user = new User();
            String indexStr = String.format("%s", i);
            user.setRowId(indexStr);
            user.setUsername(String.format("user%s", padLeft(indexStr, 3, '0')));
            System.out.println(user.getUsername());
            user.setNickname(String.format("user%s", padLeft(indexStr, 3, '0')));
            user.setMobile(String.format("131%s", padLeft(indexStr, 8, '0')));
            System.out.println(user.getMobile());
            user.setEmail(String.format("131%s@139.com", padLeft(indexStr, 8, '0')));
            user.setPassword("123");
            user.setIsAdmin(false);
            user.setIsEnable(true);
            userService.appendUser(user);
        }
    }

    @After
    public void afterTest(){
        userRepository.deleteAll();
    }

    /**
     *
     * 查询用户列表
    */


    @Test          //关键字为空白字符 页数为1 显示条数为25
    public void test_getUserList_case1(){
        List<User> userList= userService.findUsers("", 1, 25);
        Assert.assertEquals(userList.size(), 25);
    }


    @Test         //关键字为空白字符 页数为9 （最后一页仅两条数据）
    public void test_getUserList_case2(){
        final Integer pageSize = 25;
        Integer totalCount =  new Long (userRepository.count()).intValue();
        Integer pageCount = (totalCount + pageSize - 1) / pageSize;
        int lastPageRecordCount = totalCount % pageSize;

        List<User> userList= userService.findUsers("", pageCount, pageSize);
        Assert.assertEquals(userList.size(), lastPageRecordCount);
    }

/*
    @Test         //关键字为空白字符 页数为0 显示条数为25
    public void test_getUserList_case3(){
        List<User> userList= userService.findUsers("", 0, 25);
        Assert.assertEquals(userList.size(), 25);
    }

    @Test        //关键字为10 页数为1 显示条数为25(实际只有12条)
    public void test_getUserList_case4(){
        List<User> userList= userService.findUsers("10", 1, 25);
        for (User u :userList) {
            System.out.println(u.getMobile());
        }
        Assert.assertEquals(userList.size(), 13);
    }

    @Test        //关键字为%1%9 页数为1 显示条数为25(实际只有21条)
    public void test_getUserList_case5(){
        List<User> userList= userService.findUsers("%1%9", 1, 25);
        for (User u :userList) {
            System.out.println(u.getUsername());
        }
        Assert.assertEquals(userList.size(), 21);
    }

    @Test        //关键字为u %1%9 页数为1 显示条数为25(关键字中有空格)
    public void test_getUserList_case6(){
        List<User> userList= userService.findUsers("u %1%9", 1, 25);
        Assert.assertEquals(userList.size(), 0);
    }

    @Test        //关键字为空白字符 页数为1 显示条数为10
    public void test_getUserList_case7(){
        List<User> userList= userService.findUsers("", 1, 10);
        Assert.assertEquals(userList.size(), 10);
    }

    @Test        //关键字为空白字符 页数为1 显示条数为-1(实际有25条)
    public void test_getUserList_case8(){
        List<User> userList= userService.findUsers("", 1, -1);
        Assert.assertEquals(userList.size(), 25);
    }

    @Test        //关键字为空白字符 页数为1 显示条数为26
    public void test_getUserList_case9(){
        List<User> userList= userService.findUsers("", 1, 26);
        Assert.assertEquals(userList.size(), 26);
    }


    @Test        //关键字为空白字符 页数为10 显示条数为25
    public void test_getUserList_case10(){
        List<User> userList= userService.findUsers("", 10, 25);
        Assert.assertEquals(userList.size(), 0);
    }*/

    /**
     *
     * 创建用户
     */
    @Test        //成功创建测试
    public void test_appendUser_case1(){
        User u = new User();
        u.setRowId("2018001");
        u.setUsername("u1");
        u.setNickname("dandy");
        u.setPassword("123");
        u.setEmail("2018@01");
        u.setMobile("1001");
        u.setIsEnable(false);
        u.setIsAdmin(false);
        User uu=userService.appendUser(u);
        Assert.assertEquals(uu.getRowId(),"2018001");
        Assert.assertEquals(uu.getPasswordHash(),null);
    }

    @Test        //用户名为空白字符
    public void test_appendUser_case2(){
        User u = new User();
        u.setRowId("2018002");
        u.setNickname("dandy2");
        u.setPassword("123");
        u.setEmail("2018@02");
        u.setMobile("1002");
        u.setIsEnable(false);
        u.setIsAdmin(false);
        try {
            userService.appendUser(u);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.UserName_IsEmpty.getCode());
        }
    }

    @Test        //昵称为空白字符
    public void test_appendUser_case3(){
        User u = new User();
        u.setRowId("2018003");
        u.setUsername("u3");
        u.setPassword("123");
        u.setEmail("2018@03");
        u.setMobile("1003");
        u.setIsEnable(false);
        u.setIsAdmin(false);
        try {
            userService.appendUser(u);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.NickName_IsEmpty.getCode());
        }
    }

    @Test        //密码为空白字符
    public void test_appendUser_case4(){
        User u = new User();
        u.setRowId("2018004");
        u.setUsername("u4");
        u.setNickname("dandy4");
        u.setEmail("2018@04");
        u.setMobile("1004");
        u.setIsEnable(false);
        u.setIsAdmin(false);
        try {
            userService.appendUser(u);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.Password_IsEmpty.getCode());
        }
    }

//    @Deprecated()  //已修改逻辑，创建用户时，isAdmin默认是false
//    @Test        //是否管理员为空白字符
//    public void test_appendUser_case5(){
//        User u = new User();
//        u.setRowId("2018001");
//        u.setUsername("u1");
//        u.setNickname("dandy");
//        u.setPassword("123");
//        u.setEmail("2018@01");
//        u.setMobile("1001");
//        u.setIsEnable(false);
//        try {
//            userService.appendUser(u);
//            Assert.assertEquals(true,false);
//        }catch (UserSystemException ex){
//            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.IsAdmin_IsEmpty.getCode());
//        }
//    }

//    @Deprecated()  //已修改逻辑，创建用户时，isEnable默认是false
//    @Test        //是否启用为空白字符
//    public void test_appendUser_case6(){
//        User u = new User();
//        u.setRowId("2018001");
//        u.setUsername("u1");
//        u.setNickname("dandy");
//        u.setPassword("123");
//        u.setEmail("2018@01");
//        u.setMobile("1001");
//        u.setIsAdmin(false);
//        try {
//            userService.appendUser(u);
//            Assert.assertEquals(true,false);
//        }catch (UserSystemException ex){
//            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.IsEnable_IsEmpty.getCode());
//        }
//    }

    @Test        //用户名重复
    public void test_appendUser_case7(){
        User u = new User();
        u.setUsername("admin");
        u.setNickname("dandy7");
        u.setPassword("123");
        u.setEmail("2018@07");
        u.setMobile("1007");
        u.setIsAdmin(false);
        u.setIsEnable(false);
        try {
            userService.appendUser(u);
            Assert.assertEquals(true, false);
        }catch(UserSystemException ex ){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.UserName_IsExites.getCode()
            );
        }
    }

    @Test        //手机号码重复
    public void test_appendUser_case8(){
        User u1=new User();
        u1.setUsername("u8");
        u1.setNickname("dandy8");
        u1.setPassword("123");
        u1.setMobile("666");
        u1.setEmail("2018@08");
        u1.setIsAdmin(false);
        u1.setIsEnable(false);
        try {
            userService.appendUser(u1);
            Assert.assertEquals(true, false);
        }catch(UserSystemException ex ){
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.Mobile_IsExites.getCode());
        }
    }

    @Test        //邮箱冲突
    public void test_appendUser_case9(){
        User u1=new User();
        u1.setUsername("u9");
        u1.setNickname("dandy9");
        u1.setPassword("123");
        u1.setMobile("1009");
        u1.setEmail("abc@666.com");
        u1.setIsAdmin(false);
        u1.setIsEnable(false);
        try {
            userService.appendUser(u1);
            Assert.assertEquals(true, false);
        }catch(UserSystemException ex ){
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.Email_IsExites.getCode());
        }
    }

    @Test        //手机号为空白字符
    public void test_appendUser_case10(){
        User u = new User();
        u.setRowId("2018010");
        u.setUsername("u10");
        u.setNickname("dandy10");
        u.setPassword("123");
        u.setEmail("2018@10");
        u.setIsEnable(false);
        u.setIsAdmin(false);
        try {
            userService.appendUser(u);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.Mobile_IsEmpty.getCode());
        }
    }

    @Test        //邮箱为空白字符
    public void test_appendUser_case11(){
        User u = new User();
        u.setRowId("2018011");
        u.setUsername("u11");
        u.setNickname("dandy11");
        u.setPassword("123");
        u.setMobile("1010");
        u.setIsEnable(false);
        u.setIsAdmin(false);
        try {
            userService.appendUser(u);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.Email_IsEmpty.getCode());
        }
    }
    /**
    *
    * 编辑用户
    */
    @Test       //编辑成功测试
    public void test_modUser_case1(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        User u=new User();
        u.setRowId("1");
        u.setMobile("0000001");
        u.setEmail("a@001.com");
        u.setNickname("dandy");
        User ret=userService.modifyUser(u);
        System.out.println(ret.toString());
        Assert.assertEquals(ret.getNickname(),"dandy");
    }

    @Test       //rowId为空白字符
    public void test_modUser_case2(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        User u=new User();
        u.setRowId("");
        u.setMobile("0000002");
        u.setEmail("a@002.com");
        u.setNickname("dandy");
        try {
            userService.modifyUser(u);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.User_IsNotExites.getCode());
        }
    }

    @Test       //号码为空白字符
    public void test_modUser_case3(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        User u=new User();
        u.setRowId("1");
        u.setMobile("");
        u.setEmail("a@003.com");
        u.setNickname("dandy");
        try {
            userService.modifyUser(u);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.Mobile_IsEmpty.getCode());
        }
    }

    @Test       //邮箱为空白字符
    public void test_modUser_case4(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        User u=new User();
        u.setRowId("1");
        u.setMobile("0000004");
        u.setEmail("");
        u.setNickname("dandy");
        try {
            userService.modifyUser(u);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.Email_IsEmpty.getCode());
        }
    }

    @Test       //昵称为空白字符
    public void test_modUser_case5(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        User u=new User();
        u.setRowId("1");
        u.setMobile("0000005");
        u.setEmail("a@005.com");
        u.setNickname("     ");
        try {
            userService.modifyUser(u);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.NickName_IsEmpty.getCode());
        }
    }


    @Test       //用户不存在
    public void test_modUser_case6(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        User u=new User();
        u.setRowId("u124444");
        u.setMobile("0000006");
        u.setEmail("a@006.com");
        u.setNickname("dandy");
        try {
            userService.modifyUser(u);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.User_IsNotExites.getCode());
        }
    }

    @Test       //号码已占用
    public void test_modUser_case7(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        User u=new User();
        u.setRowId("1");
        u.setMobile("666");
        u.setEmail("a@007.com");
        u.setNickname("dandy");
        try {
            userService.modifyUser(u);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.Mobile_IsExites.getCode());
        }
    }

    @Test       //邮箱已占用
    public void test_modUser_case8(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        User u=new User();
        u.setRowId("1");
        u.setMobile("0000008");
        u.setEmail("abc@666.com");
        u.setNickname("dandy");
        try {
            userService.modifyUser(u);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.Email_IsExites.getCode());
        }
    }

    /**
     *
     * 删除用户
     * 管理员不允许删除
     */
    @Test(expected = MicroServiceException.class)
    public void test_delUser_case1(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        userService.deleteUser("u123");
    }

    @Test       //用户id为空白字符
    public void test_delUser_case2(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.deleteUser("");
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.User_IsNotExites.getCode());
        }
    }

    @Test       //用户不存在
    public void test_delUser_case3(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.deleteUser("u124444");
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.User_IsNotExites.getCode());
        }
    }

    /**
     *
     * 提升/降级管理员标识
     */
    @Test       //操作成功
    public void  test_setAdmin_case1(){

        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        userService.setAdmin("1",true);
        Optional<User> optional=userRepository.findById("1");
        Assert.assertEquals(optional.get().getIsAdmin(),true);
    }

    @Test       //用户id为空白字符
    public void  test_setAdmin_case2(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.setAdmin("      ",false);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.User_IsNotExites.getCode());
        }
    }

    @Test       //管理员标识为空白字符
    public void  test_setAdmin_case3(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.setAdmin("20",null);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.IsAdmin_IsEmpty.getCode());
        }
    }

    @Test       //用户不存在
    public void  test_setAdmin_case4(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.setAdmin("u124444",false);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.User_IsNotExites.getCode());
        }
    }

    @Test       //admin账号不允许撤销管理员权限
    public void  test_setAdmin_case5(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.setAdmin("u123",false);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.Admin_CanNotDisableAdmin.getCode());
        }
    }

    @Test       //非admin帐号没有权限
    public void  test_setAdmin_case6(){
        JSONObject context = new JSONObject();
        context.put("rowid", "0");
        context.put("username", "user000");
        userContextScope.mock(context);
        try {
            userService.setAdmin("2",true);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.No_Permission.getCode());
        }
    }

    /**
     *
     * 修改密码
     */
    @Test       //修改成功
    public void test_changePassword_case1(){
        JSONObject context = new JSONObject();
        context.put("rowid", "60");
        context.put("username", "user060");
        userContextScope.mock(context);
        userService.changePassword("60","123","456");
        Optional<User> optional= userRepository.findById("60");
        String pwd=optional.get().getPasswordHash();
        User user=new User();
        user.setPassword("456");
        Assert.assertEquals(pwd,user.getPasswordHash());
    }

    @Test       //用户id为空白字符
    public void test_changePassword_case2(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.changePassword("","123","456");
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.User_IsNotExites.getCode());
        }
    }

    @Test       //旧密码为空白字符
    public void test_changePassword_case3(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.changePassword("u123", "", "456");
            Assert.assertEquals(true, false);
        } catch (UserSystemException ex) {
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.OldPassword_IsEmpty.getCode());
        }
    }

    @Test       //新密码为空白字符
    public void test_changePassword_case4(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.changePassword("1", "1", "");
            Assert.assertEquals(true, false);
        } catch (UserSystemException ex) {
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.NewPassword_IsEmpty.getCode());
        }
    }

    @Test       //用户不存在
    public void  test_changePassword_case5(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.changePassword("u234","123","456");
            Assert.assertEquals(true, false);
        }catch (UserSystemException ex) {
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.User_IsNotExites.getCode());
        }
    }

    @Test       //旧密码不正确
    public void  test_changePassword_case6(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.changePassword("u123","23444","456");
            Assert.assertEquals(true, false);
        }catch (UserSystemException ex) {
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.OldPassword_isErr.getCode());
        }
    }

    @Test       //新密码与旧密码相同
    public void  test_changePassword_case7(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.changePassword("10","123","123");
            Assert.assertEquals(true, false);
        }catch (UserSystemException ex) {
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.Password_IsReqeat.getCode());
        }
    }

    /**
     *
     * 启用/禁用用户
     */
    @Test       //成功启用
    public void test_setEnable_case1(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        userService.setEnable("100",true);
    }

    @Test       //用户id为空白字符
    public void  test_setEnable_case2(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.setEnable("     ",false);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.User_IsNotExites.getCode());
        }
    }

    @Test       //是否启用为空白字符
    public void  test_setEnable_case3(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.setEnable("u123",null);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.IsEnable_IsEmpty.getCode());
        }
    }

    @Test       //用户不存在
    public void  test_setEnable_case4(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.setEnable("u234",true);
            Assert.assertEquals(true, false);
        }catch (UserSystemException ex) {
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.User_IsNotExites.getCode());
        }
    }

    /**
     *
     * 重置密码
     */
    @Test       //重置密码成功
    public void test_resetPassword_case1(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        userService.resetPassword("u123","123");
    }

    @Test       //用户id为空白字符
    public void  test_resetPassword_case2(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.resetPassword("     ","123");
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.User_IsNotExites.getCode());
        }
    }

    @Test       //新密码为空白字符
    public void  test_resetPassword_case3(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.resetPassword("u123",null);
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),UserSystemStateCode.NewPassword_IsEmpty.getCode());
        }
    }

    @Test       //用户不存在
    public void  test_resetPassword_case4(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u123");
        context.put("username", "admin");
        userContextScope.mock(context);
        try {
            userService.resetPassword("u234","123");
            Assert.assertEquals(true, false);
        }catch (UserSystemException ex) {
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.User_IsNotExites.getCode());
        }
    }

    /**
     *
     * 获取用户数量
     */
    @Test       //关键字为"10"，实际数据为12
    public void  test_getListCount_case1(){

            Integer result = userService.getListCount("10");
            System.out.println("查询数据得 "+result);
    }

    @Test       //关键字为""，实际数据为202
    public void  test_getListCount_case2(){

        Integer result = userService.getListCount("");
        System.out.println("查询数据得 "+result);
    }

    @Test       //关键字为"%1%9"，实际数据为21
    public void  test_getListCount_case3(){

        Integer result = userService.getListCount("%1%9");
        System.out.println("查询数据得 "+result);
    }

    @Test       //关键字为"u  %1%9"，实际数据为0
    public void  test_getListCount_case4(){

        Integer result = userService.getListCount("u  %1%9");
        System.out.println("查询数据得 "+result);
    }

    /**
     * @作者 尧
     * @功能 String左对齐
     */
    public static String padRight(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
        for (int i = src.length(); i < len; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }
    /** 
     * @作者 尧
     * @功能 String右对齐
     */
    public static String padLeft(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }
        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
        for (int i = 0; i < diff; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }
}
