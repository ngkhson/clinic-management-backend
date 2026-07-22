package com.clinic.booking.controller;

import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.role.*;
import com.clinic.booking.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminRoleController {

    private final RoleService roleService;

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.success(roleService.getAllRoles());
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
    public ApiResponse<List<PermissionResponse>> getAllPermissions() {
        return ApiResponse.success(roleService.getAllPermissions());
    }

    @PostMapping("/roles")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
    public ApiResponse<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return ApiResponse.success(roleService.createRole(request));
    }

    @PutMapping("/roles/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
    public ApiResponse<RoleResponse> updateRolePermissions(@PathVariable Long roleId, @RequestBody UpdateRolePermissionsRequest request) {
        return ApiResponse.success(roleService.updateRolePermissions(roleId, request));
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
    public ApiResponse<PermissionResponse> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        return ApiResponse.success(roleService.createPermission(request));
    }
}
