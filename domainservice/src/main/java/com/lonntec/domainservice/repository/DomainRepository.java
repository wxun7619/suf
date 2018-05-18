package com.lonntec.domainservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lonntec.domainservice.entity.Domain;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomainRepository extends PagingAndSortingRepository<Domain, String> {

    //根据关键字,是否管理员查询企业分页列表
    @Query("select d from Domain d where " +
            "(d.domainNumber like :keyword or d.domainName like :keyword or d.address like :keyword or " +
            "d.linkManMobile like :keyword or d.linkMan like :keyword or d.businessLicense like :keyword or d.memo like :keyword) " +
            "order by d.domainNumber")
    List<Domain> getAllByIsAdmin(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    //根据关键字,是否管理员查询企业分页列表(实施用户)
    @Query("select d from Domain d where " +
            "(d.domainNumber like :keyword or d.domainName like :keyword or d.address like :keyword or " +
            "d.linkManMobile like :keyword or d.linkMan like :keyword or d.businessLicense like :keyword or d.memo like :keyword) " +
            "and d.ownerUser.rowId=:currUserId " +
            "order by d.domainNumber")
    List<Domain> getAllByOwner(
            @Param("keyword") String keyword,
            @Param("currUserId") String currUserId,
            Pageable pageable
    );


    //根据关键字，获取企业域数量
    @Query("select count(d) from Domain d where d.domainNumber like :keyword or d.domainName like :keyword or " +
            "d.address like :keyword or d.linkManMobile like :keyword or d.linkMan like :keyword or " +
            "d.businessLicense like :keyword or d.memo like :keyword")
    Integer countByMyQuery(
            @Param("keyword") String keyword
    );
    //根据关键字，获取企业域数量(实施用户)
    @Query("select count(d) from Domain d where " +
            "(d.domainNumber like :keyword or d.domainName like :keyword or d.address like :keyword or " +
            "d.linkManMobile like :keyword or d.linkMan like :keyword or d.businessLicense like :keyword or " +
            "d.memo like :keyword) and d.ownerUser.rowId= :currUserId")
    Integer countByOwner(
            @Param("keyword") String keyword,
            @Param("currUserId") String currUserId
    );

    Optional<Domain> findByDomainName(String domainName);

    // 获取未/已开通suf企业列表
    @Query("select d from Domain d where (d.domainNumber like :keyword or d.domainName like :keyword) and " +
            "d.isActiveSuf = true and d.isEnable = true order by d.domainNumber")
    List<Domain> findAllByMyQueryIsActiveSuf(
            @Param("keyword") String keyword,
            Pageable pageable
    );
    @Query("select d from Domain d where (d.domainNumber like :keyword or d.domainName like :keyword) and " +
            "d.isActiveSuf = false and d.isEnable = true order by d.domainNumber")
    List<Domain> findAllByMyQueryNotActiveSuf(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 获取未/已开通suf企业列表(实施用户)
    @Query("select d from Domain d where (d.domainNumber like :keyword or d.domainName like :keyword) and " +
            "d.isActiveSuf = true and d.isEnable = true and d.ownerUser.rowId= :currUserId order by d.domainNumber")
    List<Domain> findAllByMyQueryIsActiveSufByOwner(
            @Param("keyword") String keyword,
            @Param("currUserId") String currUserId,
            Pageable pageable
    );
    @Query("select d from Domain d where (d.domainNumber like :keyword or d.domainName like :keyword) and " +
            "d.isActiveSuf = false and d.isEnable = true and d.ownerUser.rowId= :currUserId order by d.domainNumber")
    List<Domain> findAllByMyQueryNotActiveSufByOwner(
            @Param("keyword") String keyword,
            @Param("currUserId") String currUserId,
            Pageable pageable
    );
}
