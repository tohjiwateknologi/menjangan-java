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

import org.apache.commons.lang3.RandomStringUtils;

import java.util.function.Consumer;

public class SocketMock {

    private final String id;
    private String lastReceiveMessage;
    private boolean isOpen;

    private Consumer<String> newMessageCallback;

    public SocketMock() {
        this.id = RandomStringUtils.random(12, true, true);
        this.isOpen = true;
    }

    public void sendMessage(String message) {
        if (newMessageCallback != null) {
            newMessageCallback.accept(message);
        }
        this.lastReceiveMessage = message;
    }

    public String getLastReceiveMessage() {
        return this.lastReceiveMessage;
    }

    public void addMessageListener(Consumer<String> lambda) {
        newMessageCallback = lambda;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public void close() {
        this.isOpen = false;
    }

    public String getId() {
        return this.id;
    }
}
