package eventSystem.sender;

import eventSystem.proto.EventMessageProto.EventMessage;
import eventSystem.proto.EventMessageProto.EventMessage.EventType;
import io.dropwizard.jersey.protobuf.ProtocolBufferMediaType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

/**
 * Class responsible to send a HTTP Request with Protobuf message to the URL
 */
public class EventMessageSender {

  public static void main(String[] args) {
    String urlArgs = args[0];
    String userArgs = args[1];
    String websiteArgs = args[2];
    String eventTypeArgs = args[3];

    EventMessage.Builder eventMessageBuilder = EventMessage.newBuilder();
    eventMessageBuilder.setUser(userArgs);
    eventMessageBuilder.setWebsite(websiteArgs);
    try {
      eventMessageBuilder.setEventTypeValue(parseEventTypeArgs(eventTypeArgs));
      EventMessage eventMessage = eventMessageBuilder.build();
      Client client = ClientBuilder.newClient();
      WebTarget webTarget = client
          .target(urlArgs + EventType.forNumber(parseEventTypeArgs(eventTypeArgs)).toString()
              .toLowerCase());
      Invocation.Builder invocationBuilder = webTarget
          .request(ProtocolBufferMediaType.APPLICATION_PROTOBUF);
      invocationBuilder.post(Entity.entity(eventMessage,ProtocolBufferMediaType.APPLICATION_PROTOBUF));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static int parseEventTypeArgs(String eventTypeArgs) throws Exception {
    int eventTypeValue = Integer.parseInt(eventTypeArgs);
    if (eventTypeValue < 0 || eventTypeValue > 2) {
      throw new Exception("The value set for the event type is not valid");
    }
    return eventTypeValue;
  }

}
