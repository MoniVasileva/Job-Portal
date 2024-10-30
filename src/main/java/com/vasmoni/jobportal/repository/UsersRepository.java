package com.vasmoni.jobportal.repository;

import com.vasmoni.jobportal.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users,String> {

    Optional<Users> findByEmail(String email);
}
