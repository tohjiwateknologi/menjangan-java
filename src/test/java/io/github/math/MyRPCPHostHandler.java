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

package io.github.math;

import io.github.rpcp.model.MessageConnect;
import io.github.rpcp.model.MessageDisconnect;
import io.github.rpcp.model.RPCPHostHandler;
import io.github.rpcp.model.RPCPWebSocketSession;

public class MyRPCPHostHandler<G> implements RPCPHostHandler<G> {
    @Override
    public boolean onPreConnect(MessageConnect messageConnect, RPCPWebSocketSession session) {
        return true;
    }

    @Override
    public void onConnect(MessageConnect messageConnect, RPCPWebSocketSession session) {

    }

    @Override
    public void onDisconnect(MessageDisconnect messageDisconnect, RPCPWebSocketSession session) {

    }

    @Override
    public String getWsSessionId(G session) {
        if (session instanceof SocketMock) {
            return ((SocketMock) session).getId();
        } else {
            return null;
        }
    }

    @Override
    public RPCPWebSocketSession buildWebsocketSession(G session) {
        if (session instanceof SocketMock) {
            var socketMock = (SocketMock) session;
            return new io.github.math.RPCPWebSocketSession(socketMock);
        } else {
            return null;
        }
    }
}
