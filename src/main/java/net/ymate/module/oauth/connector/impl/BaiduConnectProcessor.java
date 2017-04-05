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

/**
 * @author 刘镇 (suninformation@163.com) on 17/4/4 上午1:37
 * @version 1.0
 */
@OAuthConnectProcessor("baidu")
public class BaiduConnectProcessor extends AbstractOAuthConnectProcessor {

    private static final String __CONNECT_URL = "https://openapi.baidu.com/oauth/2.0/authorize?";

    private static final String __TOKEN_URL = "https://openapi.baidu.com/oauth/2.0/token";

    private static final String __USERINFO_URL = "https://openapi.baidu.com/rest/2.0/passport/users/getLoggedInUser?access_token=";

    public BaiduConnectProcessor() {
        super();
        this.__doSetNeedRedirectUri(true);
    }

    public String getAuthorizeUrl(String state) {
        return __CONNECT_URL + __doBuildAuthzUrl("basic", state, true);
    }

    public OAuthConnectUser getConnectUser(String code) throws Exception {
        OAuthConnectUser _connectUser = __doGetAccessToken(code, __TOKEN_URL);
        if (_connectUser != null) {
            if (StringUtils.isNotBlank(_connectUser.getAccessToken())) {
                IHttpResponse _response = HttpClientHelper.create().get(__USERINFO_URL.concat(_connectUser.getAccessToken()), __doGetRequestHeaders());
                JSONObject _result = __doParseConnectResponseBody(_response);
                if (_result != null) {
                    _connectUser.setOpenId(_result.getString("uid"))
                            .setNickName(_result.getString("uname"))
                            .setPhotoUrl(_result.getString("portrait"));
                    String _photoUrl = _result.getString("portrait");
                    if (StringUtils.isNotBlank(_photoUrl)) {
                        _connectUser.setPhotoUrl("http://tb.himg.baidu.com/sys/portrait/item/" + _photoUrl);
                    }
                    _connectUser.setGender(OAuthConnectUser.Gender.UNKNOW);
                }
            }
        }
        return _connectUser;
    }
}
