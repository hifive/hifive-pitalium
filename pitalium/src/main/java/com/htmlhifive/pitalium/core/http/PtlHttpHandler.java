/*
 * Copyright (C) 2015 NS Solutions Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htmlhifive.pitalium.core.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * {@link PtlHttpServer}が受けたリクエストを処理する{@link com.sun.net.httpserver.HttpHandler}に設定するアノテーション。{@link #value()}
 * にはリクエストを待機するパスを&quot;/&quot;から記述します。
 * </p>
 *
 * 記述例：
 * <pre>
 * <b>&#064;PtlHttpHandler(&quot;/hoge/fuga&quot;)</b>
 * public class SampleHttpHandler implements HttpHandler {
 *     ....
 * }
 * </pre>
 * 
 * @author nakatani
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PtlHttpHandler {

	/**
	 * リクエストを待機するパス。&quot;/&quot;から記述する必要があります。
	 *
	 * @return リクエストを待機するパス
	 */
	String value();

}
