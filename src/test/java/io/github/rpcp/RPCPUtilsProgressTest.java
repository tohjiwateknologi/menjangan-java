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

import io.github.rpcp.model.HeaderProgress;
import io.github.rpcp.model.MessageProgress;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RPCPUtilsProgressTest {
    @BeforeAll
    static void setup() {
        //log.info("@BeforeAll - executes once before all test methods in this class");
    }

    @BeforeEach
    void init() {
        //log.info("@BeforeEach - executes before each test method in this class");
    }

    @Test
    void Read_ProgressValid_Success() throws Exception {
        var string = "PROGRESS procedure.getDriver 200\r\n" +
                "plan-code: FOUND_DRIVER_CANDIDATE\r\n" +
                "content-type: application/json\r\n" +
                "id: 001\r\n" +
                "\r\n" +
                "{\"status\": We found the right candidate for you.}";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageProgress);
        var progress = (MessageProgress) rpcpMessage;
        assertEquals("procedure.getDriver", progress.getMethod());
        assertEquals("200", progress.getStatusCode());
        assertEquals("FOUND_DRIVER_CANDIDATE", progress.getHeader().getPlanCode());
        assertEquals("application/json", progress.getHeader().getContentType());
        assertEquals("001", progress.getHeader().getId());
        assertEquals("{\"status\": We found the right candidate for you.}", progress.getBody());
    }

    @Test
    void Read_ProgressWithoutBody_Success() throws Exception {
        var string = "PROGRESS procedure.getDriver 200\r\n" +
                "plan-code: FOUND_DRIVER_CANDIDATE\r\n" +
                "content-type: application/json\r\n" +
                "id: 001\r\n" +
                "\r\n";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageProgress);
        var progress = (MessageProgress) rpcpMessage;
        assertEquals("procedure.getDriver", progress.getMethod());
        assertEquals("200", progress.getStatusCode());
        assertEquals("FOUND_DRIVER_CANDIDATE", progress.getHeader().getPlanCode());
        assertEquals("application/json", progress.getHeader().getContentType());
        assertEquals("001", progress.getHeader().getId());
        assertEquals("", progress.getBody());
    }

    @Test
    void Read_ProgressWrongCommand_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "PROGRES procedure.getDriver 200\r\n" +
                    "plan-code: FOUND_DRIVER_CANDIDATE\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"status\": We found the right candidate for you.}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ProgressWithoutStatusCode_ExceptionParseError() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "PROGRESS procedure.getDriver\r\n" +
                    "plan-code: FOUND_DRIVER_CANDIDATE\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"status\": We found the right candidate for you.}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Parse error", exception.getMessage());
    }

    @Test
    void Read_ProgressWithoutDestination_ExceptionParseError() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "PROGRESS 200\r\n" +
                    "plan-code: FOUND_DRIVER_CANDIDATE\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"status\": We found the right candidate for you.}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Parse error", exception.getMessage());
    }

    @Test
    void Read_ProgressWithoutPlanCode_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "PROGRESS procedure.getDriver 200\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"status\": We found the right candidate for you.}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ProgressWithoutContentType_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "PROGRESS procedure.getDriver 200\r\n" +
                    "plan-code: FOUND_DRIVER_CANDIDATE\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"status\": We found the right candidate for you.}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ProgressWithoutId_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "PROGRESS procedure.getDriver 200\r\n" +
                    "plan-code: FOUND_DRIVER_CANDIDATE\r\n" +
                    "content-type: application/json\r\n" +
                    "\r\n" +
                    "{\"status\": We found the right candidate for you.}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ProgressWithoutAllHeader_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "PROGRESS procedure.getDriver 200\r\n" +
                    "\r\n" +
                    "{\"status\": We found the right candidate for you.}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Write_ProgressValid_Success() throws Exception {
        var header = new HeaderProgress("FOUND_DRIVER_CANDIDATE", "application/json", "002");
        var progress = new MessageProgress("procedure.getDriver", "200", header);
        progress.setBody("{\"status\": \"We get an candidate driver for you\"}");

        var actual = RPCPUtils.write(progress);
        var expected = "PROGRESS procedure.getDriver 200\r\n" +
                "plan-code: FOUND_DRIVER_CANDIDATE\r\n" +
                "content-type: application/json\r\n" +
                "id: 002\r\n" +
                "\r\n" +
                "{\"status\": \"We get an candidate driver for you\"}";

        assertEquals(expected, actual);
    }

    @Test
    void Write_ProgressNull_Success() throws Exception {
        var progress = new MessageProgress(null, null, null);

        var actual = RPCPUtils.write(progress);
        var expected = "PROGRESS null null\r\n" +
                "plan-code: null\r\n" +
                "content-type: null\r\n" +
                "id: null\r\n" +
                "\r\n";

        assertEquals(expected, actual);
    }
}
