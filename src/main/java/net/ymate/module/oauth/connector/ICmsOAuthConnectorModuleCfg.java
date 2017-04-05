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

/**
 * @author 刘镇 (suninformation@163.com) on 2017/03/27 上午 01:31
 * @version 1.0
 */
public interface ICmsOAuthConnectorModuleCfg {

    /**
     * @return 缓存名称前缀, 默认值: ""
     */
    String getCacheNamePrefix();

    /**
     * @param name 连接器名称
     * @return 获取指定连接器初始化配置
     * @throws Exception 可能产生的任何异常
     */
    IOAuthConnectProcessor.ConnectInitCfg getConnectInitCfg(String name) throws Exception;

    /**
     * @return 获取连接授权回调处理器接口实现
     */
    IOAuthConnectCallbackHandler getConnectCallbackHandler();
}