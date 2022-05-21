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

import io.github.rpcp.model.HeaderError;
import io.github.rpcp.model.MessageError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RPCPUtilsErrorTest {
    @BeforeAll
    static void setup() {

    }

    @BeforeEach
    void init() {

    }

    @Test
    void Read_ErrorValid_Success() throws Exception {
        var string = "ERROR procedure.sum\r\n" +
                "message: method not found\r\n" +
                "content-type: application/json\r\n" +
                "id: 001\r\n" +
                "\r\n" +
                "{\"status\":Method not found}";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageError);
        var error = (MessageError) rpcpMessage;
        assertEquals("procedure.sum", error.getMethod());
        assertEquals("method not found", error.getHeader().getMessage());
        assertEquals("application/json", error.getHeader().getContentType());
        assertEquals("001", error.getHeader().getId());
        assertEquals("{\"status\":Method not found}", error.getBody());
    }

    @Test
    void Read_ErrorWithoutId_Success() throws Exception {
        var string = "ERROR .internal\r\n" +
                "message: connect failed\r\n" +
                "content-type: application/json\r\n" +
                "\r\n" +
                "{\"status\":Agent not supported}";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageError);
        var error = (MessageError) rpcpMessage;
        assertEquals(".internal", error.getMethod());
        assertEquals("connect failed", error.getHeader().getMessage());
        assertEquals("application/json", error.getHeader().getContentType());
        assertNull(error.getHeader().getId());
        assertEquals("{\"status\":Agent not supported}", error.getBody());
    }

    @Test
    void Read_ErrorWrongCommand_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "ERRO procedure.sum\r\n" +
                    "message: method not found\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"status\":Method not found}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ErrorWithoutDestination_ExceptionParseError() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "ERROR\r\n" +
                    "message: method not found\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"status\":Method not found}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Parse error", exception.getMessage());
    }

    @Test
    void Read_ErrorWithoutMessage_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "ERROR procedure.sum\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"status\":Method not found}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ErrorWithoutContentType_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "ERROR procedure.sum\r\n" +
                    "message: method not found\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"status\":Method not found}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ErrorWithoutAllHeader_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "ERROR procedure.sum\r\n" +
                    "\r\n" +
                    "{\"status\":Method not found}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Write_ErrorValid_Success() throws Exception {
        var header = new HeaderError("Method not found", "application/json", "003");
        var event = new MessageError("procedure.sum", header);
        event.setBody("{\"status\": \"Method not found\"}");

        var actual = RPCPUtils.write(event);
        var expected = "ERROR procedure.sum\r\n" +
                "message: Method not found\r\n" +
                "content-type: application/json\r\n" +
                "id: 003\r\n" +
                "\r\n" +
                "{\"status\": \"Method not found\"}";

        assertEquals(expected, actual);
    }

    @Test
    void Write_ErrorWithoutId_Success() throws Exception {
        var header = new HeaderError("Connect failed", "application/json");
        var event = new MessageError("procedure.sum", header);
        event.setBody("{\"status\": \"Method not found\"}");

        var actual = RPCPUtils.write(event);
        var expected = "ERROR procedure.sum\r\n" +
                "message: Connect failed\r\n" +
                "content-type: application/json\r\n" +
                "\r\n" +
                "{\"status\": \"Method not found\"}";

        assertEquals(expected, actual);
    }

    @Test
    void Write_ErrorNull_Success() throws Exception {
        var error = new MessageError(null, null);
        error.setBody(null);

        var actual = RPCPUtils.write(error);
        var expected = "ERROR null\r\n" +
                "message: null\r\n" +
                "content-type: null\r\n" +
                "\r\n";

        assertEquals(expected, actual);
    }
}
