package team.benchem.usersystem.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import team.benchem.framework.lang.MicroServiceException;
import team.benchem.framework.sdk.UserContext;
import team.benchem.usersystem.entity.User;
import team.benchem.usersystem.lang.UserSystemException;
import team.benchem.usersystem.lang.UserSystemStateCode;
import team.benchem.usersystem.repository.UserRepository;
import team.benchem.usersystem.service.UserService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional(rollbackOn = {MicroServiceException.class, RuntimeException.class})
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    //根据关键字查询用户分页列表
    @Override
    public  List<User> findUsers(String keyword, Integer page, Integer size){
        Integer queryPage = page==null || page <= 0 ? 1 : page;
        Integer querySize = size==null || size <= 0 ? 25 : size;
        PageRequest pageable = new PageRequest(queryPage -1, querySize);
        String queryKeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
        if(!queryKeywork.contains("%")){
            queryKeywork = "%" + queryKeywork + "%";
        }
        //不能返回密码 Todo
        return userRepository.findAllByUsernameLikeOrEmailLikeOrMobileLikeOrderByUsername(queryKeywork, queryKeywork,queryKeywork,pageable);
    }
    //添加用户
    @Override
    public User appendUser(User user) {
        //判断用户名，密码，是否管理员，是否启用是否为空
       if(user.getUsername()==null||user.getUsername().replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.UserName_IsEmpty);
        }
        if(user.getNickname()==null||user.getNickname().replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.NickName_IsEmpty);
        }
        if(user.getPasswordHash()==null||user.getPasswordHash().replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.Password_IsEmpty);
        }
        if(user.getMobile()==null||user.getMobile().replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.Mobile_IsEmpty);
        }
        if(user.getEmail()==null||user.getEmail().replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.Email_IsEmpty);
        }
        if(user.getIsAdmin()==null){
            user.setIsAdmin(false);
        }
        if(user.getIsEnable()==null){
            user.setIsEnable(false);
        }
        //判断用户名，邮箱，手机号码是否已注册
        Optional<User> chk1=userRepository.findByUsername(user.getUsername());
        Optional<User> chk2=userRepository.findByMobile(user.getMobile());
        Optional<User> chk3=userRepository.findByEmail(user.getEmail());
        if(chk1.isPresent()){
            throw new UserSystemException(UserSystemStateCode.UserName_IsExites);
        }
        if(chk2.isPresent()){
            throw new UserSystemException(UserSystemStateCode.Mobile_IsExites);
        }
        if(chk3.isPresent()){
            throw  new UserSystemException(UserSystemStateCode.Email_IsExites);
        }
        //返回值
        User dbrtn_user=userRepository.save(user);
        User rtn_user=new User();
        rtn_user.setRowId(dbrtn_user.getRowId());
        rtn_user.setUsername(dbrtn_user.getUsername());
        rtn_user.setNickname(dbrtn_user.getNickname());
        rtn_user.setMobile(dbrtn_user.getMobile());
        rtn_user.setEmail(dbrtn_user.getEmail());
        rtn_user.setIsAdmin(dbrtn_user.getIsAdmin());
        rtn_user.setIsEnable(dbrtn_user.getIsEnable());
        return  rtn_user;
    }
    //编辑用户
    @Override
    public User modifyUser(User user) {
        //判断电话号码，邮箱,用户id是否为空
       if(user.getMobile()==null||user.getMobile().replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.Mobile_IsEmpty);
        }else if(user.getEmail()==null||user.getEmail().replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.Email_IsEmpty);
        }else if(user.getRowId()==null||user.getRowId().replaceAll("\\s*","").equals("")){
           throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }else if(user.getNickname()==null||user.getNickname().replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.NickName_IsEmpty);
        }
        //判断用户是否存在
        Optional<User>  userOptional = userRepository.findById(user.getRowId());
        if(!userOptional.isPresent()){
            throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }
        //判断号码是否占用
        Optional<User>  mobileOptional = userRepository.findByMobile(user.getMobile());

        if(mobileOptional.isPresent() && !(mobileOptional.get().getRowId().equals(user.getRowId()))){
            throw new UserSystemException(UserSystemStateCode.Mobile_IsExites);
        }
        //判断邮箱是否占用
        Optional<User>  emailOptional = userRepository.findByEmail(user.getEmail());
        if(emailOptional.isPresent() && !(emailOptional.get().getRowId().equals(user.getRowId()))){
            throw new UserSystemException(UserSystemStateCode.Email_IsExites);
        }
        User dbUser = userOptional.get();
        dbUser.setMobile(user.getMobile());
        dbUser.setEmail(user.getEmail());
        dbUser.setNickname(user.getNickname());
        //密码 最后登录时间 Todo
        return userRepository.save(dbUser);
    }
    //删除用户
    @Override
    public void deleteUser(String rowId) {
        if(rowId==null||rowId.replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }
        Optional<User> optional=userRepository.findById(rowId);
        if(!optional.isPresent()){
            throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }
        User user = optional.get();
        if(user.getIsAdmin()==true){
            throw new UserSystemException(UserSystemStateCode.User_IsAdminNotDelete);
        }
        if(user.getIsEnable()==true){
            throw new UserSystemException(UserSystemStateCode.User_IsAvail);
        }
        userRepository.deleteById(rowId);
    }

    @Override
    public User findUser(String userName) {
        Optional<User> userOptiona = userRepository.findByUsername(userName);
        if(userOptiona.isPresent()) {
            return userOptiona.get();
        }
        return null;
    }

    //提升/降级管理员标识
    @Override
    public void setAdmin(String rowId, Boolean isAdmin) {
        if(rowId==null||rowId.replaceAll("\\s","").equals("")){
            throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }else if(isAdmin==null){
            throw new UserSystemException(UserSystemStateCode.IsAdmin_IsEmpty);
        }
        //获取当前登陆用户
        UserContext currCtx = UserContext.getCurrentUserContext();
        String currUserId = currCtx.properties.getString("rowid");
        Optional<User> currUserOptional = userRepository.findById(currUserId);

        //获取目标用户
        Optional<User> optional= userRepository.findById(rowId);
        if(!optional.isPresent()){
            throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }
        //判断当前用户名是否admin，目标用户是否自己
        if(currUserOptional.get().getIsAdmin()==false){
            throw new UserSystemException(UserSystemStateCode.No_Permission);
        }else if(!currUserOptional.get().getUsername().equalsIgnoreCase("admin")){
            throw new UserSystemException(UserSystemStateCode.No_Permission);
        }
        if(optional.get().getUsername().equalsIgnoreCase("admin")){
            throw new UserSystemException(UserSystemStateCode.Admin_CanNotDisableAdmin);
        }

        User user=optional.get();
        user.setIsAdmin(isAdmin);
        userRepository.save(user);
    }
    //修改密码
    @Override
    public void changePassword(String rowId, String oldPassword, String newPassword) {
        //判断用户id，旧密码，新密码是否为空
        if(rowId==null||rowId.replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }else if (oldPassword==null||oldPassword.replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.OldPassword_IsEmpty);
        }else if (newPassword==null||newPassword.replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.NewPassword_IsEmpty);
        }
        Optional<User> userOptional=userRepository.findById(rowId);
        //判断用户是否存在
        if (!userOptional.isPresent()){
            throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }
        User dbUser = userOptional.get();
        User chkUser = new User();
        chkUser.setPassword(oldPassword);
        //判断旧密码是否正确
        if(!dbUser.getPasswordHash() .equals(chkUser.getPasswordHash())){
            throw new UserSystemException(UserSystemStateCode.OldPassword_isErr);
        }
        //判断新密码是否与旧密码相同
        if(oldPassword.equals(newPassword)){
            throw new UserSystemException(UserSystemStateCode.Password_IsReqeat);
        }
        dbUser.setPassword(newPassword);
        userRepository.save(dbUser);
    }
    //启用禁用用户
    @Override
    public void setEnable(String rowId, Boolean isEnable) {
        if(rowId==null||rowId.replaceAll("\\s","").equals("")){
            throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }else if(isEnable==null){
            throw new UserSystemException(UserSystemStateCode.IsEnable_IsEmpty);
        }
        Optional<User>  userOptional = userRepository.findById(rowId);
        if(!userOptional.isPresent()){
            throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }
        User user=userOptional.get();
        if(user.getIsAdmin() && !isEnable){
            throw new UserSystemException(UserSystemStateCode.User_IsAdminNotDisabled);
        }
        user.setIsEnable(isEnable);
        userRepository.save(user);
    }

    //获取用户数量
    @Override
    public Integer getListCount(String keyword) {
        String querykeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
        if(!querykeywork.contains("%")){
            querykeywork = "%" + querykeywork + "%";
        }
        Integer result = userRepository.countByUsernameLikeOrEmailLikeOrMobileLike(querykeywork,querykeywork,querykeywork).intValue();
        return result;
    }

    //重置密码
    @Override
    public void resetPassword(String rowId, String newPassword) {
        //判断用户id，新密码是否为空
        if(rowId==null||rowId.replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }else if(newPassword==null||newPassword.replaceAll("\\s*","").equals("")){
            throw new UserSystemException(UserSystemStateCode.NewPassword_IsEmpty);
        }
        //用户是否存在
        Optional<User>  userOptional=userRepository.findById(rowId);
        if(!userOptional.isPresent()){
            throw new UserSystemException(UserSystemStateCode.User_IsNotExites);
        }
        User user=userOptional.get();
        user.setPassword(newPassword);
        userRepository.save(user);
    }

}
