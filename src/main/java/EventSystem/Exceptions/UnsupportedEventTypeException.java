package EventSystem.Exceptions;

public class UnsupportedEventTypeException extends Exception {

  private static String message = "The message is of an unsupported event type";

  public UnsupportedEventTypeException(){
    super(message);
  }

}
