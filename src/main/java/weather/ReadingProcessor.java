package weather;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class ReadingProcessor {
  private static final Logger LOGGER = LogManager.getFormatterLogger(ReadingProcessor.class);

  private final Database db;

  public ReadingProcessor(final Database db) {
    this.db = db;
  }

  public void handleReading(final Sensor sensor, final float reading) {
    try {
      final int rowsAffected = this.db.addReading(sensor, reading);

      if(rowsAffected == 0) {
        LOGGER.error("Failed to store reading (unknown error)");
      }
    } catch(final SQLException e) {
      LOGGER.error("Failed to store reading", e);
    }
  }
}
