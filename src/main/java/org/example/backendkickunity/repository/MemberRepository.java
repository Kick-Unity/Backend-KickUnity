package org.example.backendkickunity.repository;

import org.example.backendkickunity.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmailAndPassword(String email, String password);

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

}
