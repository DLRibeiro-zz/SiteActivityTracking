package EventSystem;

import EventSystem.Exceptions.OperationLimitException;
import EventSystem.Exceptions.UnsupportedEventTypeException;
import EventSystem.models.BrowserEvent;
import EventSystem.models.EventFactory;
import EventSystem.models.Pair;
import EventSystem.models.User;
import EventSystem.models.Website;
import EventSystem.proto.EventMessageProto.EventMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventHandler{

  private HashMap<User, List<BrowserEvent>> userEventRecords;
  private HashMap<Pair<User, Website>, List<BrowserEvent>> userWebsiteEventRecords;

  private int OPERATION_LIMIT;

  public EventHandler(int operationLimit){
    this.userEventRecords = new HashMap();
    this.userWebsiteEventRecords = new HashMap<>();
    this.OPERATION_LIMIT = operationLimit;
  }

  public synchronized void handle(EventMessage eventMessage)
      throws UnsupportedEventTypeException, OperationLimitException {
    BrowserEvent browserEvent = EventFactory.createEvent(eventMessage);
    User user = new User(browserEvent.getUser());
    Website website = new Website(browserEvent.getWebsite());
    Pair<User, Website> pairUserWebsite = new Pair<>(user, website);
    if(!userEventRecords.containsKey(user)){
      List<BrowserEvent> records = new ArrayList<>();
      records.add(browserEvent);
      userEventRecords.put(user, records);
      userWebsiteEventRecords.put(pairUserWebsite, records);
    }else{
      List<BrowserEvent> records = userEventRecords.get(user);
      records.add(browserEvent);
      userEventRecords.replace(user, records);
    }
    if(userWebsiteEventRecords.containsKey(pairUserWebsite)){
      List<BrowserEvent> records = userWebsiteEventRecords.get(pairUserWebsite);
      if(records.size() == OPERATION_LIMIT){
        throw new OperationLimitException(browserEvent);
      }
      records.add(browserEvent);
      userWebsiteEventRecords.replace(pairUserWebsite, records);
    }

  }

  public HashMap<User, List<BrowserEvent>> getUserEventRecords() {
    return userEventRecords;
  }

  public HashMap<Pair<User, Website>, List<BrowserEvent>> getUserWebsiteEventRecords() {
    return userWebsiteEventRecords;
  }

  public int getOPERATION_LIMIT() {
    return OPERATION_LIMIT;
  }
}
