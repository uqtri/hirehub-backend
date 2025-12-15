package org.example.hirehub.controller;

import org.example.hirehub.dto.fcmToken.SaveFcmTokenRequest;
import org.example.hirehub.service.FcmTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fcm-tokens")
public class FcmTokenController {
    private final FcmTokenService fcmTokenService;

    public FcmTokenController(FcmTokenService fcmTokenService) {
        this.fcmTokenService = fcmTokenService;
    }

    @PostMapping("")
    public ResponseEntity<Void> saveToken(
            @RequestBody SaveFcmTokenRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        fcmTokenService.saveToken(request.getUserId(), request.getToken());
        return ResponseEntity.ok().build();
    }

}
