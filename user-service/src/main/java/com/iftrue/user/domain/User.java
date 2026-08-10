package com.iftrue.user.domain;

import com.iftrue.user.domain.common.BaseEntity;
import com.iftrue.user.global.exception.BusinessException;
import com.iftrue.user.global.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.ValidationException;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "nickname", nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "slack_id", nullable = false)
    private String slackId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "hub_id")
    private UUID hubId;

    @Column(name = "company_id")
    private UUID companyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;



    public static User create(
            String username,
            String nickname,
            String email,
            String encodedPassword,
            String slackId,
            UserRole role,
            UUID hubId,
            UUID companyId

    ) {
        return User.builder()
                .username(username)
                .nickname(nickname)
                .email(email)
                .password(encodedPassword)
                .slackId(slackId)
                .role(role)
                .hubId(hubId)
                .companyId(companyId)

                .build();
    }

    public void changeStatus(UserStatus status) {

        if (this.status != UserStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_USER_STATUS);
        }

        if (status != UserStatus.APPROVED &&
                status != UserStatus.REJECTED) {
            throw new BusinessException(ErrorCode.INVALID_USER_STATUS);
        }

        this.status = status;
    }

    public void update(
            String username,
            String nickname,
            String email
    ) {

        this.username = username;
        this.nickname = nickname;
        this.email = email;
    }

    //회원탈퇴시에
    //유니크 속성 컬럼 중복 방지 메서드
    public void withdraw(){
        if (this.status == UserStatus.DELETED) {
            return;
        }

        this.status = UserStatus.DELETED;
        //삭제 시간 Auditing BaseEntity 상속 시에 주석 풀기
        //this.deletedAt = LocalDateTime.now();

        // unique 컬럼(email/username/nickname)을 재가입 충돌이 없도록 치환.
        // 컬럼 길이(username 50, nickname 20)를 넘지 않도록 id 기반 짧은 값 사용.
        String tag = "del_" + this.id;          // id 가 유니크 → 치환값도 유니크
        this.email = tag + "@deleted.local";    // email 컬럼(255) 충분
        this.username = tag;                    // 50 이내
        this.nickname = tag;                    // 20 이내

        softDelete(this.id); // BaseEntity deletedAt/deleteBy 세팅
    }


    public boolean isApproved() {
        return this.status == UserStatus.APPROVED;
    }


//    public static User createSocial(String username, String nickname, String email, String provider, String providerId, String encodeRandomPassword, UserRoleEnum role) {
//        return User.builder()
//                .username(username)
//                .nickname(nickname)
//                .email(email)
//                .password(encodeRandomPassword)
//                .role(role)
//                .build();
//    }
//
//
//    //소프트 딜리트 : 실제 삭제 X
//    //레디스 사용이라 리프레시토큰 DB에 저장안함 그런고로 필드 구현 X
//
//
//    // 비즈니스 로직
//    public void updateProfile(String nickname, String phone) {
//        this.nickname = nickname;
//        this.phone = phone;
//    }
//
//    public void changePassword(String encodedPassword) {
//        this.password = encodedPassword;
//    }
//
//    //회원탈퇴시에
//    //유니크 속성 컬럼 중복 방지 메서드
//    public void withdraw(){
//        if (this.status == UserStatus.DELETED) {
//            return;
//        }
//
//        this.status = UserStatus.DELETED;
//        //삭제 시간 Auditing BaseEntity 상속 시에 주석 풀기
//        //this.deletedAt = LocalDateTime.now();
//
//        // unique 컬럼(email/username/nickname)을 재가입 충돌이 없도록 치환.
//        // 컬럼 길이(username 50, nickname 20)를 넘지 않도록 id 기반 짧은 값 사용.
//        String tag = "del_" + this.id;          // id 가 유니크 → 치환값도 유니크
//        this.email = tag + "@deleted.local";    // email 컬럼(255) 충분
//        this.username = tag;                    // 50 이내
//        this.nickname = tag;                    // 20 이내
//
//        softDelete(this.id); // BaseEntity deletedAt/deleteBy 세팅
//    }
//
//    public void block(){
//        this.status = UserStatus.BLOCKED;
//    }


}