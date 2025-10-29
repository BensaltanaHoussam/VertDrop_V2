package com.vertdrop_v2;

import com.vertdrop_v2.entity.*;
import com.vertdrop_v2.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * This component runs on application startup and inserts initial data into the database.
 * It's useful for development and testing purposes.
 */
@Component
public class DataInitializerRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializerRunner.class);

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

    @Override
    @Transactional // Use @Transactional to ensure all operations are part of a single transaction
    public void run(String... args) throws Exception {
        logger.info("--- Starting Database Initialization ---");

        // Step 1: Check if data already exists to avoid re-inserting on every restart
        if (colisRepository.count() > 0) {
            logger.info("Database already contains data. Skipping initialization.");
            return;
        }

        // Step 2: Create and save prerequisite entities
        logger.info("Creating prerequisite entities...");

        Zone zone = new Zone();
        zone.setNom("Centre Ville");
        zone.setCodePostal("20000");
        zoneRepository.save(zone);

        ClientExpediteur sender = new ClientExpediteur();
        sender.setNom("Bensaltana");
        sender.setPrenom("Houssam");
        sender.setEmail("houssam.bensaltana@example.com");
        clientExpediteurRepository.save(sender);

        Destinataire recipient = new Destinataire();
        recipient.setNom("Test");
        recipient.setPrenom("User");
        recipient.setEmail("test.user@example.com");
        destinataireRepository.save(recipient);

        logger.info("Prerequisite entities saved successfully.");

        // Step 3: Create and save the main Colis entity
        logger.info("Creating a test Colis...");
        Colis newColis = new Colis();
        newColis.setDescription("Un colis de test contenant des livres");
        newColis.setPoids(new BigDecimal("1.5"));
        newColis.setStatut(StatutColis.CREE);
        newColis.setPriorite(1);
        newColis.setClientExpediteur(sender);
        newColis.setDestinataire(recipient);
        newColis.setZone(zone);
        newColis.setVilleDestination("Casablanca");

        Colis savedColis = colisRepository.save(newColis);
        logger.info("Colis saved successfully with ID: {}", savedColis.getId());

        // Step 4: Test a custom query method
        logger.info("Testing custom repository method 'findByStatut'...");
        List<Colis> foundColisList = colisRepository.findByStatut(StatutColis.CREE);

        if (!foundColisList.isEmpty()) {
            logger.info("SUCCESS: Found {} colis with status CREE. Description of first colis: '{}'",
                    foundColisList.size(), foundColisList.get(0).getDescription());
        } else {
            logger.error("FAILURE: Could not find the colis that was just saved.");
        }

        logger.info("--- Database Initialization Finished ---");


    }
}