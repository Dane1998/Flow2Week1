package facades;

import dtomapper.PersonDTO;
import dtomapper.PersonsDTO;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;

/**
 *
 * @author Dane
 */
public interface IPersonFacade {

    public PersonDTO addPerson(String fName, String lName, String phone) throws PersonNotFoundException, MissingInputException;

    public PersonDTO deletePerson(long id) throws PersonNotFoundException;

    public PersonDTO getPerson(long id) throws PersonNotFoundException;

    public PersonsDTO getAllPersons();

    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException, MissingInputException;

}
