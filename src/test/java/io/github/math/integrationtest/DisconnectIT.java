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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DisconnectIT {

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
    public void Disconnect_DisconnectWithoutConnect_ErrorMessage() {

        var strMsg = "DISCONNECT api.glexpress.id/ws\r\n" +
                "\r\n";
        System.out.println(strMsg);
        rpcpHost.onGetMessage(strMsg, socketMock);

        System.out.println("\nRPCP Client Receive Message : ");
        System.out.println("-----------------------------");
        System.out.println(socketMock.getLastReceiveMessage());
        System.out.println("\n");

        StringBuilder strExpect;
        strExpect = new StringBuilder();
        strExpect.append("ERROR .internal\r\n");
        strExpect.append("message: Disconnect failed\r\n");
        strExpect.append("content-type: application/json\r\n");
        strExpect.append("\r\n");
        strExpect.append("{\"detail\":\"Disconnect to RPCP failed, your RPCP session not found\",\"code\":\"RPCP_SESSION_NOT_FOUND\"}");

        assertEquals(strExpect.toString(), socketMock.getLastReceiveMessage());
        assertFalse(socketMock.isOpen());
    }

    @Tag("IntegrationTest")
    @Test
    public void Disconnect_Success_DisconnectMessage() {

        var strMsg1 = "CONNECT api.glexpress.id/ws RPCP/1.0\r\n" +
                "agent: gle-openapi-js/1.0\r\n" +
                "\r\n";
        System.out.println(strMsg1);
        rpcpHost.onGetMessage(strMsg1, socketMock);

        System.out.println("\nRPCP Client Receive Message : ");
        System.out.println("-----------------------------");
        System.out.println(socketMock.getLastReceiveMessage());
        System.out.println("\n");

        var strMsg2 = "DISCONNECT api.glexpress.id/ws\r\n" +
                "\r\n";
        System.out.println(strMsg2);
        rpcpHost.onGetMessage(strMsg2, socketMock);

        System.out.println("\nRPCP Client Receive Message : ");
        System.out.println("-----------------------------");
        System.out.println(socketMock.getLastReceiveMessage());
        System.out.println("\n");

        StringBuilder strExpect;
        strExpect = new StringBuilder();
        strExpect.append("DISCONNECT api.glexpress.id/ws\r\n");
        strExpect.append("\r\n");

        assertEquals(strExpect.toString(), socketMock.getLastReceiveMessage());
        assertFalse(socketMock.isOpen());
    }
}
