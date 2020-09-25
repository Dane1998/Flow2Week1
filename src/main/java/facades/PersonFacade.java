package facades;

import dtomapper.PersonDTO;
import dtomapper.PersonsDTO;
import entities.Address;
import entities.Person;
import exceptions.ExceptionDTO;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade implements IPersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public long getPersonCount() {
        EntityManager em = getEntityManager();
        try {
            long personCount = (long) em.createQuery("SELECT COUNT(m) FROM Person m").getSingleResult();
            return personCount;
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO addPerson(String fName, String lName, String phone, String street, String zip, String city) throws PersonNotFoundException, MissingInputException {
        EntityManager em = getEntityManager();
        Person person = new Person(fName, lName, phone);
        if ((fName.length() == 0) || (lName.length() == 0)) {
            throw new MissingInputException("First name and/or Last name is missing");
        }
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT a FROM Address a WHERE a.street = :street AND a.zip = :zip AND a.city = :city");
            query.setParameter("street", street);
            query.setParameter("zip", zip);
            query.setParameter("city", city);
            List<Address> addresses = query.getResultList();
            if(addresses.size() > 0){
                person.setAddress(addresses.get(0));
            }else{
                person.setAddress(new Address(street,zip,city));
            }
            em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(person);
    }

    @Override
    public PersonDTO deletePerson(long id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        Person person = em.find(Person.class, id);
        if (person == null) {
            throw new PersonNotFoundException(String.format("Could not delete person, provided id does not exist: ", id));
        } else {
            try {
                em.getTransaction().begin();
                em.remove(person);
                em.getTransaction().commit();

            } finally {
                em.close();
            }
            return new PersonDTO(person);
        }
    }

    @Override
    public PersonDTO getPerson(long id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        Person person = em.find(Person.class, id);
        if (person == null) {
            throw new PersonNotFoundException(String.format("Could not find person with provided id: ", id));
        } else {
            try {
                return new PersonDTO(person);
            } finally {
                em.close();
            }
        }
    }

    @Override
    public PersonsDTO getAllPersons() {
        EntityManager em = getEntityManager();
        try {
            return new PersonsDTO(em.createNamedQuery("Person.getAll").getResultList());
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException, MissingInputException {
        if ((p.getfName().length() == 0) || (p.getlName().length() == 0)) {
            throw new MissingInputException("First name and/or last name is missing");
        }
        EntityManager em = getEntityManager();
        Person person = em.find(Person.class, p.getId());
        if (person == null) {
            throw new PersonNotFoundException(String.format("Could not find person with provided id: ", p.getId()));
        } else {
            try {
                em.getTransaction().begin();
                person.setFirstName(p.getfName());
                person.setLastName(p.getlName());
                person.setPhone(p.getPhone());
                person.setLastEdited();
                em.getTransaction().commit();
                return new PersonDTO(person);
            } finally {
                em.close();
            }
        }
    }
}
