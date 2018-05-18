package com.lonntec.coderuleservice;


import com.lonntec.coderuleservice.service.CodeRuleService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CodeRuleServiceTest {

    @Autowired
    CodeRuleService codeRuleService;

    @Test
    public void test_codeRule_case1(){
        String domainNum = codeRuleService.generateCode("domainNumberRule");
        System.out.println(domainNum);
        Assert.assertEquals(domainNum.length(), 11);
    }

    @Test
    public void test_codeRule_case2(){
        String billNum = codeRuleService.generateCode("activeSufFormRule");
        System.out.println(billNum);
        Assert.assertEquals(billNum.length(), 13);
    }

    @Test
    public void test_codeRule_case3(){
        String billNum = codeRuleService.generateCode("applySufLicenseRule");
        System.out.println(billNum);
        Assert.assertEquals(billNum.length(), 13);
    }
}
