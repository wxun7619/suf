package team.benchem.usersystem.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import team.benchem.usersystem.entity.User;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, String> {

    List<User> findAllByUsernameLikeOrEmailLikeOrMobileLikeOrderByUsername(String username, String email, String mobile,Pageable pageable);

    Optional<User> findByUsername(String userName);

    Optional<User> findByMobile(String mobile);

    Optional<User>  findByEmail(String email);

    Long countByUsernameLikeOrEmailLikeOrMobileLike(String userName, String email, String mobile);

}
