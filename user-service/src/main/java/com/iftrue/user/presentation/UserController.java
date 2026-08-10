package com.iftrue.user.presentation;

import com.iftrue.user.application.AuthService;
import com.iftrue.user.application.UserService;
import com.iftrue.user.domain.UserRole;
import com.iftrue.user.global.response.ApiResponse;
import com.iftrue.user.presentation.request.*;
import com.iftrue.user.presentation.response.LoginResponse;
import com.iftrue.user.presentation.response.RefreshTokenResponse;
import com.iftrue.user.presentation.response.UserResponse;
import com.iftrue.user.presentation.response.UserStatusUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserService userService;
    private final AuthService authService;


    @PostMapping("/signup")
    public ApiResponse<UserResponse> signUp(
            @RequestBody SignUpRequest request
    ) {
        UserResponse response = userService.signUp(request);
        return ApiResponse.success(
                HttpStatus.CREATED,
                response
        );
    }
    @PreAuthorize("hasAnyRole('MASTER')")
    @PatchMapping("/{id}/status")
    public ApiResponse<UserStatusUpdateResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody UserStatusUpdateRequest request
    ) {
        UserStatusUpdateResponse response = userService.updateStatus(id, request.status());
        return ApiResponse.success(
                HttpStatus.OK,
                response
        );
    }


    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo(
            @AuthenticationPrincipal UUID userId
    ) {
        UserResponse response = userService.getMyInfo(userId
        );

        return ApiResponse.success(
                HttpStatus.OK,
                response
        );


    }
    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER')")
    @GetMapping
    public ApiResponse<Page<UserResponse>> getUsers(
            @PageableDefault(size = 10) Pageable pageable
    ) {

        Page<UserResponse> response = userService.getUsers(pageable);

        return ApiResponse.success(
                HttpStatus.OK,
                response
        );
    }

    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER')")
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(
            @PathVariable UUID id
    ) {

        UserResponse response = userService.getUser(id);

        return ApiResponse.success(
                HttpStatus.OK,
                response
        );
    }

    @PatchMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request
    ) {

        UserResponse response = userService.updateUser(id, request);

        return ApiResponse.success(
                HttpStatus.OK,
                response
        );
    }

    //회원탈퇴

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId,
            @AuthenticationPrincipal UserRole role

    ) {

        userService.deleteUser(id, userId, role);

        return ApiResponse.success(
                HttpStatus.OK,
                null
        );
    }

    //로그인
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response = authService.login(request);

        return ApiResponse.success(
                HttpStatus.OK,
                response
        );
    }

    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        RefreshTokenResponse response =authService.refresh(request.refreshToken());

        return ApiResponse.success(
                HttpStatus.OK,
                response

        );
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal UUID userId,
                       @RequestHeader("Authorization") String authorization) {

        authService.logout(userId,authorization);

        return ApiResponse.success(
                HttpStatus.OK
        );
    }
}
