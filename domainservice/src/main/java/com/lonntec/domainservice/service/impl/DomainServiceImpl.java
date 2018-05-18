package com.lonntec.domainservice.service.impl;

import com.lonntec.domainservice.entity.Domain;
import com.lonntec.domainservice.entity.User;
import com.lonntec.domainservice.lang.DomainSystemException;
import com.lonntec.domainservice.lang.DomainSystemStateCode;
import com.lonntec.domainservice.proxy.CodeRuleService;
import com.lonntec.domainservice.repository.DomainRepository;
import com.lonntec.domainservice.repository.UserRepository;
import com.lonntec.domainservice.service.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import team.benchem.framework.lang.MicroServiceException;
import team.benchem.framework.sdk.UserContext;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional(rollbackOn = {MicroServiceException.class, RuntimeException.class})
@Service
public class DomainServiceImpl implements DomainService{

    @Autowired
    DomainRepository domainRepository;

    @Autowired
    CodeRuleService codeRuleService;

    @Autowired
    UserRepository userRepository;
    //添加企业域
    @Override
    public Domain appendDomain(Domain domain,String ownerId) {
        //判断企业名称,企业简称,联系地址,联系人,联系人电话是否为空
        if(domain.getDomainName()==null||domain.getDomainName().replaceAll("\\s*","").equals("")){
            throw new DomainSystemException(DomainSystemStateCode.DomainName_IsEmpty);
        }
        //判断实施人员是否存在
        if(ownerId==null||ownerId.replaceAll("\\s*","").equals("")){
            throw new DomainSystemException(DomainSystemStateCode.OwnerUserId_IsEmpty);
        }
        Optional<User> userOptional = userRepository.findById(ownerId);
        if(!userOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.OwnerUserId_IsEmpty);
        }
        domain.setOwnerUser(userOptional.get());
        //判断企业名是否已存在
        Optional<Domain>  domainNameOptional = domainRepository.findByDomainName(domain.getDomainName());
        if(domainNameOptional.isPresent() && !(domainNameOptional.get().getRowId().equals(domain.getRowId()))){
            throw new DomainSystemException(DomainSystemStateCode.DomainName_IsExist);
        }
        if(domain.getDomainShortName()==null||domain.getDomainShortName().replaceAll("\\s*","").equals("")){
            throw new DomainSystemException(DomainSystemStateCode.DomainShortName_IsEmpty);
        }
        if(domain.getAddress()==null||domain.getAddress().replaceAll("\\s*","").equals("")){
            throw new DomainSystemException(DomainSystemStateCode.Address_IsEmpty);
        }
        if(domain.getLinkMan()==null||domain.getLinkMan().replaceAll("\\s*","").equals("")){
            throw new DomainSystemException(DomainSystemStateCode.LinkMan_IsEmpty);
        }
        if(domain.getLinkManMobile()==null||domain.getLinkManMobile().replaceAll("\\s*","").equals("")){
            throw new DomainSystemException(DomainSystemStateCode.LinkManMobile_IsEmpty);
        }
        Optional<Domain> domainOptional = domainRepository.findByDomainName(domain.getDomainName());
        if(domainOptional.isPresent()){
            throw new MicroServiceException(DomainSystemStateCode.DomainName_IsExist);
        }
        domain.setDomainNumber(codeRuleService.generateCode("domainNumberRule"));
        Domain db_domain=domainRepository.save(domain);
        //User rtn_user = userRepository.findById(db_domain.getOwnerUser().getRowId());

        return  db_domain;
    }

    //根据关键字查询企业分页列表
    @Override
    public List<Domain> findDomains(String keyword, Integer page, Integer size){
        Integer queryPage = page==null || page <= 0 ? 1 : page;
        Integer querySize = size==null || size <= 0 ? 25 : size;
        PageRequest pageable = new PageRequest(queryPage -1, querySize);
        String queryKeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
        if(!queryKeywork.contains("%")){
            queryKeywork = "%" + queryKeywork + "%";
        }
        // 还未实现按用户隔离数据

        UserContext currCtx = UserContext.getCurrentUserContext();
        String ownerId = currCtx.properties.getString("rowid");
        Optional<User> userOptional = userRepository.findById(ownerId);
        if(!userOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.Login_IsNot);
        }

        if(userOptional.get().getIsAdmin()==true){
            return domainRepository.getAllByIsAdmin(queryKeywork, pageable);
        }else if(userOptional.get().getIsAdmin()==false){
            return domainRepository.getAllByOwner(queryKeywork,ownerId,pageable);
        }
        return null;

    }


    //获取企业域数量
    @Override
    public Integer getListCount(String keyword) {
        String querykeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
        if(!querykeywork.contains("%")){
            querykeywork = "%" + querykeywork + "%";
        }
        //todo 还未实现按用户隔离数据
        // exp 管理员(user.isAdmin 可以查询所以企业域列表)
        //     普通用户，只能查询实施人员 === 自己的企业域列表
        UserContext currCtx = UserContext.getCurrentUserContext();
        String ownerId = currCtx.properties.getString("rowid");
        Optional<User> userOptional = userRepository.findById(ownerId);
        if(!userOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.Login_IsNot);
        }

        if(userOptional.get().getIsAdmin()==true){
            return domainRepository.countByMyQuery(querykeywork);
        }else if(userOptional.get().getIsAdmin()==false){
            return domainRepository.countByOwner(querykeywork,ownerId);
        }

        return null;
    }


    //修改企业域
    @Override
    public Domain modifyDomain(Domain domain) {
        //判断企业名称,企业简称,联系地址,联系人,联系人电话,实施人员ID是否为空
        if(domain.getDomainName()==null||domain.getDomainName().replaceAll("\\s*","").equals("")){
            throw new DomainSystemException(DomainSystemStateCode.DomainName_IsEmpty);
        }
        if(domain.getDomainShortName()==null||domain.getDomainShortName().replaceAll("\\s*","").equals("")){
            throw new DomainSystemException(DomainSystemStateCode.DomainShortName_IsEmpty);
        }
        if(domain.getAddress()==null||domain.getAddress().replaceAll("\\s*","").equals("")){
            throw new DomainSystemException(DomainSystemStateCode.Address_IsEmpty);
        }
        if(domain.getLinkMan()==null||domain.getLinkMan().replaceAll("\\s*","").equals("")){
            throw new DomainSystemException(DomainSystemStateCode.LinkMan_IsEmpty);
        }
        if(domain.getLinkManMobile()==null||domain.getLinkManMobile().replaceAll("\\s*","").equals("")){
            throw new DomainSystemException(DomainSystemStateCode.LinkManMobile_IsEmpty);
        }

        //判断企业是否为空
        Optional<Domain>  domainOptional = domainRepository.findById(domain.getRowId());
        if(!domainOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.Domain_IsNotExist);
        }

        //判断企业名是否存在
        Optional<Domain>  domainNameOptional = domainRepository.findByDomainName(domain.getDomainName());
        if(domainNameOptional.isPresent() && !(domainNameOptional.get().getRowId().equals(domain.getRowId()))){
            throw new DomainSystemException(DomainSystemStateCode.DomainName_IsExist);
        }

        Domain dbDomain = domainOptional.get();
        dbDomain.setDomainName(domain.getDomainName());
        dbDomain.setDomainShortName(domain.getDomainShortName());
        dbDomain.setAddress(domain.getAddress());
        dbDomain.setLinkMan(domain.getLinkMan());
        dbDomain.setLinkManMobile(domain.getLinkManMobile());
        dbDomain.setBusinessLicense(domain.getBusinessLicense());
        dbDomain.setMemo(domain.getMemo());
        //密码 最后登录时间 Todo
        return domainRepository.save(dbDomain);

    }

    //启用禁用企业域
    @Override
    public void setEnable(String rowId, Boolean isEnable) {
        UserContext currCtx = UserContext.getCurrentUserContext();
        String ownerId = currCtx.properties.getString("rowid");
        Optional<User> userOptional = userRepository.findById(ownerId);
        //参数不能为空
        if(rowId==null||rowId.replaceAll("\\s","").equals("")){
            throw new DomainSystemException(DomainSystemStateCode.Domain_IsNotExist);
        }else if(isEnable==null){
            throw new DomainSystemException(DomainSystemStateCode.IsEnable_IsEmpty);
        }
        Optional<Domain>  domainOptional = domainRepository.findById(rowId);
        if(!domainOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.Domain_IsNotExist);
        }else if(userOptional.get().getIsAdmin()==false &&
                !(userOptional.get().getUserName().equalsIgnoreCase(domainOptional.get().getOwnerUser().getUserName()))){
            throw new DomainSystemException(DomainSystemStateCode.No_Permissions);
        }else if(!userOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.Login_IsNot);
        }
        Domain domain=domainOptional.get();
        domain.setIsEnable(isEnable);
        domainRepository.save(domain);
    }

    //企业域实施人员变更
    @Override
    public Domain changeUser(String domainId, String newOwnerId) {

        Optional<Domain> domainOptional1 = domainRepository.findById(domainId);
        if(!domainOptional1.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.Domain_IsNotExist);
        }

        UserContext currCtx = UserContext.getCurrentUserContext();
        String operateUserId = currCtx.properties.getString("rowid");
        Optional<User> userOptional = userRepository.findById(operateUserId);
        if(!userOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.Login_IsNot);
        }

        if(userOptional.get().getIsAdmin()==false){
            throw new DomainSystemException(DomainSystemStateCode.No_Permissions);
        }

        Optional<User> newUserOptional = userRepository.findById(newOwnerId);
        if(!newUserOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.NewOwnerUserId_IsEmpty);
        }

        User newUser = newUserOptional.get();
        //userRepository.save(newUser);
        Domain domainInfo = domainOptional1.get();
        domainInfo.setOwnerUser(newUser);

        return domainRepository.save(domainInfo);
    }

    //根据关键字查询(已/未)开通SUF企业分页列表
    @Override
    public List<Domain> findActiveSufListDomains(String keyword, Integer page, Integer size,Boolean IsActiveSuf){
        Integer queryPage = page==null || page <= 0 ? 1 : page;
        Integer querySize = size==null || size <= 0 ? 25 : size;
        PageRequest pageable = new PageRequest(queryPage -1, querySize);
        String queryKeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
        if(!queryKeywork.contains("%")){
            queryKeywork = "%" + queryKeywork + "%";
        }
        UserContext currCtx = UserContext.getCurrentUserContext();
        String ownerId = currCtx.properties.getString("rowid");
        Optional<User> userOptional = userRepository.findById(ownerId);
        if(!userOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.Login_IsNot);
        }
        if(userOptional.get().getIsAdmin()==true){
            if(IsActiveSuf==true){
                return domainRepository.findAllByMyQueryIsActiveSuf(queryKeywork,pageable);
            }else if(IsActiveSuf==false){
                return domainRepository.findAllByMyQueryNotActiveSuf(queryKeywork,pageable);
            }
        }else if(userOptional.get().getIsAdmin()==false){
            if(IsActiveSuf==true){
                return domainRepository.findAllByMyQueryIsActiveSufByOwner(queryKeywork,ownerId,pageable);
            }else if(IsActiveSuf==false){
                return domainRepository.findAllByMyQueryNotActiveSufByOwner(queryKeywork,ownerId,pageable);
            }
        }

        return null;
    }
}
