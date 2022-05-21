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

package io.github.math.event;

import io.github.rpcp.EventBuilder;
import io.github.rpcp.RPCPUtils;
import io.github.rpcp.model.RPCPEvent;
import io.github.rpcp.model.RPCPSession;

import java.util.Map;

public class EventAlarm implements RPCPEvent {
    @Override
    public void execute(RPCPSession session, Map<String, Object> params, EventBuilder eventBuilder, String... a) {

        var aaa = params.get("Name");
        var ccc = params.get("Clock");

        String name = "";
        if (aaa instanceof String) {
            name = (String) (aaa);
        } else {
            System.out.println("bukan String");
        }

        int clock = 0;
        if (ccc instanceof Integer) {
            clock = (Integer) (ccc);
        }

        var body = "{\"name\":\"" + name +"\",\"clock\":\"" + clock + "\"}";
        var event = eventBuilder.build("application/json", "200", body, "004");

        String eventStr = null;
        try {
            eventStr = RPCPUtils.write(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        session.sendMessage(eventStr);
    }
}
