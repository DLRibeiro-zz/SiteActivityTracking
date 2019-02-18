package EventSystem.models;

public abstract class BrowserEvent {
  private String user;
  private String website;

  public BrowserEvent(){

  }

  public BrowserEvent(String user, String website){
    this.user = user;
    this.website = website;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }
}
