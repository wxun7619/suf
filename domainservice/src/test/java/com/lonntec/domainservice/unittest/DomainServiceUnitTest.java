package com.lonntec.domainservice.unittest;


import com.lonntec.domainservice.entity.Domain;
import com.lonntec.domainservice.entity.User;
import com.lonntec.domainservice.lang.DomainSystemException;
import com.lonntec.domainservice.lang.DomainSystemStateCode;
import com.lonntec.domainservice.repository.DomainRepository;
import com.lonntec.domainservice.repository.UserRepository;
import com.lonntec.domainservice.service.DomainService;
import com.sun.java.browser.plugin2.DOM;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DomainServiceUnitTest {

    @Autowired
    DomainRepository domainRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DomainService domainService;

    @Before
    public void beforeTest(){
        User user = new User();
        user.setRowId("111");
        user.setUserName("111");
        user.setEmail("111");
        userRepository.save(user);

        User user2 = new User();
        user2.setRowId("222");
        user2.setUserName("222");
        user2.setEmail("222");
        userRepository.save(user2);

        Domain dm = new Domain();
        dm.setRowId("123");
        dm.setDomainName("u123");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        dm.setIsActiveSuf(true);
        dm.setMemo("aaa");
        //dm.setOwnerUser(user);
        //dm设置owneruserid

        domainService.appendDomain(dm);
        for(int i=0; i<201; i++){
            Domain domain = new Domain();
            String indexStr = String.format("%s", i);
            domain.setDomainName(String.format("domain%s", padLeft(indexStr, 3, '0')));
            System.out.println(domain.getDomainName());
            domain.setDomainShortName(String.format("domain%s", padLeft(indexStr, 3, '0')));
            domain.setAddress(String.format("domain%s", padLeft(indexStr, 3, '0')));
            domain.setLinkMan(String.format("domain%s", padLeft(indexStr, 3, '0')));
            domain.setLinkManMobile(String.format("137%s", padLeft(indexStr, 8, '0')));
            System.out.println(domain.getLinkManMobile());
            domain.setBusinessLicense(String.format("137%s@139.com", padLeft(indexStr, 8, '0')));
            domain.setMemo(indexStr);
            domain.setOwnerUser(user2);

            domainService.appendDomain(domain);
        }
    }

    /**
     *
     * 创建企业域
     */
    @Test        //成功创建企业域
    public void test_appendDomain_case1(){
        Domain dm = new Domain();
        dm.setDomainName("u123");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        dm.setMemo("aaa");
        Domain uu=domainService.appendDomain(dm);
        System.out.println("11111"+uu.toString());
        //Assert.assertEquals(uu.getRowId(),"2018001");
        //Assert.assertEquals(uu.getPasswordHash(),null);

    }

    @Test        //用户不存在
    public void test_appendDomain_case2(){
        String ownerId = "333";
        Domain dm = new Domain();
        dm.setDomainName("u123");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        dm.setMemo("aaa");
        Optional<User> userOptional = userRepository.findById(ownerId);
        /*if(!userOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.OwnerUserId_IsNotExist);
        }*/
        dm.setOwnerUser(userOptional.get());
        Domain uu=domainService.appendDomain(dm);
        System.out.println("11111"+uu.toString());
        //Assert.assertEquals(uu.getRowId(),"2018001");
        //Assert.assertEquals(uu.getPasswordHash(),null);

    }

    @Test //查询已开通SUF用户
    public void test_findActiveSufListDomains_case1(){
        List<Domain> domainList= domainService.findActiveSufListDomains("", 1, 25);
        System.out.println(domainList.size());
    }

    @Test //查询未开通SUF用户
    public void test_findNotActiveSufListDomains_case1(){
        List<Domain> domainList= domainService.findNotActiveSufListDomains("", 1, 202);
        System.out.println(domainList.size());
    }

    @Test //查询企业域列表
    public void test_findUserList_case1(){
        List<Domain> domainList = domainService.findDomains("", 1, 202);
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

    @Test //修改企业域
    public void test_modify_case1(){
        Domain dm = new Domain();
        dm.setRowId("123");
        dm.setDomainName("987");
        dm.setDomainShortName("a123");
        dm.setAddress("123");
        dm.setLinkMan("q123");
        dm.setLinkManMobile("123456");
        dm.setBusinessLicense("b123");
        Domain domain = domainService.modifyDomain(dm);
        System.out.println(domain.getDomainName()+"   "+domain.getLinkManMobile());
    }

    @Test //企业域实施人员变更
    public void test_changeUser_case1(){


        Domain domain = domainService.changeUser("123","222");
        System.out.println(domain.getOwnerUser().toString());
    }

    @Test //启用禁用企业域
    public void test_setEnable_case1(){
        String rowId = "123";
        Boolean isEnable = false;
        domainService.setEnable(rowId,isEnable);
        Optional<Domain> optional=domainRepository.findById("123");
        Assert.assertEquals(optional.get().getIsEnable(),false);
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
