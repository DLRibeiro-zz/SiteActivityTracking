package eventSystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

public class EventSystemConfiguration extends Configuration {

  @NotEmpty
  private String appName;

  @NotEmpty
  private String eventType;

  @Min(1)
  @NotNull
  private int operationLimit;

  @JsonProperty
  public String getAppName() {
    return appName;
  }

  @JsonProperty
  public void setAppName(String appName) {
    this.appName = appName;
  }

  @JsonProperty
  public String getEventType() {
    return eventType;
  }
  @JsonProperty
  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  @JsonProperty
  public int getOperationLimit() {
    return operationLimit;
  }
  @JsonProperty
  public void setOperationLimit(int operationLimit) {
    this.operationLimit = operationLimit;
  }
}
