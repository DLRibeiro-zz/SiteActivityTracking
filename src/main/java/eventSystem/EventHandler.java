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
   * Handles a {@link EventMessage} recived from the {@link eventSystem.resources.EventREST}
   * @param eventMessage
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

  private void handleEventUser(BrowserEvent browserEvent, User user) {
    List<BrowserEvent> records = usersEventRecords.get(user);
    records.add(browserEvent);
    usersEventRecords.replace(user, records);
  }

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
