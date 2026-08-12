package com.iftrue.user.domain;

import com.iftrue.user.domain.common.BaseEntity;
import com.iftrue.user.global.exception.BusinessException;
import com.iftrue.user.global.exception.ErrorCode;
import jakarta.persistence.*;
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

        String id = this.id.toString().replace("-", "");

        // unique 컬럼 충돌 방지
        this.email = "deleted_" + id + "@deleted.local";
        this.username = "deleted_" + id.substring(0, 40);
        this.nickname = "del_" + id.substring(0, 16);

        softDelete(this.id); // BaseEntity deletedAt/deleteBy 세팅
    }


    public boolean isApproved() {
        return this.status == UserStatus.APPROVED;
    }

}