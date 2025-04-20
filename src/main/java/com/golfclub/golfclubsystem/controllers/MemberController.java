package com.golfclub.golfclubsystem.controllers;

import com.golfclub.golfclubsystem.models.Member;
import com.golfclub.golfclubsystem.data.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberRepository memberRepository;

    @Autowired
    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        try {
            Member savedMember = memberRepository.save(member);
            return new ResponseEntity<>(savedMember, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Optional<Member> member = memberRepository.findById(id);
        return member.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody Member memberDetails) {
        Optional<Member> memberData = memberRepository.findById(id);

        if (memberData.isPresent()) {
            Member member = memberData.get();
            member.setName(memberDetails.getName());
            member.setAddress(memberDetails.getAddress());
            member.setEmail(memberDetails.getEmail());
            member.setPhone(memberDetails.getPhone());
            member.setStartDate(memberDetails.getStartDate());
            member.setDurationMonths(memberDetails.getDurationMonths());
            return new ResponseEntity<>(memberRepository.save(member), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteMember(@PathVariable Long id) {
        try {
            memberRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Member>> searchMembers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) Integer duration) {

        try {
            if (name != null) {
                return ResponseEntity.ok(memberRepository.findByNameContainingIgnoreCase(name));
            } else if (email != null) {
                return ResponseEntity.ok(memberRepository.findByEmailContainingIgnoreCase(email));
            } else if (phone != null) {
                return ResponseEntity.ok(memberRepository.findByPhone(phone));
            } else if (startDate != null) {
                Date date = java.sql.Date.valueOf(startDate);
                return ResponseEntity.ok(memberRepository.findByMembershipStartDateAfter(date));
            } else if (duration != null) {
                return ResponseEntity.ok(memberRepository.findByMembershipDuration(duration));
            }
            return ResponseEntity.ok(memberRepository.findAll());
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}