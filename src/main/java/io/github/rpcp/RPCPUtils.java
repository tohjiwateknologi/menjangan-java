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

import io.github.rpcp.model.MessageError;
import io.github.rpcp.model.*;

import java.util.*;

/**
 * RPCP Utilities to help for Read and Write RPCP Object/ String.
 * @author Eric A. Sanjaya
 */
public class RPCPUtils {

    /**
     * Help to write RPCP String from RPCPMessage Object
     * @param rpcpMessage an RPCPMessage Object want to convert to RPCP String. It can use one of {@link MessageConnect},
     *                    {@link MessageConnected}, {@link MessageDisconnect}, {@link MessageCall}, {@link MessageResult},
     *                    {@link MessagePlan}, {@link MessageProgress}, {@link MessageEvent}, {@link MessageError}.
     * @return RPCP String representation of RPCP Message Object
     * @throws Exception if rpcpMessage not in instance one of {@link MessageConnect}, {@link MessageConnected},
     * {@link MessageDisconnect}, {@link MessageCall}, {@link MessageResult}, {@link MessagePlan}, {@link MessageProgress},
     * {@link MessageEvent}, {@link MessageError} will throw Exception with message "Not Compatible".
     */
    public static String write(RPCPMessage rpcpMessage) throws Exception {
        if (rpcpMessage instanceof MessageConnect) {
            var connect = (MessageConnect) rpcpMessage;
            return writeConnect(connect);
        } else if (rpcpMessage instanceof MessageConnected) {
            var connected = (MessageConnected) rpcpMessage;
            return writeConnected(connected);
        } else if (rpcpMessage instanceof MessageDisconnect) {
            var disconnect = (MessageDisconnect) rpcpMessage;
            return writeDisconnect(disconnect);
        } else if (rpcpMessage instanceof MessageCall) {
            var call = (MessageCall) rpcpMessage;
            return writeCall(call);
        } else if (rpcpMessage instanceof MessageResult) {
            var result = (MessageResult) rpcpMessage;
            return writeResult(result);
        } else if (rpcpMessage instanceof MessagePlan) {
            var plan = (MessagePlan) rpcpMessage;
            return writePlan(plan);
        } else if (rpcpMessage instanceof MessageProgress) {
            var progress = (MessageProgress) rpcpMessage;
            return writeProgress(progress);
        } else if (rpcpMessage instanceof MessageEvent) {
            var event = (MessageEvent) rpcpMessage;
            return writeEvent(event);
        } else if (rpcpMessage instanceof MessageError) {
            var error = (MessageError) rpcpMessage;
            return writeError(error);
        } else {
            throw new Exception("Not Compatible");
        }
    }

    private static String writeConnect(MessageConnect messageConnect) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("CONNECT ");
        stringBuilder.append(messageConnect.getvHost());
        stringBuilder.append(" ");
        stringBuilder.append(messageConnect.getProtocolVersion());
        stringBuilder.append("\r\n");
        stringBuilder.append("agent: ");

        if (messageConnect.getHeader() != null) {
            stringBuilder.append(messageConnect.getHeader().getAgent());
        } else {
            stringBuilder.append("null");
        }

        stringBuilder.append("\r\n");
        stringBuilder.append("\r\n");
        return stringBuilder.toString();
    }

    private static String writeConnected(MessageConnected connected) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("CONNECTED ");
        stringBuilder.append(connected.getvHost());
        stringBuilder.append(" ");
        stringBuilder.append(connected.getProtocolVersion());
        stringBuilder.append("\r\n");

        stringBuilder.append("server: ");
        if (connected.getHeader() != null) {
            stringBuilder.append(connected.getHeader().getServer());
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("event: ");
        if (connected.getHeader() != null) {
            stringBuilder.append(arrayToStringArray(connected.getHeader().getEvent()));
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("method: ");
        if (connected.getHeader() != null) {
            stringBuilder.append(arrayToStringArray(connected.getHeader().getMethod()));
        } else {
            stringBuilder.append("null");
        }

        stringBuilder.append("\r\n");
        stringBuilder.append("\r\n");
        return stringBuilder.toString();
    }

    private static String writeDisconnect(MessageDisconnect messageDisconnect) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("DISCONNECT ");
        stringBuilder.append(messageDisconnect.getvHost());
        stringBuilder.append("\r\n");
        stringBuilder.append("\r\n");
        return stringBuilder.toString();
    }

    private static String writeCall(MessageCall messageCall) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("CALL ");
        stringBuilder.append(messageCall.getMethod());
        stringBuilder.append("\r\n");

        stringBuilder.append("content-type: ");
        if (messageCall.getHeader() != null) {
            stringBuilder.append(messageCall.getHeader().getContentType());
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("id: ");
        if (messageCall.getHeader() != null) {
            stringBuilder.append(messageCall.getHeader().getId());
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("\r\n");

        if (messageCall.getBody() != null) {
            stringBuilder.append(messageCall.getBody());
        }

        return stringBuilder.toString();
    }

    private static String writeResult(MessageResult messageResult) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("RESULT ");
        stringBuilder.append(messageResult.getMethod());
        stringBuilder.append(" ");
        stringBuilder.append(messageResult.getStatusCode());
        stringBuilder.append("\r\n");

        if (messageResult.getHeader() != null && messageResult.getHeader().getPlanCode() != null) {
            stringBuilder.append("plan-code: ");
            stringBuilder.append(messageResult.getHeader().getPlanCode());
            stringBuilder.append("\r\n");
        }

        stringBuilder.append("content-type: ");
        if (messageResult.getHeader() != null && messageResult.getHeader().getContentType() != null) {
            stringBuilder.append(messageResult.getHeader().getContentType());
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("id: ");
        if (messageResult.getHeader() != null && messageResult.getHeader().getId() != null) {
            stringBuilder.append(messageResult.getHeader().getId());
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("\r\n");

        if (messageResult.getBody() != null) {
            stringBuilder.append(messageResult.getBody());
        }

        return stringBuilder.toString();
    }

    private static String writePlan(MessagePlan messagePlan) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("PLAN ");
        stringBuilder.append(messagePlan.getMethod());
        stringBuilder.append("\r\n");

        stringBuilder.append("planing-code: ");
        if (messagePlan.getHeader() != null) {
            stringBuilder.append(arrayToStringArray(messagePlan.getHeader().getPlaningCode()));
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("content-type: ");
        if (messagePlan.getHeader() != null) {
            stringBuilder.append(messagePlan.getHeader().getContentType());
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("id: ");
        if (messagePlan.getHeader() != null) {
            stringBuilder.append(messagePlan.getHeader().getId());
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("\r\n");

        if (messagePlan.getBody() != null) {
            stringBuilder.append(messagePlan.getBody());
        }

        return stringBuilder.toString();
    }

    private static String writeProgress(MessageProgress messageProgress) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("PROGRESS ");
        stringBuilder.append(messageProgress.getMethod());
        stringBuilder.append(" ");
        stringBuilder.append(messageProgress.getStatusCode());
        stringBuilder.append("\r\n");

        stringBuilder.append("plan-code: ");
        if (messageProgress.getHeader() != null && messageProgress.getHeader().getPlanCode() != null) {

            stringBuilder.append(messageProgress.getHeader().getPlanCode());

        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("content-type: ");
        if (messageProgress.getHeader() != null && messageProgress.getHeader().getContentType() != null) {
            stringBuilder.append(messageProgress.getHeader().getContentType());
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("id: ");
        if (messageProgress.getHeader() != null && messageProgress.getHeader().getId() != null) {
            stringBuilder.append(messageProgress.getHeader().getId());
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("\r\n");

        if (messageProgress.getBody() != null) {
            stringBuilder.append(messageProgress.getBody());
        }

        return stringBuilder.toString();
    }

    private static String writeEvent(MessageEvent messageEvent) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("EVENT ");
        stringBuilder.append(messageEvent.getMethod());
        stringBuilder.append(" ");
        stringBuilder.append(messageEvent.getStatusCode());
        stringBuilder.append("\r\n");

        stringBuilder.append("content-type: ");
        if (messageEvent.getHeader() != null && messageEvent.getHeader().getContentType() != null) {
            stringBuilder.append(messageEvent.getHeader().getContentType());
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("id: ");
        if (messageEvent.getHeader() != null && messageEvent.getHeader().getId() != null) {
            stringBuilder.append(messageEvent.getHeader().getId());
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("\r\n");

        if (messageEvent.getBody() != null) {
            stringBuilder.append(messageEvent.getBody());
        }

        return stringBuilder.toString();
    }

    private static String writeError(MessageError error) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("ERROR ");
        stringBuilder.append(error.getMethod());
        stringBuilder.append("\r\n");

        stringBuilder.append("message: ");
        if (error.getHeader() != null) {
            stringBuilder.append(error.getHeader().getMessage());
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");

        stringBuilder.append("content-type: ");
        if (error.getHeader() != null) {
            stringBuilder.append(error.getHeader().getContentType());
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append("\r\n");


        if (error.getHeader() != null && error.getHeader().getId() != null) {
            stringBuilder.append("id: ");
            stringBuilder.append(error.getHeader().getId());
            stringBuilder.append("\r\n");
        }

        stringBuilder.append("\r\n");

        if (error.getBody() != null) {
            stringBuilder.append(error.getBody());
        }

        return stringBuilder.toString();
    }

    private static String arrayToStringArray(String[] list) {
        var a = new StringJoiner(", ");

        for (var aa : list) {
            a.add(aa);
        }
        return a.toString();
    }

    /**
     * Help to read RPCP Object from RPCP Message String
     * @param rpcp RPCP Message String
     * @return RPCPMessage representation from the RPCP Message String given
     * @throws Exception if RPCP Message String not a valid RPCP specification will throw Exception with message "Invalid RPCP".
     */
    public static RPCPMessage read(String rpcp) throws Exception {
        var strings = rpcp.split("\\r\\n");
        var haveBody = false;
        var haveHeader = false;
        var headerLastIndex = 0;
        var bodyFirstIndex = 0;
        List<String[]> headerLines = new ArrayList<>();

        var blankLineIndex = 0;

        // Get Body & header line
        for (var i = 0; i < strings.length; i++) {
            if (strings[i].equals("")) {
                blankLineIndex = i;
                break;
            }
        }

        if (blankLineIndex > 0) {
            if (blankLineIndex - 1 > 0) {
                haveHeader = true;
                headerLastIndex = blankLineIndex - 1;
            }

            if (blankLineIndex + 1 < strings.length) {
                haveBody = true;
                bodyFirstIndex = blankLineIndex + 1;
            }
        } else if (strings.length > 0) {
            haveHeader = true;
            headerLastIndex = strings.length - 1;
        }

        //Command
        var commands = strings[0].split(" ");

        //Header
        if (haveHeader) {
            for (var i = 1; i <= headerLastIndex; i++) {
                headerLines.add(strings[i].split(":"));
            }
        }

        String body;
        if (haveBody) {
            // generate string body
            StringBuilder stringBody = new StringBuilder();
            for (var i = bodyFirstIndex; i < strings.length; i++) {
                stringBody.append(strings[i]);
            }
            body = stringBody.toString();
        } else {
            body = "";
        }

        //clean the header value
        for (var x : headerLines) {
            x[1] = headerCleaner(x[1]);
        }

        switch (commands[0]) {
            case "CONNECT": {
                return generateConnect(commands, headerLines);
            }
            case "CONNECTED": {
                return generateConnected(commands, headerLines);
            }
            case "DISCONNECT": {
                return generateDisconnect(commands);
            }
            case "CALL": {
                return generateCall(commands, headerLines, body);
            }
            case "RESULT": {
                return generateResult(commands, headerLines, body);
            }
            case "PLAN": {
                return generatePlan(commands, headerLines, body);
            }
            case "PROGRESS": {
                return generateProgress(commands, headerLines, body);
            }
            case "EVENT": {
                return generateEvent(commands, headerLines, body);
            }
            case "ERROR": {
                return generateError(commands, headerLines, body);
            }
            default: {
                throw new Exception("Invalid RPCP");
            }
        }
    }

    private static MessageConnect generateConnect(String[] commands, List<String[]> headerLines) throws Exception {
        if (commands.length < 3) {
            throw new Exception("Parse error");
        }

        if (headerLines.isEmpty()) {
            throw new Exception("Invalid RPCP");
        }

        String agent = null;
        for (var headerLine : headerLines) {
            if (headerLine[0].equalsIgnoreCase("agent")) {
                agent = headerLine[1];
            }
        }

        if (agent == null) {
            throw new Exception("Invalid RPCP");
        }

        if (!validateProtocol(commands[2])) {
            throw new Exception("Incorrect Protocol Format");
        }

        return new MessageConnect(commands[1], commands[2], new HeaderConnect(agent));
    }

    private static MessageConnected generateConnected(String[] commands, List<String[]> headerLines) throws Exception {
        if (commands.length < 3) {
            throw new Exception("Parse error");
        }

        if (headerLines.isEmpty()) {
            throw new Exception("Invalid RPCP");
        }

        String server = null;
        String event = null;
        String method = null;
        for (var headerLine : headerLines) {
            if (headerLine[0].equalsIgnoreCase("server")) {
                server = headerLine[1];
            } else if (headerLine[0].equalsIgnoreCase("event")) {
                event = headerLine[1];
            } else if (headerLine[0].equalsIgnoreCase("method")) {
                method = headerLine[1];
            }
        }

        if (server == null) {
            throw new Exception("Invalid RPCP");
        }

        if (event == null) {
            throw new Exception("Invalid RPCP");
        }

        if (method == null) {
            throw new Exception("Invalid RPCP");
        }

        if (!validateProtocol(commands[2])) {
            throw new Exception("Incorrect Protocol Format");
        }

        var events = eventConstruct(event);
        var methods = methodConstruct(method);
        return new MessageConnected(commands[1], commands[2], new HeaderConnected(server, events, methods));
    }

    private static MessageDisconnect generateDisconnect(String[] commands) throws Exception {
        if (commands.length < 2) {
            throw new Exception("Parse error");
        }

        return new MessageDisconnect(commands[1]);
    }

    private static MessageCall generateCall(String[] commands, List<String[]> headerLines, String body) throws Exception {
        if (commands.length < 2) {
            throw new Exception("Parse error");
        }

        if (headerLines.isEmpty()) {
            throw new Exception("Invalid RPCP");
        }

        String contentType = null;
        String id = null;
        for (var headerLine : headerLines) {
            if (headerLine[0].equalsIgnoreCase("content-type")) {
                contentType = headerLine[1];
            } else if (headerLine[0].equalsIgnoreCase("id")) {
                id = headerLine[1];
            }
        }

        if (contentType == null) {
            throw new Exception("Invalid RPCP");
        }

        if (id == null) {
            throw new Exception("Invalid RPCP");
        }

        var call = new MessageCall(commands[1], new HeaderCall(contentType, id));
        call.setBody(body);
        return call;
    }

    private static MessageResult generateResult(String[] commands, List<String[]> headerLines, String body) throws Exception {
        if (commands.length < 3) {
            throw new Exception("Parse error");
        }

        if (headerLines.isEmpty()) {
            throw new Exception("Invalid RPCP");
        }

        String contentType = null;
        String id = null;
        String planCode = null;
        for (var headerLine : headerLines) {
            if (headerLine[0].equalsIgnoreCase("content-type")) {
                contentType = headerLine[1];
            } else if (headerLine[0].equalsIgnoreCase("id")) {
                id = headerLine[1];
            } else if (headerLine[0].equalsIgnoreCase("plan-code")) {
                planCode = headerLine[1];
            }
        }

        if (contentType == null) {
            throw new Exception("Invalid RPCP");
        }

        if (id == null) {
            throw new Exception("Invalid RPCP");
        }

        var result = new MessageResult(commands[1], commands[2], new HeaderResult(planCode, contentType, id));
        result.setBody(body);
        return result;
    }

    private static MessagePlan generatePlan(String[] commands, List<String[]> headerLines, String body) throws Exception {
        if (commands.length < 2) {
            throw new Exception("Parse error");
        }

        if (headerLines.isEmpty()) {
            throw new Exception("Invalid RPCP");
        }

        String contentType = null;
        String id = null;
        String planingCode = null;
        for (var headerLine : headerLines) {
            if (headerLine[0].equalsIgnoreCase("content-type")) {
                contentType = headerLine[1];
            } else if (headerLine[0].equalsIgnoreCase("id")) {
                id = headerLine[1];
            } else if (headerLine[0].equalsIgnoreCase("planing-code")) {
                planingCode = headerLine[1];
            }
        }

        if (contentType == null) {
            throw new Exception("Invalid RPCP");
        }

        if (id == null) {
            throw new Exception("Invalid RPCP");
        }

        if (planingCode == null) {
            throw new Exception("Invalid RPCP");
        }

        var planingCodes = planingConstruct(planingCode);
        var plan = new MessagePlan(commands[1], new HeaderPlan(planingCodes, contentType, id));
        plan.setBody(body);
        return plan;
    }

    private static MessageProgress generateProgress(String[] commands, List<String[]> headerLines, String body) throws Exception {
        if (commands.length < 3) {
            throw new Exception("Parse error");
        }

        if (headerLines.isEmpty()) {
            throw new Exception("Invalid RPCP");
        }

        String contentType = null;
        String id = null;
        String planCode = null;
        for (var headerLine : headerLines) {
            if (headerLine[0].equalsIgnoreCase("content-type")) {
                contentType = headerLine[1];
            } else if (headerLine[0].equalsIgnoreCase("id")) {
                id = headerLine[1];
            } else if (headerLine[0].equalsIgnoreCase("plan-code")) {
                planCode = headerLine[1];
            }
        }

        if (contentType == null) {
            throw new Exception("Invalid RPCP");
        }

        if (id == null) {
            throw new Exception("Invalid RPCP");
        }

        if (planCode == null) {
            throw new Exception("Invalid RPCP");
        }

        var progress = new MessageProgress(commands[1], commands[2], new HeaderProgress(planCode, contentType, id));
        progress.setBody(body);
        return progress;
    }

    private static MessageEvent generateEvent(String[] commands, List<String[]> headerLines, String body) throws Exception {
        if (commands.length < 3) {
            throw new Exception("Parse error");
        }

        if (headerLines.isEmpty()) {
            throw new Exception("Invalid RPCP");
        }

        String contentType = null;
        String id = null;
        for (var headerLine : headerLines) {
            if (headerLine[0].equalsIgnoreCase("content-type")) {
                contentType = headerLine[1];
            } else if (headerLine[0].equalsIgnoreCase("id")) {
                id = headerLine[1];
            }
        }

        if (contentType == null) {
            throw new Exception("Invalid RPCP");
        }

        if (id == null) {
            throw new Exception("Invalid RPCP");
        }

        var event = new MessageEvent(commands[1], commands[2], new HeaderEvent(contentType, id));
        event.setBody(body);
        return event;
    }

    private static MessageError generateError(String[] commands, List<String[]> headerLines, String body) throws Exception {
        if (commands.length < 2) {
            throw new Exception("Parse error");
        }

        if (headerLines.isEmpty()) {
            throw new Exception("Invalid RPCP");
        }

        String message = null;
        String contentType = null;
        String id = null;
        for (var headerLine : headerLines) {
            if (headerLine[0].equalsIgnoreCase("message")) {
                message = headerLine[1];
            } else if (headerLine[0].equalsIgnoreCase("content-type")) {
                contentType = headerLine[1];
            } else if (headerLine[0].equalsIgnoreCase("id")) {
                id = headerLine[1];
            }
        }

        if (message == null) {
            throw new Exception("Invalid RPCP");
        }

        if (contentType == null) {
            throw new Exception("Invalid RPCP");
        }

        var error = new MessageError(commands[1], new HeaderError(message, contentType, id));
        error.setBody(body);
        return error;
    }

    private static boolean validateProtocol(String protocol) {
        var protocols = protocol.split("/");
        if (protocols.length < 2) {
            return false;
        }

        return protocols[0].equalsIgnoreCase("RPCP");
    }

    private static String[] planingConstruct(String string) {
        var planing = string.split(",");
        var newPlaning = new String[planing.length];
        var i = 0;
        for (var plan : planing) {
            newPlaning[i] = plan.replace(" ", "");
            i++;
        }

        return newPlaning;
    }

    private static String[] eventConstruct(String string) {
        var events = string.split(",");
        var newEvents = new String[events.length];
        int i = 0;
        for (var event : events) {
            newEvents[i] = event.replace(" ", "");
            i++;
        }

        return newEvents;
    }

    private static String[] methodConstruct(String string) {
        var methods = string.split(",");
        var newMethods = new String[methods.length];
        var i = 0;
        for (var method : methods) {
            newMethods[i] = method.replace(" ", "");
            i++;
        }

        return newMethods;
    }

    private static String headerCleaner(String value) {
        if (value.charAt(0) == ' ') {
            return value.substring(1);
        } else {
            return value;
        }
    }
}
