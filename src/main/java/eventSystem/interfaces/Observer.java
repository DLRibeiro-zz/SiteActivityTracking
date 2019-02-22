package eventSystem.interfaces;

public interface Observer {

  public void update(Observable observable);
  public void update(Observable observable, Object object);

}
