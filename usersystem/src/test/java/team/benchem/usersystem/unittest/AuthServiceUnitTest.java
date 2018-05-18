package team.benchem.usersystem.unittest;

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.benchem.framework.service.TokenService;
import team.benchem.framework.test.UserContextScope;
import team.benchem.usersystem.entity.User;
import team.benchem.usersystem.lang.UserSystemException;
import team.benchem.usersystem.lang.UserSystemStateCode;
import team.benchem.usersystem.repository.UserRepository;
import team.benchem.usersystem.service.AuthService;
import team.benchem.usersystem.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthServiceUnitTest {

    @Autowired
    AuthService authService;

    @Autowired
    UserService userService;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserContextScope userContextScope;


    @Before
    public  void beforeTest() {

        User initedTag = userService.findUser("admin124");
        if(initedTag != null){
            return;
        }
        User u = new User();
        u.setRowId("u124");
        u.setUsername("admin124");
        u.setPassword("123");
        u.setNickname("master");
        u.setMobile("123456789");
        u.setEmail("abc@123.com");
        u.setIsAdmin(true);
        u.setIsEnable(true);
        userService.appendUser(u);

        User u1 = new User();
        u1.setRowId("u125");
        u1.setUsername("user0001");
        u1.setPassword("123");
        u1.setNickname("test1");
        u1.setMobile("1234567");
        u1.setEmail("abc@12.com");
        u1.setIsAdmin(true);
        u1.setIsEnable(false);
        userService.appendUser(u1);

    }

    /**
     *
     * 登陆
     */
    @Test       //成功登陆 
    public void test_login_case1(){
        authService.login("admin124","123");
        Assert.assertEquals(true,true);
    }

    @Test        //用户名为空
    public void test_login_case2(){
        try {
            authService.login("     ","123");
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.UserName_IsEmpty.getCode());
        }

    }
    @Test        //密码为空
    public void test_login_case3(){
        try {
            authService.login("admin124","      ");
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.Password_IsEmpty.getCode());
        }
    }
    @Test        //用户不存在
    public void test_login_case4(){
        try {
            authService.login("adman","123");
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.User_IsNotExites.getCode());
        }
    }
    @Test        //密码不正确
    public void test_login_case5(){
        try {
            authService.login("admin124","1234");
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.Password_IsErr.getCode());
        }
    }
    @Test       //用户被禁用
    public void  test_login_case6(){
        try {
            authService.login("user0001","123");
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.User_IsDisabled.getCode());
        }
    }

    /**
     *
     * 注销用户
     */
    @Test
    public void test_logout_case1(){
        String token = authService.login("admin124","123");
        JSONObject tokenObj = tokenService.getToken(token);
        Assert.assertNotNull(tokenObj);
        JSONObject context = new JSONObject();
        context.put("rowid", "u124");
        context.put("username", "admin124");
        context.put("Suf-Token", tokenObj);
        userContextScope.mock(context);
        authService.logout("admin", token);
        JSONObject tokenObj2 = tokenService.getToken(token);
        Assert.assertNull(tokenObj2);
    }
    /**
     *
     * 获取当前用户
     */
    @Test
    public void test_getuser_case1(){
        JSONObject context = new JSONObject();
        context.put("rowid", "u124");
        context.put("username", "admin124");
        userContextScope.mock(context);
        User user=authService.getuser();
        Assert.assertEquals(user.getRowId(),"u124");
    }
}
