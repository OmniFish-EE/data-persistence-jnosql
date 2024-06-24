/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ee.omnifish.jnosql.jakartapersistence.communication;


import jakarta.persistence.Persistence;
import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;
import org.eclipse.jnosql.communication.semistructured.DatabaseManagerFactory;

/**
 *
 * @author Ondro Mihalyi
 */
public class PersistenceDbConfiguration implements DatabaseConfiguration {

    @Override
    public DatabaseManagerFactory apply(Settings t) {
        return new PersistenceManagerFactory(Persistence.createEntityManagerFactory("testPersistenceUnit"));
    }

}
