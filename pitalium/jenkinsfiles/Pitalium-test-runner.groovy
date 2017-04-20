/**
 * Pitalium-test-runner
 * CIテスト全体を実行する。
 */
node {
	def antHome = tool(name: 'Default_Ant')

	stage('Build')
	deleteDir()
	build(
			job: 'Build',
			parameters: [
				[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: BRANCH_NAME],
				[$class: 'StringParameterValue', name: 'IVY_PROXY_HOST', value: IVY_PROXY_HOST],
				[$class: 'StringParameterValue', name: 'IVY_PROXY_PORT', value: IVY_PROXY_PORT],
				[$class: 'StringParameterValue', name: 'HUB_HOST', value: HUB_HOST],
				[$class: 'StringParameterValue', name: 'APP_HOST', value: APP_HOST]
			],
			propagate: false
			)
	step(
			$class: 'CopyArtifact',
			projectName: 'Build',
			filter: '**',
			fingerprintArtifacts: true,
			selector: [
				$class: 'StatusBuildSelector',
				stable: false
				]
			)
	step(
			$class: 'ArtifactArchiver',
			artifacts: '**'
			)

	stage('UT')
	if (RUN_UT == 'true') {
		build(
				job: 'UT_common_core_image_junit',
				parameters: [
					[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: BRANCH_NAME],
					[$class: 'StringParameterValue', name: 'IVY_PROXY_HOST', value: IVY_PROXY_HOST],
					[$class: 'StringParameterValue', name: 'IVY_PROXY_PORT', value: IVY_PROXY_PORT],
					[$class: 'StringParameterValue', name: 'ANT_PROXY_HOST', value: ANT_PROXY_HOST],
					[$class: 'StringParameterValue', name: 'ANT_PROXY_PORT', value: ANT_PROXY_PORT]
				],
				propagate: false
				)
	} else {
		echo("#### Skip UT ####")
	}

	stage('IT (exec)')
	if (RUN_IT_EXEC == 'true') {
		build(
				job: 'IT_exec',
				parameters: [
					[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: BRANCH_NAME],
					[$class: 'StringParameterValue', name: 'IVY_PROXY_HOST', value: IVY_PROXY_HOST],
					[$class: 'StringParameterValue', name: 'IVY_PROXY_PORT', value: IVY_PROXY_PORT],
					[$class: 'StringParameterValue', name: 'ANT_PROXY_HOST', value: ANT_PROXY_HOST],
					[$class: 'StringParameterValue', name: 'ANT_PROXY_PORT', value: ANT_PROXY_PORT]
				],
				propagate: false
				)
	} else {
		echo("#### Skip IT (exec) ####")
	}

	stage 'IT (screenshot-assertion)'
	if (RUN_IT_SCREENSHOT_ASSERTION == 'true') {
		// 全ノードをログ取得モードで再起動
		restartAllNodeForCI()
		
		parallel(
				IE8: {
					if (IE8 == 'true') {
						buildITJob('IE8')
					}
				},
				IE9: {
					if (IE9 == 'true') {
						buildITJob('IE9')
					}
				},
				IE10: {
					if (IE10 == 'true') {
						buildITJob('IE10')
					}
				},
				IE11_Win7: {
					if (Windows7_IE11 == 'true') {
						buildITJob('IE11_Win7')
					}
				},
				IE11_Win10: {
					if (Windows10_IE11 == 'true') {
						buildITJob('IE11_Win10')
					}
				},
				Edge: {
					if (Edge == 'true') {
						buildITJob('Edge')
					}
				},
				Chrome_Win7: {
					if (Windows7_Chrome == 'true') {
						buildITJob('Chrome_Win7')
					}
				},
				Chrome_Win10: {
					if (Windows10_Chrome == 'true') {
						buildITJob('Chrome_Win10')
					}
				},
				FF_Win7: {
					if (Windows7_Firefox == 'true') {
						buildITJob('FF_Win7')
					}
				},
				FF_Win10: {
					if (Windows10_Firefox == 'true') {
						buildITJob('FF_Win10')
					}
				},
				Chrome_Mac: {
					if (Mac_Chrome == 'true') {
						buildITJob('Chrome_Mac')
					}
				},
				FF_Mac: {
					if (Mac_Firefox == 'true') {
						buildITJob('FF_Mac')
					}
				},
				Safari: {
					if (Safari == 'true') {
						buildITJob('Safari')
					}
				},
				FF_Linux: {
					if (Linux_Firefox == 'true') {
						buildITJob('FF_Linux')
					}
				},
				failFast: false
				)

		def kr = load('pitalium/jenkinsfiles/KillRDP.groovy')
		kr.killAllRDP()
		restartAllNodeForDay()
	} else {
		echo("#### Skip IT (screenshot-assertion) ####")
	}

	stage('SonarQube Analysis')
	// 全テストレポートの集約
	def reportPath = 'reports'
	if (RUN_UT == 'true') {
		copyReportFromJob('UT_common_core_image_junit', reportPath)
	}
	if (RUN_IT_EXEC == 'true') {
		copyReportFromJob('IT_exec', reportPath)
	}
	if (RUN_IT_SCREENSHOT_ASSERTION == 'true') {
		if (IE8 == 'true') {
			copyReportFromJob('IT_IE8', reportPath)
		}
		if (IE9 == 'true') {
			copyReportFromJob('IT_IE9', reportPath)
		}
		if (IE10 == 'true') {
			copyReportFromJob('IT_IE10', reportPath)
		}
		if (Windows7_IE11 == 'true') {
			copyReportFromJob('IT_IE11_Win7', reportPath)
		}
		if (Windows10_IE11 == 'true') {
			copyReportFromJob('IT_IE11_Win10', reportPath)
		}
		if (Edge == 'true') {
			copyReportFromJob('IT_Edge', reportPath)
		}
		if (Windows7_Chrome == 'true') {
			copyReportFromJob('IT_Chrome_Win7', reportPath)
		}
		if (Windows10_Chrome == 'true') {
			copyReportFromJob('IT_Chrome_Win10', reportPath)
		}
		if (Windows7_Firefox == 'true') {
			copyReportFromJob('IT_FF_Win7', reportPath)
		}
		if (Windows10_Firefox == 'true') {
			copyReportFromJob('IT_FF_Win10', reportPath)
		}
		if (Mac_Chrome == 'true') {
			copyReportFromJob('IT_Chrome_Mac', reportPath)
		}
		if (Mac_Firefox == 'true') {
			copyReportFromJob('IT_FF_Mac', reportPath)
		}
		if (Safari == 'true') {
			copyReportFromJob('IT_Safari', reportPath)
		}
		if (Linux_Firefox == 'true') {
			copyReportFromJob('IT_FF_Linux', reportPath)
		}
	}
	
	withEnv(["ANT_OPTS=-Dcobertura.report.dir=../${reportPath}"]) {
		bat("${antHome}/bin/ant.bat -file pitalium/ci_build.xml test_report && exit %%ERRORLEVEL%%")
	}
	// SonarQubeで解析
	def sonarScanner = tool(name: 'Default_SonarQube-Scanner')
	bat("${sonarScanner}/bin/sonar-runner.bat -e -Dsonar.host.url=${SONAR_URL} -Dsonar.sourceEncoding=UTF-8 -Dsonar.sources=pitalium/src/main/java -Dsonar.junit.reportsPath=${reportPath} -Dsonar.projectVersion=1.1.0 -Dsonar.java.binaries=pitalium/target/work/classes -Dsonar.projectKey=com.htmlhifive.test.pitalium2 -Dsonar.cobertura.reportPath=pitalium/target/work/test-cobertura/coverage.xml -Dsonar.working.directory=pitalium/tmp/sonar -Dsonar.tests=pitalium/src/test/java -Dsonar.java.libraries=pitalium/libs/*.jar -Dsonar.projectName=Pitalium-jenkins2")
	step(
			$class: 'JUnitResultArchiver',
			testResults: "${reportPath}/*.xml"
			)

}

/**
 * 指定されたブラウザのITジョブを実行する。
 */
def buildITJob(browserName) {
	build(
			job: "IT_${browserName}",
			parameters: [
				[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: BRANCH_NAME],
				[$class: 'StringParameterValue', name: 'IVY_PROXY_HOST', value: IVY_PROXY_HOST],
				[$class: 'StringParameterValue', name: 'IVY_PROXY_PORT', value: IVY_PROXY_PORT],
				[$class: 'StringParameterValue', name: 'ANT_PROXY_HOST', value: ANT_PROXY_HOST],
				[$class: 'StringParameterValue', name: 'ANT_PROXY_PORT', value: ANT_PROXY_PORT],
				[$class: 'StringParameterValue', name: 'RESULTS_DIR', value: RESULTS_DIR]
			],
			propagate: false
			)
}

/**
 * 指定されたジョブの成果物（テストレポート）を取得する。
 */
def copyReportFromJob(jobName, reportPath) {
	step(
			$class: 'CopyArtifact',
			projectName: jobName,
			filter: 'pitalium/target/work/test-reports/**/*.xml,pitalium/target/work/test-cobertura/*.ser',
			fingerprintArtifacts: true,
			flatten: true,
			selector: [$class: 'WorkspaceSelector'],
			target: reportPath
			)
}

/**
 * 全ノードのSelenium Grid Nodeサーバをログ取得モードで再起動する。
 */
def restartAllNodeForCI() {
	def nodes = [
//		'IE7',
//		'IE8',
		'IE9',
//		'IE10',
		'IE11'
	]
	for(n in nodes) {
		node(n) {
			bat "call ${SELENIUM_DIR}\\kill-grid.bat"
			bat """set BUILD_ID=dontKillMe
call ${SELENIUM_DIR}\\launchNode.bat"""
		}
	}
}

/**
 * 全ノードのSelenium Grid Nodeサーバを通常モードで再起動する。
 */
def restartAllNodeForDay() {
	def nodes = [
		//		'IE7',
		//		'IE8',
				'IE9',
		//		'IE10',
				'IE11'
			]
			for(n in nodes) {
				node(n) {
					// ノードの再起動
					bat "call ${SELENIUM_DIR}\\kill-grid.bat"
					bat """set BUILD_ID=dontKillMe
call ${SELENIUM_DIR}\\launchNode_day.bat"""
					
					// Selenium Grid ノードのログファイルを保存
					bat """if not exist ${SELENIUM_DIR}\\logs (mkdir ${SELENIUM_DIR}\\logs)
set dt=%date:~-10,4%%date:~-5,2%%date:~-2,2%
if exist ${SELENIUM_DIR}\\nodelog.txt (move ${SELENIUM_DIR}\\nodelog.txt ${SELENIUM_DIR}\\logs\\nodelog_%dt%.txt)"""
					bat """pushd ${SELENIUM_DIR}\\logs
for /f "skip=20" %%i in ('dir /a-d /b /o-d') do del %%i
exit /B 0"""
				}
			}
}