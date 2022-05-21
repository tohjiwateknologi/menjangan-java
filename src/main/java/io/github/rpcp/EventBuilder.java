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

import io.github.rpcp.model.MessageEvent;
import io.github.rpcp.model.HeaderEvent;

/**
 * Help to Build RPCP Event Message in easy way
 * @author Eric A. Sanjaya
 */
public class EventBuilder {
    private final String eventName;

    public EventBuilder(String eventName) {
        this.eventName = eventName;
    }

    public MessageEvent build(String contentType, String statusCode, String body, String id) {

        var header = new HeaderEvent(contentType, id);
        var event = new MessageEvent(eventName, statusCode, header);
        event.setBody(body);

        return event;
    }
}
