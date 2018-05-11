package team.benchem.framework.sdk;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class MicroServiceMessageProducer {
    public void publish(String queueName, JSONObject message){
        throw new RuntimeException();
    }
}
