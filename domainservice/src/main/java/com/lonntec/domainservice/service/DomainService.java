package com.lonntec.domainservice.service;

import com.lonntec.domainservice.entity.Domain;
import org.springframework.lang.Nullable;

import java.util.List;

public interface DomainService {

    List<Domain> findDomains(String keyword, Integer page, Integer size);

    List<Domain> findActiveSufListDomains(String keyword, Integer page, Integer size);

    List<Domain> findNotActiveSufListDomains(String keyword, Integer page, Integer size);

    Domain appendDomain(Domain domain);

    Domain modifyDomain(Domain domain);

    Integer getListCount(String keyword);

    void setEnable(String rowId,  Boolean isEnable);

    Domain changeUser(String domainId, String newOwnerId);


}
