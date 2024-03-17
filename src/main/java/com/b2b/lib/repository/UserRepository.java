package com.b2b.lib.repository;

import com.b2b.lib.entity.UserLib;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserLib, Long> {
}
