package team.benchem.framework.utils;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import java.util.HashMap;

public class HttpGetHystrixCommand extends HystrixCommand<String> {

    private final String host;
    private final String path;
    private HashMap<String, String> headers;
    private final HashMap<String, Object> pararms;

    public HttpGetHystrixCommand(String host, String path, HashMap<String, String> headers, HashMap<String, Object> pararms){
        super(HystrixCommandGroupKey.Factory.asKey("MicroServicesRemoteInvoke")
                , 30000);
        this.host = host;
        this.path = path;
        this.headers = headers;
        this.pararms = pararms;
    }

    @Override
    protected String run() throws Exception {
        return HttpInvokeHelper.get(host, path, headers, pararms);
    }

    @Override
    protected String getFallback() {
        JSONObject json = new JSONObject();
        json.put("statecode", -3);
        json.put("msg", "服务中心应答异常");
        return  json.toJSONString();
    }
}