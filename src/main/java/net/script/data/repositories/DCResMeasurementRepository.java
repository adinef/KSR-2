package net.script.data.repositories;

import net.script.data.entities.DCResMeasurement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DCResMeasurementRepository extends CrudRepository<DCResMeasurement, Long> {
}
