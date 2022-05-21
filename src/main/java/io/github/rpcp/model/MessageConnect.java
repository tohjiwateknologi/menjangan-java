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

import java.util.StringJoiner;

public class MessageConnect implements RPCPMessage {
    private final String vHost;
    private final String protocolVersion;
    private final HeaderConnect header;

    public MessageConnect(String vHost, String protocolVersion, HeaderConnect header) {
        this.vHost = vHost;
        this.protocolVersion = protocolVersion;
        this.header = header;
    }

    public String getvHost() {
        return vHost;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public HeaderConnect getHeader() {
        return header;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MessageConnect.class.getSimpleName() + "[", "]")
                .add("vHost='" + vHost + "'")
                .add("protocolVersion='" + protocolVersion + "'")
                .add("header=" + header)
                .toString();
    }
}
