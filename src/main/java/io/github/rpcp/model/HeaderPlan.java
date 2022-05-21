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

import java.util.Arrays;
import java.util.StringJoiner;

public class HeaderPlan {
    private final String[] planingCode;
    private final String contentType;
    private final String id;

    public HeaderPlan(String[] planingCode, String contentType, String id) {
        this.planingCode = planingCode;
        this.contentType = contentType;
        this.id = id;
    }

    public String[] getPlaningCode() {
        return planingCode;
    }

    public String getId() {
        return id;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HeaderPlan.class.getSimpleName() + "[", "]")
                .add("planingCode=" + Arrays.toString(planingCode))
                .add("contentType='" + contentType + "'")
                .add("id='" + id + "'")
                .toString();
    }
}
