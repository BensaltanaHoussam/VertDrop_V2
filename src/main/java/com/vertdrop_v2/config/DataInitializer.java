package com.vertdrop_v2.config;

import com.vertdrop_v2.entity. AuthProvider;
import com.vertdrop_v2.entity.Role;
import com.vertdrop_v2.entity.User;
import com.vertdrop_v2.repository.RoleRepository;
import com.vertdrop_v2.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java. util.Optional;
import java. util.Set;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this. userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System. out.println("🔧 DataInitializer (Order 1): Initializing roles and users...");

        // Create roles if they don't exist
        Role roleManager = createRoleIfNotExists("ROLE_MANAGER");
        Role roleLivreur = createRoleIfNotExists("ROLE_LIVREUR");
        Role roleClient = createRoleIfNotExists("ROLE_CLIENT");

        System.out.println("✅ Roles created/verified:  ROLE_MANAGER, ROLE_LIVREUR, ROLE_CLIENT");

        // Create admin user if doesn't exist
        Optional<User> adminOpt = userRepository.findByUsername("admin");
        if (adminOpt. isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin. setEmail("admin@vertdrop.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setProvider(AuthProvider.LOCAL);
            admin. setFirstName("Admin");
            admin.setLastName("User");
            admin.setEnabled(true);
            Set<Role> roles = new HashSet<>();
            roles.add(roleManager);
            admin.setRoles(roles);
            userRepository.save(admin);
            System.out.println("✅ Admin user created:");
            System. out.println("   👤 Username: admin");
            System. out.println("   📧 Email:  admin@vertdrop.com");
            System.out.println("   🔑 Password: admin123");
            System.out. println("   🎭 Role: ROLE_MANAGER");
            System.out.println("   🔐 Provider: LOCAL");
        } else {
            System.out. println("ℹ️  Admin user already exists");
        }

        // Create test livreur user if doesn't exist
        Optional<User> livreurOpt = userRepository.findByUsername("livreur1");
        if (livreurOpt.isEmpty()) {
            User livreur = new User();
            livreur.setUsername("livreur1");
            livreur. setEmail("livreur1@vertdrop.com");
            livreur.setPassword(passwordEncoder.encode("livreur123"));
            livreur.setProvider(AuthProvider.LOCAL);
            livreur.setFirstName("Youssef");
            livreur.setLastName("Alami");
            livreur.setEnabled(true);
            Set<Role> roles = new HashSet<>();
            roles.add(roleLivreur);
            livreur.setRoles(roles);
            userRepository.save(livreur);
            System. out.println("✅ Livreur user created:");
            System.out.println("   👤 Username:  livreur1");
            System.out.println("   📧 Email: livreur1@vertdrop.com");
            System.out.println("   🔑 Password: livreur123");
            System.out. println("   🎭 Role: ROLE_LIVREUR");
            System.out.println("   🔐 Provider:  LOCAL");
        } else {
            System.out.println("ℹ️  Livreur user already exists");
        }

        // Create test client user if doesn't exist
        Optional<User> clientOpt = userRepository. findByUsername("client1");
        if (clientOpt.isEmpty()) {
            User client = new User();
            client.setUsername("client1");
            client. setEmail("client1@vertdrop.com");
            client. setPassword(passwordEncoder.encode("client123"));
            client.setProvider(AuthProvider.LOCAL);
            client.setFirstName("Houssam");
            client.setLastName("Bensaltana");
            client.setEnabled(true);
            Set<Role> roles = new HashSet<>();
            roles.add(roleClient);
            client.setRoles(roles);
            userRepository.save(client);
            System.out. println("✅ Client user created:");
            System.out.println("   👤 Username: client1");
            System.out. println("   📧 Email:  client1@vertdrop.com");
            System.out.println("   🔑 Password:  client123");
            System.out.println("   🎭 Role: ROLE_CLIENT");
            System.out.println("   🔐 Provider: LOCAL");
        } else {
            System.out.println("ℹ️  Client user already exists");
        }

        System.out. println("🎉 DataInitializer (Order 1) completed successfully!\n");
    }

    private Role createRoleIfNotExists(String roleName) {
        return roleRepository. findByName(roleName)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return roleRepository.save(role);
                });
    }
}