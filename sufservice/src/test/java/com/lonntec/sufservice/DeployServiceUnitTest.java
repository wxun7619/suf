package com.lonntec.sufservice;

import com.lonntec.sufservice.entity.ApplyForm;
import com.lonntec.sufservice.entity.Domain;
import com.lonntec.sufservice.entity.DomainUser;
import com.lonntec.sufservice.entity.User;
import com.lonntec.sufservice.lang.DeploySystemException;
import com.lonntec.sufservice.lang.DeploySystemStateCode;
import com.lonntec.sufservice.repository.DeployRepository;
import com.lonntec.sufservice.repository.DomainRepository;
import com.lonntec.sufservice.repository.DomainUserRepository;
import com.lonntec.sufservice.repository.UserRepository;
import com.lonntec.sufservice.service.DeployService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DeployServiceUnitTest {

    @Autowired
    DeployService deployService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeployRepository deployRepository;

    @Autowired
    DomainRepository domainRepository;

    @Autowired
    DomainUserRepository domainUserRepository;

    @Before
    public void beforeTest(){
        //添加用户
        User user=new User();
        user.setRowId("abc");
        user.setUsername("hello");
        user.setNickname("hi");
        Optional<User> userOptional=userRepository.findById("abc");
        if(!userOptional.isPresent()){
            userRepository.save(user);
        }
        //添加企业域
        Domain domain=new Domain();
        domain.setRowId("123");
        domain.setDomainName("lonntec");
        domain.setDomainShortName("lt");
        domain.setAddress("江门市蓬江区");
        domain.setLinkMan("wxun");
        domain.setLinkManMobile("120");
        domain.setBusinessLicense("cptbtptp");
        domain.setMemo("Welcome !");
        domain.setUser(user);
        domainRepository.save(domain);
        //添加企业管理员
        DomainUser domainUser=new DomainUser();
        domainUser.setRowId("789");
        domainUser.setUserName("domainUser001");
        domainUser.setMobile("domainUser120");
        domainUser.setEmail("domain@123");
        domainUserRepository.save(domainUser);
    }
    @Test       //递交开通申请
    public void test_apply_case1(){
        ApplyForm newForm = deployService.apply("123","nihao","domainUser001","domainUser120","domain@123");
        String formId = newForm.getRowId();
        Optional<ApplyForm> dbForm = deployRepository.findById(formId);
        Assert.assertEquals(dbForm.get().getDomain().getRowId(),"123");
    }
    
    @Test       //判断domainId是否为空
    public void test_apply_case2(){
        try {
            deployService.apply("","nihao","domainUser001","domainUser120","domain@123");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.DomainId_IsEmpty.getCode());
        }
    }
    @Test       //判断domainUserName是否为空
    public void test_apply_case3(){
        try {
            deployService.apply("123","nihao","","domainUser120","domain@123");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.DomainUserName_IsEmpty.getCode());
        }
    }
    @Test       //判断domainUserMobile是否为空
    public void test_apply_case4(){
        try {
            deployService.apply("123","nihao","domainUser001","","domain@123");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.DomainUserMobile_IsEmpty.getCode());
        }
    }
    @Test       //判断domainUserEmail是否为空
    public void test_apply_case5(){
        try {
            deployService.apply("123","nihao","domainUser120","domainUser120","");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.DomainUserEmail_IsEmpty.getCode());
        }
    }
    @Test       //判断企业域是否存在
    public void test_apply_case6(){
        try {
            deployService.apply("124","nihao","domainUser120","domainUser120","domain@123");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.Domain_IsNotExist.getCode());
        }
    }


    @Test       //表单不存在
    public void test_auditApply_case1(){
        try {
            deployService.auditapply("1234",true,"nice");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DeploySystemStateCode.ApplyForm_IsExist.getCode());
        }
    }
    @Test       //审核开通申请
    public void test_auditApply_case2(){
        deployService.auditapply("123",true,"nice");
        Optional<ApplyForm> applyFormOptional=deployRepository.findById("123");
        Assert.assertEquals(applyFormOptional.get().getRowId(),"123");
    }
}
