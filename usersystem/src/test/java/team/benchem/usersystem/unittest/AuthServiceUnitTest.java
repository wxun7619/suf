package team.benchem.usersystem.unittest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
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
    UserRepository userRepository;

    @Before
    public  void beforeTest() {

        User dbUser = userService.findUser("admin");
        User u = dbUser == null ? new User() : dbUser;
        u.setRowId("u124");
        u.setUsername("admin");
        u.setPassword("123");
        u.setNickname("dandy");
        u.setMobile("123456789");
        u.setEmail("abc@123.com");
        u.setIsAdmin(false);
        u.setIsEnable(false);
        userService.appendUser(u);
    }

    /**
     *
     * 登陆
     */
    @Test       //成功登陆 
    public void test_login_case1(){
        authService.login("admin","123");
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
            authService.login("admin","      ");
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
            authService.login("admin","1234");
            Assert.assertEquals(true,false);
        }catch (UserSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), UserSystemStateCode.Password_IsErr.getCode());
        }
    }


}
