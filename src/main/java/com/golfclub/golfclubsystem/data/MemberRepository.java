package com.golfclub.golfclubsystem.data;

import com.golfclub.golfclubsystem.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByNameContainingIgnoreCase(String name);
    List<Member> findByPhone(String phone);
    List<Member> findByEmailContainingIgnoreCase(String email);

    @Query("SELECT m FROM Member m WHERE m.startDate >= :startDate")
    List<Member> findByMembershipStartDateAfter(@Param("startDate") Date startDate);

    @Query("SELECT m FROM Member m WHERE m.durationMonths = :duration")
    List<Member> findByMembershipDuration(@Param("duration") int duration);

    @Query("SELECT m FROM Member m JOIN FETCH m.tournaments t WHERE t.id = :tournamentId")
    List<Member> findByTournamentId(@Param("tournamentId") Long tournamentId);
}