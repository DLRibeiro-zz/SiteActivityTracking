package EventSystem;

import EventSystem.Exceptions.OperationLimitException;
import EventSystem.Exceptions.UnsupportedEventTypeException;
import EventSystem.models.BrowserEvent;
import EventSystem.models.User;
import EventSystem.models.Website;
import EventSystem.proto.EventMessageProto.EventMessage;
import EventSystem.proto.EventMessageProto.EventMessage.EventType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EventHandlerTest {


  private EventHandler eventHandler;
  private static int OPERATION_LIMIT = 10;
  private static String TEST_USER_STRING = "testUser";
  private static String TEST_WEBSITE_STRING = "www.testwebsite.com";

  @Before
  public void prepareTest() {
    this.eventHandler = new EventHandler(OPERATION_LIMIT);
  }

  /**
   * Builds a list of {@link EventMessage} that forces the {@link EventHandler} to throw an
   * exception due to it's operational limit
   */
  private List<EventMessage> buildEventMessagesToCheckLimitBreaking() {
    List<EventMessage> eventMessages = new ArrayList<>();
    User user = new User("testUser");
    Website website = new Website("www.testwebsite.com");
    EventMessage.Builder eventMessageBuilder = EventMessage.newBuilder();
    eventMessageBuilder.setEventType(EventType.CLICK);
    eventMessageBuilder.setUser(user.getEmail());
    eventMessageBuilder.setWebsite(website.getName());
    EventMessage eventMessage = eventMessageBuilder.build();
    for (int i = 0; i < OPERATION_LIMIT + 1; i++) {
      eventMessages.add(eventMessage);
    }
    return eventMessages;
  }

  /**
   * Builds a list of {@link EventMessage} to check if the {@link EventHandler} will throw an
   * exception due to it's operational limit
   */
  private List<EventMessage> buildEventMessagesToCheckLimitNotBreakSameUser() {
    List<EventMessage> eventMessagesSameUser = new ArrayList<>();
    User user = new User(TEST_USER_STRING);
//    Website website = new Website("www.testwebsite.com");
    EventMessage.Builder eventMessageBuilder = EventMessage.newBuilder();
    eventMessageBuilder.setEventType(EventType.CLICK);
    eventMessageBuilder.setUser(user.getEmail());
    for (int i = 0; i < OPERATION_LIMIT + 1; i++) {
      eventMessageBuilder.setWebsite("www." + i + ".com");
      EventMessage eventMessage = eventMessageBuilder.build();
      eventMessagesSameUser.add(eventMessage);
    }

    return eventMessagesSameUser;
  }

  /**
   * Builds a list of {@link EventMessage} to check if the {@link EventHandler} will throw an exception due
   * to it's operational limit
   */
  private List<EventMessage> buildEventMessagesToCheckLimitNotBreakSameWebsite() {
    List<EventMessage> eventMessagesSameWebsites = new ArrayList<>();
    Website website = new Website(TEST_WEBSITE_STRING);
    EventMessage.Builder eventMessageBuilder = EventMessage.newBuilder();
    eventMessageBuilder.setEventType(EventType.CLICK);
    eventMessageBuilder.setWebsite(website.getName());
    for (int i = 0; i < OPERATION_LIMIT + 1; i++) {
      eventMessageBuilder.setUser("" + i);
      EventMessage eventMessage = eventMessageBuilder.build();
      eventMessagesSameWebsites.add(eventMessage);
    }
    return eventMessagesSameWebsites;
  }

  @Test(expected = OperationLimitException.class)
//  @Test
  public void handleEventOperationalLimitBreaking()
      throws UnsupportedEventTypeException, OperationLimitException {
    //Prepare test
    List<EventMessage> eventMessageList = this.buildEventMessagesToCheckLimitBreaking();
    for(EventMessage eventMessage : eventMessageList){
      this.eventHandler.handleEvent(eventMessage);
    }
  }

  @Test
  public void handleEventOperationLimitNotBreakingSameUser()
      throws UnsupportedEventTypeException, OperationLimitException {
    List<EventMessage> eventMessageList = this.buildEventMessagesToCheckLimitNotBreakSameUser();
    for(EventMessage eventMessage: eventMessageList){
      this.eventHandler.handleEvent(eventMessage);
    }
    HashMap<User, List<BrowserEvent>> allUsersRecords = this.eventHandler.getUsersEventRecords();
    List<BrowserEvent> userRecords = allUsersRecords.get(new User(TEST_USER_STRING));
    Assert.assertEquals(userRecords.size(), OPERATION_LIMIT+1);
  }

  @Test
  public void handleEventOperationLimitNotBreakingSameWebsite()
      throws UnsupportedEventTypeException, OperationLimitException {
    List<EventMessage> eventMessageList = this.buildEventMessagesToCheckLimitNotBreakSameWebsite();
    for(EventMessage eventMessage: eventMessageList){
      this.eventHandler.handleEvent(eventMessage);
    }

  }
}