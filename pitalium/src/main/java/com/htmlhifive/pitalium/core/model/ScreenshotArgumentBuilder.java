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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * TODO JavaDoc
 *
 * @author nakatani
 */
public class ScreenshotArgumentBuilder {

	static class TargetParamHolder {
		final ScreenArea target;
		final List<ScreenArea> excludes = new ArrayList<ScreenArea>();
		boolean moveTarget = true;

		public TargetParamHolder(ScreenArea target) {
			this.target = target;
		}
	}

	private String screenshotId;
	private final List<TargetParamHolder> targets = new ArrayList<TargetParamHolder>();
	private final List<DomSelector> hiddenElementSelectors = new ArrayList<DomSelector>();

	private TargetParamHolder currentHolder;

	//<editor-fold desc="Constructor">

	protected ScreenshotArgumentBuilder() {
	}

	protected ScreenshotArgumentBuilder(String screenshotId) {
		this.screenshotId = screenshotId;
	}

	//</editor-fold>

	private TargetParamHolder getCurrentHolder() {
		if (currentHolder == null) {
			throw new IllegalStateException("addNewTarget is not called");
		}
		return currentHolder;
	}

	private void setCurrentHolder(ScreenArea target) {
		targets.add(currentHolder = new TargetParamHolder(target));
	}

	public ScreenshotArgument build() {
		// Validate screenshot id
		if (Strings.isNullOrEmpty(screenshotId)) {
			throw new IllegalStateException("screenshotId must not be empty");
		}

		List<CompareTarget> compareTargets = Lists.transform(targets, new Function<TargetParamHolder, CompareTarget>() {
			@Override
			public CompareTarget apply(TargetParamHolder holder) {
				return new CompareTarget(holder.target,
						holder.excludes.toArray(new ScreenArea[holder.excludes.size()]), holder.moveTarget);
			}
		});

		return new ScreenshotArgument(screenshotId, new ArrayList<CompareTarget>(compareTargets),
				new ArrayList<DomSelector>(hiddenElementSelectors));
	}

	public ScreenshotArgumentBuilder screenshotId(String screenshotId) {
		this.screenshotId = screenshotId;
		return this;
	}

	//<editor-fold desc="AddNewTarget">

	public ScreenshotArgumentBuilder addNewTarget() {
		return addNewTarget(new CompareTarget());
	}

	public ScreenshotArgumentBuilder addNewTarget(CompareTarget target) {
		return addNewTarget(target.getCompareArea()).addExcludes(target.getExcludes())
				.moveTarget(target.isMoveTarget());
	}

	public ScreenshotArgumentBuilder addNewTarget(ScreenArea target) {
		setCurrentHolder(target);
		return this;
	}

	public ScreenshotArgumentBuilder addNewTarget(SelectorType type, String value) {
		return addNewTarget(ScreenArea.of(type, value));
	}

	public ScreenshotArgumentBuilder addNewTargetById(String value) {
		return addNewTarget(SelectorType.ID, value);
	}

	public ScreenshotArgumentBuilder addNewTargetByClassName(String value) {
		return addNewTarget(SelectorType.CLASS_NAME, value);
	}

	public ScreenshotArgumentBuilder addNewTargetByCssSelector(String value) {
		return addNewTarget(SelectorType.CSS_SELECTOR, value);
	}

	public ScreenshotArgumentBuilder addNewTargetByLinkText(String value) {
		return addNewTarget(SelectorType.LINK_TEXT, value);
	}

	public ScreenshotArgumentBuilder addNewTargetByName(String value) {
		return addNewTarget(SelectorType.NAME, value);
	}

	public ScreenshotArgumentBuilder addNewTargetByPartialLinkText(String value) {
		return addNewTarget(SelectorType.PARTIAL_LINK, value);
	}

	public ScreenshotArgumentBuilder addNewTargetByTagName(String value) {
		return addNewTarget(SelectorType.TAG_NAME, value);
	}

	public ScreenshotArgumentBuilder addNewTargetByXPath(String value) {
		return addNewTarget(SelectorType.XPATH, value);
	}

	public ScreenshotArgumentBuilder addNewTarget(double x, double y, double width, double height) {
		return addNewTarget(ScreenArea.of(x, y, width, height));
	}

	//</editor-fold>

	//<editor-fold desc="AddExclude">

	public ScreenshotArgumentBuilder addExclude(SelectorType type, String value) {
		return addExclude(ScreenArea.of(type, value));
	}

	public ScreenshotArgumentBuilder addExcludeById(String value) {
		return addExclude(SelectorType.ID, value);
	}

	public ScreenshotArgumentBuilder addExcludeByClassName(String value) {
		return addExclude(SelectorType.CLASS_NAME, value);
	}

	public ScreenshotArgumentBuilder addExcludeByCssSelector(String value) {
		return addExclude(SelectorType.CSS_SELECTOR, value);
	}

	public ScreenshotArgumentBuilder addExcludeByLinkText(String value) {
		return addExclude(SelectorType.LINK_TEXT, value);
	}

	public ScreenshotArgumentBuilder addExcludeByName(String value) {
		return addExclude(SelectorType.NAME, value);
	}

	public ScreenshotArgumentBuilder addExcludeByPartialLinkText(String value) {
		return addExclude(SelectorType.PARTIAL_LINK, value);
	}

	public ScreenshotArgumentBuilder addExcludeByTagName(String value) {
		return addExclude(SelectorType.TAG_NAME, value);
	}

	public ScreenshotArgumentBuilder addExcludeByXPath(String value) {
		return addExclude(SelectorType.XPATH, value);
	}

	public ScreenshotArgumentBuilder addExclude(ScreenArea exclude) {
		TargetParamHolder holder = getCurrentHolder();
		holder.excludes.add(exclude);
		return this;
	}

	public ScreenshotArgumentBuilder addExclude(double x, double y, double width, double height) {
		return addExclude(ScreenArea.of(x, y, width, height));
	}

	public ScreenshotArgumentBuilder addExcludes(Collection<ScreenArea> excludes) {
		TargetParamHolder holder = getCurrentHolder();
		holder.excludes.addAll(excludes);
		return this;
	}

	public ScreenshotArgumentBuilder addExcludes(ScreenArea... excludes) {
		TargetParamHolder holder = getCurrentHolder();
		Collections.addAll(holder.excludes, excludes);
		return this;
	}

	//</editor-fold>

	public ScreenshotArgumentBuilder moveTarget(boolean moveTarget) {
		getCurrentHolder().moveTarget = moveTarget;
		return this;
	}

	//<editor-fold desc="HiddenElementSelector">

	public ScreenshotArgumentBuilder addHiddenElementSelector(SelectorType type, String value) {
		hiddenElementSelectors.add(new DomSelector(type, value));
		return this;
	}

	public ScreenshotArgumentBuilder addHiddenElementsById(String value) {
		return addHiddenElementSelector(SelectorType.ID, value);
	}

	public ScreenshotArgumentBuilder addHiddenElementsByClassName(String value) {
		return addHiddenElementSelector(SelectorType.CLASS_NAME, value);
	}

	public ScreenshotArgumentBuilder addHiddenElementsByCssSelector(String value) {
		return addHiddenElementSelector(SelectorType.CSS_SELECTOR, value);
	}

	public ScreenshotArgumentBuilder addHiddenElementsByLinkText(String value) {
		return addHiddenElementSelector(SelectorType.LINK_TEXT, value);
	}

	public ScreenshotArgumentBuilder addHiddenElementsByName(String value) {
		return addHiddenElementSelector(SelectorType.NAME, value);
	}

	public ScreenshotArgumentBuilder addHiddenElementsByPartialLinkText(String value) {
		return addHiddenElementSelector(SelectorType.PARTIAL_LINK, value);
	}

	public ScreenshotArgumentBuilder addHiddenElementsByTagName(String value) {
		return addHiddenElementSelector(SelectorType.TAG_NAME, value);
	}

	public ScreenshotArgumentBuilder addHiddenElementsByXPath(String value) {
		return addHiddenElementSelector(SelectorType.XPATH, value);
	}

	public ScreenshotArgumentBuilder addHiddenElementSelectors(Collection<DomSelector> selectors) {
		hiddenElementSelectors.addAll(selectors);
		return this;
	}

	public ScreenshotArgumentBuilder addHiddenElementSelectors(DomSelector... selectors) {
		Collections.addAll(hiddenElementSelectors, selectors);
		return this;
	}

	//</editor-fold>

}
