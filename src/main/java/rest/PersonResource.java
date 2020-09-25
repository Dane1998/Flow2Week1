package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtomapper.PersonDTO;
import dtomapper.PersonsDTO;
import entities.Person;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import facades.PersonFacade;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

//Todo Remove or change relevant parts before ACTUAL use
@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    
    //An alternative way to get the EntityManagerFactory, whithout having to type the details all over the code
    //EMF = EMF_Creator.createEntityManagerFactory(DbSelector.DEV, Strategy.CREATE);
    
    private static final PersonFacade FACADE =  PersonFacade.getPersonFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }
    
    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getRenameMeCount() {
        long count = FACADE.getPersonCount();
        //System.out.println("--------------->"+count);
        return "{\"count\":"+count+"}";  //Done manually so no need for a DTO
    }
    
    @Path("/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPersonById(@PathParam("id") long id ) throws PersonNotFoundException {
        PersonDTO person = FACADE.getPerson(id);
        return GSON.toJson(person);
    }
    
    @Path("all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllPersons(){
        PersonsDTO persons = FACADE.getAllPersons();
        return GSON.toJson(persons.getAll());
    }
    
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String add(String person) throws PersonNotFoundException, MissingInputException{
        PersonDTO p = GSON.fromJson(person, PersonDTO.class);
        PersonDTO pAdded = FACADE.addPerson(p.getfName(), p.getlName(), p.getPhone(), p.getStreet(), p.getZip(), p.getCity());
        return GSON.toJson(pAdded);
    }
    
    @DELETE
    @Path("{id}")
    public String deletePerson(@PathParam("id") long id) throws PersonNotFoundException{
        PersonDTO pDeleted = FACADE.deletePerson(id);
        return GSON.toJson(pDeleted);
    }
    
    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String updatePerson(@PathParam("id")long id, String person) throws PersonNotFoundException, MissingInputException{
        PersonDTO pDTO = GSON.fromJson(person, PersonDTO.class);
        PersonDTO pNew = FACADE.editPerson(pDTO);
        return GSON.toJson(pNew);
    }
}
