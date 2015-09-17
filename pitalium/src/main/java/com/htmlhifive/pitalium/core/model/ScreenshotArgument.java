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

import java.util.Collections;
import java.util.List;

import com.google.common.base.Strings;

/**
 * TODO JavaDoc
 * 
 * @author nakatani
 */
public class ScreenshotArgument {

	private static <T> List<T> toUnmodifiableList(List<T> list) {
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(list);
		}
	}

	public static ScreenshotArgumentBuilder builder() {
		return new ScreenshotArgumentBuilder();
	}

	public static ScreenshotArgumentBuilder builder(String screenshotId) {
		return new ScreenshotArgumentBuilder(screenshotId);
	}

	private final String screenshotId;

	private final List<CompareTarget> targets;

	private final List<DomSelector> hiddenElementSelectors;

	protected ScreenshotArgument(String screenshotId, List<CompareTarget> targets,
			List<DomSelector> hiddenElementSelectors) {
		if (Strings.isNullOrEmpty(screenshotId)) {
			throw new NullPointerException("screenshotId");
		}

		this.screenshotId = screenshotId;
		this.targets = toUnmodifiableList(targets);
		this.hiddenElementSelectors = toUnmodifiableList(hiddenElementSelectors);
	}

	public String getScreenshotId() {
		return screenshotId;
	}

	public List<CompareTarget> getTargets() {
		return targets;
	}

	public List<DomSelector> getHiddenElementSelectors() {
		return hiddenElementSelectors;
	}

	public ScreenshotArgument withScreenshotId(String screenshotId) {
		return new ScreenshotArgument(screenshotId, targets, hiddenElementSelectors);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ScreenshotArgument that = (ScreenshotArgument) o;

		if (!screenshotId.equals(that.screenshotId)) {
			return false;
		}
		if (!targets.equals(that.targets)) {
			return false;
		}
		return hiddenElementSelectors.equals(that.hiddenElementSelectors);

	}

	@Override
	public int hashCode() {
		int result = screenshotId.hashCode();
		result = 31 * result + targets.hashCode();
		result = 31 * result + hiddenElementSelectors.hashCode();
		return result;
	}
}
