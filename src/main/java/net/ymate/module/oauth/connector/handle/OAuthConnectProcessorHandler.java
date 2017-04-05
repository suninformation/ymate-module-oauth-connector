/*
 * Copyright 2007-2017 the original author or authors.
 *
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
 */
package net.ymate.module.oauth.connector.handle;

import net.ymate.module.oauth.connector.ICmsOAuthConnector;
import net.ymate.module.oauth.connector.IOAuthConnectProcessor;
import net.ymate.module.oauth.connector.annotation.OAuthConnectProcessor;
import net.ymate.platform.core.beans.IBeanHandler;
import net.ymate.platform.core.util.ClassUtils;

/**
 * @author 刘镇 (suninformation@163.com) on 17/3/27 上午11:42
 * @version 1.0
 */
public class OAuthConnectProcessorHandler implements IBeanHandler {

    private ICmsOAuthConnector __owner;

    public OAuthConnectProcessorHandler(ICmsOAuthConnector owner) {
        __owner = owner;
    }

    @SuppressWarnings("unchecked")
    public Object handle(Class<?> targetClass) throws Exception {
        if (ClassUtils.isInterfaceOf(targetClass, IOAuthConnectProcessor.class)) {
            __owner.registerConnectProcessorHandler(targetClass.getAnnotation(OAuthConnectProcessor.class).value(), (Class<? extends IOAuthConnectProcessor>) targetClass);
        }
        return null;
    }
}
