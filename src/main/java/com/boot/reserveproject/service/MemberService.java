package com.boot.reserveproject.service;

import com.boot.reserveproject.domain.Member;
import com.boot.reserveproject.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Long join(Member member) {
        memberRepository.save(member);
        return member.getId();
    }

    public boolean validId(String loginId) {
        List<Member> members = memberRepository.selectAll(loginId);
        if (!members.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean checkLogin(String loginId, String pw) {
        Member member = memberRepository.checkLogin(loginId, pw);
        if (member != null) {
            return true;
        }
        return false;
    }

    public List<Member> findAllMembers() {
        List<Member> list = memberRepository.findAll();
        if (list.isEmpty()) throw new IllegalStateException("데이터가 존재하지않습니다");
        return list;
    }

    @Transactional
    public void removeMember(Long id) {
        memberRepository.deleteById(id);
    }

    public Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다"));
    }

    public boolean validPhoneNumber(String phone) {
        List<Member> members = memberRepository.selectPhoneNumber(phone);
        if (!members.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean validEmail(String email) {
        List<Member> members = memberRepository.selectEmail(email);
        if (!members.isEmpty()) {
            return false;
        }
        return true;
    }

    public void updateTemp(String type, String address, String code) {
        if (type.equals("phone")) {
            memberRepository.updateTempByPhone(code, address);
        } else {
            memberRepository.updateTempByEmail(code, address);
        }
    }

    public Member selectMemberById(String loginId) {
        return memberRepository.selectMemberByLoginId(loginId);
    }
}