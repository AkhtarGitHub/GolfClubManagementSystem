package com.golfclub.golfclubsystem.controllers;

import com.golfclub.golfclubsystem.models.Tournament;
import com.golfclub.golfclubsystem.data.TournamentRepository;
import com.golfclub.golfclubsystem.models.Member;
import com.golfclub.golfclubsystem.data.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {
    private final TournamentRepository tournamentRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public TournamentController(TournamentRepository tournamentRepository, MemberRepository memberRepository) {
        this.tournamentRepository = tournamentRepository;
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament tournament) {
        try {
            Tournament savedTournament = tournamentRepository.save(tournament);
            return new ResponseEntity<>(savedTournament, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable Long id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        return tournament.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateTournament(@PathVariable Long id, @RequestBody Tournament tournamentDetails) {
        Optional<Tournament> tournamentData = tournamentRepository.findById(id);

        if (tournamentData.isPresent()) {
            Tournament tournament = tournamentData.get();
            tournament.setStartDate(tournamentDetails.getStartDate());
            tournament.setEndDate(tournamentDetails.getEndDate());
            tournament.setLocation(tournamentDetails.getLocation());
            tournament.setEntryFee(tournamentDetails.getEntryFee());
            tournament.setCashPrize(tournamentDetails.getCashPrize());
            return new ResponseEntity<>(tournamentRepository.save(tournament), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTournament(@PathVariable Long id) {
        try {
            tournamentRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{tournamentId}/members/{memberId}")
    public ResponseEntity<Tournament> addMemberToTournament(
            @PathVariable Long tournamentId,
            @PathVariable Long memberId) {

        Optional<Tournament> tournamentOpt = tournamentRepository.findById(tournamentId);
        Optional<Member> memberOpt = memberRepository.findById(memberId);

        if (tournamentOpt.isPresent() && memberOpt.isPresent()) {
            Tournament tournament = tournamentOpt.get();
            tournament.getParticipants().add(memberOpt.get());
            tournamentRepository.save(tournament);
            return ResponseEntity.ok(tournament);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<Set<Member>> getTournamentMembers(@PathVariable Long id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        return tournament.map(value -> ResponseEntity.ok(value.getParticipants()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Tournament>> searchTournaments(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Double minPrize) {

        try {
            if (location != null) {
                return ResponseEntity.ok(tournamentRepository.findByLocationContainingIgnoreCase(location));
            } else if (date != null) {
                Date searchDate = java.sql.Date.valueOf(date);
                return ResponseEntity.ok(tournamentRepository.findByDate(searchDate));
            } else if (startDate != null && endDate != null) {
                Date start = java.sql.Date.valueOf(startDate);
                Date end = java.sql.Date.valueOf(endDate);
                return ResponseEntity.ok(tournamentRepository.findByDateRange(start, end));
            } else if (minPrize != null) {
                return ResponseEntity.ok(tournamentRepository.findByMinCashPrize(minPrize));
            }
            return ResponseEntity.ok(tournamentRepository.findAll());
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}