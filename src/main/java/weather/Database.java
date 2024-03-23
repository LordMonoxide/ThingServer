package weather;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
  private final Connection connection;

  public Database(final String host, final int port, final String user, final String pass, final String db) throws SQLException {
    this.connection = DriverManager.getConnection("jdbc:mariadb://" + host + ':' + port + '/' + db, user, pass);
  }

  public int addReading(final Sensor sensor, final float reading) throws SQLException {
    try(final PreparedStatement statement = this.connection.prepareStatement("""
        INSERT INTO readings (sensor, reading)
        VALUES (?, ?)
      """)) {
      statement.setString(1, sensor.toString());
      statement.setFloat(2, reading);
      return statement.executeUpdate();
    }
  }
}
