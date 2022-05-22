package com.sample.shop.member;

import com.sample.shop.member.domain.Member;
import com.sample.shop.member.domain.MemberStatus;
import com.sample.shop.member.dto.EmptyJsonResponseDto;
import com.sample.shop.member.dto.MemberInfoRequestDto;
import com.sample.shop.shared.member.MemberAdaptor;
import com.sample.shop.shared.exception.EmailDuplicateException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(value = "/member", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class MemberController {

    private final MemberServiceImpl memberServiceImpl;

    //"회원가입을 처리한다. 성공적으로 처리되면 HttpStatus 201이 나와야 하며 member의 id값이 null이 아니여야 한며 member의 상태는 대기중(READY) 이여야 한다."
    //"회원 가입시에 email 중복 체크 기능이 있어야 한다. 중복일경우 Duplicate Exception이 발생하며 API Response 에 그 내용이 기술되어야 한다. (이유, 오류 메시지)"
    @PostMapping("/join")
    public ResponseEntity<Member> join(
        @RequestBody final MemberInfoRequestDto memberInfoRequestDto) {
        try {
            if (!memberServiceImpl.isDuplicateEmail(memberInfoRequestDto).isEmpty()) {
                return new ResponseEntity(new EmailDuplicateException("중복되는 이메일이 존재합니다."),
                    HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                //response 가 안옴..,, DB 저장은됨
                return ResponseEntity.ok(memberServiceImpl.save(memberInfoRequestDto));
            }
        } catch (Exception e) {
            return new ResponseEntity(new EmptyJsonResponseDto(e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //admin 회원가입
    @PostMapping("/join/admin")
    public String joinAdmin(@RequestBody final MemberInfoRequestDto memberInfoRequestDto) {
        memberServiceImpl.joinAdmin(memberInfoRequestDto);
        return "어드민 회원 가입 완료";
    }

    //"회원가입후 가입대기중인 회원을 대상으로 회원상태를 활성(ACTIVATE) 상태로 바뀌어야 한다."
    @PostMapping("/update/status/{id}")
    public void updateMemberStatus(@PathVariable final Long id) {
        memberServiceImpl.updateMemberStatusActivate(id);
    }

    //"등록된 회원에 한하여 회원 탈퇴가 가능해야 한다. 탈퇴가 되면 삭제가 아닌 상태를 (WITHDRAWAL)로 변경해야 한다."
    @DeleteMapping("/delete/{id}")
    public Long deleteMember(@PathVariable final Long id) {
        memberServiceImpl.updateMemberStatusWithdrawal(id);
        return id;
    }
}