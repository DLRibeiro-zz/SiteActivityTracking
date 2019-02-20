package EventSystem.models;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Website {
    private String name;

    public Website(String name){
      this.name = name;
    }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name);
  }

  @Override
  public boolean equals(Object obj) {
      if(obj instanceof Website){
        Website w = (Website) obj;
        return Objects.equals(this.name, w.getName());
      }else{
        return false;
      }
    }

  public boolean isValid(){
      Pattern p = Pattern.compile("^(https?:\\/\\/)?(www\\.)?([\\w]+\\.)+[\u200C\u200B\\w]{2,63}\\/?$");
      Matcher m = p.matcher(this.name);
      return m.matches();
  }
}
