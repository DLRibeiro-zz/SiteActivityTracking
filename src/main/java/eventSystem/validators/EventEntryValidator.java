package eventSystem.validators;

import eventSystem.models.User;
import eventSystem.models.Website;
import eventSystem.proto.EventMessageProto.EventMessage;
import eventSystem.proto.EventMessageProto.EventMessage.EventType;
import org.apache.commons.lang3.StringUtils;

public class EventEntryValidator {


  public static boolean isUserValid(User user){
    String email = user.getEmail();
    return StringUtils.isNotBlank(email) && StringUtils.isNotEmpty(email);
  }

  public static boolean isWebsiteValid(Website website){
    return website.isValid();
  }

  public static boolean isEventTypeValidForServer(EventMessage eventMessage, EventType eventType){
    if(eventMessage.getEventType().equals(eventType)){
      return true;
    }
    return false;
  }
}
