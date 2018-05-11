package team.benchem.framework.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import team.benchem.framework.service.TokenService;

import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public String grantToken(JSONObject body) {
        String tokenStr = UUID.randomUUID().toString();
        String redisKey=getRedisKey(tokenStr);
        redisTemplate.opsForValue().set(redisKey, body.toJSONString());
        return tokenStr;
    }

    @Override
    public void destroyToken(String token) {
        String redisKey = getRedisKey(token);
        redisTemplate.delete(redisKey);
    }

    @Override
    public JSONObject getToken(String token) {
        String redisKey = getRedisKey(token);
        if(!redisKey.contains(redisKey)){
            //todo: throw
        }
        String bodyStr = redisTemplate.opsForValue().get(redisKey);
        JSONObject reValue = JSONObject.parseObject(bodyStr);
        return reValue;
    }

    private String getRedisKey(String tokenKey){
        return String.format("token:%s", tokenKey);
    }
}
