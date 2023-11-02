package com.example.planservice.presentation;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planservice.application.PlanService;
import com.example.planservice.presentation.dto.request.PlanCreateRequest;
import com.example.planservice.presentation.dto.response.PlanResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanController {
    private final PlanService planService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid PlanCreateRequest request,
                                       @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long createdId = planService.create(request, userId);
        return ResponseEntity.created(URI.create("/plans/" + createdId)).build();
    }

    @GetMapping("/{planId}")
    public ResponseEntity<PlanResponse> read(@PathVariable Long planId, @RequestAttribute Long userId) {
        return ResponseEntity.ok(planService.getTotalPlanResponse(planId));
    }

    @PutMapping("/invite/{planId}")
    public ResponseEntity<Long> invite(@PathVariable Long planId, @RequestAttribute Long userId) {
        Long memberOfPlanId = planService.inviteMember(planId, userId);
        return ResponseEntity.ok(memberOfPlanId);
    }

}
