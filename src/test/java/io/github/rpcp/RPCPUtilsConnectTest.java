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

import io.github.rpcp.model.HeaderConnect;
import io.github.rpcp.model.MessageConnect;
import io.github.rpcp.model.RPCPMessageDefault;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RPCPUtilsConnectTest {

    @BeforeAll
    static void setup() {

    }

    @BeforeEach
    void init() {

    }

    @Test
    void Read_ConnectValid_Success() throws Exception {
        var string = "CONNECT api.rpcp.org RPCP/1.0\r\n" +
                "agent: menjangan-js/1.0\r\n" +
                "\r\n";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageConnect);
        var connect = (MessageConnect) rpcpMessage;
        assertEquals("api.rpcp.org", connect.getvHost());
        assertEquals("RPCP/1.0", connect.getProtocolVersion());
    }

    @Test
    void Read_ConnectValidHeadCleaner_Success() throws Exception {
        var string = "CONNECT api.rpcp.org RPCP/1.0\r\n" +
                "agent:menjangan-js/1.0\r\n" +
                "\r\n";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageConnect);
        var connect = (MessageConnect) rpcpMessage;
        assertEquals("api.rpcp.org", connect.getvHost());
        assertEquals("RPCP/1.0", connect.getProtocolVersion());
    }

    @Test
    void Write_ConnectDefaultMessage_ExceptionNotCompatible() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var connect = new RPCPMessageDefault();
            RPCPUtils.write(connect);
        });
        assertEquals("Not Compatible", exception.getMessage());
    }

    @Test
    void Write_ConnectValid_Success() throws Exception {
        var connect = new MessageConnect("api.rpcp.org", "RPCP/1.0", new HeaderConnect("menjangan-js/1.0"));

        var actual = RPCPUtils.write(connect);
        var expected = "CONNECT api.rpcp.org RPCP/1.0\r\n" +
                "agent: menjangan-js/1.0\r\n" +
                "\r\n";

        assertEquals(expected, actual);
    }

    @Test
    void Write_ConnectWithNull_Success() throws Exception {
        var connect = new MessageConnect(null, null, null);

        var actual = RPCPUtils.write(connect);
        var expected = "CONNECT null null\r\n" +
                "agent: null\r\n" +
                "\r\n";

        assertEquals(expected, actual);
    }

    @Test
    void Read_ConnectWrongCommand_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNEC api.rpcp.org RPCP/1.0\r\n" +
                    "agent: menjangan-js/1.0\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ConnectInvalidProtocol_ExceptionInvalidProtocolFormat() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECT api.rpcp.org RPC/1.0\r\n" +
                    "agent: menjangan-js/1.0\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Incorrect Protocol Format", exception.getMessage());
    }

    @Test
    void Read_ConnectInvalidProtocolVersion_ExceptionInvalidProtocolFormat() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECT api.rpcp.org RPC\r\n" +
                    "agent: menjangan-js/1.0\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Incorrect Protocol Format", exception.getMessage());
    }

    @Test
    void Read_ConnectWithoutProtocol_ExceptionParseError() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECT api.rpcp.org\r\n" +
                    "agent: menjangan-js/1.0\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Parse error", exception.getMessage());
    }

    @Test
    void Read_ConnectWithoutVHost_ExceptionParseError() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECT RPCP/1.0\r\n" +
                    "agent: menjangan-js/1.0\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Parse error", exception.getMessage());
    }

    @Test
    void Read_ConnectWithoutAgent_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECT api.rpcp.org RPCP/1.0\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ConnectWithoutAgentAddCustomHeader_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECT api.rpcp.org RPCP/1.0\r\n" +
                    "test: test\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ConnectWithoutAll_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "CONNECT api.rpcp.org RPCP/1.0\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }
}
