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

import io.github.rpcp.model.HeaderConnected;
import io.github.rpcp.model.MessageConnected;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RPCPUtilsConnectedTest {
    @BeforeAll
    static void setup() {

    }

    @BeforeEach
    void init() {

    }

    @Test
    void Read_ConnectedValid_Success() throws Exception {
        var string = "CONNECTED api.rpcp.org RPCP/1.0\r\n" +
                "server: Apache/1.3.9\r\n" +
                "event: event.onRequest, event.onCustomerCancel\r\n" +
                "method: procedure.sum, procedure.add\r\n" +
                "\r\n";
        
        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageConnected);
        var connected = (MessageConnected) rpcpMessage;
        assertEquals("api.rpcp.org", connected.getvHost());
        assertEquals("RPCP/1.0", connected.getProtocolVersion());
        assertEquals("Apache/1.3.9", connected.getHeader().getServer());
        assertEquals("[event.onRequest, event.onCustomerCancel]", Arrays.toString(connected.getHeader().getEvent()));
        assertEquals("[procedure.sum, procedure.add]", Arrays.toString(connected.getHeader().getMethod()));
    }

    @Test
    void Read_ConnectedWrongCommand_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECTE api.rpcp.org RPCP/1.0\r\n" +
                    "server: Apache/1.3.9\r\n" +
                    "event: event.onRequest, event.onCustomerCancel\r\n" +
                    "method: procedure.sum, procedure.add\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ConnectedInvalidProtocol_ExceptionProtocolFormat() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECTED api.rpcp.org RPC/1.0\r\n" +
                    "server: Apache/1.3.9\r\n" +
                    "event: event.onRequest, event.onCustomerCancel\r\n" +
                    "method: procedure.sum, procedure.add\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Incorrect Protocol Format", exception.getMessage());
    }

    @Test
    void Read_ConnectedWithoutProtocol_ExceptionParseError() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECTED api.rpcp.org\r\n" +
                    "server: Apache/1.3.9\r\n" +
                    "event: event.onRequest, event.onCustomerCancel\r\n" +
                    "method: procedure.sum, procedure.add\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Parse error", exception.getMessage());
    }

    @Test
    void Read_ConnectedWithoutVHost_ExceptionParseError() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECTED RPCP/1.0\r\n" +
                    "server: Apache/1.3.9\r\n" +
                    "event: event.onRequest, event.onCustomerCancel\r\n" +
                    "method: procedure.sum, procedure.add\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Parse error", exception.getMessage());
    }

    @Test
    void Read_ConnectedWithoutServer_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECTED api.rpcp.org RPCP/1.0\r\n" +
                    "event: event.onRequest, event.onCustomerCancel\r\n" +
                    "method: procedure.sum, procedure.add\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ConnectedWithoutEvent_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECTED api.rpcp.org RPCP/1.0\r\n" +
                    "server: Apache/1.3.9\r\n" +
                    "method: procedure.sum, procedure.add\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ConnectedWithoutServerAndEvent_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECTED api.rpcp.org RPCP/1.0\r\n" +
                    "method: procedure.sum, procedure.add\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ConnectedWithoutMethod_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECTED api.rpcp.org RPCP/1.0\r\n" +
                    "server: Apache/1.3.9\r\n" +
                    "event: event.onRequest, event.onCustomerCancel\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ConnectedWithoutAllMandatoryHeader_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECTED api.rpcp.org RPCP/1.0\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Write_ConnectedValid_Success() throws Exception {
        var events = new String[]{"event.onRequest", "event.onCustomerCancel"};
        var methods = new String[]{"procedure.sum", "procedure.add"};
        var header = new HeaderConnected("BlackSwamp/1.0", events, methods);
        var connected = new MessageConnected("api.rpcp.org", "RPCP/1.0", header);

        var actual = RPCPUtils.write(connected);
        var expected = "CONNECTED api.rpcp.org RPCP/1.0\r\n" +
                "server: BlackSwamp/1.0\r\n" +
                "event: event.onRequest, event.onCustomerCancel\r\n" +
                "method: procedure.sum, procedure.add\r\n" +
                "\r\n";

        assertEquals(expected, actual);
    }

    @Test
    void Write_ConnectedWithoutEventAndMethod_Success() throws Exception {
        var events = new String[]{};
        var methods = new String[]{};
        var header = new HeaderConnected("BlackSwamp/1.0", events, methods);
        var connected = new MessageConnected("api.rpcp.org", "RPCP/1.0", header);

        var actual = RPCPUtils.write(connected);
        var expected = "CONNECTED api.rpcp.org RPCP/1.0\r\n" +
                "server: BlackSwamp/1.0\r\n" +
                "event: \r\n" +
                "method: \r\n" +
                "\r\n";

        assertEquals(expected, actual);
    }

    @Test
    void Write_ConnectedNull_Success() throws Exception {
        var connected = new MessageConnected(null, null, null);

        var actual = RPCPUtils.write(connected);
        var expected = "CONNECTED null null\r\n" +
                "server: null\r\n" +
                "event: null\r\n" +
                "method: null\r\n" +
                "\r\n";

        assertEquals(expected, actual);
    }
}
