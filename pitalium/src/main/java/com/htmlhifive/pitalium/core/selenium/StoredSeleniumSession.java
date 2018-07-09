package com.htmlhifive.pitalium.core.selenium;

import java.util.Map;

/**
 * jsonファイルに出力するセッション情報を持つクラス
 */
public class StoredSeleniumSession {
	private final String sessionId;
	private final Map<String, Object> rawCapabilities;
	private final String dialect;

	/**
	 * コンストラクタ
	 *
	 * @param sessionId セッションID
	 * @param rawCapabilities Capabilities
	 * @param dialect Dialect
	 */
	public StoredSeleniumSession(String sessionId, Map<String, Object> rawCapabilities, String dialect) {
		this.sessionId = sessionId;
		this.rawCapabilities = rawCapabilities;
		this.dialect = dialect;
	}

	/**
	 * セッションIDを取得する
	 *
	 * @return セッションID
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * Capabilitiesを取得する
	 *
	 * @return Capabilities
	 */
	public Map<String, Object> getRawCapabilities() {
		return rawCapabilities;
	}

	/**
	 * Dialectを取得する
	 *
	 * @return Dialect
	 */
	public String getDialect() {
		return dialect;
	}
}
