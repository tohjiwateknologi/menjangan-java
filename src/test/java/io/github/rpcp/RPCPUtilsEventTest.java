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

import io.github.rpcp.model.HeaderEvent;
import io.github.rpcp.model.MessageEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RPCPUtilsEventTest {

    @BeforeAll
    static void setup() {
        //log.info("@BeforeAll - executes once before all test methods in this class");
    }

    @BeforeEach
    void init() {
        //log.info("@BeforeEach - executes before each test method in this class");
    }

    @Test
    void Read_EventValid_Success() throws Exception {
        var string = "EVENT event.onRequest 200\r\n" +
                "content-type: application/json\r\n" +
                "id: 003\r\n" +
                "\r\n" +
                "{\"status\":You got a new order}";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageEvent);
        var event = (MessageEvent) rpcpMessage;
        assertEquals("event.onRequest", event.getMethod());
        assertEquals("200", event.getStatusCode());
        assertEquals("application/json", event.getHeader().getContentType());
        assertEquals("003", event.getHeader().getId());
        assertEquals("{\"status\":You got a new order}", event.getBody());
    }

    @Test
    void Read_EventWithoutBody_Success() throws Exception {
        var string = "EVENT event.onRequest 200\r\n" +
                "content-type: application/json\r\n" +
                "id: 003\r\n" +
                "\r\n";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageEvent);
        var event = (MessageEvent) rpcpMessage;
        assertEquals("event.onRequest", event.getMethod());
        assertEquals("200", event.getStatusCode());
        assertEquals("application/json", event.getHeader().getContentType());
        assertEquals("003", event.getHeader().getId());
        assertEquals("", event.getBody());
    }

    @Test
    void Read_EventWrongCommand_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "EVEN event.onRequest 200\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 003\r\n" +
                    "\r\n" +
                    "{\"status\":You got a new order}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_EventWithoutStatusCode_ExceptionParseError() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "EVENT event.onRequest\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 003\r\n" +
                    "\r\n" +
                    "{\"status\":You got a new order}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Parse error", exception.getMessage());
    }

    @Test
    void Read_EventWithoutDestination_ExceptionParseError() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "EVENT 200\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 003\r\n" +
                    "\r\n" +
                    "{\"status\":You got a new order}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Parse error", exception.getMessage());
    }

    @Test
    void Read_EventWithoutContentType_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "EVENT event.onRequest 200\r\n" +
                    "id: 003\r\n" +
                    "\r\n" +
                    "{\"status\":You got a new order}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_EventWithoutId_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "EVENT event.onRequest 200\r\n" +
                    "content-type: application/json\r\n" +
                    "\r\n" +
                    "{\"status\":You got a new order}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_EventWithoutAllHeader_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "EVENT event.onRequest 200\r\n" +
                    "\r\n" +
                    "{\"status\":You got a new order}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Write_EventValid_Success() throws Exception {
        var header = new HeaderEvent("application/json", "003");
        var event = new MessageEvent("event.onRequest", "200", header);
        event.setBody("{\"status\": \"You get new order\"}");

        var actual = RPCPUtils.write(event);
        var expected = "EVENT event.onRequest 200\r\n" +
                "content-type: application/json\r\n" +
                "id: 003\r\n" +
                "\r\n" +
                "{\"status\": \"You get new order\"}";

        assertEquals(expected, actual);
    }

    @Test
    void Write_EventNull_Success() throws Exception {
        var event = new MessageEvent(null, null, null);

        var actual = RPCPUtils.write(event);
        var expected = "EVENT null null\r\n" +
                "content-type: null\r\n" +
                "id: null\r\n" +
                "\r\n";

        assertEquals(expected, actual);
    }
}
