package com.lonntec.coderuleservice.service.impl;

import com.lonntec.coderuleservice.service.CodeRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;
import team.benchem.framework.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class CodeRuleServiceImpl implements CodeRuleService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public String generateCode(String codeRuleKey) {
        String redisKey = codeRuleKey.equalsIgnoreCase("domainNumberRule")
                ? getRedisKey(codeRuleKey)
                : getRedisKeyByDate(codeRuleKey);
        RedisAtomicLong atomicLong = new RedisAtomicLong(redisKey, redisTemplate.getConnectionFactory());
        Long increment = atomicLong.getAndIncrement();
        String incrementStr = String.format("%s", increment.intValue()+1);

        if(codeRuleKey.equalsIgnoreCase("domainNumberRule")){
            return String.format("SUF%s", StringUtils.padLeft(incrementStr, 8, '0'));
        }else if(codeRuleKey.equalsIgnoreCase("activeSufFormRule")){
            atomicLong.expireAt(getExpireTime());
            SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
            Calendar calendar = Calendar.getInstance();
            return String.format("AS%s%s",
                    dtFormat.format(calendar.getTime()),
                    StringUtils.padLeft(incrementStr, 3, '0'));
        }else if(codeRuleKey.equalsIgnoreCase("applySufLicenseRule")){
            atomicLong.expireAt(getExpireTime());
            SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
            Calendar calendar = Calendar.getInstance();
            return String.format("AL%s%s",
                    dtFormat.format(calendar.getTime()),
                    StringUtils.padLeft(incrementStr, 3, '0'));
        } else{

        }
        return null;
    }

    private Date getExpireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date now = calendar.getTime();
        return now;
    }

    private String getRedisKey(String codeRuleKey){
        return String.format("CodeRule:%s", codeRuleKey);
    }

    private String getRedisKeyByDate(String codeRuleKey){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
        return String.format("%s:%s", getRedisKey(codeRuleKey), dtFormat.format(calendar.getTime()));
    }
}
