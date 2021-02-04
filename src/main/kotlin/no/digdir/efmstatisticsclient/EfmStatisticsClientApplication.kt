package no.digdir.efmstatisticsclient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EfmStatisticsClientApplication

fun main(args: Array<String>) {
	runApplication<EfmStatisticsClientApplication>(*args)
}
