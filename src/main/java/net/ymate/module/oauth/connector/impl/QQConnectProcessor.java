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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ymate.framework.commons.HttpClientHelper;
import net.ymate.framework.commons.IHttpResponse;
import net.ymate.framework.commons.ParamUtils;
import net.ymate.module.oauth.connector.AbstractOAuthConnectProcessor;
import net.ymate.module.oauth.connector.OAuthConnectUser;
import net.ymate.module.oauth.connector.annotation.OAuthConnectProcessor;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 17/3/27 下午1:12
 * @version 1.0
 */
@OAuthConnectProcessor("qq")
public class QQConnectProcessor extends AbstractOAuthConnectProcessor {

    private static final String __CONNECT_URL = "https://graph.qq.com/oauth2.0/authorize?";

    private static final String __TOKEN_URL = "https://graph.qq.com/oauth2.0/token?";

    private static final String __OPEN_ID_URL = "https://graph.qq.com/oauth2.0/me?access_token=";

    private static final String __USERINFO_URL = "https://graph.qq.com/user/get_user_info?";

    public String getAuthorizeUrl(String state) {
        return __CONNECT_URL + __doBuildAuthzUrl(null, state, true);
    }

    @Override
    protected OAuthConnectUser __doGetAccessToken(String code, String tokenUrl) throws Exception {
        OAuthConnectUser _connectUser = super.__doGetAccessToken(code, tokenUrl);
        if (_connectUser != null) {
            IHttpResponse _response = HttpClientHelper.create().get(__OPEN_ID_URL.concat(_connectUser.getAccessToken()), __doGetRequestHeaders());
            JSONObject _result = __doParseConnectResponseBody(_response);
            if (_result != null) {
                _connectUser.setOpenId(_result.getString("openid"));
            }
        }
        return _connectUser;
    }

    public OAuthConnectUser getConnectUser(String code) throws Exception {
        OAuthConnectUser _connectUser = __doGetAccessToken(code, __TOKEN_URL);
        if (_connectUser != null) {
            if (StringUtils.isNotBlank(_connectUser.getAccessToken()) && StringUtils.isNotBlank(_connectUser.getOpenId())) {
                Map<String, String> _params = new HashMap<String, String>();
                _params.put("access_token", _connectUser.getAccessToken());
                _params.put("oauth_consumer_key", __initCfg.getClientId());
                _params.put("openid", _connectUser.getOpenId());
                //
                IHttpResponse _response = HttpClientHelper.create().get(__USERINFO_URL, _params);
                JSONObject _result = super.__doParseConnectResponseBody(_response);
                if (_result != null) {
                    int _ret = _result.getIntValue("ret");
                    if (_ret == 0) {
                        _connectUser.setNickName(_result.getString("nickname"))
                                .setPhotoUrl(_result.getString("figureurl_qq_2"));
                        if (StringUtils.isBlank(_connectUser.getPhotoUrl())) {
                            _connectUser.setPhotoUrl(_result.getString("figureurl_qq_1"));
                        }
                        String _gender = _result.getString("gender");
                        if (StringUtils.equals(_gender, "女")) {
                            _connectUser.setGender(OAuthConnectUser.Gender.FEMALE);
                        } else if (StringUtils.equals(_gender, "男")) {
                            _connectUser.setGender(OAuthConnectUser.Gender.MALE);
                        } else {
                            _connectUser.setGender(OAuthConnectUser.Gender.UNKNOW);
                        }
                    } else {
                        throw new RuntimeException(_result.toJSONString());
                    }
                }
            }
        }
        return _connectUser;
    }

    @Override
    protected JSONObject __doParseConnectResponseBody(IHttpResponse response) throws Exception {
        if (response != null && response.getStatusCode() == 200) {
            JSONObject _result = null;
            if (StringUtils.startsWith(response.getContent(), "callback")) {
                _result = JSON.parseObject(StringUtils.substringBetween(response.getContent(), "callback(", ");"));
            } else {
                _result = new JSONObject();
                _result.putAll(ParamUtils.parseQueryParamStr(response.getContent(), true, "UTF-8"));
            }
            if (_result.containsKey("error")) {
                throw new RuntimeException(_result.toJSONString());
            }
            return _result;
        }
        return null;
    }
}
