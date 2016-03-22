package com.htmlhifive.pitalium.core.rules;

import com.htmlhifive.pitalium.core.annotation.CapabilityFilter;
import com.htmlhifive.pitalium.core.annotation.CapabilityFilters;
import org.apache.commons.lang3.StringUtils;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link AssumeCapability}を適用した時のテスト
 */
@SuppressWarnings("all")
@RunWith(Enclosed.class)
public class AssumeCapabilityTest {

//<editor-fold desc="TestBase">

	@RunWith(Parameterized.class)
	public static abstract class TestCaseBase {

		static Iterable<Object[]> parameters(Capabilities... capabilities) {
			List<Object[]> list = new ArrayList<>();
			for (int i = 0; i < capabilities.length; i++) {
				list.add(new Object[] {i, capabilities[i]});
			}
			return list;
		}

		static Capabilities capabilities(String browserName, String version, Platform platform) {
			return capabilities(browserName, version, platform, null, null);
		}

		static Capabilities capabilities(String browserName, String version, Platform platform, String deviceName, String filterGroup) {
			Map<String, Object> map = new HashMap<>();
			if (StringUtils.isNotEmpty(browserName))
				map.put(CapabilityType.BROWSER_NAME, browserName);
			if (StringUtils.isNotEmpty(version))
				map.put(CapabilityType.VERSION, version);
			if (platform != null)
				map.put(CapabilityType.PLATFORM, platform);
			if (StringUtils.isNotEmpty(deviceName))
				map.put("deviceName", deviceName);
			if (StringUtils.isNotEmpty(filterGroup))
				map.put("filterGroup", filterGroup);
			return new DesiredCapabilities(map);
		}

		@Parameterized.Parameter(0)
		public Integer number;

		@Parameterized.Parameter(1)
		public Capabilities capabilities;

		@Rule
		public AssumeCapability assumeCapability = new AssumeCapability();

		boolean assumed = false;

		@Before
		public void before() {
			try {
				assumeCapability.assumeCapability(capabilities);
			} catch (AssumptionViolatedException e) {
				assumed = true;
			}
		}

		public void assertAssumed(int... numbers) {
			for (int number : numbers) {
				if (number == this.number) {
					if (!assumed) {
						throw new AssertionError("Assumed was expected.");
					}
					return;
				}
			}

			if (assumed) {
				throw new AssertionError("Not assumed was expected.");
			}
		}

		public void assertAllAssumed() {
			if (!assumed) {
				throw new AssertionError("Assumed was expected.");
			}
		}

		public void assertNotAssumed() {
			if (assumed) {
				throw new AssertionError("Not assumed was expected.");
			}
		}

	}

//</editor-fold>

//<editor-fold desc="NotAnnotatedTestCase">

	/**
	 * アノテーション未指定。全て実行される。
	 */
	public static class NotAnnotatedTestCase extends TestCaseBase {

		@Parameterized.Parameters(name = "{0}")
		public static Iterable<Object[]> parameters() {
			return parameters(
					capabilities(BrowserType.IE, null, null),
					capabilities(BrowserType.FIREFOX, null, null),
					capabilities(BrowserType.CHROME, null, null)
			);
		}

		@Test
		public void notAnnotated() throws Exception {
			assertNotAssumed();
		}

	}

//</editor-fold>

//<editor-fold desc="SingleParamTestCase">

	public static class VersionTestCase extends TestCaseBase {

		@Parameterized.Parameters(name = "{0}")
		public static Iterable<Object[]> parameters() {
			return parameters(
					capabilities(BrowserType.IE, null, null),
					capabilities(BrowserType.FIREFOX, "45.0.1", null),
					capabilities(BrowserType.CHROME, "45.0", null)
			);
		}

		@CapabilityFilter
		@Test
		public void nonParam() throws Exception {
			assertNotAssumed();
		}

		@CapabilityFilter(version = {})
		@Test
		public void emptyParam() throws Exception {
			assertNotAssumed();
		}

		@CapabilityFilter(version = {""})
		@Test
		public void emptyStringParam() throws Exception {
			assertAssumed(1, 2);
		}

		@CapabilityFilter(version = "46.0")
		@Test
		public void nonMatchParam() throws Exception {
			assertAllAssumed();
		}

		@CapabilityFilter(version = "45.0")
		@Test
		public void singleParam() throws Exception {
			assertAssumed(0, 1);
		}

		@CapabilityFilter(version = {"45.0", "49.0"})
		@Test
		public void multipleParam() throws Exception {
			assertAssumed(0, 1);
		}

		@CapabilityFilter(version = "45\\..*")
		@Test
		public void regexParam() throws Exception {
			assertAssumed(0);
		}

	}

	public static class BrowserNameTestCase extends TestCaseBase {

		@Parameterized.Parameters(name = "{0}")
		public static Iterable<Object[]> parameters() {
			return parameters(
					capabilities(BrowserType.IE, null, null),
					capabilities(BrowserType.FIREFOX, "45.0.1", null),
					capabilities(BrowserType.CHROME, "45.0", null)
			);
		}

		@CapabilityFilter
		@Test
		public void nonParam() throws Exception {
			assertNotAssumed();
		}

		@CapabilityFilter(browserName = {})
		@Test
		public void emptyParam() throws Exception {
			assertNotAssumed();
		}

		@CapabilityFilter(browserName = {""})
		@Test
		public void emptyStringParam() throws Exception {
			assertAllAssumed();
		}

		@CapabilityFilter(browserName = BrowserType.EDGE)
		@Test
		public void nonMatchParam() throws Exception {
			assertAllAssumed();
		}

		@CapabilityFilter(browserName = BrowserType.IE)
		@Test
		public void singleParam() throws Exception {
			assertAssumed(1, 2);
		}

		@CapabilityFilter(browserName = {BrowserType.IE, BrowserType.CHROME})
		@Test
		public void multipleParam() throws Exception {
			assertAssumed(1);
		}

		@CapabilityFilter(browserName = ".*")
		@Test
		public void regexParam() throws Exception {
			assertAllAssumed();
		}

	}

	public static class PlatformTestCase extends TestCaseBase {

		@Parameterized.Parameters(name = "{0}")
		public static Iterable<Object[]> parameters() {
			return parameters(
					capabilities(BrowserType.IE, null, null),
					capabilities(BrowserType.EDGE, null, Platform.WIN10),
					capabilities(BrowserType.FIREFOX, null, Platform.WINDOWS),
					capabilities(BrowserType.SAFARI, null, Platform.MAC),
					capabilities(BrowserType.CHROME, null, Platform.ANDROID)
			);
		}

		@CapabilityFilter
		@Test
		public void nonParam() throws Exception {
			assertNotAssumed();
		}

		@CapabilityFilter(platform = {})
		@Test
		public void emptyParam() throws Exception {
			assertNotAssumed();
		}

		@CapabilityFilter(platform = Platform.ANY)
		@Test
		public void anyParam() throws Exception {
			assertNotAssumed();
		}

		@CapabilityFilter(platform = Platform.VISTA)
		@Test
		public void nonMatchParam() throws Exception {
			assertAllAssumed();
		}

		@CapabilityFilter(platform = Platform.WIN10)
		@Test
		public void singleParam() throws Exception {
			assertAssumed(0, 2, 3, 4);
		}

		@CapabilityFilter(platform = {Platform.WIN10, Platform.ANDROID})
		@Test
		public void multipleParam() throws Exception {
			assertAssumed(0, 2, 3);
		}

		@CapabilityFilter(platform = Platform.WINDOWS)
		@Test
		public void familyParamWindows() throws Exception {
			assertAssumed(0, 3, 4);
		}

		@CapabilityFilter(platform = Platform.UNIX)
		@Test
		public void familyParamUnix() throws Exception {
			assertAssumed(0, 1, 2, 3);
		}

	}

	public static class DeviceNameNameTestCase extends TestCaseBase {

		@Parameterized.Parameters(name = "{0}")
		public static Iterable<Object[]> parameters() {
			return parameters(
					capabilities(BrowserType.IE, null, null, null, null),
					capabilities(BrowserType.CHROME, null, null, "Nexus 6P", null),
					capabilities(BrowserType.CHROME, null, null, "Nexus 5X", null),
					capabilities(BrowserType.CHROME, null, null, "Xperia Z5", null)
			);
		}

		@CapabilityFilter
		@Test
		public void nonParam() throws Exception {
			assertNotAssumed();
		}

		@CapabilityFilter(deviceName = {})
		@Test
		public void emptyParam() throws Exception {
			assertNotAssumed();
		}

		@CapabilityFilter(deviceName = {""})
		@Test
		public void emptyStringParam() throws Exception {
			assertAssumed(1, 2, 3);
		}

		@CapabilityFilter(deviceName = "Galaxy S7")
		@Test
		public void nonMatchParam() throws Exception {
			assertAllAssumed();
		}

		@CapabilityFilter(deviceName = "Nexus 6P")
		@Test
		public void singleParam() throws Exception {
			assertAssumed(0, 2, 3);
		}

		@CapabilityFilter(deviceName = {"Nexus 6P", "Xperia Z5"})
		@Test
		public void multipleParam() throws Exception {
			assertAssumed(0, 2);
		}

		@CapabilityFilter(deviceName = "Nexus.+")
		@Test
		public void regexParam() throws Exception {
			assertAssumed(0, 3);
		}

	}

	public static class FilterGroupTestCase extends TestCaseBase {

		@Parameterized.Parameters(name = "{0}")
		public static Iterable<Object[]> parameters() {
			return parameters(
					capabilities(BrowserType.IE, null, null, null, null),
					capabilities(BrowserType.FIREFOX, null, null, null, "group1"),
					capabilities(BrowserType.CHROME, null, null, null, "group2")
			);
		}

		@CapabilityFilter
		@Test
		public void nonParam() throws Exception {
			assertNotAssumed();
		}

		@CapabilityFilter(filterGroup = {})
		@Test
		public void emptyParam() throws Exception {
			assertNotAssumed();
		}

		@CapabilityFilter(filterGroup = {""})
		@Test
		public void emptyStringParam() throws Exception {
			assertAssumed(1, 2);
		}

		@CapabilityFilter(filterGroup = "group")
		@Test
		public void nonMatchParam() throws Exception {
			assertAllAssumed();
		}

		@CapabilityFilter(filterGroup = "group1")
		@Test
		public void singleParam() throws Exception {
			assertAssumed(0, 2);
		}

		@CapabilityFilter(filterGroup = {"group1", "group2"})
		@Test
		public void multipleParam() throws Exception {
			assertAssumed(0);
		}

		@CapabilityFilter(filterGroup = ".*")
		@Test
		public void regexParam() throws Exception {
			assertAllAssumed();
		}

	}

//</editor-fold>

//<editor-fold desc="MultipleParamTestCase">

	public static class MultipleParamTestCase extends TestCaseBase {

		@Parameterized.Parameters(name = "{0}")
		public static Iterable<Object[]> parameters() {
			return parameters(
					capabilities(BrowserType.IE, "11.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.IE, "10.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.EDGE, null, Platform.WIN10, null, "pc"),
					capabilities(BrowserType.FIREFOX, "45.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.CHROME, "49.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.SAFARI, "9.1", Platform.EL_CAPITAN, null, "pc"),
					capabilities(BrowserType.CHROME, "49.0", Platform.ANDROID, "Nexus 6P", "mobile"),
					capabilities(BrowserType.SAFARI, "9.1", null, "iPhone6S", "mobile"),
					capabilities(BrowserType.SAFARI, "9.1", null, "iPad Air", "mobile")
			);
		}

		@CapabilityFilter(filterGroup = "pc", browserName = BrowserType.IE)
		@Test
		public void filterPC_browserIE() throws Exception {
			assertAssumed(2, 3, 4, 5, 6, 7, 8);
		}

		@CapabilityFilter(platform = Platform.WINDOWS, browserName = {BrowserType.FIREFOX, BrowserType.CHROME})
		@Test
		public void platformWindows_browserFirefoxChrome() throws Exception {
			assertAssumed(0, 1, 2, 5, 6, 7, 8);
		}

	}

//</editor-fold>

//<editor-fold desc="MultipleAnnotatedTestCase">

	public static class MultipleAnnotatedTestCase extends TestCaseBase {

		@Parameterized.Parameters(name = "{0}")
		public static Iterable<Object[]> parameters() {
			return parameters(
					capabilities(BrowserType.IE, "11.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.IE, "10.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.EDGE, null, Platform.WIN10, null, "pc"),
					capabilities(BrowserType.FIREFOX, "45.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.CHROME, "49.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.SAFARI, "9.1", Platform.EL_CAPITAN, null, "pc"),
					capabilities(BrowserType.CHROME, "49.0", Platform.ANDROID, "Nexus 6P", "mobile"),
					capabilities(BrowserType.SAFARI, "9.1", null, "iPhone6S", "mobile"),
					capabilities(BrowserType.SAFARI, "9.1", null, "iPad Air", "mobile")
			);
		}

		@CapabilityFilters({})
		@Test
		public void emptyFilter() throws Exception {
			assertNotAssumed();
		}

		@CapabilityFilters(@CapabilityFilter(filterGroup = "pc", browserName = BrowserType.IE))
		@Test
		public void singleFilter() throws Exception {
			assertAssumed(2, 3, 4, 5, 6, 7, 8);
		}

		@CapabilityFilters({
				@CapabilityFilter(filterGroup = "pc", browserName = BrowserType.IE),
				@CapabilityFilter(platform = Platform.WINDOWS, browserName = {BrowserType.FIREFOX, BrowserType.CHROME})
		})
		@Test
		public void multipleFilters() throws Exception {
			assertAssumed(2, 5, 6, 7, 8);
		}

		@CapabilityFilters({
				@CapabilityFilter(filterGroup = "pc", browserName = BrowserType.IE),
				@CapabilityFilter(filterGroup = "pc", browserName = BrowserType.IE)
		})
		@Test
		public void duplicatedFilters() throws Exception {
			assertAssumed(2, 3, 4, 5, 6, 7, 8);
		}

	}

//</editor-fold>

//<editor-fold desc="ClassAnnotatedTestCase">

	@CapabilityFilter(filterGroup = "mobile")
	public static class ClassSingleAnnotatedTestCase extends TestCaseBase {

		@Parameterized.Parameters(name = "{0}")
		public static Iterable<Object[]> parameters() {
			return parameters(
					capabilities(BrowserType.IE, "11.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.IE, "10.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.EDGE, null, Platform.WIN10, null, "pc"),
					capabilities(BrowserType.FIREFOX, "45.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.CHROME, "49.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.SAFARI, "9.1", Platform.EL_CAPITAN, null, "pc"),
					capabilities(BrowserType.CHROME, "49.0", Platform.ANDROID, "Nexus 6P", "mobile"),
					capabilities(BrowserType.SAFARI, "9.1", null, "iPhone6S", "mobile"),
					capabilities(BrowserType.SAFARI, "9.1", null, "iPad Air", "mobile")
			);
		}

		@Test
		public void notAnnotated() throws Exception {
			assertAssumed(0, 1, 2, 3, 4, 5);
		}

		@CapabilityFilter(filterGroup = "pc")
		@Test
		public void conflict() throws Exception {
			assertAllAssumed();
		}

		@CapabilityFilter(browserName = BrowserType.CHROME)
		@Test
		public void browserChrome() throws Exception {
			assertAssumed(0, 1, 2, 3, 4, 5, 7, 8);
		}

	}

	@CapabilityFilters({
			@CapabilityFilter(filterGroup = "pc", browserName = BrowserType.IE),
			@CapabilityFilter(filterGroup = "mobile")
	})
	public static class ClassMultipleAnnotatedTestCase extends TestCaseBase {

		@Parameterized.Parameters(name = "{0}")
		public static Iterable<Object[]> parameters() {
			return parameters(
					capabilities(BrowserType.IE, "11.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.IE, "10.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.EDGE, null, Platform.WIN10, null, "pc"),
					capabilities(BrowserType.FIREFOX, "45.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.CHROME, "49.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.SAFARI, "9.1", Platform.EL_CAPITAN, null, "pc"),
					capabilities(BrowserType.CHROME, "49.0", Platform.ANDROID, "Nexus 6P", "mobile"),
					capabilities(BrowserType.SAFARI, "9.1", null, "iPhone6S", "mobile"),
					capabilities(BrowserType.SAFARI, "9.1", null, "iPad Air", "mobile")
			);
		}

		@Test
		public void notAnnotated() throws Exception {
			assertAssumed(2, 3, 4, 5);
		}

		@CapabilityFilter(browserName = BrowserType.CHROME)
		@Test
		public void browserChrome() throws Exception {
			assertAssumed(0, 1, 2, 3, 4, 5, 7, 8);
		}

	}


//</editor-fold>

//<editor-fold desc="CustomAnnotatedTestCase">

	public static class CustomAnnotatedTestCase extends TestCaseBase {

		@Parameterized.Parameters(name = "{0}")
		public static Iterable<Object[]> parameters() {
			return parameters(
					capabilities(BrowserType.IE, "11.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.IE, "10.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.EDGE, null, Platform.WIN10, null, "pc"),
					capabilities(BrowserType.FIREFOX, "45.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.CHROME, "49.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.SAFARI, "9.1", Platform.EL_CAPITAN, null, "pc"),
					capabilities(BrowserType.CHROME, "49.0", Platform.ANDROID, "Nexus 6P", "mobile"),
					capabilities(BrowserType.SAFARI, "9.1", null, "iPhone6S", "mobile"),
					capabilities(BrowserType.SAFARI, "9.1", null, "iPad Air", "mobile")
			);
		}

		@InternetExplorer
		@Test
		public void singleAnnotated() throws Exception {
			assertAssumed(2, 3, 4, 5, 6, 7, 8);
		}

		@ChromeAndMacSafari
		@Test
		public void singleAnnotatedMultipleFilters() throws Exception {
			assertAssumed(0, 1, 2, 3, 7, 8);
		}

		@InternetExplorer
		@Mobile
		@Test
		public void multipleAnnotated() throws Exception {
			assertAssumed(2, 3, 4, 5);
		}

	}

	@PC
	public static class ClassCustomAnnotatedTestCase extends TestCaseBase {

		@Parameterized.Parameters(name = "{0}")
		public static Iterable<Object[]> parameters() {
			return parameters(
					capabilities(BrowserType.IE, "11.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.IE, "10.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.EDGE, null, Platform.WIN10, null, "pc"),
					capabilities(BrowserType.FIREFOX, "45.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.CHROME, "49.0", Platform.VISTA, null, "pc"),
					capabilities(BrowserType.SAFARI, "9.1", Platform.EL_CAPITAN, null, "pc"),
					capabilities(BrowserType.CHROME, "49.0", Platform.ANDROID, "Nexus 6P", "mobile"),
					capabilities(BrowserType.SAFARI, "9.1", null, "iPhone6S", "mobile"),
					capabilities(BrowserType.SAFARI, "9.1", null, "iPad Air", "mobile")
			);
		}

		@InternetExplorer
		@Test
		public void singleAnnotated() throws Exception {
			assertAssumed(2, 3, 4, 5, 6, 7, 8);
		}

		@ChromeAndMacSafari
		@Test
		public void singleAnnotatedMultipleFilters() throws Exception {
			assertAssumed(0, 1, 2, 3, 6, 7, 8);
		}

		@InternetExplorer
		@Mobile
		@Test
		public void multipleAnnotated() throws Exception {
			assertAssumed(2, 3, 4, 5, 6, 7, 8);
		}

	}

//</editor-fold>

//<editor-fold desc="Annotation">

	@CapabilityFilter(browserName = BrowserType.IE)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD})
	public @interface InternetExplorer {
	}

	@CapabilityFilter(filterGroup = "pc")
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD})
	public @interface PC {
	}

	@CapabilityFilter(filterGroup = "mobile")
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD})
	public @interface Mobile {
	}

	@CapabilityFilters({
			@CapabilityFilter(browserName = BrowserType.CHROME),
			@CapabilityFilter(browserName = BrowserType.SAFARI, platform = Platform.MAC)
	})
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD})
	public @interface ChromeAndMacSafari {
	}

//</editor-fold>

}
