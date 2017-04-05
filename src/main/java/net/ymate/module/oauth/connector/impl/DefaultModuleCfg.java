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
package net.ymate.module.oauth.connector.impl;

import net.ymate.module.oauth.connector.ICmsOAuthConnector;
import net.ymate.module.oauth.connector.ICmsOAuthConnectorModuleCfg;
import net.ymate.module.oauth.connector.IOAuthConnectCallbackHandler;
import net.ymate.module.oauth.connector.IOAuthConnectProcessor;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.lang.BlurObject;
import net.ymate.platform.core.support.IPasswordProcessor;
import net.ymate.platform.core.util.ClassUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/03/27 上午 01:31
 * @version 1.0
 */
public class DefaultModuleCfg implements ICmsOAuthConnectorModuleCfg {

    private String __cacheNamePrefix;

    private Map<String, String> __moduleCfgs;

    private IOAuthConnectCallbackHandler __callbackHandler;

    private boolean __isPasswordEncrypted;

    private IPasswordProcessor __password;

    public DefaultModuleCfg(YMP owner) {
        __moduleCfgs = owner.getConfig().getModuleConfigs(ICmsOAuthConnector.MODULE_NAME);
        //
        __cacheNamePrefix = StringUtils.trimToEmpty(__moduleCfgs.get("cache_name_prefix"));
        //
        __callbackHandler = ClassUtils.impl(__moduleCfgs.get("callback_handler_class"), IOAuthConnectCallbackHandler.class, this.getClass());
        if (__callbackHandler == null) {
            __callbackHandler = new DefaultConnectCallbackHandler();
        }
        //
        __isPasswordEncrypted = BlurObject.bind(__moduleCfgs.get("password_encrypted")).toBooleanValue();
        //
        try {
            __password = ClassUtils.impl(__moduleCfgs.get("password_class"), IPasswordProcessor.class, this.getClass());
        } catch (Exception ignored) {
        }
    }

    public String getCacheNamePrefix() {
        return __cacheNamePrefix;
    }

    public IOAuthConnectProcessor.ConnectInitCfg getConnectInitCfg(String name) throws Exception {
        String _clientId = __moduleCfgs.get(name + ".client_id");
        String _clientSecret = __moduleCfgs.get(name + ".client_secret");
        if (StringUtils.isBlank(_clientId) || StringUtils.isBlank(_clientSecret)) {
            return null;
        } else if (__isPasswordEncrypted && __password != null) {
            _clientSecret = __password.decrypt(_clientSecret);
        }
        return new IOAuthConnectProcessor.ConnectInitCfg(name, _clientId, _clientSecret, __moduleCfgs.get(name + ".redirect_uri"));
    }

    public IOAuthConnectCallbackHandler getConnectCallbackHandler() {
        return __callbackHandler;
    }

    public boolean isPasswordEncrypted() {
        return false;
    }

    public Class<? extends IPasswordProcessor> getPasswordClass() {
        return null;
    }
}