/**
 * Build
 * ソースコードのチェックアウトとコンパイルを行う。
 */
node {
	stage('Checkout')
	deleteDir()
	git(
			branch: BRANCH_NAME,
			url: 'https://github.com/hifive/hifive-pitalium.git'
			)

	stage('Download Libraries')
	def antHome = tool(name: 'Default_Ant')
	withEnv(["ANT_OPTS=-Dhttp.proxyHost=${IVY_PROXY_HOST} -Dhttp.proxyPort=${IVY_PROXY_PORT}"]) {
		bat("${antHome}/bin/ant.bat -file pitalium/ivy_build.xml resolve-test && exit %%ERRORLEVEL%%")
	}

	stage('Create Setting Files')
	createSettingFilesForAll()

	stage('Compile')
	bat("${antHome}/bin/ant.bat -file pitalium/ci_build.xml clean build test_instrument && exit %%ERRORLEVEL%%")
	// コンパイル済のソースコードを含むワークスペース全体を保存
	step(
			$class: 'ArtifactArchiver',
			artifacts: '**'
			)
}

/**
 * CIテスト用の設定ファイルを生成する。
 */
def createSettingFilesForAll() {
	// EnvironmentConfig.jsonの生成
	writeFile(
			encoding: 'UTF-8',
			file: 'pitalium\\src\\test\\resources\\environmentConfig.json',
			text: """\
{
	"execMode": "SET_EXPECTED",
	"hubHost": "${HUB_HOST}",
	"maxThreadExecuteTime": 3600,
	"capabilitiesFilePath": "capabilities.json"
}"""
			)
	writeFile(
			encoding: 'UTF-8',
			file: 'pitalium\\src\\test\\resources\\environmentConfig_test.json',
			text: """\
{
	"execMode": "RUN_TEST",
	"hubHost": "${HUB_HOST}",
	"maxThreadExecuteTime": 3600,
	"capabilitiesFilePath": "capabilities.json"
}"""
			)
	// TestAppConfig.jsonの生成
	writeFile(
			encoding: 'UTF-8',
			file: 'pitalium\\src\\test\\resources\\testAppConfig.json',
			text: """\
{
	"baseUrl": "http://${APP_HOST}/pitalium-test-site/",
	"windowHeight": 960,
	"windowWidth": 1280
}"""
			)
	writeFile(
			encoding: 'UTF-8',
			file: 'pitalium\\src\\test\\resources\\testAppConfig_scroll.json',
			text: """\
{
	"baseUrl": "http://${APP_HOST}/pitalium-test-site/",
	"windowHeight": 960,
	"windowWidth": 1280
}"""
			)
}