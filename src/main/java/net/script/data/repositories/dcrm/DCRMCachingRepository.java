package net.script.data.repositories.dcrm;

import net.script.data.entities.DCResMeasurement;
import net.script.data.repositories.CachingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DCRMCachingRepository extends CachingRepository<DCResMeasurement> {
    @Autowired
    public DCRMCachingRepository(DCResMeasurementRepository dcResMeasurementRepository) {
        super(dcResMeasurementRepository);
    }

    @Override
    public Class<DCResMeasurement> getItemClass() {
        return DCResMeasurement.class;
    }
}
