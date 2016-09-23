/**
 * IT_SET_EXPECTED
 * 結合テストをSET_EXPECTEDモードで実行する。
 */
node {

	def antHome = tool(name: 'Default_Ant')

	stage('Copy Artifact from Parent Job')
	deleteDir()
	try {
		// 呼び出し元のジョブからビルド済のワークスペースをコピー
		step(
			$class: 'CopyArtifact',
			projectName: 'IT_SET_EXPECTED_ALL',
			filter: '**',
			fingerprintArtifacts: true,
			selector: [
				$class: 'TriggeredBuildSelector',
				allowUpstreamDependencies: false,
				fallbackToLastSuccessful: false,
				upstreamFilterStrategy: 'UseGlobalSetting']
		)
	} catch (err) {
		// このジョブを単体で実行する場合は直接ビルド実行
		stage('Build')
		build(
			job: 'Build',
			parameters: [
				[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: BRANCH_NAME],
				[$class: 'StringParameterValue', name: 'IVY_PROXY_HOST', value: IVY_PROXY_HOST],
				[$class: 'StringParameterValue', name: 'IVY_PROXY_PORT', value: IVY_PROXY_PORT]
			],
			propagate: false
		)
		step(
			$class: 'CopyArtifact',
			projectName: 'Build',
			filter: '**',
			fingerprintArtifacts: true,
			selector: [$class: 'StatusBuildSelector', stable: false]
		)
	}

	stage("IT (assertion_set_expected): ${BROWSER_NAME}")
	if (BROWSER_NAME.startsWith('IE')) {
		// IEマシンはRDP接続しておく
		bat("call ${RDP_DIR}\\startRDP_${BROWSER_NAME}.bat")
	}
	// 設定ファイルのコピー
	bat("copy /Y pitalium\\target\\work\\test-classes\\ci\\capabilities_${BROWSER_NAME}.json pitalium\\target\\work\\test-classes\\capabilities.json")

	// テスト実行
	withEnv(["ANT_OPTS=-Dant.proxy.host=${ANT_PROXY_HOST} -Dant.proxy.port=${ANT_PROXY_PORT}"]) {
		bat("${antHome}/bin/ant.bat -file pitalium/ci_build.xml it_test_screenshot_set_expected it_test_assertion_set_expected && exit %%ERRORLEVEL%%")
	 }

	stage('Archive Artifact')
	step(
		$class: 'JUnitResultArchiver',
		testResults: 'pitalium/target/work/test-reports/*.xml'
	)
	step(
		$class: 'ArtifactArchiver',
		artifacts: 'pitalium/target/work/test-cobertura/test-result/results/**/*'
	)

	// 目視確認用に別フォルダにコピー
	bat("""if not exist ${RESULTS_DIR} (mkdir ${RESULTS_DIR})
if not exist ${RESULTS_DIR}\\${BROWSER_NAME} (mkdir ${RESULTS_DIR}\\${BROWSER_NAME})
set jobName=%JOB_NAME:Pitalium/=%
if not exist ${RESULTS_DIR}\\${BROWSER_NAME}\\%jobName%-${env.BUILD_ID} (mkdir ${RESULTS_DIR}\\${BROWSER_NAME}\\%jobName%-${env.BUILD_ID})
pushd pitalium\\target\\work\\test-cobertura\\test-result\\results
set dt=%date:~-10,4%_%date:~-5,2%_%date:~-2,2%
for /d %%i in (%dt%_*) do robocopy /e %%i ${RESULTS_DIR}\\${BROWSER_NAME}\\%jobName%-${env.BUILD_ID}

pushd ${RESULTS_DIR}\\${BROWSER_NAME}
for /f "skip=20" %%i in (\'dir /ad /b /o-d %jobName%-*\') do rmdir /s /q %%i
exit /B 0"""
	)

}