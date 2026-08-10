package com.iftrue.user.presentation;

//import com.iftrue.user.application.AuthService;
import com.iftrue.user.application.UserService;
import com.iftrue.user.domain.User;
import com.iftrue.user.domain.UserRole;
import com.iftrue.user.global.exception.ErrorCode;
import com.iftrue.user.global.response.ApiResponse;
//import com.iftrue.user.global.security.UserDetailsImpl;
import com.iftrue.user.presentation.request.LoginRequest;
import com.iftrue.user.presentation.request.SignUpRequest;
import com.iftrue.user.presentation.request.UserStatusUpdateRequest;
import com.iftrue.user.presentation.request.UserUpdateRequest;
import com.iftrue.user.presentation.response.LoginResponse;
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
//    private final AuthService authService;


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
//    @PreAuthorize("hasAnyRole('MASTER')")
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

    // TODO: Gateway JWT 인증 구현 후
    // X-User-Id → requestUserId
    // X-User-Role → requestRole
    // 로 전달받아 권한 검증

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo(
            @RequestHeader("X-User-Id") UUID userId
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        System.out.println(userId);
        UserResponse response = userService.getMyInfo(userId
//                userDetails.getUserId()
        );

        return ApiResponse.success(
                HttpStatus.OK,
                response
        );


    }
//    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER')")
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

//    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER')")
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

        UserResponse response =
                userService.updateUser(id, request);

        return ApiResponse.success(
                HttpStatus.OK,
                response
        );
    }

    //회원탈퇴

    // TODO: Gateway JWT 인증 구현 후
    // X-User-Id → requestUserId
    // X-User-Role → requestRole
    // 로 전달받아 권한 검증
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
             @RequestHeader("X-User-Role") UserRole role

//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        userService.deleteUser(id,
//                userDetails.getUserId(),
//                userDetails.getRole()
                id,
                null
                );

        return ApiResponse.success(
                HttpStatus.OK,
                null
        );
    }

    //로그인 //인증 올린 후 활성화
//    @PostMapping("/login")
//    public ApiResponse<LoginResponse> login(
//            @Valid @RequestBody LoginRequest request
//    ) {
//        LoginResponse response = authService.login(request);
//
//        return ApiResponse.success(
//                HttpStatus.OK,
//                response
//        );
//    }
}
