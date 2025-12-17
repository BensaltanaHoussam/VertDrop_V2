package com.vertdrop_v2.config;

import com.vertdrop_v2.entity.*;
import com.vertdrop_v2.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@Order(2)
public class BusinessDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BusinessDataInitializer. class);

    @Autowired
    private ColisRepository colisRepository;
    @Autowired
    private ClientExpediteurRepository clientExpediteurRepository;
    @Autowired
    private DestinataireRepository destinataireRepository;
    @Autowired
    private ZoneRepository zoneRepository;
    @Autowired
    private LivreurRepository livreurRepository;
    @Autowired
    private UserRepository userRepository;  // NEW

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("🚚 --- Starting Business Data Initialization ---");

        if (colisRepository.count() > 0) {
            logger. info("ℹ️  Business data already exists. Skipping initialization.");
            return;
        }

        logger.info("📦 Creating prerequisite entities...");

        // Create Zone
        Zone zone = new Zone();
        zone.setNom("Centre Ville");
        zone.setCodePostal("20000");
        zoneRepository.save(zone);
        logger.info("✅ Zone created: {}", zone.getNom());

        // Link existing test users to business entities
        User clientUser = userRepository.findByUsername("client1").orElse(null);
        User livreurUser = userRepository.findByUsername("livreur1").orElse(null);

        // Create ClientExpediteur linked to client1 user
        ClientExpediteur sender = new ClientExpediteur();
        sender.setNom("Bensaltana");
        sender.setPrenom("Houssam");
        sender.setEmail("houssam. bensaltana@example.com");
        sender.setTelephone("+212 6 12 34 56 78");
        sender.setAdresse("123 Rue Mohammed V, Casablanca");
        if (clientUser != null) {
            sender.setUser(clientUser);
            logger.info("🔗 Linking ClientExpediteur to user: client1");
        }
        clientExpediteurRepository. save(sender);
        logger.info("✅ ClientExpediteur created:  {} {}", sender.getPrenom(), sender.getNom());

        // Create Livreur linked to livreur1 user
        Livreur livreur = new Livreur();
        livreur.setNom("Alami");
        livreur.setPrenom("Youssef");
        livreur.setTelephone("+212 6 98 76 54 32");
        livreur.setVehicule("Moto Yamaha 125");
        livreur.setZoneAssignee(zone);
        if (livreurUser != null) {
            livreur.setUser(livreurUser);
            logger.info("🔗 Linking Livreur to user: livreur1");
        }
        livreurRepository.save(livreur);
        logger.info("✅ Livreur created: {} {}", livreur.getPrenom(), livreur.getNom());

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
        newColis.setLivreur(livreur);  // Assign to livreur
        newColis.setVilleDestination("Casablanca");

        Colis savedColis = colisRepository.save(newColis);
        logger.info("✅ Colis saved with ID: {} (assigned to livreur: {})",
                savedColis.getId(), livreur.getPrenom());

        // Test query
        logger.info("🔍 Testing custom repository method 'findByStatut'...");
        List<Colis> foundColisList = colisRepository.findByStatut(StatutColis. CREE);

        if (! foundColisList.isEmpty()) {
            logger.info("✅ SUCCESS: Found {} colis with status CREE", foundColisList. size());
        } else {
            logger.error("❌ FAILURE: Could not find the colis that was just saved.");
        }

        logger.info("🎉 --- Business Data Initialization Finished ---");
        logger.info("📝 Test accounts ready:");
        logger.info("   • client1/client123 → ClientExpediteur: {} {}", sender.getPrenom(), sender.getNom());
        logger.info("   • livreur1/livreur123 → Livreur: {} {}", livreur.getPrenom(), livreur.getNom());
    }
}