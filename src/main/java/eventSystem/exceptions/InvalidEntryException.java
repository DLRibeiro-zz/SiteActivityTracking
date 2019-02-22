package eventSystem.exceptions;

import eventSystem.models.User;
import eventSystem.models.Website;
import eventSystem.proto.EventMessageProto.EventMessage.EventType;

public class InvalidEntryException extends Exception {

  public static String INVALID_USER_MESSAGE = "There is an invalid user in the entry";
  public static String INVALID_WEBSITE_MESSAGE = "There is an invalid website in the entry";
  public static String INVALID_EVENT_TYPE_ON_SERVER = "The server expects the EventType: ";

  public InvalidEntryException(User user) {
    super(INVALID_USER_MESSAGE);
  }

  public InvalidEntryException(Website website) {
    super(INVALID_WEBSITE_MESSAGE);
  }

  public InvalidEntryException(EventType eventType) {
    super(INVALID_EVENT_TYPE_ON_SERVER + eventType);
  }
}
