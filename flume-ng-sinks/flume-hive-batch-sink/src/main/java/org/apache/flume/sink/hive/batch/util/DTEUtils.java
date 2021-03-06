package org.apache.flume.sink.hive.batch.util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tao Li on 2016/3/2.
 */
public class DTEUtils {
  private static Logger LOG = LoggerFactory.getLogger(DTEUtils.class);

  public static void updateLogDetail(String serviceURL, int logId, String logdate) {
    String url = String.format("%s/%s/%s", serviceURL, logId, logdate);
    Client client = Client.create();
    WebResource resource = client.resource(url);
    resource.post();
  }
}
