/*
 * Copyright (C) 2015-2016 NS Solutions Corporation
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

package com.htmlhifive.pitalium.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * {@link org.junit.runners.Parameterized}テストにおいて、各パラメーター毎に全てのメソッドが終わった時に一度だけ呼ばれるメソッドを指定します。
 * </p>
 * 以下のサンプルのように利用してください。
 * 
 * <pre>
 * &#064;RunWith(Parameterized.class)
 * &#064;Parameterized.UseParametersRunnerFactory(PtlBlockJUnit4ClassRunnerWithParametersFactory.class)
 * public class SampleTest {
 * 
 *     &#064;Parameterized.Parameters
 *     public static Collection&lt;Object[]&gt; parameters() {
 *         return Arrays.asList(new Object[] {&quot;1&quot;, 1}, new Object[] {&quot;2&quot;, 2});
 *     }
 * 
 *     &#064;ParameterizedAfterClass
 *     public static void parameterizedAfterClass(<b>String param1, int param2</b>) {
 * 
 *     }
 * 
 * }
 * </pre>
 * 
 * @see org.junit.AfterClass
 * @author nakatani
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ParameterizedAfterClass {
}
