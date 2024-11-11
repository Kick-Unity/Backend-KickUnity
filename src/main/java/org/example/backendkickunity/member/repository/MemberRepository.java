package org.example.backendkickunity.member.repository;

import org.example.backendkickunity.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmailAndPassword(String email, String password);

    Member findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByName(String name);


}
