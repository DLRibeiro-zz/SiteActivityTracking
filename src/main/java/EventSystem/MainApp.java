package EventSystem;

import EventSystem.resources.EventREST;
import io.dropwizard.Application;
import io.dropwizard.jersey.protobuf.ProtobufBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.model.Resource;

public class MainApp extends Application<EventSystemConfiguration> {

  public static void main(final String[] args) throws Exception {
    new MainApp().run(args);
  }

  @Override
  public void initialize(Bootstrap<EventSystemConfiguration> bootstrap) {
    bootstrap.addBundle(new ProtobufBundle());
  }

  @Override
  public void run(EventSystemConfiguration configuration, Environment environment)
      throws Exception {
      this.buildRESTAPI(configuration,environment);
  }


  private void buildRESTAPI(EventSystemConfiguration configuration, Environment environment) {
    String pathName = configuration.getEventType().toLowerCase();
    Resource.Builder resourceBuilder = Resource.builder(EventREST.class).path(pathName);
    int operationLimit = configuration.getOperationLimit();
    environment.jersey().register(resourceBuilder.build());
    environment.jersey().register(new AbstractBinder() {
      @Override
      protected void configure() {
        bind(new EventHandler(operationLimit)).to(EventHandler.class);
      }
    });

  }
}
