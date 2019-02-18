package EventSystem;

import EventSystem.resources.EventREST;
import EventSystem.util.PropertiesUtil;
import io.dropwizard.Application;
import io.dropwizard.jersey.DropwizardResourceConfig;
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
    int operationLimit = Integer.parseInt(PropertiesUtil.loadProperties().getProperty("operation_limit"));
    Resource.Builder resourceBuilder
    environment.jersey().register(EventREST.class);
    environment.jersey().register(new AbstractBinder() {
      @Override
      protected void configure() {
        bind(new EventHandler(operationLimit)).to(EventHandler.class);
      }
    });
  }


  private void buildRESTAPI(Environment environment){

  }
}
