/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package ee.omnifish.jnosql.jakartapersistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.List;

@Entity(name = "Person")
public class Person {

  @Id
  private long id;

  @Column
  private String name;

  @Column
  private List<String> phones;
}

