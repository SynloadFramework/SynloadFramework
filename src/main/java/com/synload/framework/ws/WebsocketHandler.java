package com.synload.framework.ws;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebsocketHandler extends WebSocketHandler {
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(300000);
        factory.getPolicy().setMaxBinaryMessageSize(104857600);
        factory.getPolicy().setMaxTextMessageSize(104857600);
        factory.setCreator(new WSCreator());
        factory.getExtensionFactory().unregister("permessage-deflate");
    }

    public class WSCreator implements WebSocketCreator {
        public Object createWebSocket(ServletUpgradeRequest request,
                ServletUpgradeResponse response) {
            return new WSHandler();
        }
    }
}