package eventSystem;

import eventSystem.exceptions.InvalidEntryException;
import eventSystem.exceptions.OperationLimitException;
import eventSystem.exceptions.UnsupportedEventTypeException;
import eventSystem.models.BrowserEvent;
import eventSystem.models.EventFactory;
import eventSystem.models.Pair;
import eventSystem.models.User;
import eventSystem.models.Website;
import eventSystem.proto.EventMessageProto.EventMessage;
import eventSystem.proto.EventMessageProto.EventMessage.EventType;
import eventSystem.validators.EventEntryValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class responsible for Handling each Event recieved throught the REST API
 */
public class EventHandler{

  private HashMap<User, List<BrowserEvent>> usersEventRecords;
  private HashMap<Pair<User, Website>, List<BrowserEvent>> usersWebsitesEventRecords;
  private EventType eventType;
  private int OPERATION_LIMIT;

  public EventHandler(int operationLimit, String eventType){
    this.usersEventRecords = new HashMap<>();
    this.eventType = EventType.valueOf(eventType);
    this.usersWebsitesEventRecords = new HashMap<>();
    this.OPERATION_LIMIT = operationLimit;
  }

  /**
   * Handles a {@link EventMessage} received from the {@link eventSystem.resources.EventREST}
   * @param eventMessage, the Event received through the API
   * @throws UnsupportedEventTypeException
   * @throws OperationLimitException
   */
  public synchronized void handleEvent(EventMessage eventMessage)
      throws UnsupportedEventTypeException, OperationLimitException, InvalidEntryException {
    BrowserEvent browserEvent = EventFactory.createEvent(eventMessage);
    User user = new User(browserEvent.getUser());
    Website website = new Website(browserEvent.getWebsite());
    if(!EventEntryValidator.isUserValid(user)){
      throw new InvalidEntryException(user);
    }
    if(!EventEntryValidator.isWebsiteValid(website)){
      throw new InvalidEntryException(website);
    }
    if(!EventEntryValidator.isEventTypeValidForServer(eventMessage, this.eventType)){
      throw new InvalidEntryException(this.eventType);
    }
    Pair<User, Website> pairUserWebsite = new Pair<>(user, website);
    if(!usersEventRecords.containsKey(user)){
      handleFirstEvent(browserEvent, user, pairUserWebsite);
    }else{
      handleEventUser(browserEvent, user);
      handleEventUserWebsite(browserEvent, pairUserWebsite);
    }

  }

  /**
   * Handles each event, taking into consideration the pair formed by each User and Website
   * @param browserEvent, the Event received through the API
   * @param pairUserWebsite, the pair formed by the User and Website from the event
   * @throws OperationLimitException, when the operation done by the user on the Website has reached it's limit
   */
  private void handleEventUserWebsite(BrowserEvent browserEvent,
      Pair<User, Website> pairUserWebsite) throws OperationLimitException {
    List<BrowserEvent> records = usersWebsitesEventRecords.get(pairUserWebsite);
    if(records == null){
      records = new ArrayList<>();
      usersWebsitesEventRecords.put(pairUserWebsite, records);
    }
    if(records.size() == OPERATION_LIMIT){
      throw new OperationLimitException(browserEvent);
    }
    records.add(browserEvent);
    usersWebsitesEventRecords.replace(pairUserWebsite, records);
  }

  /**
   * Handles the events taking into account only the User
   * @param browserEvent, the Event received through the API
   * @param user
   */
  private void handleEventUser(BrowserEvent browserEvent, User user) {
    List<BrowserEvent> records = usersEventRecords.get(user);
    records.add(browserEvent);
    usersEventRecords.replace(user, records);
  }

  /**
   * Handles the first event of the User
   * @param browserEvent, the Event received through the API
   * @param user, the User sending the event
   * @param pairUserWebsite, the pair formed by the User and the Website from the Event
   */
  private void handleFirstEvent(BrowserEvent browserEvent, User user,
      Pair<User, Website> pairUserWebsite) {
    List<BrowserEvent> records = new ArrayList<>();
    records.add(browserEvent);
    usersEventRecords.put(user, records);
    usersWebsitesEventRecords.put(pairUserWebsite, records);
  }

  public HashMap<User, List<BrowserEvent>> getUsersEventRecords() {
    return usersEventRecords;
  }

  public HashMap<Pair<User, Website>, List<BrowserEvent>> getUsersWebsitesEventRecords() {
    return usersWebsitesEventRecords;
  }

  public int getOPERATION_LIMIT() {
    return OPERATION_LIMIT;
  }
}
