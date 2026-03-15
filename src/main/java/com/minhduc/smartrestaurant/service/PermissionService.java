package com.minhduc.smartrestaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.Permission;
import com.minhduc.smartrestaurant.domain.Role;
import com.minhduc.smartrestaurant.domain.request.ReqCreatePermissionDTO;
import com.minhduc.smartrestaurant.domain.request.ReqUpdatePermissionDTO;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO.Meta;
import com.minhduc.smartrestaurant.repository.PermissionRepository;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(ReqCreatePermissionDTO permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(),
                permission.getApiPath(), permission.getMethod());
    }

    public Permission handleCreatePermission(ReqCreatePermissionDTO requestPermission) {
        Permission permission = new Permission();
        permission.setName(requestPermission.getName());
        permission.setApiPath(requestPermission.getApiPath());
        permission.setMethod(requestPermission.getMethod());
        permission.setModule(requestPermission.getModule());
        return this.permissionRepository.save(permission);
    }

    public Permission fetchPermissionById(long id) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        if (permissionOptional.isPresent()) {
            return permissionOptional.get();
        }
        return null;
    }

    public Permission handleUpdatePermission(ReqUpdatePermissionDTO requestPermission, Permission currentPermission) {
        // set name, apiPath, method, module
        currentPermission.setName(requestPermission.getName());
        currentPermission.setApiPath(requestPermission.getApiPath());
        currentPermission.setMethod(requestPermission.getMethod());
        currentPermission.setModule(requestPermission.getModule());

        // update
        currentPermission = this.permissionRepository.save(currentPermission);
        return currentPermission;
    }

    public ResultPaginationDTO fetchAllPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pagePermission.getTotalPages());
        meta.setTotal(pagePermission.getTotalElements());

        result.setMeta(meta);
        result.setResult(pagePermission.getContent());

        return result;
    }

    public void deletePermission(long id) {
        // get permission by id
        Permission currentPermission = this.fetchPermissionById(id);

        // check role exist
        if (currentPermission.getRoles() != null) {
            List<Role> roles = currentPermission.getRoles();
            for (int i = 0; i < roles.size(); i++) {
                // Lấy từng Role trong danh sách
                Role role = roles.get(i);
                // Xóa currentPermission khỏi danh sách permissions của Role
                role.getPermissions().remove(currentPermission);
            }
        }

        // delete permission
        this.permissionRepository.delete(currentPermission);
    }

    public boolean isSameName(ReqUpdatePermissionDTO p) {
        Permission permissionDB = this.fetchPermissionById(p.getId());
        if (permissionDB != null) {
            if (permissionDB.getName().equals(p.getName()))
                return true;
        }
        return false;
    }

    public boolean isPermissionExistForUpdate(ReqUpdatePermissionDTO permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(),
                permission.getApiPath(), permission.getMethod());
    }
}