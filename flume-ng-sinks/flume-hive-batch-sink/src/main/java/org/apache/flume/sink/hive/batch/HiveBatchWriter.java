package org.apache.flume.sink.hive.batch;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Writer;
import org.apache.hadoop.hive.serde2.Deserializer;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by Tao Li on 2/16/16.
 */
public class HiveBatchWriter {
  private static final Logger LOG = LoggerFactory.getLogger(HiveBatchWriter.class);

  private long lastWriteTime = -1;  // -1: no data write yet
  private Writer writer;
  private Deserializer deserializer;
  private String file;
  private long idleTimeout = 5000;
  private List<Callback> initCallbacks = null;
  private List<Callback> closeCallbacks = null;
  private long minFinishedTimestamp = 0;

  public interface Callback {
    void run();
  }

  public HiveBatchWriter(Configuration conf, Deserializer deserializer, String file)
      throws IOException, SerDeException {
    this.deserializer = deserializer;
    this.file = file;

    OrcFile.WriterOptions writerOptions = OrcFile.writerOptions(conf);
    writerOptions.inspector(deserializer.getObjectInspector());
    this.writer = OrcFile.createWriter(new Path(file), writerOptions);

    if (this.initCallbacks != null) {
      for (Callback callback : this.initCallbacks) {
        callback.run();
      }
    }
  }

  public void append(byte[] bytes) throws IOException, SerDeException {
    writer.addRow(deserializer.deserialize(new Text(bytes)));
    lastWriteTime = System.currentTimeMillis();
  }

  public void close() throws IOException {
    writer.close();

    if (closeCallbacks != null) {
      for (Callback callback : closeCallbacks) {
        callback.run();
      }
    }
  }

  public boolean isIdle() {
    long currentTimestamp = System.currentTimeMillis();
    return lastWriteTime > 0
        && currentTimestamp > minFinishedTimestamp
        && currentTimestamp - lastWriteTime >= idleTimeout;
  }


  public void setIdleTimeout(long idleTimeout) {
    this.idleTimeout = idleTimeout;
  }

  public void setInitCallbacks(List<Callback> initCallbacks) {
    this.initCallbacks = initCallbacks;
  }

  public void setCloseCallbacks(List<Callback> closeCallbacks) {
    this.closeCallbacks = closeCallbacks;
  }

  public void setMinFinishedTimestamp(long minFinishedTimestamp) {
    this.minFinishedTimestamp = minFinishedTimestamp;
  }

  public String getFile() {
    return file;
  }
}
