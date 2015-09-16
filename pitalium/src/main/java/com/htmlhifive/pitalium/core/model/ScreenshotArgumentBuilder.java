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

package com.htmlhifive.pitalium.core.model;

import java.util.Collection;

/**
 * TODO JavaDoc
 *
 * @author nakatani
 */
public class ScreenshotArgumentBuilder {

	//<editor-fold desc="Constructor">

	protected ScreenshotArgumentBuilder() {
		// TODO
	}

	protected ScreenshotArgumentBuilder(String screenshotId) {
		// TODO
	}

	//</editor-fold>

	public ScreenshotArgument build() {
		throw new UnsupportedOperationException("TODO");
	}

	public ScreenshotArgumentBuilder screenshotId(String screenshotId) {
		// TODO
		return this;
	}

	//<editor-fold desc="AddNewTarget">

	public ScreenshotArgumentBuilder addNewTarget() {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addNewTarget(CompareTarget target) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addNewTarget(ScreenArea area) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addNewTarget(SelectorType type, String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addNewTargetById(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addNewTargetByClassName(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addNewTargetByCssSelector(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addNewTargetByLinkText(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addNewTargetByName(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addNewTargetByPartialLinkText(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addNewTargetByTagName(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addNewTargetByXPath(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addNewTarget(double x, double y, double width, double height) {
		// TODO
		return this;
	}

	//</editor-fold>

	//<editor-fold desc="AddExclude">

	public ScreenshotArgumentBuilder addExclude(SelectorType type, String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addExcludeById(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addExcludeByClassName(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addExcludeByCssSelector(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addExcludeByLinkText(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addExcludeByName(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addExcludeByPartialLinkText(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addExcludeByTagName(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addExcludeByXPath(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addExclude(double x, double y, double width, double height) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addExcludes(Collection<ScreenArea> excludes) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addExcludes(ScreenArea... excludes) {
		// TODO
		return this;
	}

	//</editor-fold>

	public ScreenshotArgumentBuilder moveTarget(boolean moveTarget) {
		// TODO
		return this;
	}

	//<editor-fold desc="HiddenElementSelector">

	public ScreenshotArgumentBuilder addHiddenElementSelector(SelectorType type, String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addHiddenElementsById(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addHiddenElementsByClassName(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addHiddenElementsByCssSelector(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addHiddenElementsByLinkText(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addHiddenElementsByName(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addHiddenElementsByPartialLinkText(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addHiddenElementsByTagName(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addHiddenElementsByXPath(String value) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addHiddenElementSelectors(Collection<DomSelector> selectors) {
		// TODO
		return this;
	}

	public ScreenshotArgumentBuilder addHiddenElementSelectors(DomSelector... selectors) {
		// TODO
		return this;
	}

	//</editor-fold>

}
