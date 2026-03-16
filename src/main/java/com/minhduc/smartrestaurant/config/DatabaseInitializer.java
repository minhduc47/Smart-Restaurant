package com.minhduc.smartrestaurant.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhduc.smartrestaurant.domain.Permission;
import com.minhduc.smartrestaurant.domain.Role;
import com.minhduc.smartrestaurant.domain.User;
import com.minhduc.smartrestaurant.repository.PermissionRepository;
import com.minhduc.smartrestaurant.repository.RoleRepository;
import com.minhduc.smartrestaurant.repository.UserRepository;
import com.minhduc.smartrestaurant.util.constant.GenderEnum;

@Service
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countUsers = this.userRepository.count();
        ArrayList<Permission> arr = new ArrayList<>();
        arr.add(new Permission("Create a dish", "/api/v1/dishes", "POST", "DISHES"));
        arr.add(new Permission("Update a dish", "/api/v1/dishes", "PUT", "DISHES"));
        arr.add(new Permission("Delete a dish", "/api/v1/dishes/{id}", "DELETE", "DISHES"));
        arr.add(new Permission("Get a dish by id", "/api/v1/dishes/{id}", "GET", "DISHES"));
        arr.add(new Permission("Get dishes with pagination", "/api/v1/dishes", "GET", "DISHES"));
        arr.add(new Permission("Create a category", "/api/v1/categories", "POST", "CATEGORIES"));
        arr.add(new Permission("Update a category", "/api/v1/categories", "PUT", "CATEGORIES"));
        arr.add(new Permission("Delete a category", "/api/v1/categories/{id}", "DELETE", "CATEGORIES"));
        arr.add(new Permission("Get a category by id", "/api/v1/categories/{id}", "GET", "CATEGORIES"));
        arr.add(new Permission("Get categories with pagination", "/api/v1/categories", "GET", "CATEGORIES"));
        arr.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
        arr.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
        arr.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
        arr.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
        arr.add(new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS"));
        arr.add(new Permission("Create a order", "/api/v1/orders", "POST", "ORDERS"));
        arr.add(new Permission("Update a order", "/api/v1/orders/{id}", "PUT", "ORDERS"));
        arr.add(new Permission("Delete a order", "/api/v1/orders/{id}", "DELETE", "ORDERS"));
        arr.add(new Permission("Get a order by id", "/api/v1/orders/{id}", "GET", "ORDERS"));
        arr.add(new Permission("Get orders with pagination", "/api/v1/orders", "GET", "ORDERS"));
        arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
        arr.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
        arr.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
        arr.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
        arr.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES"));
        arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
        arr.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
        arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
        arr.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS"));
        arr.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));
        arr.add(new Permission("Create a table", "/api/v1/tables", "POST", "TABLES"));
        arr.add(new Permission("Update a table", "/api/v1/tables/{id}", "PUT", "TABLES"));
        arr.add(new Permission("Delete a table", "/api/v1/tables/{id}", "DELETE", "TABLES"));
        arr.add(new Permission("Get a table by id", "/api/v1/tables/{id}", "GET", "TABLES"));
        arr.add(new Permission("Get tables with pagination", "/api/v1/tables", "GET", "TABLES"));
        arr.add(new Permission("Download a file", "/api/v1/files", "POST", "FILES"));
        arr.add(new Permission("Upload a file", "/api/v1/files", "GET", "FILES"));
        arr.add(new Permission("Payment a order", "/api/v1/payments/checkout", "POST", "PAYMENTS"));

        List<Permission> dbPermissions = this.permissionRepository.findAll();
        Map<String, Permission> permissionByMethodAndPath = dbPermissions.stream()
                .collect(Collectors.toMap(
                        permission -> buildPermissionKey(permission.getMethod(), permission.getApiPath()),
                        permission -> permission,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));

        List<Permission> permissionsToCreate = new ArrayList<>();
        List<Permission> permissionsToUpdate = new ArrayList<>();
        for (Permission permission : arr) {
            String key = buildPermissionKey(permission.getMethod(), permission.getApiPath());
            Permission existingPermission = permissionByMethodAndPath.get(key);
            if (existingPermission == null) {
                permissionsToCreate.add(permission);
            } else {
                boolean needUpdate = false;
                if (!permission.getName().equals(existingPermission.getName())) {
                    existingPermission.setName(permission.getName());
                    needUpdate = true;
                }
                if (!permission.getModule().equals(existingPermission.getModule())) {
                    existingPermission.setModule(permission.getModule());
                    needUpdate = true;
                }
                if (needUpdate) {
                    permissionsToUpdate.add(existingPermission);
                }
            }
        }

        if (!permissionsToCreate.isEmpty()) {
            List<Permission> createdPermissions = this.permissionRepository.saveAll(permissionsToCreate);
            for (Permission createdPermission : createdPermissions) {
                permissionByMethodAndPath.put(
                        buildPermissionKey(createdPermission.getMethod(), createdPermission.getApiPath()),
                        createdPermission);
            }
        }

        if (!permissionsToUpdate.isEmpty()) {
            this.permissionRepository.saveAll(permissionsToUpdate);
        }

        List<Role> dbRoles = this.roleRepository.findAll();
        Map<String, Role> roleByName = dbRoles.stream()
                .collect(Collectors.toMap(Role::getName, role -> role, (existing, replacement) -> existing));

        Role superAdminRole = roleByName.get("ADMIN");
        if (superAdminRole == null) {
            superAdminRole = new Role();
            superAdminRole.setName("ADMIN");
            superAdminRole.setDescription("Admin thì full permissions");
            superAdminRole.setActive(true);
            superAdminRole.setPermissions(new ArrayList<>());
        }

        Map<Long, Permission> adminPermissionById = new LinkedHashMap<>();
        if (superAdminRole.getPermissions() != null) {
            for (Permission permission : superAdminRole.getPermissions()) {
                adminPermissionById.put(permission.getId(), permission);
            }
        }
        for (Permission permission : permissionByMethodAndPath.values()) {
            adminPermissionById.putIfAbsent(permission.getId(), permission);
        }
        superAdminRole.setPermissions(new ArrayList<>(adminPermissionById.values()));
        superAdminRole = this.roleRepository.save(superAdminRole);

        Role userRole = roleByName.get("USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("USER");
            userRole.setDescription("Người dùng mặc định");
            userRole.setActive(true);
            userRole.setPermissions(new ArrayList<>());
        }

        Set<String> userPermissionKeys = Set.of(
                "POST:/api/v1/orders",
                "GET:/api/v1/dishes",
                "GET:/api/v1/categories",
                "GET:/api/v1/dishes/{id}",
                "GET:/api/v1/categories/{id}",
                "POST:/api/v1/payments/checkout");

        Map<Long, Permission> userPermissionById = new LinkedHashMap<>();
        if (userRole.getPermissions() != null) {
            for (Permission permission : userRole.getPermissions()) {
                userPermissionById.put(permission.getId(), permission);
            }
        }
        for (String key : userPermissionKeys) {
            Permission requiredPermission = permissionByMethodAndPath.get(key);
            if (requiredPermission != null) {
                userPermissionById.putIfAbsent(requiredPermission.getId(), requiredPermission);
            }
        }
        userRole.setPermissions(new ArrayList<>(userPermissionById.values()));
        this.roleRepository.save(userRole);

        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setAddress("hn");
            adminUser.setAge(25);
            adminUser.setGender(GenderEnum.MALE);
            adminUser.setName("I'm super admin");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));
            if (superAdminRole != null) {
                adminUser.setRole(superAdminRole);
            }
            this.userRepository.save(adminUser);
        }
        System.out.println(">>> END INIT DATABASE");
    }

    private String buildPermissionKey(String method, String apiPath) {
        return method + ":" + apiPath;
    }

}