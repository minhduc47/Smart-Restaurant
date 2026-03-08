package com.minhduc.smartrestaurant.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.minhduc.smartrestaurant.domain.Role;
import com.minhduc.smartrestaurant.repository.RoleRepository;

@Component
public class RoleDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleDataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        createRoleIfMissing("USER", "Default role for customer account");
        createRoleIfMissing("ADMIN", "Default role for admin account");
    }

    private void createRoleIfMissing(String name, String description) {
        if (this.roleRepository.existsByName(name)) {
            return;
        }

        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setActive(true);
        this.roleRepository.save(role);
    }
}
