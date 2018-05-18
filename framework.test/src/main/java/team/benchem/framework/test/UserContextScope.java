package team.benchem.framework.test;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.benchem.framework.sdk.UserContext;
import team.benchem.framework.service.TokenService;

@Service
public class UserContextScope {

    private String token;

    @Autowired
    TokenService tokenService;

    public void mock(JSONObject userContext){

        if(!userContext.containsKey("Suf-Token")) {
            String token = tokenService.grantToken(userContext);
            this.token = token;
            userContext.put("Suf-Token", token);
        } else {
            this.token = userContext.getString("Suf-Token");
        }
        UserContext.createUserContext(userContext);
    }

    public UserContext getUserContext(){
        return UserContext.getCurrentUserContext();
    }

    public String getCurrnetToken(){
        return token;
    }
}
