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

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class CallIT {

    static RPCPHost<SocketMock> rpcpHost;
    static SocketMock socketMock = new SocketMock();

    @BeforeAll
    static void setup() {
        // Config RPCP
        rpcpHost = new RPCPHost<>("api.glexpress.id/ws", new MyRPCPHostHandler<>());

        // Add Method to RPCP
        rpcpHost.addMethod(new Sum());
        rpcpHost.addMethod(new Add());
        rpcpHost.addMethod("custom.add", new Add());

        // Add Event to RPCP
        rpcpHost.addEvent(new EventAlarm());
        rpcpHost.addEvent("custom.eventAlarm", new EventAlarm());

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
    public void Call_AddSuccess_ResultMessage() {

        socketMock.addMessageListener(message -> {
            System.out.println("\nRPCP Client Receive Message : ");
            System.out.println("-----------------------------");
            System.out.println(message);
            System.out.println("\n");
        });

        var strMsg1 = "CONNECT api.glexpress.id/ws RPCP/1.0\r\n" +
                "agent: gle-openapi-js/1.0\r\n" +
                "\r\n";
        System.out.println(strMsg1);
        rpcpHost.onGetMessage(strMsg1, socketMock);

        var strMsg2 = "CALL io.github.math.method.Add\r\n" +
                "content-type: application/json\r\n" +
                "id: 002\r\n" +
                "\r\n" +
                "{\"a\": 30, \"b\": 20}";
        System.out.println(strMsg2);
        rpcpHost.onGetMessage(strMsg2, socketMock);

        StringBuilder strExpect;
        strExpect = new StringBuilder();
        strExpect.append("RESULT io.github.math.method.Add 200\r\n");
        strExpect.append("content-type: application/json\r\n");
        strExpect.append("id: 002\r\n");
        strExpect.append("\r\n");
        strExpect.append("{\"result\": 50}");

        assertEquals(strExpect.toString(), socketMock.getLastReceiveMessage());
        assertTrue(socketMock.isOpen());
    }

    @Tag("IntegrationTest")
    @Test
    public void Call_CustomAddSuccess_ResultMessage() {

        socketMock.addMessageListener(message -> {
            System.out.println("\nRPCP Client Receive Message : ");
            System.out.println("-----------------------------");
            System.out.println(message);
            System.out.println("\n");
        });

        var strMsg1 = "CONNECT api.glexpress.id/ws RPCP/1.0\r\n" +
                "agent: gle-openapi-js/1.0\r\n" +
                "\r\n";
        System.out.println(strMsg1);
        rpcpHost.onGetMessage(strMsg1, socketMock);

        var strMsg2 = "CALL custom.add\r\n" +
                "content-type: application/json\r\n" +
                "id: 002\r\n" +
                "\r\n" +
                "{\"a\": 30, \"b\": 20}";
        System.out.println(strMsg2);
        rpcpHost.onGetMessage(strMsg2, socketMock);

        StringBuilder strExpect;
        strExpect = new StringBuilder();
        strExpect.append("RESULT custom.add 200\r\n");
        strExpect.append("content-type: application/json\r\n");
        strExpect.append("id: 002\r\n");
        strExpect.append("\r\n");
        strExpect.append("{\"result\": 50}");

        assertEquals(strExpect.toString(), socketMock.getLastReceiveMessage());
        assertTrue(socketMock.isOpen());
    }

    @Tag("IntegrationTest")
    @Test
    public void Call_SumSuccess_ResultMessage() {

        AtomicInteger msgNumber = new AtomicInteger();
        socketMock.addMessageListener(message -> {
            System.out.println("\nRPCP Client Receive Message : ");
            System.out.println("-----------------------------");
            System.out.println(message);
            System.out.println("\n");

            if(msgNumber.get() == 0) {
                StringBuilder strExpect;
                strExpect = new StringBuilder();
                strExpect.append("CONNECTED api.glexpress.id/ws RPCP/1.0\r\n");
                strExpect.append("server: Menjangan/1.0\r\n");
                strExpect.append("event: io.github.math.event.EventAlarm, custom.eventAlarm\r\n");
                strExpect.append("method: io.github.math.method.Sum, io.github.math.method.Add, custom.add\r\n");
                strExpect.append("\r\n");
                assertEquals(strExpect.toString(), message);
            }

            if(msgNumber.get() == 1) {
                StringBuilder strExpect;
                strExpect = new StringBuilder();
                strExpect.append("PLAN io.github.math.method.Sum\r\n");
                strExpect.append("planing-code: REQUEST_DRIVER, GET_DRIVER\r\n");
                strExpect.append("content-type: application/json\r\n");
                strExpect.append("id: 002\r\n");
                strExpect.append("\r\n");
                strExpect.append("kami sudah menyiapkan rencana yang terbaik");
                assertEquals(strExpect.toString(), message);
            }

            if(msgNumber.get() == 2) {
                StringBuilder strExpect;
                strExpect = new StringBuilder();
                strExpect.append("PROGRESS io.github.math.method.Sum 200\r\n");
                strExpect.append("plan-code: REQUEST_DRIVER\r\n");
                strExpect.append("content-type: application/json\r\n");
                strExpect.append("id: 002\r\n");
                strExpect.append("\r\n");
                strExpect.append("kami sudah menerima dan mencarikan driver yang tepat untuk anda");
                assertEquals(strExpect.toString(), message);
            }

            if(msgNumber.get() == 3) {
                StringBuilder strExpect;
                strExpect = new StringBuilder();
                strExpect.append("PROGRESS io.github.math.method.Sum 200\r\n");
                strExpect.append("plan-code: GET_DRIVER\r\n");
                strExpect.append("content-type: application/json\r\n");
                strExpect.append("id: 002\r\n");
                strExpect.append("\r\n");
                strExpect.append("kami sudah menerima dan mencarikan driver yang tepat untuk anda");
                assertEquals(strExpect.toString(), message);
            }

            if(msgNumber.get() == 4) {
                StringBuilder strExpect;
                strExpect = new StringBuilder();
                strExpect.append("RESULT io.github.math.method.Sum 200\r\n");
                strExpect.append("content-type: application/json\r\n");
                strExpect.append("id: 002\r\n");
                strExpect.append("\r\n");
                strExpect.append("result: 20");
                assertEquals(strExpect.toString(), message);
            }

            msgNumber.getAndIncrement();
        });

        var strMsg1 = "CONNECT api.glexpress.id/ws RPCP/1.0\r\n" +
                "agent: gle-openapi-js/1.0\r\n" +
                "\r\n";
        System.out.println(strMsg1);
        rpcpHost.onGetMessage(strMsg1, socketMock);

        var strMsg2 = "CALL io.github.math.method.Sum\r\n" +
                "content-type: application/json\r\n" +
                "id: 002\r\n" +
                "\r\n" +
                "{\"lat\": 10, \"lng\": 10}";
        System.out.println(strMsg2);
        rpcpHost.onGetMessage(strMsg2, socketMock);
        assertTrue(socketMock.isOpen());
    }

    @Tag("IntegrationTest")
    @Test
    public void Call_CallWithoutSession_ErrorMessage() {

        socketMock.addMessageListener(message -> {
            System.out.println("\nRPCP Client Receive Message : ");
            System.out.println("-----------------------------");
            System.out.println(message);
            System.out.println("\n");
        });

        var strMsg = "CALL io.github.math.method.Sum\r\n" +
                "content-type: application/json\r\n" +
                "id: 002\r\n" +
                "\r\n" +
                "{\"lat\": 10, \"lng\": 10}";
        System.out.println(strMsg);
        rpcpHost.onGetMessage(strMsg, socketMock);

        StringBuilder strExpect;
        strExpect = new StringBuilder();
        strExpect.append("ERROR .internal\r\n");
        strExpect.append("message: Session not valid\r\n");
        strExpect.append("content-type: application/json\r\n");
        strExpect.append("\r\n");

        assertEquals(strExpect.toString(), socketMock.getLastReceiveMessage());
        assertFalse(socketMock.isOpen());
    }
}
