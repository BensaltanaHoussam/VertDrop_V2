package com.vertdrop_v2.repository;

import com.vertdrop_v2.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
}
