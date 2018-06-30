package com.htmlhifive.pitalium.core.model;

/**
 * Navigation Timingを保持するクラス。
 */
public class PerformanceTiming {

	private long navigationStart;
	private long domainLookupStart;
	private long domainLookupEnd;
	private long connectStart;
	private long connectEnd;
	private long fetchStart;
	private long requestStart;
	private long domLoading;
	private long domComplete;
	private long domInteractive;
	private long loadEventStart;
	private long loadEventEnd;
	private long domContentLoadedEventStart;
	private long domContentLoadedEventEnd;
	private long responseStart;
	private long responseEnd;
	private long redirectStart;
	private long redirectEnd;
	private long secureConnectionStart;
	private long unloadEventStart;
	private long unloadEventEnd;

	/**
	 * 空のオブジェクトを生成します。
	 */
	/**
	 * 
	 */
	public PerformanceTiming() {
	}

	/**
	 * 前のドキュメントのunloadイベントの開始時間を取得します。
	 * 
	 * @return 前のドキュメントのunloadイベントの開始時間
	 */
	public long getUnloadEventStart() {
		return unloadEventStart;
	}

	/**
	 * 前のドキュメントのunloadイベントの開始時間を設定します。
	 * 
	 * @param unloadEventStart 前のドキュメントのunloadイベントの開始時間
	 */
	public void setUnloadEventStart(long unloadEventStart) {
		this.unloadEventStart = unloadEventStart;
	}

	/**
	 * 前のドキュメントのunloadイベントの完了時間を取得します。
	 * 
	 * @return 前のドキュメントのunloadイベントの完了時間
	 */
	public long getUnloadEventEnd() {
		return unloadEventEnd;
	}

	/**
	 * 前のドキュメントのunloadイベントの完了時間を設定します。
	 * 
	 * @param unloadEventEnd 前のドキュメントのunloadイベントの完了時間
	 */
	public void setUnloadEventEnd(long unloadEventEnd) {
		this.unloadEventEnd = unloadEventEnd;
	}

	/**
	 * 前のドキュメントがあればunload完了時間、なければドキュメントの取得準備ができた時間を取得します。
	 * 
	 * @return 前のドキュメントがあればunload完了時間、なければドキュメントの取得準備ができた時間
	 */
	public long getNavigationStart() {
		return navigationStart;
	}

	/**
	 * 前のドキュメントがあればunload完了時間、なければドキュメントの取得準備ができた時間を設定します。
	 * 
	 * @param navigationStart 前のドキュメントがあればunload完了時間、なければドキュメントの取得準備ができた時間
	 */
	public void setNavigationStart(long navigationStart) {
		this.navigationStart = navigationStart;
	}

	/**
	 * リダイレクト開始時間を取得します。
	 * <p>
	 * リダイレクトがないか、オリジンが異なるリダイレクトがあれば0となります。
	 * </p>
	 * 
	 * @return リダイレクト開始時間
	 */
	public long getRedirectStart() {
		return redirectStart;
	}

	/**
	 * リダイレクト開始時間を設定します。
	 * 
	 * @param redirectStart リダイレクト開始時間
	 */
	public void setRedirectStart(long redirectStart) {
		this.redirectStart = redirectStart;
	}

	/**
	 * 最後のリダイレクトの完了時間を取得します。
	 * <p>
	 * リダイレクトがないか、オリジンが異なるリダイレクトがあれば0です。
	 * </p>
	 * 
	 * @return 最後のリダイレクトの完了時間
	 */
	public long getRedirectEnd() {
		return redirectEnd;
	}

	/**
	 * 最後のリダイレクトの完了時間を設定します。
	 * 
	 * @param redirectEnd 最後のリダイレクトの完了時間
	 */
	public void setRedirectEnd(long redirectEnd) {
		this.redirectEnd = redirectEnd;
	}

	/**
	 * セキュアな接続のハンドシェイク開始時間を取得します。
	 * <p>
	 * セキュアな接続がなければ0です。
	 * </p>
	 * 
	 * @return セキュアな接続のハンドシェイク開始時間
	 */
	public long getSecureConnectionStart() {
		return secureConnectionStart;
	}

	/**
	 * セキュアな接続のハンドシェイク開始時間を設定します。
	 * 
	 * @param secureConnectionStart セキュアな接続のハンドシェイク開始時間
	 */
	public void setSecureConnectionStart(long secureConnectionStart) {
		this.secureConnectionStart = secureConnectionStart;
	}

	/**
	 * ブラウザによるリソースの名前解決開始時間を取得します。
	 * 
	 * @return ブラウザによるリソースの名前解決開始時間
	 */
	public long getDomainLookupStart() {
		return domainLookupStart;
	}

	/**
	 * ブラウザによるリソースの名前解決開始時間を設定します。
	 * 
	 * @param domainLookupStart ブラウザによるリソースの名前解決開始時間
	 */
	public void setDomainLookupStart(long domainLookupStart) {
		this.domainLookupStart = domainLookupStart;
	}

	/**
	 * ブラウザによるリソースの名前解決完了時間を取得します。
	 * 
	 * @return ブラウザによるリソースの名前解決完了時間
	 */
	public long getDomainLookupEnd() {
		return domainLookupEnd;
	}

	/**
	 * ブラウザによるリソースの名前解決完了時間を設定します。
	 * 
	 * @param domainLookupEnd ブラウザによるリソースの名前解決完了時間
	 */
	public void setDomainLookupEnd(long domainLookupEnd) {
		this.domainLookupEnd = domainLookupEnd;
	}

	/**
	 * ブラウザからサーバへの接続開始時間を取得します。
	 * <p>
	 * トランスポート層でエラーが発生し接続確立を再開した場合は、最後の接続開始時間を表します。
	 * </p>
	 * 
	 * @return ブラウザからサーバへの接続開始時間
	 */
	public long getConnectStart() {
		return connectStart;
	}

	/**
	 * ブラウザからサーバへの接続開始時間を設定します。
	 * 
	 * @param connectStart ブラウザからサーバへの接続開始時間
	 */
	public void setConnectStart(long connectStart) {
		this.connectStart = connectStart;
	}

	/**
	 * ブラウザからサーバへの接続確立完了時間を取得します。
	 * 
	 * @return ブラウザからサーバへの接続確立完了時間
	 */
	public long getConnectEnd() {
		return connectEnd;
	}

	/**
	 * ブラウザからサーバへの接続確立完了時間を設定します。
	 * 
	 * @param connectEnd ブラウザからサーバへの接続確立完了時間
	 */
	public void setConnectEnd(long connectEnd) {
		this.connectEnd = connectEnd;
	}

	/**
	 * ドキュメントの取得準備ができた時間を取得します。
	 * 
	 * @return ドキュメントの取得準備ができた時間
	 */
	public long getFetchStart() {
		return fetchStart;
	}

	/**
	 * ドキュメントの取得準備ができた時間を設定します。
	 * 
	 * @param fetchStart ドキュメントの取得準備ができた時間
	 */
	public void setFetchStart(long fetchStart) {
		this.fetchStart = fetchStart;
	}

	/**
	 * ブラウザからサーバへのリクエスト開始時間を取得します。
	 * <p>
	 * トランスポート層でエラーが発生し接続が再開された場合は、新しい方のリクエストの開始時間を表します。
	 * </p>
	 * 
	 * @return ブラウザからサーバへのリクエスト開始時間
	 */
	public long getRequestStart() {
		return requestStart;
	}

	/**
	 * ブラウザからサーバへのリクエスト開始時間を設定します。
	 * 
	 * @param requestStart ブラウザからサーバへのリクエスト開始時間
	 */
	public void setRequestStart(long requestStart) {
		this.requestStart = requestStart;
	}

	/**
	 * HTML解析の開始時間を取得します。
	 * 
	 * @return HTML解析の開始時間
	 */
	public long getDomLoading() {
		return domLoading;
	}

	/**
	 * HTML解析の開始時間を設定します。
	 * 
	 * @param domLoading HTML解析の開始時間
	 */
	public void setDomLoading(long domLoading) {
		this.domLoading = domLoading;
	}

	/**
	 * ドキュメント内のリソース読込完了時間を取得します。
	 * 
	 * @return ドキュメント内のリソース読込完了時間
	 */
	public long getDomComplete() {
		return domComplete;
	}

	/**
	 * ドキュメント内のリソース読込完了時間を設定します。
	 * 
	 * @param domComplete ドキュメント内のリソース読込完了時間
	 */
	public void setDomComplete(long domComplete) {
		this.domComplete = domComplete;
	}

	/**
	 * HTML解析の完了時間を取得します。
	 * 
	 * @return HTML解析の完了時間
	 */
	public long getDomInteractive() {
		return domInteractive;
	}

	/**
	 * HTML解析の完了時間を設定します。
	 * 
	 * @param domInteractive HTML解析の完了時間
	 */
	public void setDomInteractive(long domInteractive) {
		this.domInteractive = domInteractive;
	}

	/**
	 * loadイベントの開始時間を取得します。
	 * 
	 * @return loadイベントの開始時間
	 */
	public long getLoadEventStart() {
		return loadEventStart;
	}

	/**
	 * loadイベントの開始時間を設定します。
	 * 
	 * @param loadEventStart loadイベントの開始時間
	 */
	public void setLoadEventStart(long loadEventStart) {
		this.loadEventStart = loadEventStart;
	}

	/**
	 * loadイベントの完了時間を取得します。
	 * 
	 * @return loadイベントの完了時間
	 */
	public long getLoadEventEnd() {
		return loadEventEnd;
	}

	/**
	 * loadイベントの完了時間を設定します。
	 * 
	 * @param loadEventEnd loadイベントの完了時間
	 */
	public void setLoadEventEnd(long loadEventEnd) {
		this.loadEventEnd = loadEventEnd;
	}

	/**
	 * DOMContentLoadedイベントの開始時間を取得します。
	 * 
	 * @return DOMContentLoadedイベントの開始時間
	 */
	public long getDomContentLoadedEventStart() {
		return domContentLoadedEventStart;
	}

	/**
	 * DOMContentLoadedイベントの開始時間を設定します。
	 * 
	 * @param domContentLoadedEventStart DOMContentLoadedイベントの開始時間
	 */
	public void setDomContentLoadedEventStart(long domContentLoadedEventStart) {
		this.domContentLoadedEventStart = domContentLoadedEventStart;
	}

	/**
	 * DOMContentLoadedイベントの完了時間を取得します。
	 * 
	 * @return DOMContentLoadedイベントの完了時間
	 */
	public long getDomContentLoadedEventEnd() {
		return domContentLoadedEventEnd;
	}

	/**
	 * DOMContentLoadedイベントの完了時間を設定します。
	 * 
	 * @param domContentLoadedEventEnd DOMContentLoadedイベントの完了時間
	 */
	public void setDomContentLoadedEventEnd(long domContentLoadedEventEnd) {
		this.domContentLoadedEventEnd = domContentLoadedEventEnd;
	}

	/**
	 * レスポンスの最初のデータの受信時間を取得します。
	 * 
	 * @return レスポンスの最初のデータの受信時間
	 */
	public long getResponseStart() {
		return responseStart;
	}

	/**
	 * レスポンスの最初のデータの受信時間を設定します。
	 * 
	 * @param responseStart レスポンスの最初のデータの受信時間
	 */
	public void setResponseStart(long responseStart) {
		this.responseStart = responseStart;
	}

	/**
	 * レスポンスのデータの受信完了時間を取得します。
	 * 
	 * @return レスポンスのデータの受信完了時間
	 */
	public long getResponseEnd() {
		return responseEnd;
	}

	/**
	 * レスポンスのデータの受信完了時間を設定します。
	 * 
	 * @param responseEnd レスポンスのデータの受信完了時間
	 */
	public void setResponseEnd(long responseEnd) {
		this.responseEnd = responseEnd;
	}
}
