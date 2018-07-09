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

import net.ymate.framework.commons.ParamUtils;
import net.ymate.framework.core.Optional;
import net.ymate.framework.core.util.WebUtils;
import net.ymate.platform.webmvc.context.WebContext;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 17/3/27 上午11:40
 * @version 1.0
 */
public interface IOAuthConnectProcessor {

    void init(ConnectInitCfg initCfg);

    /**
     * @param state 自定义状态值(用于防CSRF攻击)
     * @return 返回获取第三方授权码的URL地址
     */
    String getAuthorizeUrl(String state);

    /**
     * @param code 第三方授权码
     * @return 返回通过授权码获取的用户信息
     * @throws Exception 可能产生的任何异常
     */
    OAuthConnectUser getConnectUser(String code) throws Exception;

    /**
     * 授权连接器初始化配置
     */
    class ConnectInitCfg {

        private String name;

        private String clientId;

        private String clientSecret;

        private String redirectUri;

        private Map<String, String> attributes;

        public ConnectInitCfg(String name, String clientId, String clientSecret, String redirectUri, Map<String, String> attributes) {
            this.name = name;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.redirectUri = redirectUri;
            //
            this.attributes = attributes != null ? attributes : Collections.<String, String>emptyMap();
        }

        public String getName() {
            return name;
        }

        public String getClientId() {
            return clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public String getRedirectUri() {
            if (StringUtils.isBlank(redirectUri)) {
                redirectUri = WebUtils.buildURL(WebContext.getRequest(), "/oauth2/connect/" + name + "/redirect", true);
            }
            String _redirectUrl = WebContext.getRequest().getParameter(Optional.REDIRECT_URL);
            if (StringUtils.isNotBlank(_redirectUrl)) {
                Map<String, String> _params = new HashMap<String, String>();
                _params.put(Optional.REDIRECT_URL, _redirectUrl);
                return ParamUtils.appendQueryParamValue(redirectUri, _params, true, "UTF-8");
            }
            return redirectUri;
        }

        public String getAttribute(String attrKey) {
            return this.attributes.get(attrKey);
        }

        public Map<String, String> getAttributes() {
            return Collections.unmodifiableMap(attributes);
        }
    }
}
