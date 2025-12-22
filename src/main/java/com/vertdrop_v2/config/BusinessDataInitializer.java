package com.vertdrop_v2.config;

import com.vertdrop_v2.entity.*;
import com.vertdrop_v2.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util. List;

@Component
@Order(2)
public class BusinessDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BusinessDataInitializer.class);

    private final ColisRepository colisRepository;
    private final ClientExpediteurRepository clientExpediteurRepository;
    private final DestinataireRepository destinataireRepository;
    private final ZoneRepository zoneRepository;
    private final LivreurRepository livreurRepository;
    private final UserRepository userRepository;

    public BusinessDataInitializer(
            ColisRepository colisRepository,
            ClientExpediteurRepository clientExpediteurRepository,
            DestinataireRepository destinataireRepository,
            ZoneRepository zoneRepository,
            LivreurRepository livreurRepository,
            UserRepository userRepository) {
        this.colisRepository = colisRepository;
        this.clientExpediteurRepository = clientExpediteurRepository;
        this.destinataireRepository = destinataireRepository;
        this.zoneRepository = zoneRepository;
        this.livreurRepository = livreurRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger. info("🚚 --- Starting Business Data Initialization ---");

        if (colisRepository.count() > 0) {
            logger. info("ℹ️  Business data already exists. Skipping initialization.");

            // BUT still check if linking is needed
            checkAndFixLinking();
            return;
        }

        logger.info("📦 Creating business entities...");

        // Create Zone
        Zone zone = new Zone();
        zone.setNom("Centre Ville");
        zone.setCodePostal("20000");
        zoneRepository.save(zone);
        logger.info("✅ Zone created: {}", zone.getNom());

        // Get users (they should exist from DataInitializer Order 1)
        User clientUser = userRepository.findByUsername("client1")
                .orElseThrow(() -> new RuntimeException("❌ User 'client1' not found!  DataInitializer must run first! "));
        User livreurUser = userRepository.findByUsername("livreur1")
                .orElseThrow(() -> new RuntimeException("❌ User 'livreur1' not found! DataInitializer must run first!"));

        logger.info("✅ Found client1 user: ID = {}", clientUser.getId());
        logger.info("✅ Found livreur1 user: ID = {}", livreurUser.getId());

        // Create ClientExpediteur LINKED to client1
        ClientExpediteur sender = new ClientExpediteur();
        sender.setNom("Bensaltana");
        sender.setPrenom("Houssam");
        sender.setEmail("houssam.bensaltana@example.com");
        sender.setTelephone("+212 6 12 34 56 78");
        sender.setAdresse("123 Rue Mohammed V, Casablanca");
        sender.setUser(clientUser);  // ← LINK HERE
        clientExpediteurRepository.save(sender);
        logger.info("✅ ClientExpediteur created: {} {} (linked to user: {})",
                sender.getPrenom(), sender.getNom(), clientUser.getUsername());

        // Create Livreur LINKED to livreur1
        Livreur livreur = new Livreur();
        livreur.setNom("Alami");
        livreur.setPrenom("Youssef");
        livreur.setTelephone("+212 6 98 76 54 32");
        livreur.setVehicule("Moto Yamaha 125");
        livreur.setZoneAssignee(zone);
        livreur.setUser(livreurUser);  // ← LINK HERE
        livreurRepository.save(livreur);
        logger.info("✅ Livreur created:  {} {} (linked to user: {})",
                livreur.getPrenom(), livreur.getNom(), livreurUser.getUsername());

        // Create Destinataire
        Destinataire recipient = new Destinataire();
        recipient.setNom("Test");
        recipient.setPrenom("User");
        recipient.setEmail("test. user@example.com");
        recipient.setTelephone("+212 6 11 22 33 44");
        recipient.setAdresse("456 Avenue Hassan II, Rabat");
        destinataireRepository.save(recipient);
        logger.info("✅ Destinataire created: {} {}", recipient.getPrenom(), recipient.getNom());

        // Create Colis assigned to the livreur
        logger.info("📦 Creating test Colis...");
        Colis newColis = new Colis();
        newColis.setDescription("Un colis de test contenant des livres");
        newColis.setPoids(new BigDecimal("1.5"));
        newColis.setStatut(StatutColis.CREE);
        newColis.setPriorite(1);
        newColis.setClientExpediteur(sender);
        newColis.setDestinataire(recipient);
        newColis.setZone(zone);
        newColis.setLivreur(livreur);
        newColis.setVilleDestination("Casablanca");

        Colis savedColis = colisRepository.save(newColis);
        logger.info("✅ Colis saved with ID: {} (assigned to livreur: {})",
                savedColis.getId(), livreur.getPrenom());

        // Verify linking
        verifyLinking();

        logger.info("🎉 --- Business Data Initialization Finished ---");
    }

    private void checkAndFixLinking() {
        logger.info("🔍 Checking user-entity linking...");

        User clientUser = userRepository.findByUsername("client1").orElse(null);
        User livreurUser = userRepository.findByUsername("livreur1").orElse(null);

        if (clientUser != null) {
            ClientExpediteur client = clientExpediteurRepository. findByUser(clientUser).orElse(null);
            if (client == null) {
                logger.warn("⚠️ client1 user exists but not linked to ClientExpediteur!");
                // Try to find by email and link
                ClientExpediteur existing = clientExpediteurRepository
                        .findAll().stream()
                        .filter(c -> "houssam.bensaltana@example.com".equals(c.getEmail()))
                        . findFirst()
                        .orElse(null);
                if (existing != null && existing.getUser() == null) {
                    existing.setUser(clientUser);
                    clientExpediteurRepository.save(existing);
                    logger.info("✅ Linked existing ClientExpediteur to client1 user");
                }
            }
        }

        if (livreurUser != null) {
            Livreur livreur = livreurRepository.findByUser(livreurUser).orElse(null);
            if (livreur == null) {
                logger.warn("⚠️ livreur1 user exists but not linked to Livreur!");
                // Try to find by name and link
                Livreur existing = livreurRepository
                        .findAll().stream()
                        .filter(l -> "Alami".equals(l.getNom()) && "Youssef".equals(l.getPrenom()))
                        .findFirst()
                        .orElse(null);
                if (existing != null && existing.getUser() == null) {
                    existing.setUser(livreurUser);
                    livreurRepository.save(existing);
                    logger.info("✅ Linked existing Livreur to livreur1 user");
                }
            }
        }
    }

    private void verifyLinking() {
        logger.info("🔍 Verifying user-entity linking...");

        User clientUser = userRepository.findByUsername("client1").orElse(null);
        if (clientUser != null) {
            ClientExpediteur client = clientExpediteurRepository.findByUser(clientUser).orElse(null);
            if (client != null) {
                logger.info("✅ client1 → ClientExpediteur: {} {} (ID: {})",
                        client.getPrenom(), client.getNom(), client.getId());
            } else {
                logger. error("❌ client1 user NOT linked to any ClientExpediteur!");
            }
        }

        User livreurUser = userRepository.findByUsername("livreur1").orElse(null);
        if (livreurUser != null) {
            Livreur livreur = livreurRepository.findByUser(livreurUser).orElse(null);
            if (livreur != null) {
                logger.info("✅ livreur1 → Livreur: {} {} (ID:  {})",
                        livreur.getPrenom(), livreur.getNom(), livreur.getId());
            } else {
                logger.error("❌ livreur1 user NOT linked to any Livreur!");
            }
        }
    }
}