package com.lonntec.sufservice.proxy;

import org.springframework.stereotype.Service;
import team.benchem.framework.annotation.MicroServiceMethodProxy;
import team.benchem.framework.lang.MicroServiceException;
import team.benchem.framework.lang.RequestType;
import team.benchem.framework.lang.StateCodeImpl;

@Service
public class CodeRuleService {

    @MicroServiceMethodProxy(microserviceKey = "codeRuleService", path = "/coderule/generate", type = RequestType.POST)
    public String generateCode(String ruleKey){
        throw new MicroServiceException(new StateCodeImpl(-3, ""));
    }
}
