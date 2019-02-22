package eventSystem.interfaces;

public interface Observable {

  public void addObserver(Observer observer);
  public void removeObserver(Observer observer);
  public void removeAll();
  public void notifyAllObservers();
  public void setChanged();

}
