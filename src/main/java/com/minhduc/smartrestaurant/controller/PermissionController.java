package com.minhduc.smartrestaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhduc.smartrestaurant.domain.Permission;
import com.minhduc.smartrestaurant.service.PermissionService;
import com.minhduc.smartrestaurant.util.annotation.ApiMessage;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> createNewPermission(@Valid @RequestBody Permission requestPermission)
            throws IdInvalidException {
        // check module, apiPath, method exist
        boolean exist = this.permissionService.isPermissionExist(requestPermission);
        if (exist) {
            throw new IdInvalidException("Permission đã tồn tại");
        }

        Permission createPermission = this.permissionService.handleCreatePermission(requestPermission);
        return ResponseEntity.status(HttpStatus.CREATED).body(createPermission);
    }

}