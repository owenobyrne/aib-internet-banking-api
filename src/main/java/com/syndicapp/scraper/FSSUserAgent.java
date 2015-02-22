package com.syndicapp.scraper;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.log4j.Logger;

public class FSSUserAgent {
    public static HttpClient httpclient = HttpClientBuilder.create().build();
    public static Logger log = Logger.getLogger(FSSUserAgent.class);
    public static BasicHttpContext context = new BasicHttpContext();

    public FSSUserAgent() {
    }


}