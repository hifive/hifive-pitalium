/**
 * RDPプロセスをkllする。
 */
def killAllRDP() {
	bat("taskkill /im mstsc.exe /f")
}

return this;