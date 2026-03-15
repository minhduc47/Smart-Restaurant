package com.minhduc.smartrestaurant.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.Permission;
import com.minhduc.smartrestaurant.domain.Role;
import com.minhduc.smartrestaurant.domain.request.ReqRoleDTO;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO.Meta;
import com.minhduc.smartrestaurant.repository.PermissionRepository;
import com.minhduc.smartrestaurant.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role handleCreateRole(ReqRoleDTO requestRole) {
        Role role = new Role();
        role.setName(requestRole.getName());
        role.setDescription(requestRole.getDescription());
        role.setActive(requestRole.isActive());
        role.setPermissions(resolvePermissionsByIds(requestRole.getPermissionIds()));

        return this.roleRepository.save(role);
    }

    public Role fetchRoleById(long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isPresent()) {
            return roleOptional.get();
        }

        return null;
    }

    public Role updateRole(ReqRoleDTO requestRole, Role currentRole) {
        currentRole.setName(requestRole.getName());
        currentRole.setDescription(requestRole.getDescription());
        currentRole.setActive(requestRole.isActive());
        currentRole.setPermissions(resolvePermissionsByIds(requestRole.getPermissionIds()));

        currentRole = this.roleRepository.save(currentRole);

        return currentRole;
    }

    private List<Permission> resolvePermissionsByIds(List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new ArrayList<>();
        }
        return this.permissionRepository.findByIdIn(permissionIds);
    }

    public ResultPaginationDTO fetchAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getTotalElements());

        result.setMeta(meta);
        result.setResult(pageRole.getContent());

        return result;
    }

    public void deleteRole(long id) {
        this.roleRepository.deleteById(id);
    }

    public Role findByName(String name) {
        return this.roleRepository.findByName(name);
    }
}