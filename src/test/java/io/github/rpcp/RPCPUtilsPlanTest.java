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

import io.github.rpcp.model.HeaderPlan;
import io.github.rpcp.model.MessagePlan;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RPCPUtilsPlanTest {
    @BeforeAll
    static void setup() {
        //log.info("@BeforeAll - executes once before all test methods in this class");
    }

    @BeforeEach
    void init() {
        //log.info("@BeforeEach - executes before each test method in this class");
    }

    @Test
    void Read_PlanValid_Success() throws Exception {
        var string = "PLAN procedure.getDriver\r\n" +
                "planing-code: SEARCH_DRIVER, FOUND_DRIVER_CANDIDATE, FOUND_DRIVER\r\n" +
                "content-type: application/json\r\n" +
                "id: 002\r\n" +
                "\r\n" +
                "{\"status\": We have prepared the best}";

        var rpcpMessage = RPCPUtils.read(string);

        assertTrue(rpcpMessage instanceof MessagePlan);
        var plan = (MessagePlan) rpcpMessage;
        assertEquals("procedure.getDriver", plan.getMethod());
        assertEquals("[SEARCH_DRIVER, FOUND_DRIVER_CANDIDATE, FOUND_DRIVER]", Arrays.toString(plan.getHeader().getPlaningCode()));
        assertEquals("application/json", plan.getHeader().getContentType());
        assertEquals("002", plan.getHeader().getId());
        assertEquals("{\"status\": We have prepared the best}", plan.getBody());
    }

    @Test
    void Read_PlanWithoutBody_Success() throws Exception {
        var string = "PLAN procedure.getDriver\r\n" +
                "planing-code: SEARCH_DRIVER, FOUND_DRIVER_CANDIDATE, FOUND_DRIVER\r\n" +
                "content-type: application/json\r\n" +
                "id: 002\r\n" +
                "\r\n";

        var rpcpMessage = RPCPUtils.read(string);
        assertTrue(rpcpMessage instanceof MessagePlan);
        var plan = (MessagePlan) rpcpMessage;
        assertEquals("procedure.getDriver", plan.getMethod());
        assertEquals("[SEARCH_DRIVER, FOUND_DRIVER_CANDIDATE, FOUND_DRIVER]", Arrays.toString(plan.getHeader().getPlaningCode()));
        assertEquals("application/json", plan.getHeader().getContentType());
        assertEquals("002", plan.getHeader().getId());
        assertEquals("", plan.getBody());
    }

    @Test
    void Read_PlanWrongCommand_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "PLA procedure.getDriver\r\n" +
                    "planing-code: SEARCH_DRIVER, FOUND_DRIVER_CANDIDATE, FOUND_DRIVER\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 002\r\n" +
                    "\r\n" +
                    "{\"status\": We have prepared the best}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_PlanWithoutDestination_ExceptionParseError() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "PLAN\r\n" +
                    "planing-code: SEARCH_DRIVER, FOUND_DRIVER_CANDIDATE, FOUND_DRIVER\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 002\r\n" +
                    "\r\n" +
                    "{\"status\": We have prepared the best}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Parse error", exception.getMessage());
    }

    @Test
    void Read_PlanWithoutPlaningCode_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "PLAN procedure.getDriver\r\n" +
                    "content-type: application/json\r\n" +
                    "id: 002\r\n" +
                    "\r\n" +
                    "{\"status\": We have prepared the best}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_PlanWithoutContentType_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "PLAN procedure.getDriver\r\n" +
                    "planing-code: SEARCH_DRIVER, FOUND_DRIVER_CANDIDATE, FOUND_DRIVER\r\n" +
                    "id: 002\r\n" +
                    "\r\n" +
                    "{\"status\": We have prepared the best}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_PlanWithoutId_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "PLAN procedure.getDriver\r\n" +
                    "planing-code: SEARCH_DRIVER, FOUND_DRIVER_CANDIDATE, FOUND_DRIVER\r\n" +
                    "content-type: application/json\r\n" +
                    "\r\n" +
                    "{\"status\": We have prepared the best}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Read_PlanWithoutAllHeader_ExceptionInvalidRPCP() {
        Throwable exception;

        exception = assertThrows(Exception.class, () -> {
            var string = "PLAN procedure.getDriver\r\n" +
                    "\r\n" +
                    "{\"status\": We have prepared the best}\r\n";
            RPCPUtils.read(string);
        });
        assertEquals("Invalid RPCP", exception.getMessage());
    }

    @Test
    void Write_PlanValid_Success() throws Exception {
        var planCodes = new String[]{"SEARCH_DRIVER", "FOUND_DRIVER_CANDIDATE", "FOUND_DRIVER"};
        System.out.println(Arrays.toString(planCodes));

        var header = new HeaderPlan(planCodes, "application/json", "002");
        var plan = new MessagePlan("procedure.getDriver", header);
        plan.setBody("{\"status\": \"We have a great plan for you\"}");

        var actual = RPCPUtils.write(plan);
        var expected = "PLAN procedure.getDriver\r\n" +
                "planing-code: SEARCH_DRIVER, FOUND_DRIVER_CANDIDATE, FOUND_DRIVER\r\n" +
                "content-type: application/json\r\n" +
                "id: 002\r\n" +
                "\r\n" +
                "{\"status\": \"We have a great plan for you\"}";

        assertEquals(expected, actual);
    }

    @Test
    void Write_PlanNull_Success() throws Exception {
        var plan = new MessagePlan(null, null);

        var actual = RPCPUtils.write(plan);
        var expected = "PLAN null\r\n" +
                "planing-code: null\r\n" +
                "content-type: null\r\n" +
                "id: null\r\n" +
                "\r\n";

        assertEquals(expected, actual);
    }
}
