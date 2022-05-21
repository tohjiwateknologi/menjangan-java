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

package io.github.math.integrationtest;

import io.github.math.MyRPCPHostHandler;
import io.github.math.SocketMock;
import io.github.math.event.EventAlarm;
import io.github.math.method.Add;
import io.github.math.method.Sum;
import io.github.rpcp.RPCPHost;
import io.github.rpcp.RPCPUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EventIT {

    static RPCPHost<SocketMock> rpcpHost;
    static SocketMock socketMock = new SocketMock();

    @BeforeAll
    static void setup() {
        // Config RPCP
        rpcpHost = new RPCPHost<>("api.glexpress.id/ws", new MyRPCPHostHandler<>());

        // Add Method to RPCP
        rpcpHost.addMethod(new Sum());
        rpcpHost.addMethod(new Add());

        // Add Event to RPCP
        rpcpHost.addEvent(new EventAlarm());

        // Set message and session to websocket handler
        rpcpHost.onWsConnect(socketMock);

        // Set message and session to websocket handler
        rpcpHost.onWsDisconnect(socketMock, "mati");
    }

    @BeforeEach
    void init() {
        socketMock = new SocketMock();
    }

    @Tag("IntegrationTest")
    @Test
    void Event_SuccessValid_EventMessage() {

        socketMock.addMessageListener(message -> {
            System.out.println("\nRPCP Client Receive Message : ");
            System.out.println("-----------------------------");
            System.out.println(message);
            System.out.println("\n");
        });

        StringBuilder strMsg;
        strMsg = new StringBuilder();
        strMsg.append("CONNECT api.glexpress.id/ws RPCP/1.0\r\n");
        strMsg.append("agent: gle-openapi-js/1.0\r\n");
        strMsg.append("\r\n");
        System.out.println(strMsg);
        rpcpHost.onGetMessage(strMsg.toString(), socketMock);

        StringBuilder strExpect;
        strExpect = new StringBuilder();
        strExpect.append("CONNECTED api.glexpress.id/ws RPCP/1.0\r\n");
        strExpect.append("server: Menjangan/1.0\r\n");
        strExpect.append("event: io.github.math.event.EventAlarm\r\n");
        strExpect.append("method: io.github.math.method.Sum, io.github.math.method.Add\r\n");
        strExpect.append("\r\n");

        assertEquals(strExpect.toString(), socketMock.getLastReceiveMessage());

        Map<String, Object> params = new HashMap<>();
        params.put("Name", "Morning wakeup");
        params.put("Clock", 10);
        try {
            rpcpHost.triggerEvent("io.github.math.event.EventAlarm", params, socketMock.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StringBuilder strExpect1;
        strExpect1 = new StringBuilder();
        strExpect1.append("EVENT io.github.math.event.EventAlarm 200\r\n");
        strExpect1.append("content-type: application/json\r\n");
        strExpect1.append("id: 004\r\n");
        strExpect1.append("\r\n");
        strExpect1.append("{\"name\":\"Morning wakeup\",\"clock\":\"10\"}");
        assertEquals(strExpect1.toString(), socketMock.getLastReceiveMessage());
        assertTrue(socketMock.isOpen());
    }

    @Tag("IntegrationTest")
    @Test
    void Event_NotRegisteredEvent_EventMessage() {

        socketMock.addMessageListener(message -> {
            System.out.println("\nRPCP Client Receive Message : ");
            System.out.println("-----------------------------");
            System.out.println(message);
            System.out.println("\n");
        });

        StringBuilder strMsg;
        strMsg = new StringBuilder();
        strMsg.append("CONNECT api.glexpress.id/ws RPCP/1.0\r\n");
        strMsg.append("agent: gle-openapi-js/1.0\r\n");
        strMsg.append("\r\n");
        System.out.println(strMsg);
        rpcpHost.onGetMessage(strMsg.toString(), socketMock);

        StringBuilder strExpect;
        strExpect = new StringBuilder();
        strExpect.append("CONNECTED api.glexpress.id/ws RPCP/1.0\r\n");
        strExpect.append("server: Menjangan/1.0\r\n");
        strExpect.append("event: io.github.math.event.EventAlarm\r\n");
        strExpect.append("method: io.github.math.method.Sum, io.github.math.method.Add\r\n");
        strExpect.append("\r\n");

        assertEquals(strExpect.toString(), socketMock.getLastReceiveMessage());

        Map<String, Object> params = new HashMap<>();
        params.put("Name", "Morning wakeup");
        params.put("Clock", 10);
        try {
            rpcpHost.triggerEvent("io.github.math.event.EventNotExist", params, socketMock.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StringBuilder strExpect1;
        strExpect1 = new StringBuilder();
        strExpect1.append("ERROR .internal\r\n");
        strExpect1.append("message: Event Not Found\r\n");
        strExpect1.append("content-type: application/json\r\n");
        strExpect1.append("\r\n");
        strExpect1.append("{\"detail\":\"Event Not Found\",\"code\":\"Event_NOT_FOUND\"}");
        assertEquals(strExpect1.toString(), socketMock.getLastReceiveMessage());
        assertTrue(socketMock.isOpen());
    }

    @Tag("IntegrationTest")
    @Test
    void Event_RPCPSessionNotFound_EventMessage() {

        socketMock.addMessageListener(message -> {
            System.out.println("\nRPCP Client Receive Message : ");
            System.out.println("-----------------------------");
            System.out.println(message);
            System.out.println("\n");
        });

        StringBuilder strMsg;
        strMsg = new StringBuilder();
        strMsg.append("CONNECT api.glexpress.id/ws RPCP/1.0\r\n");
        strMsg.append("agent: gle-openapi-js/1.0\r\n");
        strMsg.append("\r\n");
        System.out.println(strMsg);
        rpcpHost.onGetMessage(strMsg.toString(), socketMock);

        StringBuilder strExpect;
        strExpect = new StringBuilder();
        strExpect.append("CONNECTED api.glexpress.id/ws RPCP/1.0\r\n");
        strExpect.append("server: Menjangan/1.0\r\n");
        strExpect.append("event: io.github.math.event.EventAlarm\r\n");
        strExpect.append("method: io.github.math.method.Sum, io.github.math.method.Add\r\n");
        strExpect.append("\r\n");

        assertEquals(strExpect.toString(), socketMock.getLastReceiveMessage());

        Map<String, Object> params = new HashMap<>();
        params.put("Name", "Morning wakeup");
        params.put("Clock", 10);

        var exception = assertThrows(Exception.class, () -> {
            rpcpHost.triggerEvent("io.github.math.event.EventNotExist", params, "xxx");
        });
        assertEquals("RPCP Session Not Found", exception.getMessage());
    }

}
