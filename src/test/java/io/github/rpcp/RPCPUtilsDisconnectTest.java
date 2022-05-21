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

import io.github.rpcp.model.MessageDisconnect;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RPCPUtilsDisconnectTest {
    @BeforeAll
    static void setup() {

    }

    @BeforeEach
    void init() {

    }

    @Test
    void Read_DisconnectValid_Success() throws Exception {
        var string = "DISCONNECT api.rpcp.org\r\n" +
                "\r\n";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageDisconnect);
        var disconnect = (MessageDisconnect) rpcpMessage;
        assertEquals("api.rpcp.org", disconnect.getvHost());
    }

    @Test
    void Read_DisconnectWrongCommand_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "DISCONNEC\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_DisconnectWithoutVHost_ExceptionParseError() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "DISCONNECT\r\n" +
                    "\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Parse error", exception.getMessage());
    }

    @Test
    void Write_DisconnectValid_Success() throws Exception {
        var disconnect = new MessageDisconnect("api.rpcp.org");

        var actual = RPCPUtils.write(disconnect);
        var expected = "DISCONNECT api.rpcp.org\r\n" +
                "\r\n";

        assertEquals(expected, actual);
    }

    @Test
    void Write_DisconnectNull_Success() throws Exception {
        var disconnect = new MessageDisconnect(null);

        var actual = RPCPUtils.write(disconnect);
        var expected = "DISCONNECT null\r\n" +
                "\r\n";

        assertEquals(expected, actual);
    }
}
