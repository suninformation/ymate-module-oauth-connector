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
import net.ymate.framework.commons.HttpClientHelper;
import net.ymate.framework.commons.IHttpResponse;
import net.ymate.module.oauth.connector.AbstractOAuthConnectProcessor;
import net.ymate.module.oauth.connector.OAuthConnectUser;
import net.ymate.module.oauth.connector.annotation.OAuthConnectProcessor;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * @author 刘镇 (suninformation@163.com) on 17/3/27 下午1:16
 * @version 1.0
 */
@OAuthConnectProcessor("github")
public class GitHubConnectProcessor extends AbstractOAuthConnectProcessor {

    private static final String __CONNECT_URL = "https://github.com/login/oauth/authorize?";

    private static final String __TOKEN_URL = "https://github.com/login/oauth/access_token";

    private static final String __USERINFO_URL = "https://api.github.com/user?access_token=";

    @Override
    public String getAuthorizeUrl(String state) {
        return __CONNECT_URL + __doBuildAuthzUrl("user", state, false);
    }

    @Override
    protected Header[] __doGetRequestHeaders() {
        return new Header[]{new BasicHeader("Accept", "application/json")};
    }

    @Override
    public OAuthConnectUser getConnectUser(String code) throws Exception {
        OAuthConnectUser _connectUser = __doGetAccessToken(code, __TOKEN_URL, true);
        if (_connectUser != null) {
            if (StringUtils.isNotBlank(_connectUser.getAccessToken())) {
                IHttpResponse _response = HttpClientHelper.create().get(__USERINFO_URL.concat(_connectUser.getAccessToken()), __doGetRequestHeaders());
                JSONObject _result = __doParseConnectResponseBody(_response);
                if (_result != null) {
                    _connectUser.setOpenId(_result.getString("id"))
                            .setNickName(_result.getString("login"))
                            .setPhotoUrl(_result.getString("avatar_url"));
                }
            }
        }
        return _connectUser;
    }
}
