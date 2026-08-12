package com.iftrue.user.application;

import com.iftrue.user.domain.User;
import com.iftrue.user.domain.UserRepository;
import com.iftrue.user.domain.UserRole;
import com.iftrue.user.domain.UserStatus;
import com.iftrue.user.global.exception.BusinessException;
import com.iftrue.user.global.exception.ErrorCode;
import com.iftrue.user.global.response.ApiResponse;
import com.iftrue.user.infrastructure.client.delivery.DeliveryClient;
import com.iftrue.user.infrastructure.client.delivery.DeliveryManagerType;
import com.iftrue.user.infrastructure.client.delivery.dto.DeliveryManagerCreateRequest;
import com.iftrue.user.infrastructure.client.delivery.dto.DeliveryManagerCreateResponse;
import com.iftrue.user.presentation.request.SignUpRequest;
import com.iftrue.user.presentation.request.UserUpdateRequest;
import com.iftrue.user.presentation.response.UserResponse;
import com.iftrue.user.presentation.response.UserStatusUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.*;
import java.security.Principal;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final DeliveryClient deliveryClient;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

//    회원가입 및 요청passwordEncoder
    @Transactional
    public UserResponse signUp(SignUpRequest request) {

        validateDuplicate(request);

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.create(
                request.getUsername(),
                request.getNickname(),
                request.getEmail(),
                encodedPassword,
                request.getSlackId(),
                request.getRole(),
                request.getHubId(),
                request.getCompanyId()
        );

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

//    사용자 상태 변경 (가입 승인/거절)
    @Transactional
    public UserStatusUpdateResponse updateStatus(UUID id, UserStatus status) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND));


        // 여기서 PENDING 여부 및 APPROVED/REJECTED 여부 검증
        user.changeStatus(status);

        //승인된 배송 담당자만 Delivery Service에 생성
        if (status == UserStatus.APPROVED
                && user.getRole() == UserRole.DELIVERY_MANAGER) {
            createDeliveryManager(user);

        }
        return new UserStatusUpdateResponse(
                user.getId(),
                user.getStatus(),
                user.getUpdatedAt()
        );
    }

//    내 정보 조회
    @Transactional(readOnly = true)
    public UserResponse getMyInfo(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );

        return UserResponse.from(user);
    }

//    사용자 목록 조회
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(UserResponse::from);
    }
//    사용자 단건 조회
    @Transactional(readOnly = true)
    public UserResponse getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );
        return UserResponse.from(user);
    }
//    사용자 정보 수정

    @Transactional
    public UserResponse updateUser(
            UUID id,
            UserUpdateRequest request
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );
        user.update(
                request.username(),
                request.nickname(),
                request.email()
        );
        return UserResponse.from(user);
    }

//    회원 탈퇴/비활성화
    @Transactional
    public void deleteUser(
            UUID targetUserId,
            UUID requestUserId,
            UserRole requestRole
    ) {
        if (!isAdmin(requestRole)
                && !targetUserId.equals(requestUserId)) {
            throw new BusinessException(
                    ErrorCode.ACCESS_DENIED
            );
        }
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );
        user.withdraw();
    }

    private boolean isAdmin(UserRole role) {

        return role == UserRole.MASTER
                || role == UserRole.HUB_MANAGER;
    }

    private void validateDuplicate(SignUpRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_DUPLICATED);
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATED);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }
    }

    private void createDeliveryManager(User user) {

        DeliveryManagerType type =
                user.getHubId() == null
                        ? DeliveryManagerType.HUB
                        : DeliveryManagerType.COMPANY;

        DeliveryManagerCreateRequest request =
                new DeliveryManagerCreateRequest(
                        user.getId(),
                        user.getSlackId(),
                        user.getHubId(),
                        type
                );

        ApiResponse<DeliveryManagerCreateResponse> response =
                deliveryClient.createDelivery(
                        request
                );
        // 응답 자체가 없는 경우
        if (response == null) {
            throw new BusinessException(
                    ErrorCode.DELIVERY_MANAGER_CREATE_FAILED
            );
        }
        // data가 없는 경우
        DeliveryManagerCreateResponse deliveryResponse =
                response.getData();

        if (deliveryResponse == null
                || deliveryResponse.deliveryManagerId() == null) {

            throw new BusinessException(
                    ErrorCode.DELIVERY_MANAGER_CREATE_FAILED
            );
        }

    }

}
