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
package net.ymate.module.oauth.connector;

import net.ymate.module.oauth.connector.annotation.OAuthConnectProcessor;
import net.ymate.module.oauth.connector.handle.OAuthConnectProcessorHandler;
import net.ymate.module.oauth.connector.impl.DefaultModuleCfg;
import net.ymate.platform.core.Version;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.module.IModule;
import net.ymate.platform.core.module.annotation.Module;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/03/27 上午 01:31
 * @version 1.0
 */
@Module
public class OAuthConnector implements IModule, IOAuthConnector {

    private static final Log _LOG = LogFactory.getLog(OAuthConnector.class);

    public static final Version VERSION = new Version(1, 0, 0, OAuthConnector.class.getPackage().getImplementationVersion(), Version.VersionType.Alphal);

    private static volatile IOAuthConnector __instance;

    private YMP __owner;

    private IOAuthConnectorModuleCfg __moduleCfg;

    private boolean __inited;

    private Map<String, IOAuthConnectProcessor> __connectProcessors;

    public static IOAuthConnector get() {
        if (__instance == null) {
            synchronized (VERSION) {
                if (__instance == null) {
                    __instance = YMP.get().getModule(OAuthConnector.class);
                }
            }
        }
        return __instance;
    }

    public String getName() {
        return IOAuthConnector.MODULE_NAME;
    }

    public void init(YMP owner) throws Exception {
        if (!__inited) {
            //
            _LOG.info("Initializing ymate-module-oauth-connector-" + VERSION);
            //
            __owner = owner;
            __moduleCfg = new DefaultModuleCfg(owner);
            __owner.registerHandler(OAuthConnectProcessor.class, new OAuthConnectProcessorHandler(this));
            //
            __moduleCfg.getConnectCallbackHandler().init(this);
            //
            __connectProcessors = new HashMap<String, IOAuthConnectProcessor>();
            //
            __inited = true;
        }
    }

    public boolean isInited() {
        return __inited;
    }

    public void registerConnectProcessorHandler(String name, Class<? extends IOAuthConnectProcessor> targetClass) throws Exception {
        if (StringUtils.isNotBlank(name) && targetClass != null) {
            IOAuthConnectProcessor.ConnectInitCfg _initCfg = __moduleCfg.getConnectInitCfg(name);
            if (_initCfg != null) {
                _LOG.info("--> Initializing connect processor " + targetClass);
                //
                IOAuthConnectProcessor _processor = targetClass.newInstance();
                _processor.init(_initCfg);
                //
                __connectProcessors.put(name, _processor);
            }
        }
    }

    public IOAuthConnectProcessor getConnectProcessor(String name) {
        return __connectProcessors.get(name);
    }

    public void destroy() throws Exception {
        if (__inited) {
            __inited = false;
            //
            __moduleCfg = null;
            __owner = null;
        }
    }

    public YMP getOwner() {
        return __owner;
    }

    public IOAuthConnectorModuleCfg getModuleCfg() {
        return __moduleCfg;
    }
}
