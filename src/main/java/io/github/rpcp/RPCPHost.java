/*
 * Copyright 2022 PT Tohjiwa Teknologi Indonesia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.rpcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rpcp.model.MessageError;
import io.github.rpcp.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Object to create a vHost RPCP in top of websocket.
 * Create a new RPCP Host and wrap the native websocket session.
 * Websocket session can object from Java websocket, Spring, Quarkus, or Micronaut
 * <p>
 * For detail how to include in your framework choice :
 * <ul>
 *   <li><a href="doc-files/menjangan-spring-guide.html">Spring Framework</a></li>
 *   <li><a href="doc-files/menjangan-micronaut-guide.html">Micronaut</a></li>
 *   <li><a href="doc-files/menjangan-quarkus-guide.html">Quarkus</a></li>
 * </ul>
 *
 * @param <NativeWSSession> Object native from websocket session (Java websocket, Spring, Micronaut, or Quarkus)
 * @author Eric A. Sanjaya
 *
 */
public class RPCPHost<NativeWSSession> {
    final String vHost;
    RPCPHostHandler<NativeWSSession> rpcpHostHandler;

    private final ConcurrentHashMap<String, RPCPWebSocketSession> rpcpWebSocketSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RPCPSession> rpcpSessions = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, RPCPEvent> events = new ConcurrentHashMap<>();
    private final Set<String> eventsName = new CopyOnWriteArraySet<>();
    private final ConcurrentHashMap<String, RPCPMethod> methods = new ConcurrentHashMap<>();
    private final Set<String> methodsName = new CopyOnWriteArraySet<>();

    /**
     * Create new RPCP Host
     *
     * <pre>
     * example used inside spring:
     * {@code
     *      RPCPHost<WebSocketSession> rpcpHost = new RPCPHost<>("api.glexpress.id/ws", new MyRPCPHostHandler<>());
     * }
     *
     * <i>*note</i>
     * <b>WebSocketSession</b> : is a WebSocket Session from Spring
     * <b>MyRPCPHostHandler</b> : Your implementation of {@link RPCPHostHandler}
     * </pre>
     *
     * @param vHost           host name
     * @param rpcpHostHandler RPCP Handler
     */
    public RPCPHost(String vHost, RPCPHostHandler<NativeWSSession> rpcpHostHandler) {
        this.vHost = vHost;
        this.rpcpHostHandler = rpcpHostHandler;
    }

    /**
     * Add new event to RPCP Host with auto generate name/ destination. the name/destination will use the package name.
     * ex. rpcp.event.alarm
     *
     * @param event Event object want to add to Host
     */
    public void addEvent(RPCPEvent event) {
        this.eventsName.add(event.getClass().getName());
        this.events.put(event.getClass().getName(), event);
    }

    /**
     * Add new event to RPCP Host with custom name
     *
     * @param eventName The event name want to register
     * @param event     Event object want to add to Host
     */
    public void addEvent(String eventName, RPCPEvent event) {
        this.eventsName.add(eventName);
        this.events.put(eventName, event);
    }

    /**
     * Add new method to RPCP Host with auto generate name/ destination. the name/ destination will use the package name.
     * ex. rpcp.method.sum
     *
     * @param method Method object want to add to Host
     */
    public void addMethod(RPCPMethod method) {
        this.methodsName.add(method.getClass().getName());
        this.methods.put(method.getClass().getName(), method);
    }

    /**
     * Add new method to RPCP Host with custom name
     *
     * @param methodName The method name want to register
     * @param method     Method object want to add to Host
     */
    public void addMethod(String methodName, RPCPMethod method) {
        this.methodsName.add(methodName);
        this.methods.put(methodName, method);
    }

    public void onWsConnect(NativeWSSession session) {

    }

    public void onWsDisconnect(NativeWSSession session, String closeStatus) {

    }

    /**
     * Used to forward message receive by websocket to RPCP mechanism
     *
     * @param message   Message string receive by websocket want to forward to RPCP
     * @param wsSession Websocket Session
     */
    public void onGetMessage(String message, NativeWSSession wsSession) {

        var session = getOrRegisterWebsocketSession(wsSession);

        RPCPMessage rpcpMessage = null;
        try {
            rpcpMessage = RPCPUtils.read(message);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equalsIgnoreCase("Invalid RPCP")) {
                var xxx = new MessageError(".internal", new HeaderError("Invalid RPCP", "application/json"));
                var bodyError = new MessageBodyError("Invalid RPCP, check your RPCP message", "INVALID_RPCP");
                var objectMapper = new ObjectMapper();

                String bodyStr = null;
                try {
                    bodyStr = objectMapper.writeValueAsString(bodyError);
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
                xxx.setBody(bodyStr);
                String z = null;
                try {
                    z = RPCPUtils.write(xxx);
                } catch (Exception ex) {
                    e.printStackTrace();
                }
                try {
                    session.sendMessage(z);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    session.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (e.getMessage().equalsIgnoreCase("Incorrect protocol format")) {
                var xxx = new MessageError(".internal", new HeaderError("Incorrect protocol format", "application/json"));
                var bodyError = new MessageBodyError("Incorrect protocol format, check your RPCP message", "INVALID_RPCP");
                var objectMapper = new ObjectMapper();

                String bodyStr = null;
                try {
                    bodyStr = objectMapper.writeValueAsString(bodyError);
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
                xxx.setBody(bodyStr);
                String z = null;
                try {
                    z = RPCPUtils.write(xxx);
                } catch (Exception ex) {
                    e.printStackTrace();
                }
                try {
                    session.sendMessage(z);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    session.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (rpcpMessage instanceof MessageConnect) {
            var connect = (MessageConnect) rpcpMessage;
            onRPCPConnect(connect, session);
        } else if (rpcpMessage instanceof MessageDisconnect) {
            var disconnect = (MessageDisconnect) rpcpMessage;
            onRPCPDisconnect(disconnect, session);
        } else if (rpcpMessage instanceof MessageCall) {
            var call = (MessageCall) rpcpMessage;
            onRPCPCall(call, session);
        }
    }

    /**
     * Trigger available event in RPCP Host
     *
     * @param eventName Name of Event want to Trigger
     * @param params    Value want to pass to the event
     * @param sessionId Session id where the event want to send
     * @throws Exception If RPCP session not found will throw Exception with message "RPCP Session Not Found"
     */
    public void triggerEvent(String eventName, Map<String, Object> params, String sessionId) throws Exception {
        var rpcpSession = rpcpSessions.get(sessionId);
        if (rpcpSession == null) {
            throw new Exception("RPCP Session Not Found");
        }

        var event = this.events.get(eventName);
        if (event == null) {
            var xxx = new MessageError(".internal", new HeaderError("Event Not Found", "application/json"));

            var bodyError = new MessageBodyError("Event Not Found", "Event_NOT_FOUND");
            var objectMapper = new ObjectMapper();

            String bodyStr = null;
            try {
                bodyStr = objectMapper.writeValueAsString(bodyError);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
            xxx.setBody(bodyStr);

            String z = null;
            try {
                z = RPCPUtils.write(xxx);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                rpcpSession.sendMessage(z);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        var eventBuilder = new EventBuilder(eventName);
        event.execute(rpcpSession, params, eventBuilder);
    }

    private void onRPCPConnect(MessageConnect messageConnect, RPCPWebSocketSession session) {

        if (!messageConnect.getvHost().equalsIgnoreCase(vHost)) {
            var xxx = new MessageError(".internal", new HeaderError("Connect failed", "application/json"));

            var bodyError = new MessageBodyError("Connect to RPCP failed, check your vHost", "VHOST_NOT_FOUND");
            var objectMapper = new ObjectMapper();

            String bodyStr = null;
            try {
                bodyStr = objectMapper.writeValueAsString(bodyError);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
            xxx.setBody(bodyStr);

            String z = null;
            try {
                z = RPCPUtils.write(xxx);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                session.sendMessage(z);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        String protocol = null;
        try {
            if (!validateProtocolVersion(messageConnect.getProtocolVersion())) {
                var xxx = new MessageError(".internal", new HeaderError("connect failed", "application/json"));
                String z = null;
                try {
                    z = RPCPUtils.write(xxx);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                session.sendMessage(z);
                session.close();
                return;
            } else {
                protocol = messageConnect.getProtocolVersion();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        var preConnectResult = rpcpHostHandler.onPreConnect(messageConnect, session);
        if (!preConnectResult) {
            var rpcpError = new MessageError(".internal", new HeaderError("Connect failed", "application/json"));

            var bodyError = new MessageBodyError("Connect to RPCP failed, check pre-connect validation", "PRE_CONNECT_INVALID");
            var objectMapper = new ObjectMapper();

            String bodyStr = null;
            try {
                bodyStr = objectMapper.writeValueAsString(bodyError);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
            rpcpError.setBody(bodyStr);

            String rpcpMsgStr = null;
            try {
                rpcpMsgStr = RPCPUtils.write(rpcpError);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                session.sendMessage(rpcpMsgStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        String[] eventsNameArray = this.eventsName.toArray(new String[0]);
        String[] methodsNameArray = this.methodsName.toArray(new String[0]);
        var a = new MessageConnected(vHost, protocol, new HeaderConnected(Config.hostVersion, eventsNameArray, methodsNameArray));
        String rpcpMsgStr = null;
        try {
            rpcpMsgStr = RPCPUtils.write(a);
        } catch (Exception e) {
            e.printStackTrace();
        }

        var rpcpSession = new RPCPSession(session);
        registerRPCPSession(rpcpSession);
        try {
            session.sendMessage(rpcpMsgStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        rpcpHostHandler.onConnect(messageConnect, session);
    }

    private void onRPCPDisconnect(MessageDisconnect messageDisconnect, RPCPWebSocketSession session) {

        if (getRPCPSession(session.getId()) == null) {
            var xxx = new MessageError(".internal", new HeaderError("Disconnect failed", "application/json"));

            var bodyError = new MessageBodyError("Disconnect to RPCP failed, your RPCP session not found", "RPCP_SESSION_NOT_FOUND");
            var objectMapper = new ObjectMapper();

            String bodyStr = null;
            try {
                bodyStr = objectMapper.writeValueAsString(bodyError);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
            xxx.setBody(bodyStr);

            String z = null;
            try {
                z = RPCPUtils.write(xxx);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                session.sendMessage(z);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        var a = new MessageDisconnect(vHost);
        String z = null;
        try {
            z = RPCPUtils.write(a);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            session.sendMessage(z);
        } catch (Exception e) {
            e.printStackTrace();
        }

        removeWebsocketSession(session.getId());
        removeRPCPSession(session.getId());
        rpcpHostHandler.onDisconnect(messageDisconnect, session);
        try {
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onRPCPCall(MessageCall messageCall, RPCPWebSocketSession session) {

        RPCPSession rpcpSession;
        if (getRPCPSession(session.getId()) == null) {
            var xxx = new MessageError(".internal", new HeaderError("Session not valid", "application/json"));
            String z = null;
            try {
                z = RPCPUtils.write(xxx);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                session.sendMessage(z);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        } else {
            rpcpSession = getRPCPSession(session.getId());
        }

        if (methods.containsKey(messageCall.getMethod())) {
            var method = methods.get(messageCall.getMethod());

            var builder = new RPCPBuilder(messageCall);

            method.execute(rpcpSession, messageCall, builder);
        } else {
            try {
                session.sendMessage("method not found");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void registerWebsocketSession(RPCPWebSocketSession session) {
        rpcpWebSocketSessions.putIfAbsent(session.getId(), session);
    }

    private RPCPWebSocketSession getWebsocketSession(String id) {
        return rpcpWebSocketSessions.get(id);
    }

    /**
     * Create a websocket session wrap by RPCP WebsocketSession if the session id not found in sessions list,
     * if websocket session id found in sessions list these object will return.
     *
     * @param session native session from websocket framework ex: spring, micronaut
     * @return WebsocketSession
     */
    private RPCPWebSocketSession getOrRegisterWebsocketSession(NativeWSSession session) {
        var sessionId = rpcpHostHandler.getWsSessionId(session);
        if (getWebsocketSession(sessionId) != null) {
            return getWebsocketSession(sessionId);
        } else {
            var newSession = rpcpHostHandler.buildWebsocketSession(session);
            registerWebsocketSession(newSession);
            return newSession;
        }
    }

    private void removeWebsocketSession(String id) {
        rpcpWebSocketSessions.remove(id);
    }

    private void registerRPCPSession(RPCPSession session) {
        rpcpSessions.putIfAbsent(session.getId(), session);
    }

    private RPCPSession getRPCPSession(String id) {
        return rpcpSessions.get(id);
    }

    private RPCPSession getOrRegisterRPCPSession(RPCPWebSocketSession session) {
        var sessionId = session.getId();
        if (getWebsocketSession(sessionId) != null) {
            return getRPCPSession(sessionId);
        } else {
            var newSession = new RPCPSession(session);
            registerRPCPSession(newSession);
            return newSession;
        }
    }

    private void removeRPCPSession(String id) {
        rpcpSessions.remove(id);
    }

    private static boolean validateProtocolVersion(String protocol) throws Exception {
        var aa = protocol.split("/");
        if (aa.length < 2) {
            throw new Exception("Format Protocol salah");
        }

        if (!aa[0].equalsIgnoreCase("RPCP")) {
            throw new Exception("Format Protocol salah");
        }

        var aaaa = new ArrayList<>(Arrays.asList(aa));
        return aaaa.stream().anyMatch(str -> Config.supportProtocolVersion.contains(str));
    }
}
