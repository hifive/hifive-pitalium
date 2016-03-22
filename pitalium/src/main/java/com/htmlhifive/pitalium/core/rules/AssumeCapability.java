package com.htmlhifive.pitalium.core.rules;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.htmlhifive.pitalium.core.annotation.CapabilityFilter;
import com.htmlhifive.pitalium.core.annotation.CapabilityFilters;
import org.junit.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.regex.Pattern;

/**
 * {@link CapabilityFilter}および{@link CapabilityFilters}でテスト実行を制限します。
 */
public class AssumeCapability extends TestWatcher {

	private static final Logger LOG = LoggerFactory.getLogger(AssumeCapability.class);

	private Description description;

	@Override
	protected void starting(Description description) {
		super.starting(description);
		this.description = description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

	public void assumeCapability(final Capabilities capabilities) {
		if (description == null) {
			LOG.warn("[AssumeCapability] Description is empty. assumeCapability should be called after test starting.");
			return;
		}

		// ClassのAnnotation取得
		List<CapabilityFilter> classFilters = new ArrayList<>();
		for (Annotation annotation : description.getTestClass().getAnnotations()) {
			if (annotation instanceof CapabilityFilter) {
				classFilters.add((CapabilityFilter) annotation);
				continue;
			}
			if (annotation instanceof CapabilityFilters) {
				Collections.addAll(classFilters, ((CapabilityFilters) annotation).value());
				continue;
			}

			// AnnotationのAnnotation取得
			for (Annotation a : annotation.annotationType().getDeclaredAnnotations()) {
				if (a instanceof CapabilityFilter) {
					classFilters.add((CapabilityFilter) a);
					continue;
				}
				if (a instanceof CapabilityFilters) {
					Collections.addAll(classFilters, ((CapabilityFilters) a).value());
				}
			}
		}

		if (!classFilters.isEmpty()) {
			boolean classResult = FluentIterable.from(classFilters).anyMatch(new Predicate<CapabilityFilter>() {
				@Override
				public boolean apply(CapabilityFilter f) {
					return isMatch(f, capabilities);
				}
			});
			if (!classResult) {
				throw new AssumptionViolatedException("AssumeCapability");
			}
		}

		List<CapabilityFilter> methodFilters = new ArrayList<>();
		for (Annotation annotation : description.getAnnotations()) {
			if (annotation instanceof CapabilityFilter) {
				methodFilters.add((CapabilityFilter) annotation);
				continue;
			}
			if (annotation instanceof CapabilityFilters) {
				Collections.addAll(methodFilters, ((CapabilityFilters) annotation).value());
				continue;
			}

			// AnnotationのAnnotation取得
			for (Annotation a : annotation.annotationType().getDeclaredAnnotations()) {
				if (a instanceof CapabilityFilter) {
					methodFilters.add((CapabilityFilter) a);
					continue;
				}
				if (a instanceof CapabilityFilters) {
					Collections.addAll(methodFilters, ((CapabilityFilters) a).value());
				}
			}
		}
		if (methodFilters.isEmpty()) {
			return;
		}

		boolean methodResult = FluentIterable.from(methodFilters).anyMatch(new Predicate<CapabilityFilter>() {
			@Override
			public boolean apply(CapabilityFilter f) {
				return isMatch(f, capabilities);
			}
		});
		if (!methodResult) {
			throw new AssumptionViolatedException("AssumeCapability");
		}
	}

	private static boolean isMatch(CapabilityFilter filter, Capabilities capabilities) {
		if (filter.version().length > 0) {
			final String version = capabilities.getVersion();
			boolean result = FluentIterable.of(filter.version()).anyMatch(new Predicate<String>() {
				@Override
				public boolean apply(String s) {
					return Strings.isNullOrEmpty(s)
							? Strings.isNullOrEmpty(version)
							: Pattern.compile(s).matcher(version).find();
				}
			});
			if (!result) {
				return false;
			}
		}

		if (filter.platform().length > 0) {
			final Collection<Platform> platforms = toPlatformFamily(capabilities.getPlatform());
			boolean result = FluentIterable.of(filter.platform()).anyMatch(new Predicate<Platform>() {
				@Override
				public boolean apply(Platform p) {
					return p == Platform.ANY || platforms.contains(p);
				}
			});
			if (!result) {
				return false;
			}
		}

		if (filter.browserName().length > 0) {
			final String browserName = capabilities.getBrowserName();
			boolean result = FluentIterable.of(filter.browserName()).anyMatch(new Predicate<String>() {
				@Override
				public boolean apply(String s) {
					return Strings.isNullOrEmpty(s)
							? Strings.isNullOrEmpty(browserName)
							: s.equals(browserName);
				}
			});
			if (!result) {
				return false;
			}
		}

		if (filter.deviceName().length > 0) {
			final String deviceName = Strings.nullToEmpty((String) capabilities.getCapability("deviceName"));
			boolean result = FluentIterable.of(filter.deviceName()).anyMatch(new Predicate<String>() {
				@Override
				public boolean apply(String s) {
					return Strings.isNullOrEmpty(s)
							? Strings.isNullOrEmpty(deviceName)
							: Pattern.compile(s).matcher(deviceName).find();
				}
			});
			if (!result) {
				return false;
			}
		}

		if (filter.filterGroup().length == 0) {
			return true;
		}

		final String filterGroup = Strings.nullToEmpty((String) capabilities.getCapability("filterGroup"));
		return FluentIterable.of(filter.filterGroup()).anyMatch(new Predicate<String>() {
			@Override
			public boolean apply(String s) {
				return Strings.isNullOrEmpty(s)
						? Strings.isNullOrEmpty(filterGroup)
						: s.equals(filterGroup);
			}
		});
	}

	private static Collection<Platform> toPlatformFamily(Platform platform) {
		Set<Platform> platforms = EnumSet.noneOf(Platform.class);
		if (platform == null) {
			return platforms;
		}

		platforms.add(platform);

		Platform parent = platform;
		while ((parent = parent.family()) != Platform.ANY) {
			platforms.add(parent);
		}
		return platforms;
	}


}
