package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.CreateOwnerRequest;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.domain.model.UserModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserRestController {

    private final IUserHandler userHandler;

    @Operation(summary = "Add a new object")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "user created", content = @Content),
            @ApiResponse(responseCode = "409", description = "user already exists", content = @Content)
    })

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/owner")
    public ResponseEntity<Void> saveOwner(@Valid @RequestBody CreateOwnerRequest createOwnerRequest) {
        userHandler.saveUser(createOwnerRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/{id}")
    public ResponseEntity<UserModel> findById(@PathVariable Long id) {
        UserModel user = userHandler.getUserId(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

}