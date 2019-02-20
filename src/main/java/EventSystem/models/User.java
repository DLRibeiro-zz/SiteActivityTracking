package EventSystem.models;

import java.util.Objects;

public class User {
    private String email;

    public User(String email){
      this.email = email;
    }

  public String getEmail() {
    return email;
  }

  @Override
  public int hashCode() {
    return Objects.hash(email);
  }

  @Override
  public boolean equals(Object obj) {
      if(obj instanceof User){
        User u = (User) obj;
        return this.email.equals(u.getEmail());
      }else {
        return false;
      }
  }
}
