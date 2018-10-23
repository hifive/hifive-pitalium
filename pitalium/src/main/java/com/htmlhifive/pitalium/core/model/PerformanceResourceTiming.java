package com.htmlhifive.pitalium.core.model;

/**
 * Resource Timingを保持するクラス。
 */
public class PerformanceResourceTiming {
	private String name;
	private double connectStart;
	private double connectEnd;
	private long decodedBodySize;
	private double domainLookupEnd;
	private double domainLookupStart;
	private long encodedBodySize;
	private double fetchStart;
	private String initiatorType;
	private String nextHopProtocol;
	private double redirectEnd;
	private double redirectStart;
	private double requestStart;
	private double responseEnd;
	private double responseStart;
	private double secureConnectionStart;
	private long transferSize;
	private double workerStart;

	/**
	 * 空のオブジェクトを生成します。
	 */
	public PerformanceResourceTiming() {
	}

	/**
	 * ブラウザからサーバへの接続開始時間を取得します。
	 * 
	 * @return ブラウザからサーバへの接続開始時間
	 */
	public double getConnectStart() {
		return connectStart;
	}

	/**
	 * ブラウザからサーバへの接続開始時間を設定します。
	 * 
	 * @param connectStart ブラウザからサーバへの接続開始時間
	 */
	public void setConnectStart(double connectStart) {
		this.connectStart = connectStart;
	}

	/**
	 * リソースのURLを取得します。
	 * 
	 * @return リソースのURL
	 */
	public String getName() {
		return name;
	}

	/**
	 * リソースのURLを設定します。
	 * 
	 * @param name リソースのURL
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * ブラウザからサーバへの接続確立完了時間を取得します。
	 * 
	 * @return ブラウザからサーバへの接続確立完了時間
	 */
	public double getConnectEnd() {
		return connectEnd;
	}

	/**
	 * ブラウザからサーバへの接続確立完了時間を設定します。
	 * 
	 * @param connectEnd ブラウザからサーバへの接続確立完了時間
	 */
	public void setConnectEnd(double connectEnd) {
		this.connectEnd = connectEnd;
	}

	/**
	 * レスポンスボディのバイト数を取得します。
	 * 
	 * @return レスポンスボディのバイト数
	 */
	public long getDecodedBodySize() {
		return decodedBodySize;
	}

	/**
	 * レスポンスボディのバイト数を設定します。
	 * 
	 * @param decodedBodySize レスポンスボディのバイト数
	 */
	public void setDecodedBodySize(long decodedBodySize) {
		this.decodedBodySize = decodedBodySize;
	}

	/**
	 * ブラウザによるリソースの名前解決完了時間を取得します。
	 * 
	 * @return ブラウザによるリソースの名前解決完了時間
	 */
	public double getDomainLookupEnd() {
		return domainLookupEnd;
	}

	/**
	 * ブラウザによるリソースの名前解決完了時間を設定します。
	 * 
	 * @param domainLookupEnd ブラウザによるリソースの名前解決完了時間
	 */
	public void setDomainLookupEnd(double domainLookupEnd) {
		this.domainLookupEnd = domainLookupEnd;
	}

	/**
	 * ブラウザによるリソースの名前解決開始時間を取得します。
	 * 
	 * @return ブラウザによるリソースの名前解決開始時間
	 */
	public double getDomainLookupStart() {
		return domainLookupStart;
	}

	/**
	 * ブラウザによるリソースの名前解決開始時間を設定します。
	 * 
	 * @param domainLookupStart ブラウザによるリソースの名前解決開始時間
	 */
	public void setDomainLookupStart(double domainLookupStart) {
		this.domainLookupStart = domainLookupStart;
	}

	/**
	 * レスポンスのペイロードのバイト数を取得します。
	 * 
	 * @return レスポンスのペイロードのバイト数
	 */
	public long getEncodedBodySize() {
		return encodedBodySize;
	}

	/**
	 * レスポンスのペイロードのバイト数を設定します。
	 * 
	 * @param encodedBodySize レスポンスのペイロードのバイト数
	 */
	public void setEncodedBodySize(long encodedBodySize) {
		this.encodedBodySize = encodedBodySize;
	}

	/**
	 * ブラウザによるリソース取得開始時間を取得します。
	 * 
	 * @return ブラウザによるリソース取得開始時間
	 */
	public double getFetchStart() {
		return fetchStart;
	}

	/**
	 * ブラウザによるリソース取得開始時間を設定します。
	 * 
	 * @param fetchStart ブラウザによるリソース取得開始時間
	 */
	public void setFetchStart(double fetchStart) {
		this.fetchStart = fetchStart;
	}

	/**
	 * リソース取得の契機の種別を取得します。
	 * 
	 * @return リソース取得の契機の種別
	 */
	public String getInitiatorType() {
		return initiatorType;
	}

	/**
	 * リソース取得の契機の種別を設定します。
	 * 
	 * @param initiatorType リソース取得の契機の種別
	 */
	public void setInitiatorType(String initiatorType) {
		this.initiatorType = initiatorType;
	}

	/**
	 * リソース取得に用いられたネットワークプロトコルを取得します。
	 * 
	 * @return リソース取得に用いられたネットワークプロトコル
	 */
	public String getNextHopProtocol() {
		return nextHopProtocol;
	}

	/**
	 * リソース取得に用いられたネットワークプロトコルを設定します。
	 * 
	 * @param nextHopProtocol リソース取得に用いられたネットワークプロトコル
	 */
	public void setNextHopProtocol(String nextHopProtocol) {
		this.nextHopProtocol = nextHopProtocol;
	}

	/**
	 * 最後のリダイレクトの完了時間を取得します。
	 * 
	 * @return 最後のリダイレクトの完了時間
	 */
	public double getRedirectEnd() {
		return redirectEnd;
	}

	/**
	 * 最後のリダイレクトの完了時間を設定します。
	 * 
	 * @param redirectEnd 最後のリダイレクトの完了時間
	 */
	public void setRedirectEnd(double redirectEnd) {
		this.redirectEnd = redirectEnd;
	}

	/**
	 * リダイレクトの開始時間を取得します。
	 * 
	 * @return リダイレクトの開始時間
	 */
	public double getRedirectStart() {
		return redirectStart;
	}

	/**
	 * リダイレクトの開始時間を取得します。
	 * 
	 * @param redirectStart リダイレクトの開始時間
	 */
	public void setRedirectStart(double redirectStart) {
		this.redirectStart = redirectStart;
	}

	/**
	 * ブラウザからサーバへのリクエスト開始時間を取得します。
	 * 
	 * @return ブラウザからサーバへのリクエスト開始時間
	 */
	public double getRequestStart() {
		return requestStart;
	}

	/**
	 * ブラウザからサーバへのリクエスト開始時間を設定します。
	 * 
	 * @param requestStart ブラウザからサーバへのリクエスト開始時間
	 */
	public void setRequestStart(double requestStart) {
		this.requestStart = requestStart;
	}

	/**
	 * リソースのデータ受信完了時間を取得します。
	 * 
	 * @return リソースのデータ受信完了時間
	 */
	public double getResponseEnd() {
		return responseEnd;
	}

	/**
	 * リソースのデータ受信完了時間を設定します。
	 * 
	 * @param responseEnd リソースのデータ受信完了時間
	 */
	public void setResponseEnd(double responseEnd) {
		this.responseEnd = responseEnd;
	}

	/**
	 * リソースのデータ受信開始時間を取得します。
	 * 
	 * @return リソースのデータ受信開始時間
	 */
	public double getResponseStart() {
		return responseStart;
	}

	/**
	 * リソースのデータ受信開始時間を設定します。
	 * 
	 * @param responseStart リソースのデータ受信開始時間
	 */
	public void setResponseStart(double responseStart) {
		this.responseStart = responseStart;
	}

	/**
	 * セキュアな接続のハンドシェイク開始時間を取得します。
	 * 
	 * @return セキュアな接続のハンドシェイク開始時間
	 */
	public double getSecureConnectionStart() {
		return secureConnectionStart;
	}

	/**
	 * セキュアな接続のハンドシェイク開始時間を設定します。
	 * 
	 * @param secureConnectionStart セキュアな接続のハンドシェイク開始時間
	 */
	public void setSecureConnectionStart(double secureConnectionStart) {
		this.secureConnectionStart = secureConnectionStart;
	}

	/**
	 * リソースの転送にかかった受信バイト数を取得します。
	 * 
	 * @return リソースの転送にかかった受信バイト数
	 */
	public long getTransferSize() {
		return transferSize;
	}

	/**
	 * リソースの転送にかかった受信バイト数を設定します。
	 * 
	 * @param transferSize リソースの転送にかかった受信バイト数
	 */
	public void setTransferSize(long transferSize) {
		this.transferSize = transferSize;
	}

	/**
	 * Service Workerがリソース要求への割り込みを開始した時間を取得します。
	 * <p>
	 * Service Workerが稼働していなければfetchStartの直後です。 Service Workerがリソース要求への割り込みを行わなかった場合は0です。
	 * </p>
	 * 
	 * @return Service Workerがリソース要求への割り込みを開始した時間
	 */
	public double getWorkerStart() {
		return workerStart;
	}

	/**
	 * Service Workerがリソース要求への割り込みを開始した時間を設定します。
	 * 
	 * @param workerStart Service Workerがリソース要求への割り込みを開始した時間
	 */
	public void setWorkerStart(double workerStart) {
		this.workerStart = workerStart;
	}
}
