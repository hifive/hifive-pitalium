package com.htmlhifive.pitalium.core.rules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.core.config.FilePersisterConfig;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.io.FileNameFormatter;
import com.htmlhifive.pitalium.core.io.PersistMetadata;
import com.htmlhifive.pitalium.core.model.Performance;
import com.htmlhifive.pitalium.core.result.TestResultManager;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;
import com.htmlhifive.pitalium.core.selenium.TelemetricWebDriver;

/**
 * 性能測定結果の記録を行うRule用クラスです。 {@link com.htmlhifive.pitalium.core.TelemetricTestBase}を拡張した場合は、既に定義済みのため指定する必要はありません。
 */
public class PerformanceTelemetry extends TestWatcher {

	private static final Logger LOG = LoggerFactory.getLogger(PerformanceTelemetry.class);

	private TelemetricWebDriver telemetricWebDriver;

	private String className;
	private String methodName;
	private String currentId;
	private PtlCapabilities capabilities;
	private List<Performance> performanceResults = new ArrayList<Performance>();
	private String reportTemplateName = "detail";
	private TemplateEngine templateEngine = createTemplateEngine();

	@Override
	protected void starting(Description desc) {
		className = desc.getTestClass().getSimpleName();
		methodName = desc.getMethodName().split("\\[")[0];
		currentId = TestResultManager.getInstance().getCurrentId();
	}

	@Override
	protected void finished(Description desc) {
		measurePerformance();
		writeDetails(getDetailFile());
	}

	private String makeDetailFileName() {
		FilePersisterConfig config = PtlTestConfig.getInstance().getPersisterConfig().getFile();
		PersistMetadata metadata = new PersistMetadata(currentId, className, methodName, "performance", capabilities);
		FileNameFormatter fileNameFormatter = new FileNameFormatter(config.getTargetResultFileName());
		return config.getResultDirectory() + File.separator + metadata.getExpectedId() + File.separator
				+ metadata.getClassName() + File.separator
				+ fileNameFormatter.format(metadata).replaceAll("\\..+$", ".html");

	}

	private File getDetailFile() {
		File file = new File(makeDetailFileName());
		File parent = file.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
			throw new TestRuntimeException(String.format(Locale.US, "mkdir error \"%s\"", parent));
		}
		if (!parent.canWrite()) {
			throw new TestRuntimeException(String.format(Locale.US, "No write permission at \"%s\"", parent));
		}
		return file;
	}

	private void writeDetails(File file) {
		try (PrintWriter writer = new PrintWriter(file)) {
			IContext context = new Context();
			context.getVariables().put("performanceResults", performanceResults);
			templateEngine.process(reportTemplateName, context, writer);
		} catch (FileNotFoundException e) {
			LOG.info("[Testcase warning] skip writing performance details", e);
		}
	}

	private TemplateEngine createTemplateEngine() {
		TemplateEngine engine = new TemplateEngine();
		TemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setTemplateMode("XHTML");
		resolver.setPrefix("templates/");
		resolver.setSuffix(".html");
		resolver.setCharacterEncoding("UTF-8");
		engine.setTemplateResolver(resolver);
		return engine;
	}

	/**
	 * 性能測定結果のレポートのテンプレート名を設定します。
	 * 
	 * @param reportTemplateName 性能測定結果のレポートのテンプレート名
	 */
	public void setReportTemplateName(String reportTemplateName) {
		this.reportTemplateName = reportTemplateName;
	}

	/**
	 * 性能を測定します。
	 */
	public void measurePerformance() {
		measurePerformance(null);
	}

	/**
	 * 性能を測定します。
	 * 
	 * @param label 性能測定結果のラベル
	 */
	public void measurePerformance(String label) {
		if (telemetricWebDriver == null) {
			throw new IllegalStateException("TelemetricWebDriver is not set");
		}
		telemetricWebDriver.measurePerformance(label);
	}

	/**
	 * 性能測定結果を記録に追加します。追加済みの性能測定結果であった場合は更新します。
	 * 
	 * @param performance 性能測定結果
	 */
	public void addPerformance(Performance performance) {
		if (performance.getLabel() == null) {
			performance.setLabel(performance.getUrl());
		}
		performance.setId(String.valueOf(performance.getNavigationTiming().getNavigationStart()));

		for (Performance result : performanceResults) {
			if (result.getId().equals(performance.getId())) {
				result.updateWith(performance);
				return;
			}
		}
		performanceResults.add(performance);
	}

	/**
	 * 性能測定機能を持つWebDriverを設定します。
	 * 
	 * @param telemetricWebDriver 性能測定機能を持つWebDriver
	 */
	public void setTelemetricWebDriver(TelemetricWebDriver telemetricWebDriver) {
		this.telemetricWebDriver = telemetricWebDriver;
	}

	/**
	 * ブラウザスペック情報を設定します。
	 * 
	 * @param capabilities ブラウザスペック情報
	 */
	public void setCapabilities(PtlCapabilities capabilities) {
		this.capabilities = capabilities;
	}

}
