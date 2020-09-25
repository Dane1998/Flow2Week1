package facades;

import dtomapper.PersonDTO;
import entities.Address;
import utils.EMF_Creator;
import entities.Person;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sun.security.pkcs11.P11Util;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    private static Person p1;
    private static Person p2;
    

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
       emf = EMF_Creator.createEntityManagerFactoryForTest();
       facade = PersonFacade.getPersonFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        p1 = new Person("Hans", "Peder", "123123");
        p1.setAddress(new Address("Hansvej", "111", "Pederby"));
        p2 = new Person("Knud", "Erik", "987987");
        p2.setAddress(new Address("Knudvej", "222", "Knudby"));
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.persist(p1);
            em.persist(p2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    // TODO: Delete or change this method 
    @Test
    public void testAFacadeMethod() {
        assertEquals(2, facade.getPersonCount(), "Expects two rows in the database");
    }
    
    @Test 
    public void testAddPerson() throws Exception{
        String fName = "Daniel";
        String lName = "Poulsen";
        String phone = "123123";
        String street = "Ligustervænget";
        String zip = "375";
        String city = "Little Whining";
        
        PersonDTO expResult = new PersonDTO(fName, lName, phone, street, zip, city);
        expResult.setId(expResult.getId());
    }

    @Test
    public void testDeletePerson() throws Exception{
        long id = p1.getId();
        EntityManagerFactory emf = null;
        PersonFacade instance = PersonFacade.getPersonFacade(emf);
        PersonDTO expResult = new PersonDTO(p1);
        PersonDTO result = instance.deletePerson(id);
        assertEquals(expResult, result);
    }
    
    @Test 
    public void testEditPerson() throws Exception{
        PersonDTO person = new PersonDTO(p1);
        EntityManagerFactory emf = null;
        PersonFacade instance = PersonFacade.getPersonFacade(emf);
        PersonDTO expResult = new PersonDTO(p1);
        expResult.setPhone("123123123");
        person.setPhone("123123123");
        PersonDTO result = instance.editPerson(person);
        assertEquals(expResult.getPhone(), result.getPhone());
                
    }
}
