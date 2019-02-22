package eventSystem.healtchecks;

import eventSystem.EventSystemConfiguration;
import eventSystem.proto.EventMessageProto.EventMessage;
import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.jersey.protobuf.ProtocolBufferMediaType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Healtchecks that sends an empty request to verify if the server is up and responding with the right code
 */
public class EventRESTHealthCheck extends HealthCheck {

  private Invocation.Builder pingRequestBuilder;
  private EventMessage eventMessage;

  public EventRESTHealthCheck(EventSystemConfiguration eventSystemConfiguration) {
    Client client = ClientBuilder.newClient();
    WebTarget webTarget = client
        .target("http://localhost:8080/" + eventSystemConfiguration.getEventType().toLowerCase());
    EventMessage.Builder eventMessageBuilder = EventMessage.newBuilder();
    eventMessageBuilder.setUser("");
    eventMessageBuilder.setWebsite("");
    int eventTypeValue = 0;
    switch (eventSystemConfiguration.getEventType()) {
      case "CLICK":
        eventTypeValue = 0;
        break;
      case "VIEW":
        eventTypeValue = 1;
        break;
      case "IMPRESSION":
        eventTypeValue = 2;
        break;
      default:
        eventTypeValue = 0;
        break;
    }
    eventMessageBuilder.setEventTypeValue(eventTypeValue);
    this.pingRequestBuilder = webTarget.request(ProtocolBufferMediaType.APPLICATION_PROTOBUF);
    this.eventMessage = eventMessageBuilder.build();
  }

  @Override
  protected Result check() throws Exception {
    Response response = this.pingRequestBuilder
        .post(Entity.entity(this.eventMessage, ProtocolBufferMediaType.APPLICATION_PROTOBUF));
    if (response.getStatus() != 400) {
      return Result.unhealthy("The message was not the expected code, there is something wrong");
    } else {
      return Result.healthy();
    }
  }
}
