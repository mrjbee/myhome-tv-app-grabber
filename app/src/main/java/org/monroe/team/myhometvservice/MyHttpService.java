package org.monroe.team.myhometvservice;

import fi.iki.elonen.NanoHTTPD;

public class MyHttpService extends NanoHTTPD {
    private String currentAppPackage = "";

    // Constructor to start the server
    public MyHttpService() {
        super(9999);  // Port number for your HTTP server
    }

    // Override the serve method to handle requests
    @Override
    public Response serve(IHTTPSession session) {
        String responseMessage = PowerStateReceiver.screenOn + ":" + currentAppPackage;
        return newFixedLengthResponse(Response.Status.OK, "text/plain", responseMessage);
    }

    // Set the current app information
    public void updateCurrentApp(String packageName) {
        currentAppPackage = packageName;
    }
}

