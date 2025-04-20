package com.golfclub.golfclubsystem.data;

import com.golfclub.golfclubsystem.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByLocationContainingIgnoreCase(String location);

    @Query("SELECT t FROM Tournament t WHERE t.startDate = :date OR t.endDate = :date")
    List<Tournament> findByDate(@Param("date") Date date);

    @Query("SELECT t FROM Tournament t WHERE t.startDate >= :startDate AND t.endDate <= :endDate")
    List<Tournament> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT t FROM Tournament t WHERE t.cashPrize >= :minPrize")
    List<Tournament> findByMinCashPrize(@Param("minPrize") double minPrize);
}