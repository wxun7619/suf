package team.benchem.framework.test;

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.benchem.framework.sdk.UserContext;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserContextScopeTest {

    @Autowired
    UserContextScope userContextScope;

    @Test
    public void test_userContextScope_case1(){
        JSONObject userContext = new JSONObject();
        userContext.put("rowid", UUID.randomUUID().toString());
        userContext.put("username", "admin");
        userContextScope.mock(userContext);
        Assert.assertNotEquals(userContextScope.getCurrnetToken(), "");
        UserContext currContext = UserContext.getCurrentUserContext();
        Assert.assertNotEquals(currContext.properties.getString("rowid"), "");
    }

}
