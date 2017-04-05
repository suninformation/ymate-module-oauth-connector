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

import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 17/3/27 下午1:15
 * @version 1.0
 */
@OAuthConnectProcessor("weibo")
public class WeiboConnectProcessor extends AbstractOAuthConnectProcessor {

    private static final String __CONNECT_URL = "https://api.weibo.com/oauth2/authorize?";

    private static final String __TOKEN_URL = "https://api.weibo.com/oauth2/access_token";

    private static final String __USERINFO_URL = "https://api.weibo.com/2/users/show.json";

    public WeiboConnectProcessor() {
        super("uid");
        this.__doSetErrorFlag("error_code");
        this.__doSetNeedRedirectUri(true);
    }

    public String getAuthorizeUrl(String state) {
        return __CONNECT_URL + __doBuildAuthzUrl("email", state, true);
    }

    public OAuthConnectUser getConnectUser(String code) throws Exception {
        OAuthConnectUser _connectUser = __doGetAccessToken(code, __TOKEN_URL, true);
        if (_connectUser != null) {
            if (StringUtils.isNotBlank(_connectUser.getAccessToken())) {
                Map<String, String> _params = new HashMap<String, String>();
                _params.put("access_token", _connectUser.getAccessToken());
                _params.put("uid", _connectUser.getOpenId());
                //
                IHttpResponse _response = HttpClientHelper.create().get(__USERINFO_URL, _params, __doGetRequestHeaders());
                JSONObject _result = __doParseConnectResponseBody(_response);
                if (_result != null) {
                    _connectUser.setNickName(_result.getString("screen_name"))
                            .setPhotoUrl(_result.getString("avatar_large"));
                    String _gender = _result.getString("gender");
                    if (StringUtils.equals(_gender, "m")) {
                        _connectUser.setGender(OAuthConnectUser.Gender.FAMALE);
                    } else if (StringUtils.equals(_gender, "f")) {
                        _connectUser.setGender(OAuthConnectUser.Gender.MALE);
                    } else {
                        _connectUser.setGender(OAuthConnectUser.Gender.UNKNOW);
                    }
                    //
                    _connectUser.putAttribute("province", _result.getString("province"));
                    _connectUser.putAttribute("city", _result.getString("city"));
                    //
                    return _connectUser;
                }
            }
        }
        return null;
    }
}
