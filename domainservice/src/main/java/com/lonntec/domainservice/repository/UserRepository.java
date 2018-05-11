package com.lonntec.domainservice.repository;

import com.lonntec.domainservice.entity.Domain;
import com.lonntec.domainservice.entity.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, String> {


}
