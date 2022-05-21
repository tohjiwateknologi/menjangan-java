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

package io.github.rpcp.model;

public class RPCPSession {
    private final RPCPWebSocketSession rpcpWebSocketSession;

    public RPCPSession(RPCPWebSocketSession rpcpWebSocketSession) {
        this.rpcpWebSocketSession = rpcpWebSocketSession;
    }

    public void sendMessage(String message) {
        try {
            rpcpWebSocketSession.sendMessage(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            rpcpWebSocketSession.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isOpen() {
        try {
            return rpcpWebSocketSession.isOpen();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getId() {
        return rpcpWebSocketSession.getId();
    }

}
