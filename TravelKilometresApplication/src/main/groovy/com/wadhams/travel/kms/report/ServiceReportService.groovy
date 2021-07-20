package com.wadhams.travel.kms.report

import java.math.MathContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import com.wadhams.travel.kms.dto.ServiceDTO
import com.wadhams.travel.kms.dto.ServiceEventDTO
import com.wadhams.travel.kms.dto.TravelDTO

class ServiceReportService {
	def execute(ServiceDTO serviceDTO, BigDecimal totalCaravanKms, BigDecimal carOdometerKms) {
		NumberFormat nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)
		
		File f = new File("out/service-report.txt")
		
		f.withPrintWriter {pw ->
			pw.println 'SERVICE REPORT'
			pw.println '-------------'
			pw.println "Total car Kms....: ${nf.format(carOdometerKms)}"
			pw.println "Total caravan Kms: ${nf.format(totalCaravanKms)}"
			pw.println ''
			
			transmissionReport(serviceDTO, carOdometerKms, pw)
			pw.println ''
			caravanReport(serviceDTO, totalCaravanKms, pw)
			pw.println ''
			carReport(serviceDTO, carOdometerKms, pw)
		}
	}
	
	def transmissionReport(ServiceDTO serviceDTO, BigDecimal carOdometerKms, PrintWriter pw) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
		NumberFormat nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)
		NumberFormat cf = NumberFormat.getCurrencyInstance()
		cf.setMaximumFractionDigits(2)

		pw.println "Transmission Service - Frequency: ${nf.format(serviceDTO.transmissionFrequency)}"
		
		serviceDTO.transmissionList.each {se ->
			pw.println "\t${sdf.format(se.serviceEventDate)} (${cf.format(se.serviceEventCost)}) Scheduled: ${nf.format(se.serviceEventSchedule).padRight(7, ' ')} Car odometer: ${nf.format(se.serviceEventOdometer).padRight(7, ' ')} at ${se.serviceEventLocation}"
		}
		
		ServiceEventDTO lastTransmission = serviceDTO.transmissionList[-1]
		BigDecimal nextServiceOdometer = lastTransmission.serviceEventOdometer.add(serviceDTO.transmissionFrequency)
		BigDecimal nextServiceRemaining = nextServiceOdometer.subtract(carOdometerKms)
		pw.println "\tNext service is due in: ${nf.format(nextServiceRemaining)} car Kms. At ${nf.format(nextServiceOdometer)}"
	}

	def caravanReport(ServiceDTO serviceDTO, BigDecimal totalCaravanKms, PrintWriter pw) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
		NumberFormat nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)
		NumberFormat cf = NumberFormat.getCurrencyInstance()
		cf.setMaximumFractionDigits(2)

		pw.println "Caravan Service - Frequency: ${nf.format(serviceDTO.caravanFrequency)}"
		
		serviceDTO.caravanList.each {se ->
			pw.println "\t${sdf.format(se.serviceEventDate)} (${cf.format(se.serviceEventCost)}) Scheduled: ${nf.format(se.serviceEventSchedule).padRight(7, ' ')} Caravan odometer: ${nf.format(se.serviceEventOdometer).padRight(7, ' ')} at ${se.serviceEventLocation}"
		}
		
		ServiceEventDTO lastCaravan = serviceDTO.caravanList[-1]
		BigDecimal nextServiceOdometer = lastCaravan.serviceEventOdometer.add(serviceDTO.caravanFrequency)
		BigDecimal nextServiceRemaining = nextServiceOdometer.subtract(totalCaravanKms)
		pw.println "\tNext service is due in: ${nf.format(nextServiceRemaining)} caravan Kms."
	}

	def carReport(ServiceDTO serviceDTO, BigDecimal carOdometerKms, PrintWriter pw) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
		NumberFormat nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)
		NumberFormat cf = NumberFormat.getCurrencyInstance()
		cf.setMaximumFractionDigits(2)

		pw.println "Car Service - Frequency: ${nf.format(serviceDTO.carFrequency)}"
		
		serviceDTO.carList.each {se ->
			pw.println "\t${sdf.format(se.serviceEventDate)} (${cf.format(se.serviceEventCost)}) Scheduled: ${nf.format(se.serviceEventSchedule).padRight(7, ' ')} Car odometer: ${nf.format(se.serviceEventOdometer).padRight(7, ' ')} at ${se.serviceEventLocation}"
		}
		
		ServiceEventDTO lastCar = serviceDTO.carList[-1]
		BigDecimal nextServiceOdometer = lastCar.serviceEventOdometer.add(serviceDTO.carFrequency)
		BigDecimal nextServiceRemaining = nextServiceOdometer.subtract(carOdometerKms)
		pw.println "\tNext service is due in: ${nf.format(nextServiceRemaining)} car Kms. At ${nf.format(nextServiceOdometer)}"
	}

}
