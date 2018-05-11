package team.benchem.framework.service;

import com.alibaba.fastjson.JSONObject;

public interface TokenService {

    String grantToken(JSONObject body);

    void destroyToken(String token);

    JSONObject getToken(String token);

}
