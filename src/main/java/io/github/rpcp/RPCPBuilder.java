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

/**
Help to Build RPCP Message in easy way
@author Eric A. Sanjaya
 */
public class RPCPBuilder {
    private final MessageCall messageCall;

    public RPCPBuilder(MessageCall messageCall) {
        this.messageCall = messageCall;
    }

    public MessageResult result(String statusCode, String body) {
        var header = new HeaderResult(messageCall.getHeader().getContentType(), messageCall.getHeader().getId());
        var result = new MessageResult(messageCall.getMethod(), statusCode, header);
        result.setBody(body);

        return result;
    }

    public MessageResult result(String planCode, String statusCode, String body) {
        var header = new HeaderResult(planCode, messageCall.getHeader().getContentType(), messageCall.getHeader().getId());
        var result = new MessageResult(messageCall.getMethod(), statusCode, header);
        result.setBody(body);

        return result;
    }

    public MessagePlan plan(String[] planingCode, String body) {
        var header = new HeaderPlan(planingCode, messageCall.getHeader().getContentType(), messageCall.getHeader().getId());
        var plan = new MessagePlan(messageCall.getMethod(), header);
        plan.setBody(body);

        return plan;
    }

    public MessageEvent event(String statusCode, String body) {
        var header = new HeaderEvent(messageCall.getHeader().getContentType(), messageCall.getHeader().getId());
        var event = new MessageEvent(messageCall.getMethod(), statusCode, header);
        event.setBody(body);

        return event;
    }

    public MessageError error(String message, String body, String id) {
        var header = new HeaderError(message, messageCall.getHeader().getContentType(), id);
        var error = new MessageError(messageCall.getMethod(), header);
        error.setBody(body);

        return error;
    }

    public MessageError error(String message, String body) {
        var header = new HeaderError(message, messageCall.getHeader().getContentType());
        var error = new MessageError(".internal", header);
        error.setBody(body);

        return error;
    }

    public MessageProgress progress(String planCode, String statusCode, String body) {
        var header = new HeaderProgress(planCode, messageCall.getHeader().getContentType(), messageCall.getHeader().getId());
        var progress = new MessageProgress(messageCall.getMethod(), statusCode, header);
        progress.setBody(body);

        return progress;
    }
}
