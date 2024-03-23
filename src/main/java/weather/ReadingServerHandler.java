package weather;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReadingServerHandler extends ChannelInboundHandlerAdapter {
  private static final Logger LOGGER = LogManager.getFormatterLogger(ReadingServerHandler.class);

  private final ReadingProcessor processor;

  public ReadingServerHandler(final ReadingProcessor processor) {
    this.processor = processor;
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
    final ByteBuf in = (ByteBuf)msg;
    try {
      final String packet = in.toString(CharsetUtil.US_ASCII);

      for(final String input : packet.split(";")) {
        final String[] split = input.split(":");

        LOGGER.info(input);

        if(split.length != 2) {
          this.logMalformed("Wrong number of parts", input);
          continue;
        }

        final Sensor sensor;
        final float reading;

        try {
          sensor = Sensor.valueOf(split[0]);
        } catch(final IllegalArgumentException e) {
          this.logMalformed("Invalid sensor", input);
          continue;
        }

        try {
          reading = Float.parseFloat(split[1]);
        } catch(final NumberFormatException e) {
          this.logMalformed("Invalid reading", input);
          continue;
        }

        this.processor.handleReading(sensor, reading);
      }
    } finally {
      in.release();
      ctx.close();
    }
  }

  private void logMalformed(final String reason, final String input) {
    LOGGER.error("Malformed input: %s (%s)", reason, input);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
    LOGGER.error("Channel exception", cause);
    ctx.close();
  }
}
