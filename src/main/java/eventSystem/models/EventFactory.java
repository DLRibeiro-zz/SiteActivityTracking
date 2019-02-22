package eventSystem.models;

import eventSystem.exceptions.UnsupportedEventTypeException;
import eventSystem.proto.EventMessageProto.EventMessage;

public class EventFactory {

  public static BrowserEvent createEvent(EventMessage eventMessage)
      throws UnsupportedEventTypeException {
    BrowserEvent browserEvent;
    String user = eventMessage.getUser();
    String website = eventMessage.getWebsite();
    switch (eventMessage.getEventType()){
      case CLICK: browserEvent = new ClickEvent(user, website);
      break;
      case VIEW: browserEvent = new ViewEvent(user, website);
      break;
      case IMPRESSION: browserEvent = new ImpressionEvent(user,website);
      break;
      default: throw new UnsupportedEventTypeException();
    }
    return browserEvent;
  }

}
