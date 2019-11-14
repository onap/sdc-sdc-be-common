/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.sdc.security.logging.elements;

public class ErrorLogOptionalData {
    private String targetEntity;
    private String targetServiceName;

    public ErrorLogOptionalData() {
    }

    String getTargetEntity() {
        return targetEntity;
    }

    private void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    String getTargetServiceName() {
        return targetServiceName;
    }

    private void setTargetServiceName(String targetServiceName) {
        this.targetServiceName = targetServiceName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final ErrorLogOptionalData instance;

        private Builder() {
            instance = new ErrorLogOptionalData();
        }

        public Builder targetEntity(String targetEntity) {
            instance.setTargetEntity(targetEntity);
            return this;
        }

        public Builder targetServiceName(String targetServiceName) {
            instance.setTargetServiceName(targetServiceName);
            return this;
        }

        public ErrorLogOptionalData build() {
            return instance;
        }
    }
}
