/*
 * Copyright 2019 dc-square GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hivemq.extension.sdk.api.interceptor.connect;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.Interceptor;
import com.hivemq.extension.sdk.api.interceptor.connect.parameter.ConnectInterceptorInput;
import com.hivemq.extension.sdk.api.interceptor.connect.parameter.ConnectInterceptorOutput;

/**
 * Interface for the connect interception.
 * <p>
 * Interceptors are always called by the same Thread for all messages from the same client.
 * <p>
 * If the same instance is shared between multiple clients it can be called in different Threads and must therefore be
 * thread-safe.
 * <p>
 *
 * @author Lukas Brandl
 * @since 4.2.0
 */
@FunctionalInterface
public interface ConnectInterceptor extends Interceptor {

    /**
     * When a {@link ConnectInterceptor} is set through any extension,
     * this method gets called for every inbound CONNECT packet from any MQTT client.
     *
     * @param connectInterceptorInput  The {@link ConnectInterceptorInput} parameter.
     * @param connectInterceptorOutput The {@link ConnectInterceptorOutput} parameter.
     * @since 4.2.0
     */
    void onConnect(@NotNull ConnectInterceptorInput connectInterceptorInput, @NotNull ConnectInterceptorOutput connectInterceptorOutput);
}
