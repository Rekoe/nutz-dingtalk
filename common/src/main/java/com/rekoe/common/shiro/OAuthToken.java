/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.rekoe.common.shiro;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

public final class OAuthToken implements AuthenticationToken, RememberMeAuthenticationToken, HostAuthenticationToken {

	private static final long serialVersionUID = 3376624432421737333L;

	private String openid;

	private boolean locked;

	private String nickName;

	protected boolean rememberMe;

	protected String host;

	private long userid;

	public OAuthToken(String openid, long userid, boolean isLocked, String host) {
		this.openid = openid;
		this.locked = isLocked;
		this.host = host;
		this.userid = userid;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public OAuthToken(String openid, long userid, boolean isLocked, String nickName, String host) {
		super();
		this.openid = openid;
		this.locked = isLocked;
		this.nickName = nickName;
		this.host = host;
		this.userid = userid;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	@Override
	public Object getPrincipal() {
		return this;
	}

	@Override
	public Object getCredentials() {
		return this;
	}

	@Override
	public boolean isRememberMe() {
		return true;
	}

	@Override
	public String getHost() {
		return this.host;
	}
}