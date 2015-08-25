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

package com.htmlhifive.pitalium.core.http.handler;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.http.PtlHttpServerUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class TakeScreenshotHandlerTest extends PtlTestBase {

    @Rule
    public ExpectedException expected = ExpectedException.none();

    boolean takeScreenshotCalled;

    @Test
    public void testRequestTakeScreenshot() throws Exception {
        takeScreenshotCalled = false;

        driver.get(null);
        PtlHttpServerUtils.loadPitaliumFunctions(driver);
        PtlHttpServerUtils.requestTakeScreenshot(driver, 30000L, new PtlHttpServerUtils.TakeScreenshotAction(driver) {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
e.printStackTrace();
                }

                requestTakeScreenshot();
            }
        }, new Runnable() {
            @Override
            public void run() {
                driver.takeScreenshot("testRequestTakeScreenshot");
                takeScreenshotCalled = true;
            }
        });

        assertThat(takeScreenshotCalled, is(true));
    }

}