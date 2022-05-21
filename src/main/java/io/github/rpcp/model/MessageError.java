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

public class MessageError implements RPCPMessage{
    private final String method;
    private final HeaderError header;
    private String body;

    public MessageError(String method, HeaderError header) {
        this.method = method;
        this.header = header;
    }

    public String getMethod() {
        return method;
    }

    public HeaderError getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MessageError.class.getSimpleName() + "[", "]")
                .add("method='" + method + "'")
                .add("header=" + header)
                .add("body='" + body + "'")
                .toString();
    }
}
