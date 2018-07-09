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
import net.ymate.platform.core.lang.BlurObject;
import net.ymate.platform.core.support.IPasswordProcessor;
import net.ymate.platform.core.util.ClassUtils;
import net.ymate.platform.core.util.RuntimeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/03/27 上午 01:31
 * @version 1.0
 */
public class DefaultModuleCfg implements IOAuthConnectorModuleCfg {

    private String __cacheNamePrefix;

    private Map<String, String> __moduleCfgs;

    private IOAuthConnectCallbackHandler __callbackHandler;

    private boolean __isPasswordEncrypted;

    private IPasswordProcessor __password;

    private Map<String, IOAuthConnectProcessor.ConnectInitCfg> __connectInitCfgs = new ConcurrentHashMap<String, IOAuthConnectProcessor.ConnectInitCfg>();

    public DefaultModuleCfg(YMP owner) {
        __moduleCfgs = owner.getConfig().getModuleConfigs(IOAuthConnector.MODULE_NAME);
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
            Map<String, String> _cfgMap = RuntimeUtils.keyStartsWith(__moduleCfgs, name + ".");
            for (Map.Entry<String, String> _entry : _cfgMap.entrySet()) {
                if ("client_id".equalsIgnoreCase(_entry.getKey())) {
                    _clientId = _entry.getValue();
                } else if ("client_secret".equalsIgnoreCase(_entry.getValue())) {
                    _clientSecret = _entry.getValue();
                } else if ("redirect_uri".equalsIgnoreCase(_entry.getValue())) {
                    _redirectUrl = _entry.getValue();
                } else {
                    _attributes.put(_entry.getKey(), _entry.getValue());
                }
            }
            if (StringUtils.isBlank(_clientId) && StringUtils.isBlank(_clientSecret)) {
                if (__isPasswordEncrypted && __password != null) {
                    _clientSecret = __password.decrypt(_clientSecret);
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