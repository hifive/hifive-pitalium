/**
 * IT_exec
 * 結合テスト（execパッケージ以下）を実行する。
 */
node {
	def antHome = tool(name: 'Default_Ant')

	stage('Copy Artifact from Parent Job')
	deleteDir()
	try {
		// 呼び出し元のrunnerジョブからビルド済のワークスペースをコピー
		step(
			$class: 'CopyArtifact',
			projectName: 'Pitalium-test-runner',
			filter: '**',
			fingerprintArtifacts: true,
			selector: [
				$class: 'TriggeredBuildSelector',
				allowUpstreamDependencies: false,
				fallbackToLastSuccessful: false,
				upstreamFilterStrategy: 'UseGlobalSetting'
			]
		)
	} catch (err) {
		// このジョブを単体で実行する場合は直接ビルド実行
		stage('Build')
		build(
			job: 'Build',
			parameters: [
				[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: BRANCH_NAME],
				[$class: 'StringParameterValue', name: 'IVY_PROXY_HOST', value: IVY_PROXY_HOST],
				[$class: 'StringParameterValue', name: 'IVY_PROXY_PORT', value: IVY_PROXY_PORT],
				[$class: 'StringParameterValue', name: 'IVY_PROXY_PORT', value: IVY_PROXY_USER],
				[$class: 'StringParameterValue', name: 'IVY_PROXY_PORT', value: IVY_PROXY_PASSWORD]
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

	stage('IT (exec)')
	// 設定ファイルのコピー
	bat("copy /Y pitalium\\target\\work\\test-classes\\ci\\capabilities_UT_IT_exec.json pitalium\\target\\work\\test-classes\\capabilities.json")

	// テスト実行
	withEnv(["ANT_OPTS=-Dant.proxy.host=${ANT_PROXY_HOST} -Dant.proxy.port=${ANT_PROXY_PORT}"]) {
		bat("${antHome}/bin/ant.bat -file pitalium/ci_build.xml it_test_exec && exit %%ERRORLEVEL%%")
	}

	stage('Archive Artifact')
	// カバレッジレポートが集約時に競合しないよう、ファイル名を変更
	bat('''set browserName=%JOB_NAME:Pitalium/IT_=%
rename pitalium\\target\\work\\test-cobertura\\cobertura.ser %browserName%-cobertura.ser'''
		)

	step(
		$class: 'JUnitResultArchiver',
		testResults: 'pitalium/target/work/test-reports/*.xml'
	)
	step(
		$class: 'ArtifactArchiver',
		artifacts: 'pitalium/target/work/test-reports/*.xml,pitalium/target/work/test-cobertura/*.ser'
	)

}