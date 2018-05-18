package com.lonntec.coderuleservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.lonntec.coderuleservice.service.CodeRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/coderule")
@RestController
public class CodeRuleController {

    @Autowired
    CodeRuleService codeRuleService;

    @PostMapping("/generate")
    public String generateCode(@RequestBody JSONObject postForm){
        String ruleKey = postForm.getString("ruleKey");
        return codeRuleService.generateCode(ruleKey);
    }

}
