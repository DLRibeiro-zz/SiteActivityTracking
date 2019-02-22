package eventSystem;

import eventSystem.exceptions.InvalidEntryException;
import eventSystem.exceptions.OperationLimitException;
import eventSystem.exceptions.UnsupportedEventTypeException;
import eventSystem.models.BrowserEvent;
import eventSystem.models.User;
import eventSystem.models.Website;
import eventSystem.proto.EventMessageProto.EventMessage;
import eventSystem.proto.EventMessageProto.EventMessage.EventType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class EventHandlerTest {


  private EventHandler eventHandler;
  private static int OPERATION_LIMIT = 10;
  private static String TEST_USER_STRING = "testUser";
  private static String TEST_WEBSITE_STRING = "www.testwebsite.com";
  private static String EVENT_TYPE_STRING="CLICK";
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void prepareTest() {
    this.eventHandler = new EventHandler(OPERATION_LIMIT, EVENT_TYPE_STRING);
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
    eventMessageBuilder.setEventType(EventType.valueOf(EVENT_TYPE_STRING));
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
    eventMessageBuilder.setEventType(EventType.valueOf(EVENT_TYPE_STRING));
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
    eventMessageBuilder.setEventType(EventType.valueOf(EVENT_TYPE_STRING));
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
      throws UnsupportedEventTypeException, OperationLimitException, InvalidEntryException {
    //Prepare test
    List<EventMessage> eventMessageList = this.buildEventMessagesToCheckLimitBreaking();
    for(EventMessage eventMessage : eventMessageList){
      this.eventHandler.handleEvent(eventMessage);
    }
  }

  @Test
  public void handleEventOperationLimitNotBreakingSameUser()
      throws UnsupportedEventTypeException, OperationLimitException, InvalidEntryException {
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
      throws UnsupportedEventTypeException, OperationLimitException, InvalidEntryException {
    List<EventMessage> eventMessageList = this.buildEventMessagesToCheckLimitNotBreakSameWebsite();
    for(EventMessage eventMessage: eventMessageList){
      this.eventHandler.handleEvent(eventMessage);
    }
  }

  @Test
  public void handleEventInvalidUser()
      throws InvalidEntryException, UnsupportedEventTypeException, OperationLimitException {
    this.expectedException.expect(InvalidEntryException.class);
    this.expectedException.expectMessage(InvalidEntryException.INVALID_USER_MESSAGE);
    EventMessage eventMessage = buildEventMessageInvalidUser();
    this.eventHandler.handleEvent(eventMessage);
  }

  @Test
  public void handleEventInvalidWebsite()
      throws InvalidEntryException, UnsupportedEventTypeException, OperationLimitException {
    this.expectedException.expect(InvalidEntryException.class);
    this.expectedException.expectMessage(InvalidEntryException.INVALID_WEBSITE_MESSAGE);
    EventMessage eventMessage = buildEventMessageInvalidWebsite();
    this.eventHandler.handleEvent(eventMessage);
  }

  private EventMessage buildEventMessageInvalidUser() {
    EventMessage.Builder eventMessageBuilder = EventMessage.newBuilder();
    eventMessageBuilder.setUser("");
    eventMessageBuilder.setWebsite(TEST_WEBSITE_STRING);
    eventMessageBuilder.setEventType(EventType.valueOf(EVENT_TYPE_STRING));
    return eventMessageBuilder.build();
  }

  private EventMessage buildEventMessageInvalidWebsite() {
    EventMessage.Builder eventMessageBuilder = EventMessage.newBuilder();
    eventMessageBuilder.setUser(TEST_USER_STRING);
    eventMessageBuilder.setWebsite("");
    eventMessageBuilder.setEventType(EventType.valueOf(EVENT_TYPE_STRING));
    return eventMessageBuilder.build();
  }
}