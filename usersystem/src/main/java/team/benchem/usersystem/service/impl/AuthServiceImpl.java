package team.benchem.usersystem.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.benchem.framework.sdk.UserContext;
import team.benchem.framework.service.TokenService;
import team.benchem.usersystem.entity.Channel;
import team.benchem.usersystem.entity.User;
import team.benchem.usersystem.lang.UserSystemException;
import team.benchem.usersystem.lang.UserSystemStateCode;
import team.benchem.usersystem.repository.UserRepository;
import team.benchem.usersystem.service.AuthService;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenService tokenService;

    //用户登录
    @Override
    public String login(String username, String password) {
        //输出日志方式
        logger.debug("call login %s", username);
        //用户名，密码是否为空
        if(username==null||username.replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.UserName_IsEmpty);
        }else if (password==null||password.replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.Password_IsEmpty);
        }
        Optional<User> userOptional= userRepository.findByUsername(username);
        //用户是否存在
        if(!userOptional.isPresent()){
            throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }
        User dbUser = userOptional.get();
        String dbpassword=dbUser.getPasswordHash();
        User user=new User();
        user.setPassword(password);
        //密码是否正确
        if(!user.getPasswordHash().equals(dbpassword)){
            throw new UserSystemException(UserSystemStateCode.Password_IsErr);
        }
        //是否为禁用用户
        if(dbUser.getIsEnable()==false){
            throw new UserSystemException(UserSystemStateCode.User_IsDisabled);
        }
        //最后登录时间
        Calendar calendar = Calendar.getInstance();
        dbUser.setLastLoginTime(calendar.getTime());
        userRepository.save(dbUser);

        JSONObject tokenBody = new JSONObject();
        tokenBody.put("rowid", dbUser.getRowId());
        tokenBody.put("username", dbUser.getUsername());
        tokenBody.put("nickname",dbUser.getNickname());
        String token  =  tokenService.grantToken(tokenBody);
        return token;
    }
    //
    @Override
    public String sayOnline(String username, String token) {
        return null;
    }
    //用户登出
    @Override
    public void logout(String username, String token) {
        UserContext currCtx=UserContext.getCurrentUserContext();
        String tokenusername=currCtx.properties.getString("username");
        if(tokenusername==null){
            throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }
        tokenService.destroyToken(token);

    }

    @Override
    public List<Channel> getUserMenus(String userName) {
        return null;
    }
    //获取当前用户
    @Override
    public User getuser() {
        UserContext currCtx =UserContext.getCurrentUserContext();
        String userRowId = currCtx.properties.getString("rowid");
        Optional<User> userOptional= userRepository.findById(userRowId);
        //用户是否存在
        if (!userOptional.isPresent()){
            throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }
        return userOptional.get();
    }
}
