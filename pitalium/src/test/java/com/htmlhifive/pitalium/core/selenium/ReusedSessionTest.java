package com.htmlhifive.pitalium.core.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.SessionId;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.htmlhifive.pitalium.common.util.JSONUtils;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.config.WebDriverSessionLevel;

public class ReusedSessionTest {
	private List<PtlCapabilities> capabilities;
	private List<PtlWebDriver> drivers;

	@Before
	public void testBefore() {
		// セッションを再利用するためWebDriverSessionLevelをPERSISTEDに設定
		PtlTestConfig.getInstance().getEnvironment().setWebDriverSessionLevel(WebDriverSessionLevel.PERSISTED);

		// capabilities読み込み
		TypeReference<List<Map<String, Object>>> reference = new TypeReference<List<Map<String, Object>>>() {
		};
		String filePath = PtlTestConfig.getInstance().getEnvironment().getCapabilitiesFilePath();
		InputStream in = PtlCapabilities.class.getClassLoader().getResourceAsStream(filePath);
		List<Map<String, Object>> maps = JSONUtils.readValue(in, reference);

		capabilities = Lists.transform(maps, new Function<Map<String, Object>, PtlCapabilities>() {
			@Override
			public PtlCapabilities apply(Map<String, Object> input) {
				return new PtlCapabilities(input);
			}
		});

		// 再利用前のdriverを保持
		drivers = new ArrayList<>();
		for (PtlCapabilities cap : capabilities) {
			PtlWebDriver driver = PtlWebDriverFactory.getInstance(cap).getDriver();
			drivers.add(driver);
		}
	}

	@After
	public void testAfter() {
		for (PtlWebDriver driver : drivers) {
			try {
				driver.quit();
			} catch (WebDriverException e) {
				// 例外が発生しても他のdriverを終了させたいので続行
			}
		}

		drivers = null;
		capabilities = null;
	}

	/**
	 * session.jsonが存在しない場合、新しいセッションを作成する
	 *
	 * @throws Exception
	 */
	@Test
	public void testNotExistSessionFile() throws Exception {
		// 事前にセッションを作成してからsession.jsonを削除する
		List<SessionId> sessions = new ArrayList<>();
		for (PtlWebDriver driver : drivers) {
			// 再利用前のsessionIdを保持
			sessions.add(driver.getSessionId());
			// driver終了
			driver.quit();
		}

		// session.jsonが存在しないこと
		File file = new File("src/main/resources/session.json");
		if (file.exists()) {
			file.delete();
		}
		assertThat(file.exists(), is(false));

		// 各ブラウザごとにテストを実行
		for (int i = 0; i < drivers.size(); i++) {
			PtlWebDriver driver = PtlWebDriverFactory.getInstance(capabilities.get(i)).getDriver();

			// driverがキャッシュされていないこと
			assertThat(driver, not(drivers.get(i)));

			// セッションが新規作成されてsessionIdが等しくないこと
			assertThat(driver.getSessionId(), not(sessions.get(i)));

			// 新規作成したdriver終了
			driver.quit();
		}
	}

	/**
	 * session.jsonが存在して接続可能なセッションがある場合、セッションを再利用する
	 *
	 * @throws Exception
	 */
	@Test
	public void testExistConnectableSession() throws Exception {
		List<SessionId> sessions = new ArrayList<>();
		for (PtlWebDriver driver : drivers) {
			// 再利用前のsessionIdを保持
			sessions.add(driver.getSessionId());
		}

		// session.jsonが存在すること
		File file = new File("src/main/resources/session.json");
		assertThat(file.exists(), is(true));

		// 各ブラウザごとにテストを実行
		for (int i = 0; i < drivers.size(); i++) {
			PtlWebDriver driver = PtlWebDriverFactory.getInstance(capabilities.get(i)).getDriver();

			// driverがキャッシュされていないこと
			assertThat(driver, not(drivers.get(i)));

			// セッションが再利用されてsessionIdが等しいこと
			assertThat(driver.getSessionId(), is(sessions.get(i)));
		}
	}

	/**
	 * session.jsonが存在して接続可能なセッションがない場合、新しいセッションを作成する
	 *
	 * @throws Exception
	 */
	@Test
	public void testNotExistConnectableSession() throws Exception {
		List<SessionId> sessions = new ArrayList<>();
		for (PtlWebDriver driver : drivers) {
			// 再利用前のsessionIdを保持
			sessions.add(driver.getSessionId());

			// driver終了
			driver.quit();
		}

		// session.jsonが存在すること
		File file = new File("src/main/resources/session.json");
		assertThat(file.exists(), is(true));

		// 各ブラウザごとにテストを実行
		for (int i = 0; i < drivers.size(); i++) {
			PtlWebDriver driver = PtlWebDriverFactory.getInstance(capabilities.get(i)).getDriver();

			// driverがキャッシュされていないこと
			assertThat(driver, not(drivers.get(i)));

			// セッションが新規作成されてsessionIdが等しくないこと
			assertThat(driver.getSessionId(), not(sessions.get(i)));

			// 新規作成したdriver終了
			driver.quit();
		}
	}

	/**
	 * session.jsonが存在してブラウザ停止中、driverのみ起動中でセッションに接続できない場合、新しいセッションを作成する
	 *
	 * @throws Exception
	 */
	public void testDriverOnlyRunning() throws Exception {
		// TODO: テストPCからブラウザが起動しているPCのブラウザを閉じる方法が不明なので後で考える
	}

	/**
	 * session.jsonが存在してブラウザのみ起動中、driver停止中でセッションに接続できない場合、新しいセッションを作成する
	 *
	 * @throws Exception
	 */
	public void testBrowserOnlyRunning() throws Exception {
		// TODO: テストPCからdriverが起動しているPCのdriverを閉じる方法が不明なので後で考える
	}

	/**
	 * 出力されたsession.jsonの値が正しいことを確認するため、新規作成したセッションとsession.jsonを読み込み再利用したセッションの値が等しいことを確認する
	 *
	 * @throws Exception
	 */
	@Test
	public void testOutputSession() throws Exception {
		// 各ブラウザごとにテストを実行
		for (int i = 0; i < drivers.size(); i++) {
			PtlWebDriver driver = drivers.get(i);
			PtlWebDriver newDriver = PtlWebDriverFactory.getInstance(capabilities.get(i)).getDriver();

			// driverがキャッシュされていないこと
			assertThat(newDriver, not(driver));

			// sessionIdが保持されていること
			assertThat(newDriver.getSessionId(), is(driver.getSessionId()));

			// rawCapabilitiesが保持されていること
			Map<String, ?> storedCapabilitiesMap = driver.getRawCapabilities().asMap();
			Map<String, ?> capabilitiesMap = newDriver.getRawCapabilities().asMap();
			for (Map.Entry<String, ?> entry : capabilitiesMap.entrySet()) {
				String key = entry.getKey();
				assertThat(entry.getValue(), is(storedCapabilitiesMap.get(key)));
			}

			// dialectが保持されていること
			CustomHttpCommandExecutor newExecutor = (CustomHttpCommandExecutor) newDriver.getCommandExecutor();
			CustomHttpCommandExecutor executor = (CustomHttpCommandExecutor) driver.getCommandExecutor();
			assertThat(newExecutor.getDialect(), is(executor.getDialect()));
		}
	}

}