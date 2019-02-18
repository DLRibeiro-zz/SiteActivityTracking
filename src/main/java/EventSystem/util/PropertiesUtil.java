package EventSystem.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
  public static Properties loadProperties() throws IOException {
    Properties properties = new Properties();
    InputStream inputStream = PropertiesUtil.class.getClassLoader()
        .getResourceAsStream("config.properties");
    if (inputStream != null) {
      properties.load(inputStream);
    }
    return properties;
  }
}
