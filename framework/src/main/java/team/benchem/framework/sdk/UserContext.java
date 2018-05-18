package team.benchem.framework.sdk;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UserContext {

    static Logger logger = LoggerFactory.getLogger(UserContext.class);

    static final HashMap<Long, UserContext> globalUserContexts = new HashMap<>();

    static void appendUserContext(Long threadId, UserContext context){
        globalUserContexts.put(threadId, context);
    }

    static void removeUserContext(Long threadId){
        if(globalUserContexts.containsKey(threadId)){
            globalUserContexts.remove(threadId);
        }
    }

    public static UserContext createUserContext(JSONObject context){
        Thread currThread = Thread.currentThread();
        Long threadId = currThread.getId();

        UserContext userContextcontext = new UserContext();
        userContextcontext.properties.put("threadId", threadId);
        for (Map.Entry<String, Object> item : context.entrySet()){
            userContextcontext.properties.put(item.getKey(), item.getValue());
        }
        appendUserContext(threadId, userContextcontext);
        return userContextcontext;
    }

    static UserContext createUserContext(){
        Thread currThread = Thread.currentThread();
        Long threadId = currThread.getId();

        UserContext context = new UserContext();
        context.properties.put("threadId", threadId);
        appendUserContext(threadId, context);
        return context;
    }

    public static void removeCurrentUserContext(){
        Thread currThread = Thread.currentThread();
        Long threadId = currThread.getId();
        removeUserContext(threadId);
    }

    public static UserContext getCurrentUserContext(){
        Thread currThread = Thread.currentThread();
        Long threadId = currThread.getId();
        logger.debug(String.format("currThreadId:%s", threadId));
        if(globalUserContexts.containsKey(threadId)){
            return globalUserContexts.get(threadId);
        }
        return null;
    }

    public JSONObject properties = new JSONObject();
}
