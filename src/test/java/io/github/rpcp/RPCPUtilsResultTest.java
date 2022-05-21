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

import io.github.rpcp.model.HeaderResult;
import io.github.rpcp.model.MessageResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RPCPUtilsResultTest {
    @BeforeAll
    static void setup() {
        //log.info("@BeforeAll - executes once before all test methods in this class");
    }

    @BeforeEach
    void init() {
        //log.info("@BeforeEach - executes before each test method in this class");
    }

    @Test
    void Read_ResultValid_Success() throws Exception {
        var string = "RESULT procedure.sum 200\r\n" +
                "plan-code: FOUND_DRIVER_CANDIDATE\r\n" +
                "content-type: application/json\r\n" +
                "id: 001\r\n" +
                "\r\n" +
                "{\"result\":30}";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageResult);
        var result = (MessageResult) rpcpMessage;
        assertEquals("procedure.sum", result.getMethod());
        assertEquals("200", result.getStatusCode());
        assertEquals("FOUND_DRIVER_CANDIDATE", result.getHeader().getPlanCode());
        assertEquals("application/json", result.getHeader().getContentType());
        assertEquals("001", result.getHeader().getId());
        assertEquals("{\"result\":30}", result.getBody());
    }

    @Test
    void Read_ResultWithoutPlan_Success() throws Exception {
        var string = "RESULT procedure.sum 200\r\n" +
                "content-type: application/json\r\n" +
                "id: 001\r\n" +
                "\r\n" +
                "{\"result\":30}\r\n";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageResult);
        var result = (MessageResult) rpcpMessage;
        assertEquals("procedure.sum", result.getMethod());
        assertEquals("200", result.getStatusCode());
        assertNull(result.getHeader().getPlanCode());
        assertEquals("application/json", result.getHeader().getContentType());
        assertEquals("001", result.getHeader().getId());
        assertEquals("{\"result\":30}", result.getBody());
    }

    @Test
    void Read_ResultWithoutBody_Success() throws Exception {
        var string = "RESULT procedure.sum 200\r\n" +
                "content-type: application/json\r\n" +
                "id: 001\r\n" +
                "\r\n";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessageResult);
        var result = (MessageResult) rpcpMessage;
        assertEquals("procedure.sum", result.getMethod());
        assertEquals("200", result.getStatusCode());
        assertNull(result.getHeader().getPlanCode());
        assertEquals("application/json", result.getHeader().getContentType());
        assertEquals("001", result.getHeader().getId());
        assertEquals("", result.getBody());
    }

    @Test
    void Read_ResultWrongCommand_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "RESUL procedure.sum 200\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"result\":30}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ResultWithoutStatusCode_ExceptionParseError() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "RESULT procedure.sum\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"result\":30}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Parse error", exception.getMessage());
    }

    @Test
    void Read_ResultWithoutDestination_ExceptionParseError() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "RESULT 200\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"result\":30}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Parse error", exception.getMessage());
    }

    @Test
    void Read_ResultWithoutContentType_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "RESULT procedure.sum 200\r\n" +
                    "id: 001\r\n" +
                    "\r\n" +
                    "{\"result\":30}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ResultWithoutId_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "RESULT procedure.sum 200\r\n" +
                    "content-type: application/json\r\n" +
                    "\r\n" +
                    "{\"result\":30}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_ResultWithoutAllHeader_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "RESULT procedure.sum 200\r\n" +
                    "\r\n" +
                    "{\"result\":30}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Write_ResultValid_Success() throws Exception {
        var header = new HeaderResult("FOUND_DRIVER", "application/json", "002");
        var result = new MessageResult("procedure.getDriver", "200", header);
        result.setBody("{\"status\": We get an driver for you}");

        var actual = RPCPUtils.write(result);
        var expected = "RESULT procedure.getDriver 200\r\n" +
                "plan-code: FOUND_DRIVER\r\n" +
                "content-type: application/json\r\n" +
                "id: 002\r\n" +
                "\r\n" +
                "{\"status\": We get an driver for you}";

        assertEquals(expected, actual);
    }

    @Test
    void Write_ResultWithoutPlanCode_Success() throws Exception {
        var header = new HeaderResult("application/json", "002");
        var result = new MessageResult("procedure.getDriver", "200", header);
        result.setBody("{\"status\": \"We get an driver for you\"}");

        var actual = RPCPUtils.write(result);
        var expected = "RESULT procedure.getDriver 200\r\n" +
                "content-type: application/json\r\n" +
                "id: 002\r\n" +
                "\r\n" +
                "{\"status\": \"We get an driver for you\"}";

        assertEquals(expected, actual);
    }

    @Test
    void Write_ResultPlanCodeNullContentTypeNullIdNull_Success() throws Exception {
        var header = new HeaderResult(null, null, null);
        var result = new MessageResult("procedure.getDriver", "200", header);
        result.setBody("{\"status\": \"We get an driver for you\"}");

        var actual = RPCPUtils.write(result);
        var expected = "RESULT procedure.getDriver 200\r\n" +
                "content-type: null\r\n" +
                "id: null\r\n" +
                "\r\n" +
                "{\"status\": \"We get an driver for you\"}";

        assertEquals(expected, actual);
    }

    @Test
    void Write_ResultNull_Success() throws Exception {
        var result = new MessageResult(null, null, null);

        var actual = RPCPUtils.write(result);
        var expected = "RESULT null null\r\n" +
                "content-type: null\r\n" +
                "id: null\r\n" +
                "\r\n";

        assertEquals(expected, actual);
    }
}
