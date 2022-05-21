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
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectIT {

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
    void Connect_Success_ConnectedMessage() {

        StringBuilder strMsg;
        strMsg = new StringBuilder();
        strMsg.append("CONNECT api.glexpress.id/ws RPCP/1.0\r\n");
        strMsg.append("agent: gle-openapi-js/1.0\r\n");
        strMsg.append("\r\n");
        System.out.println(strMsg);
        rpcpHost.onGetMessage(strMsg.toString(), socketMock);

        System.out.println("\nRPCP Client Receive Message : ");
        System.out.println("-----------------------------");
        System.out.println(socketMock.getLastReceiveMessage());
        System.out.println("\n");

        StringBuilder strExpect;
        strExpect = new StringBuilder();
        strExpect.append("CONNECTED api.glexpress.id/ws RPCP/1.0\r\n");
        strExpect.append("server: Menjangan/1.0\r\n");
        strExpect.append("event: io.github.math.event.EventAlarm\r\n");
        strExpect.append("method: io.github.math.method.Sum, io.github.math.method.Add\r\n");
        strExpect.append("\r\n");

        assertEquals(strExpect.toString(), socketMock.getLastReceiveMessage());
        assertTrue(socketMock.isOpen());
    }

    @Tag("IntegrationTest")
    @Test
    void Connect_IncorrectCommand_ErrorMessage() {

        StringBuilder strMsg;
        strMsg = new StringBuilder();
        strMsg.append("CONNEC api.glexpress.id/ws RPCP/1.0\r\n");
        strMsg.append("agent: gle-openapi-js/1.0\r\n");
        strMsg.append("\r\n");
        System.out.println(strMsg);
        rpcpHost.onGetMessage(strMsg.toString(), socketMock);

        System.out.println("\nRPCP Client Receive Message : ");
        System.out.println("-----------------------------");
        System.out.println(socketMock.getLastReceiveMessage());
        System.out.println("\n");

        StringBuilder strExpect;
        strExpect = new StringBuilder();
        strExpect.append("ERROR .internal\r\n");
        strExpect.append("message: Invalid RPCP\r\n");
        strExpect.append("content-type: application/json\r\n");
        strExpect.append("\r\n");
        strExpect.append("{\"detail\":\"Invalid RPCP, check your RPCP message\",\"code\":\"INVALID_RPCP\"}");

        assertEquals(strExpect.toString(), socketMock.getLastReceiveMessage());
        assertFalse(socketMock.isOpen());
    }

    @Tag("IntegrationTest")
    @Test
    void Connect_IncorrectProtocol_ErrorMessage() {

        StringBuilder strMsg;
        strMsg = new StringBuilder();
        strMsg.append("CONNECT api.glexpress.id/ws RPC/1.0\r\n");
        strMsg.append("agent: gle-openapi-js/1.0\r\n");
        strMsg.append("\r\n");
        System.out.println(strMsg);
        rpcpHost.onGetMessage(strMsg.toString(), socketMock);

        System.out.println("\nRPCP Client Receive Message : ");
        System.out.println("-----------------------------");
        System.out.println(socketMock.getLastReceiveMessage());
        System.out.println("\n");

        StringBuilder strExpect;
        strExpect = new StringBuilder();
        strExpect.append("ERROR .internal\r\n");
        strExpect.append("message: Incorrect protocol format\r\n");
        strExpect.append("content-type: application/json\r\n");
        strExpect.append("\r\n");
        strExpect.append("{\"detail\":\"Incorrect protocol format, check your RPCP message\",\"code\":\"INVALID_RPCP\"}");

        assertEquals(strExpect.toString(), socketMock.getLastReceiveMessage());
        assertFalse(socketMock.isOpen());
    }

    @Tag("IntegrationTest")
    @Test
    void Connect_vHostInvalid_ErrorMessage() {

        StringBuilder strMsg;
        strMsg = new StringBuilder();
        strMsg.append("CONNECT api.tohjiwateknologi.co.id RPCP/1.0\r\n");
        strMsg.append("agent: gle-openapi-js/1.0\r\n");
        strMsg.append("\r\n");
        System.out.println(strMsg);
        rpcpHost.onGetMessage(strMsg.toString(), socketMock);

        System.out.println("\nRPCP Client Receive Message : ");
        System.out.println("-----------------------------");
        System.out.println(socketMock.getLastReceiveMessage());
        System.out.println("\n");

        StringBuilder strExpect;
        strExpect = new StringBuilder();
        strExpect.append("ERROR .internal\r\n");
        strExpect.append("message: Connect failed\r\n");
        strExpect.append("content-type: application/json\r\n");
        strExpect.append("\r\n");
        strExpect.append("{\"detail\":\"Connect to RPCP failed, check your vHost\",\"code\":\"VHOST_NOT_FOUND\"}");

        assertEquals(strExpect.toString(), socketMock.getLastReceiveMessage());
        assertFalse(socketMock.isOpen());
    }

    @Tag("IntegrationTest")
    @Test
    void Connect_MissingMandatoryHeader_ErrorMessage() {

        StringBuilder strMsg;
        strMsg = new StringBuilder();
        strMsg.append("CONNECT api.tohjiwateknologi.co.id RPCP/1.0\r\n");
        strMsg.append("\r\n");
        System.out.println(strMsg);
        rpcpHost.onGetMessage(strMsg.toString(), socketMock);

        System.out.println("\nRPCP Client Receive Message : ");
        System.out.println("-----------------------------");
        System.out.println(socketMock.getLastReceiveMessage());
        System.out.println("\n");

        StringBuilder strExpect;
        strExpect = new StringBuilder();
        strExpect.append("ERROR .internal\r\n");
        strExpect.append("message: Invalid RPCP\r\n");
        strExpect.append("content-type: application/json\r\n");
        strExpect.append("\r\n");
        strExpect.append("{\"detail\":\"Invalid RPCP, check your RPCP message\",\"code\":\"INVALID_RPCP\"}");

        assertEquals(strExpect.toString(), socketMock.getLastReceiveMessage());
        assertFalse(socketMock.isOpen());
    }
}
