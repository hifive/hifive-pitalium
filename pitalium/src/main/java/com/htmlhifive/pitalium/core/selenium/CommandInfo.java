package com.htmlhifive.pitalium.core.selenium;

import org.openqa.selenium.remote.http.HttpMethod;

public class CommandInfo {
	private final String url;
	private final HttpMethod method;

	public CommandInfo(String url, HttpMethod method) {
		this.url = url;
		this.method = method;
	}

	String getUrl() {
		return url;
	}

	HttpMethod getMethod() {
		return method;
	}
}