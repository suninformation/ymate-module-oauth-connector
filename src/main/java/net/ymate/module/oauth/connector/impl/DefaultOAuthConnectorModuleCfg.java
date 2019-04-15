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

import net.ymate.module.oauth.connector.IOAuthConnectCallbackHandler;
import net.ymate.module.oauth.connector.IOAuthConnectProcessor;
import net.ymate.module.oauth.connector.IOAuthConnector;
import net.ymate.module.oauth.connector.IOAuthConnectorModuleCfg;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.support.IConfigReader;
import net.ymate.platform.core.support.IPasswordProcessor;
import net.ymate.platform.core.support.impl.MapSafeConfigReader;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/03/27 上午 01:31
 * @version 1.0
 */
public class DefaultOAuthConnectorModuleCfg implements IOAuthConnectorModuleCfg {

    private YMP __owner;

    private String __cacheNamePrefix;

    private IConfigReader __moduleCfg;

    private IOAuthConnectCallbackHandler __callbackHandler;

    private boolean __isPasswordEncrypted;

    private IPasswordProcessor __password;

    private Map<String, IOAuthConnectProcessor.ConnectInitCfg> __connectInitCfgs = new ConcurrentHashMap<String, IOAuthConnectProcessor.ConnectInitCfg>();

    public DefaultOAuthConnectorModuleCfg(YMP owner) {
        __owner = owner;
        __moduleCfg = MapSafeConfigReader.bind(owner.getConfig().getModuleConfigs(IOAuthConnector.MODULE_NAME));
        //
        __cacheNamePrefix = StringUtils.trimToEmpty(__moduleCfg.getString(CACHE_NAME_PREFIX));
        //
        __callbackHandler = __moduleCfg.getClassImpl(CALLBACK_HANDLER_CLASS, IOAuthConnectCallbackHandler.class);
        if (__callbackHandler == null) {
            __callbackHandler = new DefaultConnectCallbackHandler();
        }
        //
        __isPasswordEncrypted = __moduleCfg.getBoolean(PASSWORD_ENCRYPTED);
        //
        if (__isPasswordEncrypted) {
            try {
                __password = __moduleCfg.getClassImpl(PASSWORD_CLASS, IPasswordProcessor.class);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public String getCacheNamePrefix() {
        return __cacheNamePrefix;
    }

    @Override
    public IOAuthConnectProcessor.ConnectInitCfg getConnectInitCfg(String name) throws Exception {
        IOAuthConnectProcessor.ConnectInitCfg _initCfg = __connectInitCfgs.get(name);
        if (_initCfg == null) {
            String _clientId = null;
            String _clientSecret = null;
            String _redirectUrl = null;
            //
            Map<String, String> _attributes = new HashMap<String, String>();
            Map<String, String> _cfgMap = __moduleCfg.getMap(name + ".");
            for (Map.Entry<String, String> _entry : _cfgMap.entrySet()) {
                if (CLIENT_ID.equalsIgnoreCase(_entry.getKey())) {
                    _clientId = _entry.getValue();
                } else if (CLIENT_SECRET.equalsIgnoreCase(_entry.getKey())) {
                    _clientSecret = _entry.getValue();
                } else if (REDIRECT_URI.equalsIgnoreCase(_entry.getKey())) {
                    _redirectUrl = _entry.getValue();
                } else {
                    _attributes.put(_entry.getKey(), _entry.getValue());
                }
            }
            if (StringUtils.isNotBlank(_clientId) && StringUtils.isNotBlank(_clientSecret)) {
                if (__isPasswordEncrypted) {
                    if (__password != null) {
                        _clientSecret = __password.decrypt(_clientSecret);
                    } else {
                        _clientSecret = __owner.getConfig().getDefaultPasswordClass().newInstance().decrypt(_clientSecret);
                    }
                }
                _initCfg = new IOAuthConnectProcessor.ConnectInitCfg(name, _clientId, _clientSecret, _redirectUrl, _attributes);
                //
                __connectInitCfgs.put(name, _initCfg);
            }
        }
        return _initCfg;
    }

    @Override
    public IOAuthConnectCallbackHandler getConnectCallbackHandler() {
        return __callbackHandler;
    }
}