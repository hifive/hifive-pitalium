/**
 * IT_SET_EXPECTED_ALL
 * 結合テスト（screenshot, scrollパッケージ以下）を
 * パラメータで指定した全てのブラウザで実行する。
 */
node {
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
		selector: [
			$class: 'StatusBuildSelector',
			stable: false
		]
	)
    step(
		$class: 'ArtifactArchiver',
		artifacts: '**'
	)
    
    stage('IT (SET_EXPECTED)')
    parallel(
		IE8: {
			if (IE8 == 'true') {
				buildITSetExpectedJob('IE8')
			}
		},
		IE9: {
			if (IE9 == 'true') {
				buildITSetExpectedJob('IE9')
			}
		},
		IE10: {
			if (IE10 == 'true') {
				buildITSetExpectedJob('IE10')
			}
		},
		IE11_Win7: {
			if (Windows7_IE11 == 'true') {
				buildITSetExpectedJob('IE11_Win7')
			}
		},
		IE11_Win10: {
			if (Windows10_IE11 == 'true') {
				buildITSetExpectedJob('IE11_Win10')
			}
		},
		Edge: {
			if (Edge == 'true') {
				buildITSetExpectedJob('Edge')
			}
		},
		Chrome_Win7: {
			if (Windows7_Chrome == 'true') {
				buildITSetExpectedJob('Chrome_Win7')
			}
		},
		Chrome_Win10: {
			if (Windows10_Chrome == 'true') {
				buildITSetExpectedJob('Chrome_Win10')
			}
		},
		FF_Win7: {
			if (Windows7_Firefox == 'true') {
				buildITSetExpectedJob('FF_Win7')
			}
		},
		FF_Win10: {
			if (Windows10_Firefox == 'true') {
				buildITSetExpectedJob('FF_Win10')
			}
		},
		Chrome_Mac: {
			if (Mac_Chrome == 'true') {
				buildITSetExpectedJob('Chrome_Mac')
			}
		},
		FF_Mac: {
			if (Mac_Firefox == 'true') {
				buildITSetExpectedJob('FF_Mac')
			}
		},
		Safari: {
			if (Safari == 'true') {
				buildITSetExpectedJob('Safari')
			}
		},
		FF_Linux: {
			if (Linux_Firefox == 'true') {
				buildITSetExpectedJob('FF_Linux')
			}
		},
    	failFast: false
	)
    
    def kr = load('pitalium/jenkinsfiles/KillRDP.groovy')
    kr.killAllRDP()
}

/**
 * 指定されたブラウザのIT_SET_EXPECTEDジョブを実行する。
 */
def buildITSetExpectedJob(browserName) {
	build(
		job: "IT_${browserName}_SET_EXPECTED",
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