package com.iftrue.user.presentation;

import com.iftrue.user.application.InternalUserService;
import com.iftrue.user.global.response.ApiResponse;
import com.iftrue.user.presentation.response.InternalUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/users")
public class InternalUserController {

    private final InternalUserService internalUserService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<InternalUserResponse>> getUser(
            @PathVariable UUID userId
    ) {
        InternalUserResponse response = internalUserService.getUser(userId);
        return ResponseEntity.ok()
                .body(ApiResponse.success(HttpStatus.OK,response));
    }

}
