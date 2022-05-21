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

import io.github.rpcp.RPCPBuilder;
import io.github.rpcp.RPCPUtils;
import io.github.rpcp.model.MessageCall;
import io.github.rpcp.model.RPCPMethod;
import io.github.rpcp.model.RPCPSession;

public class Sum implements RPCPMethod {

    @Override
    public void execute(RPCPSession rpcpSession, MessageCall messageCall, RPCPBuilder rpcpBuilder) {

        var plan = rpcpBuilder.plan(new String[]{"REQUEST_DRIVER", "GET_DRIVER"}, "kami sudah menyiapkan rencana yang terbaik");
        String planStr = null;
        try {
            planStr = RPCPUtils.write(plan);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rpcpSession.sendMessage(planStr);

        var progress1 = rpcpBuilder.progress("REQUEST_DRIVER", "200", "kami sudah menerima dan mencarikan driver yang tepat untuk anda");
        String progressStr = null;
        try {
            progressStr = RPCPUtils.write(progress1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rpcpSession.sendMessage(progressStr);

        var progress2 = rpcpBuilder.progress("GET_DRIVER", "200", "kami sudah menerima dan mencarikan driver yang tepat untuk anda");
        String progressStr1 = null;
        try {
            progressStr1 = RPCPUtils.write(progress2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rpcpSession.sendMessage(progressStr1);

        var xn = rpcpBuilder.result("200", "result: 20");
        String z = null;
        try {
            z = RPCPUtils.write(xn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rpcpSession.sendMessage(z);

    }
}
