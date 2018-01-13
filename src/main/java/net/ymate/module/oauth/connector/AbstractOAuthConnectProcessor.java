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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ymate.framework.commons.HttpClientHelper;
import net.ymate.framework.commons.IHttpResponse;
import net.ymate.framework.commons.ParamUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 17/3/27 下午6:27
 * @version 1.0
 */
public abstract class AbstractOAuthConnectProcessor implements IOAuthConnectProcessor {

    private static final Log _LOG = LogFactory.getLog(AbstractOAuthConnectProcessor.class);

    private String __clientParamName;

    private String __secretParamName;

    private String __openIdParamName;

    /**
     * 异常键标识
     */
    private String __errorFlag;

    private boolean __needRedirectUri;

    protected ConnectInitCfg __initCfg;

    public AbstractOAuthConnectProcessor() {
        this(null, null, null);
    }

    public AbstractOAuthConnectProcessor(String openIdParamName) {
        this(null, null, openIdParamName);
    }

    public AbstractOAuthConnectProcessor(String clientParamName, String secretParamName, String openIdParamName) {
        __clientParamName = StringUtils.defaultIfBlank(clientParamName, "client_id");
        __secretParamName = StringUtils.defaultIfBlank(secretParamName, "client_secret");
        __openIdParamName = openIdParamName;
    }

    @Override
    public void init(ConnectInitCfg initCfg) {
        __initCfg = initCfg;
    }

    protected void __doSetErrorFlag(String errorFlag) {
        __errorFlag = StringUtils.defaultIfBlank(errorFlag, "error");
    }

    protected void __doSetNeedRedirectUri(boolean needRedirectUri) {
        __needRedirectUri = needRedirectUri;
    }

    protected String __doBuildAuthzUrl(String scope, String state, boolean code) {
        Map<String, String> _params = new HashMap<String, String>();
        _params.put(__clientParamName, __initCfg.getClientId());
        _params.put("redirect_uri", __initCfg.getRedirectUri());
        if (code) {
            _params.put("response_type", "code");
        }
        if (StringUtils.isNotBlank(scope)) {
            _params.put("scope", scope);
        }
        if (StringUtils.isNotBlank(state)) {
            _params.put("state", state);
        }
        return ParamUtils.buildQueryParamStr(_params, true, "UTF-8");
    }

    protected OAuthConnectUser __doGetAccessToken(String code, String tokenUrl) throws Exception {
        return __doGetAccessToken(code, tokenUrl, false);
    }

    protected OAuthConnectUser __doGetAccessToken(String code, String tokenUrl, boolean usePost) throws Exception {
        Map<String, String> _params = new HashMap<String, String>();
        _params.put(__clientParamName, __initCfg.getClientId());
        _params.put(__secretParamName, __initCfg.getClientSecret());
        if (__needRedirectUri) {
            _params.put("redirect_uri", __initCfg.getRedirectUri());
        }
        _params.put("code", code);
        _params.put("grant_type", "authorization_code");
        //
        IHttpResponse _response = null;
        if (usePost) {
            _response = HttpClientHelper.create().post(tokenUrl, _params, __doGetRequestHeaders());
        } else {
            _response = HttpClientHelper.create().get(tokenUrl, _params, __doGetRequestHeaders());
        }
        JSONObject _result = __doParseConnectResponseBody(_response);
        if (_result != null) {
            return new OAuthConnectUser()
                    .setAccessToken(_result.getString("access_token"))
                    .setRefreshToken(_result.getString("refresh_token"))
                    .setExpiresIn(_result.getIntValue("expires_in"))
                    .setOpenId(StringUtils.isBlank(__openIdParamName) ? null : _result.getString(__openIdParamName))
                    .setScope(_result.getString("scope"));
        }
        return null;
    }

    protected Header[] __doGetRequestHeaders() {
        return null;
    }

    /**
     * @param response 回应对象
     * @return 解析请求回应结果，存在异常信息则直接抛出异常
     * @throws Exception 可能产生的任何异常
     */
    protected JSONObject __doParseConnectResponseBody(IHttpResponse response) throws Exception {
        if (response != null) {
            if (response.getStatusCode() == 200) {
                JSONObject _result = JSON.parseObject(response.getContent());
                if (_result.containsKey(__errorFlag)) {
                    throw new RuntimeException(_result.toJSONString());
                }
                return _result;
            } else if (_LOG.isDebugEnabled()) {
                _LOG.debug("ResponseBody: " + response.toString());
            }
        }
        return null;
    }
}
