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

import com.alibaba.fastjson.JSONObject;
import net.ymate.module.oauth.connector.ICmsOAuthConnector;
import net.ymate.module.oauth.connector.IOAuthConnectCallbackHandler;
import net.ymate.module.oauth.connector.OAuthConnectUser;
import net.ymate.platform.webmvc.context.WebContext;
import net.ymate.platform.webmvc.util.CookieHelper;
import net.ymate.platform.webmvc.view.IView;
import net.ymate.platform.webmvc.view.View;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.http.HttpServletResponse;

/**
 * 默认连接授权回调处理器接口实现
 *
 * @author 刘镇 (suninformation@163.com) on 17/3/28 下午12:21
 * @version 1.0
 */
public class DefaultConnectCallbackHandler implements IOAuthConnectCallbackHandler {

    private String __cookieName;

    public void init(ICmsOAuthConnector owner) {
        __cookieName = owner.getModuleCfg().getCacheNamePrefix() + ICmsOAuthConnector.MODULE_NAME + "_state";
    }

    public void connect(String connectName, String state) throws Exception {
        CookieHelper.bind(WebContext.getContext().getOwner()).setCookie(__cookieName, __doEncryptState(connectName, state));
    }

    public IView handle(String connectName, OAuthConnectUser connectUser, String state) throws Exception {
        CookieHelper _helper = CookieHelper.bind(WebContext.getContext().getOwner());
        try {
            String _cookieState = _helper.getCookie(__cookieName).toStringValue();
            if (__doEncryptState(connectName, state).equals(_cookieState)) {
                return __doHandle(connectName, connectUser, state);
            }
            return View.httpStatusView(HttpServletResponse.SC_BAD_REQUEST);
        } finally {
            _helper.removeCookie(__cookieName);
        }
    }

    protected IView __doHandle(String connectName, OAuthConnectUser connectUser, String state) throws Exception {
        JSONObject _json = new JSONObject();
        _json.put(connectName, connectUser);
        _json.put("state", state);
        _json.put("status", true);
        //
        return View.jsonView(_json);
    }

    protected String __doEncryptState(String connectName, String state) throws Exception {
        return DigestUtils.md5Hex(connectName + "|" + state);
    }
}
