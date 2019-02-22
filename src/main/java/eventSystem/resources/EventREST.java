package eventSystem.resources;


import eventSystem.EventHandler;
import eventSystem.exceptions.InvalidEntryException;
import eventSystem.exceptions.OperationLimitException;
import eventSystem.exceptions.UnsupportedEventTypeException;
import eventSystem.proto.EventMessageProto.EventMessage;
import io.dropwizard.jersey.protobuf.ProtocolBufferMediaType;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


@Path("/{event}/")
@Produces(ProtocolBufferMediaType.APPLICATION_PROTOBUF)
public class EventREST {

  private EventHandler eventHandler;

  public EventREST(){

  }
  @Inject
  public EventREST(EventHandler eventHandler){
    this.eventHandler = eventHandler;
  }

  @POST
  public Response registerEvent(EventMessage eventMessage) {
    try {
      System.out.println("Recieved event: " +  eventMessage.toString());
      this.eventHandler.handleEvent(eventMessage);
      return Response.ok().build();
    } catch (UnsupportedEventTypeException e) {
      throw new NotAcceptableException(e.getMessage());
    } catch (OperationLimitException e) {
      throw new ForbiddenException(e.getMessage());
    } catch (InvalidEntryException e) {
      throw new BadRequestException(e.getMessage());
    }
  }
}
