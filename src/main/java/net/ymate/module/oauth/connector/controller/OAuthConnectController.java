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
package net.ymate.module.oauth.connector.controller;

import net.ymate.framework.commons.ParamUtils;
import net.ymate.framework.webmvc.intercept.UserSessionStatusInterceptor;
import net.ymate.module.oauth.connector.IOAuthConnectProcessor;
import net.ymate.module.oauth.connector.OAuthConnectUser;
import net.ymate.module.oauth.connector.OAuthConnector;
import net.ymate.platform.core.beans.annotation.Before;
import net.ymate.platform.webmvc.annotation.Controller;
import net.ymate.platform.webmvc.annotation.PathVariable;
import net.ymate.platform.webmvc.annotation.RequestMapping;
import net.ymate.platform.webmvc.annotation.RequestParam;
import net.ymate.platform.webmvc.view.IView;
import net.ymate.platform.webmvc.view.View;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletResponse;

/**
 * @author 刘镇 (suninformation@163.com) on 17/3/27 上午11:30
 * @version 1.0
 */
@Controller
@RequestMapping("/oauth2/connect")
@Before(UserSessionStatusInterceptor.class)
public class OAuthConnectController {

    /**
     * @param connectName 连接器名称
     * @param state       自定义状态值(用于防CSRF攻击)
     * @return 跳转至获取第三方授权码的URL地址
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping("/{connectName}")
    public IView __connect(@PathVariable String connectName, @RequestParam String state) throws Exception {
        IOAuthConnectProcessor _processor = OAuthConnector.get().getConnectProcessor(connectName);
        if (_processor != null) {
            state = StringUtils.defaultIfBlank(state, ParamUtils.createNonceStr());
            IView _view = OAuthConnector.get().getModuleCfg().getConnectCallbackHandler().connect(connectName, state);
            //
            if (_view == null) {
                return View.redirectView(_processor.getAuthorizeUrl(state));
            }
            return _view;
        }
        return View.httpStatusView(HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * @param connectName 连接器名称
     * @param code        授权CODE
     * @param state       自定义状态值(用于防CSRF攻击)
     * @return 接收第三方授权后的回调处理
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping("/{connectName}/redirect")
    public IView __redirect(@PathVariable String connectName, @RequestParam String code, @RequestParam String state) throws Exception {
        IOAuthConnectProcessor _processor = OAuthConnector.get().getConnectProcessor(connectName);
        if (_processor != null) {
            OAuthConnectUser _connectUser = _processor.getConnectUser(code);
            if (_connectUser != null && StringUtils.isNotBlank(_connectUser.getOpenId())) {
                return OAuthConnector.get().getModuleCfg().getConnectCallbackHandler().handle(connectName, _connectUser, state);
            }
        }
        return View.httpStatusView(HttpServletResponse.SC_BAD_REQUEST);
    }
}
