/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ee.omnifish.jnosql.jakartapersistence;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository
public interface PersonRepository extends CrudRepository<Person, String> {
    long countAll();
}

