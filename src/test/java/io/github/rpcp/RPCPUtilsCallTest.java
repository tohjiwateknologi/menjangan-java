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

import io.github.rpcp.model.HeaderCall;
import io.github.rpcp.model.MessageCall;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RPCPUtilsCallTest {
    @BeforeAll
    static void setup() {

    }

    @BeforeEach
    void init() {

    }

    @Test
    void Read_CallValid_Success() throws Exception {
        var string = "CALL procedure.sum\r\n" +
                "content-type: application/json\r\n" +
                "id: 001\r\n" +
                "\r\n" +
                "{\"a\":10, \"b\":20}";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageCall);
        var call = (MessageCall) rpcpMessage;
        assertEquals("procedure.sum", call.getMethod());
        assertEquals("application/json", call.getHeader().getContentType());
        assertEquals("001", call.getHeader().getId());
        assertEquals("{\"a\":10, \"b\":20}", call.getBody());
    }

    @Test
    void Read_CallWithoutBody_Success() throws Exception {
        var string = "CALL procedure.sum\r\n" +
                "content-type: application/json\r\n" +
                "id: 001\r\n" +
                "\r\n";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageCall);
        var call = (MessageCall) rpcpMessage;
        assertEquals("procedure.sum", call.getMethod());
        assertEquals("application/json", call.getHeader().getContentType());
        assertEquals("001", call.getHeader().getId());
        assertEquals("", call.getBody());
    }

    @Test
    void Read_CallWrongCommand_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CAL procedure.sum\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"a\":10, \"b\":20}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_CallWithoutDestination_ExceptionParseError() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CALL\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"a\":10, \"b\":20}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Parse error", exception.getMessage());
    }

    @Test
    void Read_CallWithoutContentType_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CALL procedure.sum\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"a\":10, \"b\":20}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_CallWithoutId_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CALL procedure.sum\r\n" +
                    "content-type: application/json\r\n" +
                    "\r\n" +
                    "{\"a\":10, \"b\":20}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_CallWithoutAllHeader_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CALL procedure.sum\r\n" +
                    "\r\n" +
                    "{\"a\":10, \"b\":20}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Write_CallValid_Success() throws Exception {
        var header = new HeaderCall("application/json", "001");
        var call = new MessageCall("procedure.sum", header);
        call.setBody("{\"a\": 10, \"b\": 10}");

        var actual = RPCPUtils.write(call);
        var expected = "CALL procedure.sum\r\n" +
                "content-type: application/json\r\n" +
                "id: 001\r\n" +
                "\r\n" +
                "{\"a\": 10, \"b\": 10}";

        assertEquals(expected, actual);
    }

    @Test
    void Write_CallAllHeaderNull_Success() throws Exception {
        var header = new HeaderCall(null, null);
        var call = new MessageCall("procedure.sum", header);
        call.setBody("{\"a\": 10, \"b\": 10}");

        var actual = RPCPUtils.write(call);
        var expected = "CALL procedure.sum\r\n" +
                "content-type: null\r\n" +
                "id: null\r\n" +
                "\r\n" +
                "{\"a\": 10, \"b\": 10}";

        assertEquals(expected, actual);
    }

    @Test
    void Write_CallNull_Success() throws Exception {
        var call = new MessageCall(null, null);

        var actual = RPCPUtils.write(call);
        var expected = "CALL null\r\n" +
                "content-type: null\r\n" +
                "id: null\r\n" +
                "\r\n";

        assertEquals(expected, actual);
    }
}
