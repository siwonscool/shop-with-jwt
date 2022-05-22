package com.sample.shop.member.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Builder
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "AUTHORITY_ID")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private String role;

    public static Authority ofUser(Member member) {
        return Authority.builder()
            .role("ROLE_USER")
            .member(member)
            .build();
    }

    public static Authority ofAdmin(Member member) {
        return Authority.builder()
            .role("ROLE_ADMIN")
            .member(member)
            .build();
    }

    @Override
    public String getAuthority() {
        return null;
    }
}