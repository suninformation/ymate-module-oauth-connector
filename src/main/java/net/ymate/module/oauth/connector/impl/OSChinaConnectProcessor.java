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
 * @author 刘镇 (suninformation@163.com) on 17/4/4 上午12:58
 * @version 1.0
 */
@OAuthConnectProcessor("oschina")
public class OSChinaConnectProcessor extends AbstractOAuthConnectProcessor {

    private static final String __CONNECT_URL = "https://www.oschina.net/action/oauth2/authorize?";

    private static final String __TOKEN_URL = "https://www.oschina.net/action/openapi/token?dataType=json";

    private static final String __USERINFO_URL = "https://www.oschina.net/action/openapi/user?dataType=json&access_token=";

    public OSChinaConnectProcessor() {
        super("uid");
        this.__doSetNeedRedirectUri(true);
    }

    public String getAuthorizeUrl(String state) {
        return __CONNECT_URL + __doBuildAuthzUrl(null, state, true);
    }

    @Override
    protected Header[] __doGetRequestHeaders() {
        return new Header[]{new BasicHeader("Accept", "application/json"), new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")};
    }

    public OAuthConnectUser getConnectUser(String code) throws Exception {
        OAuthConnectUser _connectUser = __doGetAccessToken(code, __TOKEN_URL);
        if (_connectUser != null) {
            if (StringUtils.isNotBlank(_connectUser.getAccessToken())) {
                IHttpResponse _response = HttpClientHelper.create().get(__USERINFO_URL.concat(_connectUser.getAccessToken()), __doGetRequestHeaders());
                JSONObject _result = __doParseConnectResponseBody(_response);
                if (_result != null) {
                    _connectUser.setOpenId(_result.getString("id"))
                            .setNickName(_result.getString("name"))
                            .setPhotoUrl(_result.getString("avatar"));
                    String _gender = _result.getString("gender");
                    if (StringUtils.isNotBlank(_gender)) {
                        _connectUser.setGender(OAuthConnectUser.Gender.valueOf(_gender.toUpperCase()));
                    } else {
                        _connectUser.setGender(OAuthConnectUser.Gender.UNKNOW);
                    }
                }
            }
        }
        return _connectUser;
    }
}
