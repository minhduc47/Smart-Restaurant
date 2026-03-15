package com.minhduc.smartrestaurant.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countPermissions = this.permissionRepository.count();
        long countUsers = this.userRepository.count();
        if (countPermissions == 0) {
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
            arr.add(new Permission("Update a order", "/api/v1/orders", "PUT", "ORDERS"));
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
            arr.add(new Permission("Update a table", "/api/v1/tables", "PUT", "TABLES"));
            arr.add(new Permission("Delete a table", "/api/v1/tables/{id}", "DELETE", "TABLES"));
            arr.add(new Permission("Get a table by id", "/api/v1/tables/{id}", "GET", "TABLES"));
            arr.add(new Permission("Get tables with pagination", "/api/v1/tables", "GET", "TABLES"));
            arr.add(new Permission("Download a file", "/api/v1/files", "POST", "FILES"));
            arr.add(new Permission("Upload a file", "/api/v1/files", "GET", "FILES"));
            this.permissionRepository.saveAll(arr);
        }

        List<Permission> allPermissions = this.permissionRepository.findAll();

        Role superAdminRole = this.roleRepository.findByName("ADMIN");
        if (superAdminRole == null) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Admin thì full permissions");
            adminRole.setActive(true);
            adminRole.setPermissions(allPermissions);
            superAdminRole = this.roleRepository.save(adminRole);
        }

        Role userRole = this.roleRepository.findByName("USER");
        if (userRole == null) {
            Map<String, Permission> permissionByMethodAndPath = allPermissions.stream()
                    .collect(Collectors.toMap(
                            permission -> permission.getMethod() + ":" + permission.getApiPath(),
                            permission -> permission,
                            (existing, replacement) -> existing));

            Set<String> userPermissionKeys = Set.of(
                    "POST:/api/v1/orders",
                    "GET:/api/v1/dishes",
                    "GET:/api/v1/categories",
                    "GET:/api/v1/dishes/{id}",
                    "GET:/api/v1/categories/{id}");

            List<Permission> userPermissions = userPermissionKeys.stream()
                    .map(permissionByMethodAndPath::get)
                    .filter(permission -> permission != null)
                    .collect(Collectors.toList());

            Role defaultUserRole = new Role();
            defaultUserRole.setName("USER");
            defaultUserRole.setDescription("Người dùng mặc định");
            defaultUserRole.setActive(true);
            defaultUserRole.setPermissions(userPermissions);
            this.roleRepository.save(defaultUserRole);
        }
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
        if (countPermissions > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            System.out.println(">>> END INIT DATABASE");
    }

}