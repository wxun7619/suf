package com.lonntec.domainservice.service.impl;

import com.lonntec.domainservice.entity.Domain;
import com.lonntec.domainservice.entity.User;
import com.lonntec.domainservice.lang.DomainSystemException;
import com.lonntec.domainservice.lang.DomainSystemStateCode;
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
    UserRepository userRepository;
    //添加企业域
    @Override
    public Domain appendDomain(Domain domain) {
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

        //返回值
        Domain db_domain=domainRepository.save(domain);
        //User rtn_user = userRepository.findById(db_domain.getOwnerUser().getRowId());
        Domain rtn_domain=new Domain();
        rtn_domain.setRowId(db_domain.getRowId());
        rtn_domain.setDomainNumber(db_domain.getDomainNumber());
        rtn_domain.setDomainName(db_domain.getDomainName());
        rtn_domain.setDomainShortName(db_domain.getDomainShortName());
        rtn_domain.setAddress(db_domain.getAddress());
        rtn_domain.setLinkMan(db_domain.getLinkMan());
        rtn_domain.setLinkManMobile(db_domain.getLinkManMobile());
        rtn_domain.setMemo(db_domain.getMemo());
        rtn_domain.setIsEnable(db_domain.getIsEnable());
        rtn_domain.setUserCount(db_domain.getUserCount());
        rtn_domain.setExpireDate(db_domain.getExpireDate());
        rtn_domain.setOwnerUser(db_domain.getOwnerUser());

        return  rtn_domain;
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
        //todo 还未实现按用户隔离数据
        // exp 管理员(user.isAdmin 可以查询所以企业域列表)
        //     普通用户，只能查询domain.owner.rowId === usercontext.prop.rowid
        UserContext currCtx = UserContext.getCurrentUserContext();
        String ownerId = currCtx.properties.getString("rowid");
        Optional<User> userOptional = userRepository.findById(ownerId);
        if(!userOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.OwnerUserId_IsNotExist);
        }

        if(userOptional.get().getIsAdmin()==true){
            return domainRepository.findAllByDomainNumberLikeOrDomainNameLikeOrAddressLikeOrLinkManLikeOrLinkManMobileLikeOrBusinessLicenseLikeOrMemoLikeOrderByDomainNumber(queryKeywork, queryKeywork,queryKeywork,queryKeywork, queryKeywork,queryKeywork,queryKeywork, pageable);
        }else if(userOptional.get().getIsAdmin()==false){
            return domainRepository.findAllByMyQuery(queryKeywork,ownerId,pageable);
        }
        return null;

    }


    //获取用户数量
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
            throw new DomainSystemException(DomainSystemStateCode.OwnerUserId_IsNotExist);
        }

        if(userOptional.get().getIsAdmin()==true){
            return domainRepository.countByDomainNumberLikeOrDomainNameLikeOrAddressLikeOrLinkManLikeOrLinkManMobileLikeOrBusinessLicenseLikeOrMemoLike(querykeywork,querykeywork,querykeywork,querykeywork,querykeywork,querykeywork,querykeywork).intValue();
        }else if(userOptional.get().getIsAdmin()==false){
            return domainRepository.countByMyQuery(querykeywork,ownerId).intValue();
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
        if(domainNameOptional.isPresent() && domainNameOptional.get().getRowId() != domain.getRowId()){
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
        if(rowId==null||rowId.replaceAll("\\s","").equals("")){
            throw new DomainSystemException(DomainSystemStateCode.Domain_IsNotExist);
        }else if(isEnable==null){
            throw new DomainSystemException(DomainSystemStateCode.IsEnable_IsEmpty);
        }
        Optional<Domain>  domainOptional = domainRepository.findById(rowId);
        if(!domainOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.Domain_IsNotExist);
        }

        UserContext currCtx = UserContext.getCurrentUserContext();
        String ownerId = currCtx.properties.getString("rowid");
        Optional<User> userOptional = userRepository.findById(ownerId);
        if(!userOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.OwnerUserId_IsNotExist);
        }

        if(userOptional.get().getIsAdmin()==false){
            throw new DomainSystemException(DomainSystemStateCode.OwnerUser_IsNotAdmin);
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
        String ownerId = currCtx.properties.getString("rowid");
        Optional<User> userOptional = userRepository.findById(ownerId);
        if(!userOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.OwnerUserId_IsNotExist);
        }

        if(userOptional.get().getIsAdmin()==false){
            throw new DomainSystemException(DomainSystemStateCode.OwnerUser_IsNotAdmin);
        }

        Optional<User> newUserOptional = userRepository.findById(newOwnerId);
        if(!newUserOptional.isPresent()){
            throw new DomainSystemException(DomainSystemStateCode.OwnerUserId_IsNotExist);
        }

        User newUser = newUserOptional.get();
        userRepository.save(newUser);
        Domain domainInfo = domainOptional1.get();
        domainInfo.setOwnerUser(newUser);

        return domainRepository.save(domainInfo);
    }

    //根据关键字查询已开通SUF企业分页列表
    @Override
    public List<Domain> findActiveSufListDomains(String keyword, Integer page, Integer size){
        Integer queryPage = page==null || page <= 0 ? 1 : page;
        Integer querySize = size==null || size <= 0 ? 25 : size;
        PageRequest pageable = new PageRequest(queryPage -1, querySize);
        String queryKeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
        if(!queryKeywork.contains("%")){
            queryKeywork = "%" + queryKeywork + "%";
        }
        //不能返回密码 Todo
        return domainRepository.findAllByDomainNumberLikeOrDomainNameLikeAndIsActiveSufIsTrueOrderByDomainNumber(queryKeywork, queryKeywork, pageable);
    }

    //根据关键字查询未开通SUF企业分页列表
    @Override
    public List<Domain> findNotActiveSufListDomains(String keyword, Integer page, Integer size) {
        Integer queryPage = page==null || page <= 0 ? 1 : page;
        Integer querySize = size==null || size <= 0 ? 25 : size;
        PageRequest pageable = new PageRequest(queryPage -1, querySize);
        String queryKeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
        if(!queryKeywork.contains("%")){
            queryKeywork = "%" + queryKeywork + "%";
        }
        //不能返回密码 Todo
        return domainRepository.findAllByDomainNumberLikeOrDomainNameLikeAndIsActiveSufIsFalseOrderByDomainNumber(queryKeywork, queryKeywork, pageable);
    }
}
