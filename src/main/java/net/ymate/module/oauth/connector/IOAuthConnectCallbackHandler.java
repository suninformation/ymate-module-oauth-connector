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

import net.ymate.platform.webmvc.view.IView;

/**
 * 连接授权回调处理器接口
 *
 * @author 刘镇 (suninformation@163.com) on 17/3/28 上午11:55
 * @version 1.0
 */
public interface IOAuthConnectCallbackHandler {

    /**
     * 初始化
     *
     * @param owner 所属模块实例
     */
    void init(ICmsOAuthConnector owner);

    /**
     * 连接调用操作前执行
     *
     * @param connectName 连接器名称
     * @param state       自定义状态值(用于防CSRF攻击)
     * @throws Exception 可能产生的任何异常
     */
    void connect(String connectName, String state) throws Exception;

    /**
     * @param connectName 连接器名称
     * @param connectUser 已获取第三方用户信息
     * @param state       自定义状态值(用于防CSRF攻击)
     * @return 自定义处理结果并返回视图对象
     * @throws Exception 可能产生的任何异常
     */
    IView handle(String connectName, OAuthConnectUser connectUser, String state) throws Exception;
}
