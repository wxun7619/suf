package com.lonntec.domainservice.unittest;


import com.alibaba.fastjson.JSONObject;
import com.lonntec.domainservice.entity.Domain;
import com.lonntec.domainservice.entity.User;
import com.lonntec.domainservice.lang.DomainSystemException;
import com.lonntec.domainservice.lang.DomainSystemStateCode;
import com.lonntec.domainservice.repository.DomainRepository;
import com.lonntec.domainservice.repository.UserRepository;
import com.lonntec.domainservice.service.DomainService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.benchem.framework.test.UserContextScope;
import team.benchem.framework.utils.StringUtils;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DomainServiceUnitTest {

    Logger logger = LoggerFactory.getLogger(DomainServiceUnitTest.class);

    @Autowired
    UserContextScope userContextScope;

    @Autowired
    DomainRepository domainRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DomainService domainService;

    @Before
    public void beforeTest(){



        Optional<User> adminOptional = userRepository.findById("admin000");
        if(adminOptional.isPresent()) {
            return ;
        }

        User adminUser = new User();
        adminUser.setRowId("admin000");
        adminUser.setUserName("admin");
        adminUser.setPassword("123");
        adminUser.setEmail("111");
        adminUser.setIsAdmin(true);
        userRepository.save(adminUser);

        //userContextScope.login("admin", "123");
        //userContextScope.getUserContext().properties.put("rowid", adminUser.getRowId());

        User user = new User();
        user.setRowId("1111");
        user.setUserName("user1111");
        user.setPassword("123");
        user.setEmail("111");
        user.setIsAdmin(false);
        userRepository.save(user);

        User user2 = new User();
        user2.setRowId("2222");
        user2.setUserName("user2222");
        user2.setPassword("123");
        user2.setEmail("222");
        user2.setIsAdmin(false);
        userRepository.save(user2);


        JSONObject context = new JSONObject();
        context.put("rowid", "admin000");
        context.put("username", "admin");
        userContextScope.mock(context);

        Domain dm = new Domain();
        dm.setRowId("dm1");
        dm.setDomainName("dm1");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        dm.setIsActiveSuf(true);
        dm.setIsEnable(true);
        dm.setMemo("aaa");
        domainService.appendDomain(dm,"1111");

        Domain dm2 = new Domain();
        dm2.setRowId("dm2");
        dm2.setDomainName("dm2");
        dm2.setDomainShortName("a123");
        dm2.setAddress("123");
        dm2.setLinkMan("q123");
        dm2.setLinkManMobile("123456");
        dm2.setBusinessLicense("b123");
        dm2.setIsActiveSuf(true);
        dm2.setIsEnable(false);
        dm2.setMemo("aaa");
        domainService.appendDomain(dm2,"2222");

        for(int i=0; i<20; i++){
            Domain domain = new Domain();
            String indexStr = String.format("%s", i);
            domain.setDomainName(String.format("domain%s", StringUtils.padLeft(indexStr, 3, '0')));
            domain.setDomainShortName(String.format("domain%s", StringUtils.padLeft(indexStr, 3, '0')));
            domain.setAddress(String.format("domain%s", StringUtils.padLeft(indexStr, 3, '0')));
            domain.setLinkMan(String.format("domain%s",StringUtils.padLeft(indexStr, 3, '0')));
            domain.setLinkManMobile(String.format("137%s", StringUtils.padLeft(indexStr, 8, '0')));
            domain.setBusinessLicense(String.format("137%s@139.com", StringUtils.padLeft(indexStr, 8, '0')));
            domain.setMemo(indexStr);
            //domain.setOwnerUser(user2);
            domain.setIsActiveSuf(false);
            domain.setIsEnable(false);

            domainService.appendDomain(domain,"2222");
        }
    }

    /**
     *
     * 创建企业域
     */
    @Test        //成功创建企业域
    public void test_appendDomain_case1(){
        Domain dm = new Domain();
        dm.setDomainName("u123456");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        dm.setMemo("aaa");
        Domain uu=domainService.appendDomain(dm,"1111");
        System.out.println("11111"+uu.toString());
        //Assert.assertEquals(uu.getRowId(),"2018001");
        //Assert.assertEquals(uu.getPasswordHash(),null);

    }

    @Test        //实施人员不存在
    public void test_appendDomain_case2(){
        Domain dm = new Domain();
        dm.setDomainName("u12301");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        dm.setMemo("aaa");
        try {
            domainService.appendDomain(dm,"555");
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DomainSystemStateCode.OwnerUserId_IsEmpty.getCode());
        }

    }

    @Test        //实施人员ID为空
    public void test_appendDomain_case3(){
        Domain dm = new Domain();
        dm.setDomainName("u12302");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        dm.setMemo("aaa");
        try {
            domainService.appendDomain(dm,"");
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.OwnerUserId_IsEmpty.getCode());
        }
    }

    @Test        //企业名称为空
    public void test_appendDomain_case4(){
        Domain dm = new Domain();
        dm.setDomainName("");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        dm.setMemo("aaa");
        try {
            domainService.appendDomain(dm,"1111");
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.DomainName_IsEmpty.getCode());
        }
    }

    @Test        //企业简称为空
    public void test_appendDomain_case5(){
        Domain dm = new Domain();
        dm.setDomainName("u12303");
        dm.setDomainShortName("");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        dm.setMemo("aaa");
        try {
            domainService.appendDomain(dm,"1111");
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.DomainShortName_IsEmpty.getCode());
        }
    }

    @Test        //	联系地址为空
    public void test_appendDomain_case6(){
        Domain dm = new Domain();
        dm.setDomainName("u12304");
        dm.setDomainShortName("a123");
        dm.setAddress("");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        dm.setMemo("aaa");
        try {
            domainService.appendDomain(dm,"1111");
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.Address_IsEmpty.getCode());
        }
    }

    @Test        //联系人为空
    public void test_appendDomain_case7(){
        Domain dm = new Domain();
        dm.setDomainName("u12305");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        dm.setMemo("aaa");
        try {
            domainService.appendDomain(dm,"1111");
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.LinkMan_IsEmpty.getCode());
        }
    }

    @Test        //联系人电话为空
    public void test_appendDomain_case8(){
        Domain dm = new Domain();
        dm.setDomainName("u12306");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("");
        dm.setBusinessLicense("b123");
        dm.setMemo("aaa");
        try {
            domainService.appendDomain(dm,"1111");
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.LinkManMobile_IsEmpty.getCode());
        }
    }

    @Test        //企业名已存在
    public void test_appendDomain_case9(){
        Domain dm = new Domain();
        dm.setDomainName("dm1");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("q123");
        dm.setBusinessLicense("b123");
        dm.setMemo("aaa");
        try {
            domainService.appendDomain(dm,"1111");
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.DomainName_IsExist.getCode());
        }
    }

    @Test //查询已开通SUF用户
    public void test_findActiveSufListDomains_case1(){

        List<Domain> domainList= domainService.findActiveSufListDomains("", 1, 25,true);
        System.out.println(domainList.size());
    }

    @Test //非管理员查询已开通SUF用户
    public void test_findActiveSufListDomains_case2(){

        JSONObject context = new JSONObject();
        context.put("rowid", "2222");
        context.put("username", "user2222");
        userContextScope.mock(context);

        List<Domain> domainList= domainService.findActiveSufListDomains("", 1, 25,true);
        System.out.println(domainList.size());
    }

    @Test //查询未开通SUF用户
    public void test_findNotActiveSufListDomains_case1(){
        List<Domain> domainList= domainService.findActiveSufListDomains("",1,25,false);
        System.out.println(domainList.size());
    }

    @Test //非管理员查询未开通SUF用户
    public void test_findNotActiveSufListDomains_case2(){
        JSONObject context = new JSONObject();
        context.put("rowid", "2222");
        context.put("username", "user2222");
        userContextScope.mock(context);

        List<Domain> domainList= domainService.findActiveSufListDomains("",1,25,false);
        System.out.println(domainList.size());
    }

    @Test //查询企业域列表
    public void test_findUserList_case1(){
        List<Domain> domainList = domainService.findDomains("", 1, 5);
        System.out.println(domainList.size());
    }

    @Test //查询企业域列表
    public void test_findUserList_case2(){
        List<Domain> domainList = domainService.findDomains("", 1, 30);
        System.out.println(domainList.size());
    }

    @Test //查询企业域列表
    public void test_findUserList_case3(){
        List<Domain> domainList = domainService.findDomains("19", 1, 30);
        for(Domain domain:domainList){
            System.out.println(domain.getDomainName());
        }
        System.out.println(domainList.size());

    }

    @Test //获取企业域数量
    public void test_getListCount_case1(){

        Integer integer = domainService.getListCount("");
        System.out.println(integer);
    }

    @Test //获取企业域数量
    public void test_getListCount_case2(){

        Integer integer = domainService.getListCount("19");
        System.out.println(integer);
    }

    @Test //成功修改企业域
    public void test_modify_case1(){
        Domain dm = new Domain();
        dm.setRowId("dm1");
        dm.setDomainName("987");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        Domain domain = domainService.modifyDomain(dm);
        System.out.println(domain.getDomainName()+"   "+domain.getLinkManMobile());
    }

    @Test //成功修改企业域(企业名不改变)
    public void test_modify_case2(){
        Domain dm = new Domain();
        dm.setRowId("dm1");
        dm.setDomainName("dm1");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        Domain domain = domainService.modifyDomain(dm);
        System.out.println(domain.getDomainName()+"   "+domain.getLinkManMobile());
    }

    @Test //企业域不存在
    public void test_modify_case3(){
        Domain dm = new Domain();
        dm.setRowId("qqqq");
        dm.setDomainName("8888");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        try {
            domainService.modifyDomain(dm);
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DomainSystemStateCode.Domain_IsNotExist.getCode());
        }
    }

    @Test //企业名已存在
    public void test_modify_case4(){
        Domain dm = new Domain();
        dm.setRowId("dm1");
        dm.setDomainName("domain000");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        try {
            domainService.modifyDomain(dm);
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.DomainName_IsExist.getCode());
        }
    }

    @Test //企业域ID为空
    public void test_modify_case5(){
        Domain dm = new Domain();
        dm.setRowId("");
        dm.setDomainName("8888");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        try {
            domainService.modifyDomain(dm);
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.Domain_IsNotExist.getCode());
        }
    }

    @Test //企业域名字为空
    public void test_modify_case6(){
        Domain dm = new Domain();
        dm.setRowId("dm1");
        dm.setDomainName("");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        try {
            domainService.modifyDomain(dm);
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.DomainName_IsEmpty.getCode());
        }
    }

    @Test //企业域简称为空
    public void test_modify_case7(){
        Domain dm = new Domain();
        dm.setRowId("dm1");
        dm.setDomainName("8888");
        dm.setDomainShortName("");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        try {
            domainService.modifyDomain(dm);
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.DomainShortName_IsEmpty.getCode());
        }
    }

    @Test //企业地址为空
    public void test_modify_case8(){
        Domain dm = new Domain();
        dm.setRowId("dm1");
        dm.setDomainName("8888");
        dm.setDomainShortName("a123");
        dm.setAddress("");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        try {
            domainService.modifyDomain(dm);
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.Address_IsEmpty.getCode());
        }
    }

    @Test //企业联系人为空
    public void test_modify_case9(){
        Domain dm = new Domain();
        dm.setRowId("dm1");
        dm.setDomainName("8888");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        try {
            domainService.modifyDomain(dm);
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.LinkMan_IsEmpty.getCode());
        }
    }

    @Test //企业联系人手机号为空
    public void test_modify_case10(){
        Domain dm = new Domain();
        dm.setRowId("dm1");
        dm.setDomainName("8888");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("");
        dm.setBusinessLicense("b123");
        try {
            domainService.modifyDomain(dm);
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.LinkManMobile_IsEmpty.getCode());
        }
    }

    @Test //企业域实施人员变更
    public void test_changeUser_case1(){

        JSONObject context = new JSONObject();
        context.put("rowid", "admin000");
        context.put("username", "admin");
        userContextScope.mock(context);
        Domain domain = domainService.changeUser("dm1","2222");
        System.out.println(domain.getOwnerUser().toString());
    }

    @Test //修改后的实施人员不存在
    public void test_changeUser_case2(){
        try {
            domainService.changeUser("dm1","555");
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.NewOwnerUserId_IsEmpty.getCode());
        }
    }

    @Test //修改后的实施人员不存在（实施人员id为空）
    public void test_changeUser_case3(){
        try {
            domainService.changeUser("dm1","");
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.NewOwnerUserId_IsEmpty.getCode());
        }
    }

    @Test //企业域不存在
    public void test_changeUser_case4(){
        try {
            domainService.changeUser("dm111111","2222");
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.Domain_IsNotExist.getCode());
        }
    }

    @Test //企业域不存在(企业域id为空)
    public void test_changeUser_case5(){
        try {
            domainService.changeUser("","2222");
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.Domain_IsNotExist.getCode());
        }
    }

    @Test //普通用户没有权利修改实施人员
    public void test_changeUser_case6(){
        JSONObject context = new JSONObject();
        context.put("rowid", "1111");
        context.put("username", "user1111");
        userContextScope.mock(context);

        try {
            domainService.changeUser("dm1","2222");
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.No_Permissions.getCode());
        }
    }

    @Test  //管理员禁用 user1 下得一家企业域,期望是能正常被禁用
    public void test_setEnable_case1(){
        JSONObject context = new JSONObject();
        context.put("rowid", "admin000");
        context.put("username", "admin");
        userContextScope.mock(context);

        String rowId="dm1";
        Boolean isEnable = false;
        domainService.setEnable(rowId,isEnable);
        Optional<Domain> optional=domainRepository.findById("dm1");
        Assert.assertEquals(optional.get().getIsEnable(),false);

    }

    @Test //管理员启用 user1 下得一家企业域,期望是能正常被启用
    public void test_setEnable_case2(){
        JSONObject context = new JSONObject();
        context.put("rowid", "admin000");
        context.put("username", "admin");
        userContextScope.mock(context);

        String rowId="dm1";
        Boolean isEnable = true;
        domainService.setEnable(rowId,isEnable);
        Optional<Domain> optional=domainRepository.findById("dm1");
        Assert.assertEquals(optional.get().getIsEnable(),true);

    }

    @Test //启用 user2 下的一家企业域,期望抛异常“无权操作非自己所属企业域”
    public void test_setEnable_case3(){
        JSONObject context = new JSONObject();
        context.put("rowid", "1111");
        context.put("username", "user1111");
        userContextScope.mock(context);

        String rowId="dm2";
        Boolean isEnable = true;
        try {
            domainService.setEnable(rowId,isEnable);
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.No_Permissions.getCode());
        }

    }

    @Test //禁用 user2 下的一家企业域,期望抛异常“无权操作非自己所属企业域”
    public void test_setEnable_case4(){
        JSONObject context = new JSONObject();
        context.put("rowid", "1111");
        context.put("username", "user1111");
        userContextScope.mock(context);

        String rowId="dm2";
        Boolean isEnable = false;
        try {
            domainService.setEnable(rowId,isEnable);
            Assert.assertEquals(true,false);
        }catch (DomainSystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(),DomainSystemStateCode.No_Permissions.getCode());
        }
    }

    @Test //启用 user1 下的一家企业域,期望是能正常被启用
    public void test_setEnable_case5(){
        JSONObject context = new JSONObject();
        context.put("rowid", "1111");
        context.put("username", "user1111");
        userContextScope.mock(context);

        String rowId="dm1";
        Boolean isEnable = true;
        domainService.setEnable(rowId,isEnable);
        Optional<Domain> optional=domainRepository.findById("dm1");
        Assert.assertEquals(optional.get().getIsEnable(),true);
    }

    @Test //禁用 user1 下的一家企业域,期望是能正常被禁用
    public void test_setEnable_case6(){
        JSONObject context = new JSONObject();
        context.put("rowid", "1111");
        context.put("username", "user1111");
        userContextScope.mock(context);

        String rowId="dm1";
        Boolean isEnable = false;
        domainService.setEnable(rowId,isEnable);
        Optional<Domain> optional=domainRepository.findById("dm1");
        Assert.assertEquals(optional.get().getIsEnable(),false);
    }
}
