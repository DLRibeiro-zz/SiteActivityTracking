package eventSystem.exceptions;

import eventSystem.models.BrowserEvent;

public class OperationLimitException extends Exception{

  private static String message = "The operation %s by user %s on website %s has reached it's limit";
  public OperationLimitException(BrowserEvent browserEvent){
    super(String.format(message, browserEvent.getClass().getName(), browserEvent.getUser(), browserEvent.getWebsite()));
  }
}
