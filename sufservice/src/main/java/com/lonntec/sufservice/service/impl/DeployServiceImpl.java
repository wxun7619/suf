package com.lonntec.sufservice.service.impl;

import com.lonntec.sufservice.entity.ApplyForm;
import com.lonntec.sufservice.entity.Domain;
import com.lonntec.sufservice.entity.DomainUser;
import com.lonntec.sufservice.entity.User;
import com.lonntec.sufservice.lang.DeploySystemException;
import com.lonntec.sufservice.lang.DeploySystemStateCode;
import com.lonntec.sufservice.repository.DeployRepository;
import com.lonntec.sufservice.repository.DomainRepository;
import com.lonntec.sufservice.repository.DomainUserRepository;
import com.lonntec.sufservice.repository.UserRepository;
import com.lonntec.sufservice.service.DeployService;
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

@Transactional(rollbackOn = {MicroServiceException.class, RuntimeException.class})
@Service
public class DeployServiceImpl implements DeployService{

    @Autowired
    DeployRepository deployRepository;

    @Autowired
    DomainRepository domainRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DomainUserRepository domainUserRepository;
    @Autowired
    TokenService tokenService;
    /**
     *
     *获取开通申请列表
     */
    @Override
    public List<ApplyForm> getdeploylist(String keyword, Integer page, Integer size) {
        UserContext currCtx=UserContext.getCurrentUserContext();
        String tokenuserId=currCtx.properties.getString("rowid");
        Optional<User> user=userRepository.findById(tokenuserId);
        //判断用户是否存在
        if(!user.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.Login_IsNot);
        }
        Integer queryPage = page==null || page <= 0 ? 1 : page;
        Integer querySize = size==null|| size <= 0 ? 25 : size;
        PageRequest pageable = new PageRequest(queryPage -1, querySize);
        String queryKeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
        if(!queryKeywork.contains("%")){
            queryKeywork = "%" + queryKeywork + "%";
        }
        //若果是admin可以查所有，如果是实施人员只能查自己创建的
        if(user.isPresent()){
            if(user.get().getAdmin()==true){
                return  deployRepository.findAllByMyQueryIsAdmin(queryKeywork,pageable);
            }else if(user.get().getAdmin()==false){
                return deployRepository.findAllByMyQuery(queryKeywork,tokenuserId,pageable);
            }
        }
        return null;
    }

    /**
     *
     *获取开通申请数量
     */
    @Override
    public Integer countdeploy(String keyword) {
        UserContext currCtx=UserContext.getCurrentUserContext();
        String tokenuserId=currCtx.properties.getString("rowid");
        Optional<User> user=userRepository.findById(tokenuserId);
        //判断用户是否存在
        if(!user.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.Login_IsNot);
        }else if(user.isPresent()){
            String queryKeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
            if(!queryKeywork.contains("%")){
                queryKeywork = "%" + queryKeywork + "%";
            }
            //若果是admin可以查所有，如果是实施人员只能查自己创建的
            if(user.get().getAdmin()==true){
                return  deployRepository.countByMyQueryIsAdmin(queryKeywork);
            }else if(user.get().getAdmin()==false){
                return deployRepository.countByMyQuery(queryKeywork,tokenuserId);
            }
        }
        return 0;
    }

    /**
     *
     *递交开通申请
     */
    @Override
    public ApplyForm apply(String domainId, String memo, String domainUserName, String domainUserMobile, String domainUserEmainl) {
        //判断是否传入空值 domainId domianUserName domainUserMobile domainUserEmail
        if(domainId==null||domainId.replaceAll("\\s*","").equals("")){
            throw new DeploySystemException(DeploySystemStateCode.DomainId_IsEmpty);
        }else if (domainUserName==null||domainUserName.replaceAll("\\s*","").equals("")){
            throw new DeploySystemException(DeploySystemStateCode.DomainUserName_IsEmpty);
        }else if (domainUserMobile==null||domainUserMobile.replaceAll("\\s*","").equals("")){
            throw new DeploySystemException(DeploySystemStateCode.DomainUserMobile_IsEmpty);
        }else if (domainUserEmainl==null||domainUserEmainl.replaceAll("\\s*","").equals("")){
            throw new DeploySystemException(DeploySystemStateCode.DomainUserEmail_IsEmpty);
        }
        //获取企业域信息
        Optional<Domain> domain= domainRepository.findById(domainId);
        //判断企业域是否存在
        if(!domain.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.Domain_IsNotExist);
        }else if(domain.get().getIsActiveSuf()==true){
            throw new DeploySystemException(DeploySystemStateCode.Suf_IsDeploy);
        }else if (domain.get().getIsEnable()==false){
            throw new DeploySystemException(DeploySystemStateCode.Domain_IsNotEnable);
        }
        //获取owneruser
        Optional<User> userOptional=userRepository.findById(domain.get().getUser().getRowId());
        //获取domainUser
        DomainUser domainUser=domainUserRepository.findByUserName(domainUserName);

        ApplyForm applyForm=new ApplyForm();
        //申请单单号
        SimpleDateFormat sdf=new SimpleDateFormat("yyMMddHHmmssSSS");
        Calendar calendar=Calendar.getInstance();
        //申请单号 Todo（完善）
        applyForm.setBillNumber("SQ"+sdf.format(new Date()));
        applyForm.setDomain(domain.get());
        applyForm.getDomain().setUser(userOptional.get());
        applyForm.setDomainUserName(domainUserName);
        applyForm.setDomainUserMobile(domainUserMobile);
        applyForm.setDomainUserEmain(domainUserEmainl);
        applyForm.setMemo(memo);
        applyForm.setCreateTime(calendar.getTime());
        applyForm.setBillState(1);
        deployRepository.save(applyForm);
        return applyForm;
    }

    /**
     *
     *审核开通申请
     */
    @Override
    public void auditapply(String applyid, Boolean isPass, String auditMemo) {
        //获取申请单
        Optional<ApplyForm> applyFormOptional= deployRepository.findById(applyid);
        if(!applyFormOptional.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.ApplyForm_IsExist);
        }else{
            ApplyForm applyForm=applyFormOptional.get();
            if(isPass==true){
                applyForm.setBillState(2);
                //修改企业域是否开通suf
                Domain domain=applyFormOptional.get().getDomain();
                domain.setIsActiveSuf(true);
                Calendar calendar=Calendar.getInstance();
                calendar.add(Calendar.MONTH,2);
                domain.setExpireDate(calendar.getTime());
                domain.setUsercount(10);
                domainRepository.save(domain);
            }else if (isPass==false||isPass==null){
                applyForm.setBillState(3);

            }
            applyForm.setAuditMemo(auditMemo);
            deployRepository.save(applyForm);
        }
    }
}
