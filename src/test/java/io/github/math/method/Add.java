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

package io.github.math.method;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rpcp.RPCPBuilder;
import io.github.rpcp.RPCPUtils;
import io.github.rpcp.model.MessageCall;
import io.github.rpcp.model.RPCPMethod;
import io.github.rpcp.model.RPCPSession;

public class Add implements RPCPMethod {
    @Override
    public void execute(RPCPSession rpcpSession, MessageCall messageCall, RPCPBuilder rpcpBuilder) {
        var objectMapper = new ObjectMapper();

        JsonNode bodyJson;
        try {
            bodyJson = objectMapper.readTree(messageCall.getBody());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        var a = bodyJson.get("a").asInt();
        var b = bodyJson.get("b").asInt();

        var c = a + b;

        var xn = rpcpBuilder.result("200", "{\"result\": " + c + "}");
        String z = null;
        try {
            z = RPCPUtils.write(xn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rpcpSession.sendMessage(z);
    }
}
