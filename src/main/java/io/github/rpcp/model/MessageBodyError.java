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

public class MessageBodyError {
    private final String detail;
    private final String code;

    public MessageBodyError(String detail, String code) {
        this.detail = detail;
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MessageBodyError.class.getSimpleName() + "[", "]")
                .add("detail='" + detail + "'")
                .add("code='" + code + "'")
                .toString();
    }
}
