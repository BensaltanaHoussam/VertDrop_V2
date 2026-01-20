package com.vertdrop_v2.config;

import com.vertdrop_v2.entity.Permission;
import com.vertdrop_v2.entity.Role;
import com.vertdrop_v2.repository.PermissionRepository;
import com.vertdrop_v2.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@Order(3) // Run after DataInitializer (1) and BusinessDataInitializer (2)
public class PermissionInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PermissionInitializer.class);

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public PermissionInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("🔐 --- Starting Permission Initialization ---");

        if (permissionRepository.count() > 0) {
            logger.info("ℹ️ Permissions already exist. Skipping initialization.");
            return;
        }

        // Create permissions
        List<Permission> permissions = Arrays.asList(
                createPermission("COLIS_CREATE", "Create new colis"),
                createPermission("COLIS_READ", "View colis"),
                createPermission("COLIS_UPDATE", "Update colis"),
                createPermission("COLIS_DELETE", "Delete colis"),
                createPermission("COLIS_STATUS_UPDATE", "Update colis status"),

                createPermission("ZONE_MANAGE", "Manage zones"),
                createPermission("LIVREUR_MANAGE", "Manage livreurs"),
                createPermission("CLIENT_MANAGE", "Manage clients"),

                createPermission("STATS_VIEW", "View statistics"),
                createPermission("ADMIN_PANEL", "Access admin panel"));

        permissionRepository.saveAll(permissions);
        logger.info("✅ Created {} permissions", permissions.size());

        // Assign permissions to roles
        assignPermissionsToRoles();

        logger.info("🎉 --- Permission Initialization Finished ---");
    }

    private Permission createPermission(String name, String description) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        return permission;
    }

    private void assignPermissionsToRoles() {
        Role managerRole = roleRepository.findByName("ROLE_MANAGER")
                .orElseThrow(() -> new RuntimeException("ROLE_MANAGER not found"));
        Role livreurRole = roleRepository.findByName("ROLE_LIVREUR")
                .orElseThrow(() -> new RuntimeException("ROLE_LIVREUR not found"));
        Role clientRole = roleRepository.findByName("ROLE_CLIENT")
                .orElseThrow(() -> new RuntimeException("ROLE_CLIENT not found"));

        // MANAGER: All permissions
        managerRole.getPermissions().addAll(permissionRepository.findAll());
        roleRepository.save(managerRole);
        logger.info("✅ Assigned all permissions to ROLE_MANAGER");

        // LIVREUR: Read colis, update status
        livreurRole.getPermissions().add(findPermission("COLIS_READ"));
        livreurRole.getPermissions().add(findPermission("COLIS_STATUS_UPDATE"));
        roleRepository.save(livreurRole);
        logger.info("✅ Assigned permissions to ROLE_LIVREUR");

        // CLIENT: Create colis, read own colis
        clientRole.getPermissions().add(findPermission("COLIS_CREATE"));
        clientRole.getPermissions().add(findPermission("COLIS_READ"));
        roleRepository.save(clientRole);
        logger.info("✅ Assigned permissions to ROLE_CLIENT");
    }

    private Permission findPermission(String name) {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + name));
    }
}