package com.mobileshop.group8.repository;

import com.mobileshop.group8.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Member, String> {
    Member findByUserId(@Param("userID")String userId);
    Member findByUserIdAndPassword(String username, String password);
}
