package com.htmlhifive.pitalium.core.rules;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Capabilities;

import com.htmlhifive.pitalium.core.annotation.CapabilityFilter;
import com.htmlhifive.pitalium.core.annotation.CapabilityFilters;

/**
 * {@link CapabilityFilter}および{@link CapabilityFilters}でテスト実行を制限します。
 */
public class AssumeCapability extends TestWatcher {

	private Description description;

	public void setDescription(Description description) {
		this.description = description;
	}

	public void assumeCapability(Capabilities capabilities) {
		// TODO
	}

}
