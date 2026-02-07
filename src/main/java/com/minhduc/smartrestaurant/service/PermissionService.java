package com.minhduc.smartrestaurant.service;

import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.Permission;
import com.minhduc.smartrestaurant.repository.PermissionRepository;

import jakarta.validation.Valid;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(),
                permission.getApiPath(), permission.getMethod());
    }

    public Permission handleCreatePermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }
}