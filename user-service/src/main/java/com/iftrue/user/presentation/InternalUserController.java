package com.iftrue.user.presentation;

import com.iftrue.user.application.InternalUserService;
import com.iftrue.user.presentation.response.InternalUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/users")
public class InternalUserController {

    private final InternalUserService internalUserService;

    @GetMapping("/{userId}")
    public ResponseEntity<InternalUserResponse> getUser(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                internalUserService.getUser(userId)
        );
    }

}
