package com.lonntec.sufservice.service.impl;

import com.lonntec.sufservice.entity.Domain;
import com.lonntec.sufservice.entity.License;
import com.lonntec.sufservice.entity.User;
import com.lonntec.sufservice.lang.DeploySystemException;
import com.lonntec.sufservice.lang.DeploySystemStateCode;
import com.lonntec.sufservice.repository.DomainRepository;
import com.lonntec.sufservice.repository.LicenseRepository;
import com.lonntec.sufservice.repository.UserRepository;
import com.lonntec.sufservice.service.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import team.benchem.framework.lang.MicroServiceException;
import team.benchem.framework.sdk.UserContext;
import team.benchem.framework.service.TokenService;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Transactional(rollbackOn = {MicroServiceException.class, RuntimeException.class})
@Service
public class LicenseServiceImpl implements LicenseService{
    @Autowired
    LicenseRepository licenseRepository;
    @Autowired
    DomainRepository domainRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenService tokenService;
    /**
     *
     *获取授权申请列表
     */
    @Override
    public List<License> getlist(String keyword, Integer page, Integer size) {
        //用户是否存在 若果是admin可以查所有，如果是实施人员没有权限
        UserContext currCtx=UserContext.getCurrentUserContext();
        String tokenuserId=currCtx.properties.getString("rowid");
        Optional<User> userOptional=userRepository.findById(tokenuserId);
        Integer queryPage = page==null || page <= 0 ? 1 : page;
        Integer querySize = size==null|| size <= 0 ? 25 : size;
        PageRequest pageable = new PageRequest(queryPage -1, querySize);
        String queryKeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
        if(!queryKeywork.contains("%")){
            queryKeywork = "%" + queryKeywork + "%";
        }
        //若果是admin可查所有，如果是实施人员只能查自己创建的
        if(userOptional.isPresent()){
            if(userOptional.get().getAdmin()==true){
                return  licenseRepository.findAllByMyQueryIsAdmin(queryKeywork,pageable);
            }else if(userOptional.get().getAdmin()==false){
                return licenseRepository.findAllByMyQuery(queryKeywork,tokenuserId,pageable);
            }
        }
        return null;

    }
    /**
     *
     * 获取授权申请数量
     */
    @Override
    public Integer getlistcount(String keyword) {
        UserContext currCtx=UserContext.getCurrentUserContext();
        String tokenuserId=currCtx.properties.getString("rowid");
        Optional<User> userOptional=userRepository.findById(tokenuserId);
        //判断用户是否存在
        if(!userOptional.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.Login_IsNot);
        }
        String queryKeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
        if(!queryKeywork.contains("%")){
            queryKeywork = "%" + queryKeywork + "%";
        }
        //若果是admin可以查所有，如果是实施人员只能查自己创建的
        if(userOptional.get().getAdmin()==true){
            return  licenseRepository.countByMyQueryIsAdmin(queryKeywork);
        }else if(userOptional.get().getAdmin()==false){
            return licenseRepository.countByMyQuery(queryKeywork,tokenuserId);
        }
     return 0;
    }
    /**
     *
     * 递交授权申请
     */
    @Override
    public License apply(String domainId, String memo, Integer userCount, Date expireDate) {
        //判断是否传入空值 domainId userCount(是否为数字) expireDate
        String count=userCount.toString();
        if(domainId==null||domainId.replaceAll("\\s*","").equals("")){
            throw new DeploySystemException(DeploySystemStateCode.DomainId_IsEmpty);
        }else if (userCount==null||!Pattern.compile("[0-9]*").matcher(count).matches()){
            throw new DeploySystemException(DeploySystemStateCode.UserCount_IsNotFigure);
        }else if (expireDate==null){
            throw new DeploySystemException(DeploySystemStateCode.ExpireDate_IsEmpty);
        }
        //获取企业域信息
        Optional<Domain> domainOptional=domainRepository.findById(domainId);
        //判断企业域是否存在
        if(!domainOptional.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.Domain_IsNotExist);
        }
        //获取owneruser
        Optional<User> userOptional=userRepository.findById(domainOptional.get().getUser().getRowId());
        License license=new License();
        //申请单单号
        SimpleDateFormat sdf=new SimpleDateFormat("yyMMddHHmmssSSS");
        Calendar calendar=Calendar.getInstance();
        //申请单号 Todo（完善）
        license.setBillNumber("SQ"+sdf.format(new Date()));
        license.setDomain(domainOptional.get());
        license.getDomain().setUser(userOptional.get());
        license.setApplyUserCount(userCount);
        license.setApplyExpireDate(expireDate);
        license.setMemo(memo);
        license.setCreateTime(calendar.getTime());
        license.setBillState(1);
        return licenseRepository.save(license);
    }
    /**
     *
     * 审核授权申请
     */
    @Override
    public void auditapply(String applyId, Boolean isPass, String auditMemo) {
        //判断用户是否存在 是否admin
        UserContext currCtx=UserContext.getCurrentUserContext();
        String tokenuserId=currCtx.properties.getString("rowid");
        Optional<User> userOptional=userRepository.findById(tokenuserId);
        if(!userOptional.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.Login_IsNot);
        }else if(userOptional.get().getAdmin()==false){
            throw new DeploySystemException(DeploySystemStateCode.User_IsNotAdmin);
        }
        //获取申请单
        Optional<License> licenseOptional= licenseRepository.findById(applyId);
        //判断表单是否存在，审核表单
        if(!licenseOptional.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.License_IsNotExist);
        }else if (licenseOptional.isPresent()){
            License licenseForm=licenseOptional.get();
            if(isPass==true){
                licenseForm.setBillState(2);
                //修改企业域是否开通suf
                Domain domain=licenseForm.getDomain();
                domain.setIsActiveSuf(true);
                domain.setUsercount(licenseForm.getApplyUserCount());
                domainRepository.save(domain);
            }else if (isPass==false||isPass==null){
                licenseForm.setBillState(3);
            }
            //审批备注
            licenseForm.setAuditMemo(auditMemo);
            //修改审核人信息
            licenseForm.setAuditer(userOptional.get());

            licenseRepository.save(licenseForm);
        }
    }
}
