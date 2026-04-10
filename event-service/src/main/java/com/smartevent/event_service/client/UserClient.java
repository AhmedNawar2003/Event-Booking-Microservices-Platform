package com.smartevent.event_service.client;

import com.smartevent.event_service.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/me")
    UserResponse getCurrentUser(@RequestHeader("Authorization") String token);
}
