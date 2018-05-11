package com.lonntec.domainservice.repository;

import com.lonntec.domainservice.entity.User;
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

    List<Domain> findAllByDomainNumberLikeOrDomainNameLikeOrAddressLikeOrLinkManLikeOrLinkManMobileLikeOrBusinessLicenseLikeOrMemoLikeOrderByDomainNumber(String domainnumber, String domainname, String address,String linkman, String linkmanmobile, String businesslicense,String memo,Pageable pageable);

    Long countByDomainNumberLikeOrDomainNameLikeOrAddressLikeOrLinkManLikeOrLinkManMobileLikeOrBusinessLicenseLikeOrMemoLike(String domainnumber, String domainname, String address,String linkman, String linkmanmobile, String businesslicense,String memo);

    Optional<Domain> findByDomainName(String domainName);

    Optional<Domain> findByOwnerUser(User user);

    List<Domain> findAllByDomainNumberLikeOrDomainNameLikeAndIsActiveSufIsTrueOrderByDomainNumber(String domainnumber,String domainname,Pageable pageable);
    List<Domain> findAllByDomainNumberLikeOrDomainNameLikeAndIsActiveSufIsFalseOrderByDomainNumber(String domainnumber,String domainname,Pageable pageable);


    //List<Domain> findAllByDomainNumberLikeOrDomainNameLikeOrAddressLikeOrLinkManLikeOrLinkManMobileLikeOrBusinessLicenseLikeOrMemoLikeAndOwnerUserEqualsOrderByDomainNumber(String domainnumber, String domainname, String address,String linkman, String linkmanmobile, String businesslicense,String memo,Pageable pageable);

    @Query("select d from Domain d where d.ownerUser.rowId = :ownerId and (" +
            "d.domainNumber like :keyword or d.domainName like :keyword or d.domainShortName like :keyword or " +
            "d.linkMan like :keyword or d.linkManMobile like :keyword or d.businessLicense like :keyword" +
            ")  order by d.domainNumber")
    List<Domain> findAllByMyQuery(
            @Param("keyword") String keyword,
            @Param("ownerId") String ownerId,
            Pageable pageable
    );

    @Query("select count(d) from Domain d where d.ownerUser.rowId = :ownerId and (" +
            "d.domainNumber like :keyword or d.domainName like :keyword or d.domainShortName like :keyword or " +
            "d.linkMan like :keyword or d.linkManMobile like :keyword or d.businessLicense like :keyword" +
            ") order by d.domainNumber")
    Long countByMyQuery(
            @Param("keyword") String keyword,
            @Param("ownerId") String ownerId
    );
}
