package com.clinic.booking.service;

import com.clinic.booking.dto.role.*;
import com.clinic.booking.entity.Permission;
import com.clinic.booking.entity.Role;
import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;
import com.clinic.booking.repository.PermissionRepository;
import com.clinic.booking.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream().map(this::mapRoleToDTO).collect(Collectors.toList());
    }

    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream().map(this::mapPermissionToDTO).collect(Collectors.toList());
    }

    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new AppException(ErrorCode.INVALID_ACTION);
        }

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
            role.setPermissions(new HashSet<>(permissions));
        } else {
            role.setPermissions(new HashSet<>());
        }

        role = roleRepository.save(role);
        return mapRoleToDTO(role);
    }

    @Transactional
    public RoleResponse updateRolePermissions(Long roleId, UpdateRolePermissionsRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_ACTION));

        if (request.getPermissionIds() != null) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
            role.setPermissions(new HashSet<>(permissions));
        } else {
            role.setPermissions(new HashSet<>());
        }

        role = roleRepository.save(role);
        return mapRoleToDTO(role);
    }

    @Transactional
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        if (permissionRepository.findByName(request.getName()).isPresent()) {
            throw new AppException(ErrorCode.INVALID_ACTION);
        }

        Permission permission = Permission.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        permission = permissionRepository.save(permission);
        return mapPermissionToDTO(permission);
    }

    private RoleResponse mapRoleToDTO(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(role.getPermissions() != null ? 
                    role.getPermissions().stream().map(this::mapPermissionToDTO).collect(Collectors.toList()) 
                    : List.of())
                .build();
    }

    private PermissionResponse mapPermissionToDTO(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}
