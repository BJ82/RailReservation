package com.rail.app.railreservation.security.repository;

import com.rail.app.railreservation.security.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<Users,Long> {

    Optional<Users> findByUsername(String username);
}
