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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 17/3/27 上午1:42
 * @version 1.0
 */
public class OAuthConnectUser implements Serializable {

    private String openId;

    private String accessToken;

    private String refreshToken;

    private String scope;

    private int expiresIn;

    //

    private String unionId;

    private String nickName;

    private Gender gender;

    private String photoUrl;

    private Map<String, Object> attributes;

    public OAuthConnectUser() {
        this.attributes = new HashMap<String, Object>();
    }

    public String getOpenId() {
        return openId;
    }

    public OAuthConnectUser setOpenId(String openId) {
        this.openId = openId;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public OAuthConnectUser setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public OAuthConnectUser setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public String getScope() {
        return scope;
    }

    public OAuthConnectUser setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public OAuthConnectUser setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public String getUnionId() {
        return unionId;
    }

    public OAuthConnectUser setUnionId(String unionId) {
        this.unionId = unionId;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public OAuthConnectUser setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public OAuthConnectUser setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public OAuthConnectUser setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public OAuthConnectUser setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }

    public OAuthConnectUser putAttribute(String attrKey, Object attrValue) {
        this.attributes.put(attrKey, attrValue);
        return this;
    }

    /**
     * 用户性别枚举
     */
    public enum Gender {

        MALE(1), FEMALE(2), UNKNOW(0);

        private int code;

        Gender(int code) {
            this.code = code;
        }
    }
}
