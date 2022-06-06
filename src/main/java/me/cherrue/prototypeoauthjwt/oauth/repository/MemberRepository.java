package me.cherrue.prototypeoauthjwt.oauth.repository;

import me.cherrue.prototypeoauthjwt.oauth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM member m WHERE m.registration_id = ?1 AND m.o_auth_id = ?2")
    Optional<Member> findByRegistrationIdAndOAuthId(String registrationId, String oAuthId);
}
