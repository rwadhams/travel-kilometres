package com.wadhams.travel.kms.report

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

import com.wadhams.travel.kms.dto.ServiceDTO
import com.wadhams.travel.kms.dto.ServiceEventDTO

class ServiceReportService {
		SimpleDateFormat sdf
		NumberFormat nf
		NumberFormat cf

	def ServiceReportService() {
		sdf = new SimpleDateFormat("dd/MM/yyyy")
		nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)
		cf = NumberFormat.getCurrencyInstance()
		cf.setMaximumFractionDigits(2)

	}
	
	def execute(ServiceDTO serviceDTO, BigDecimal totalCaravanKms, BigDecimal carOdometerKms) {
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
			pw.println ''
			carTyresReport(serviceDTO, carOdometerKms, pw)
			pw.println ''
			caravanTyresReport(serviceDTO, totalCaravanKms, pw)
			pw.println ''
			fuelFilterReport(serviceDTO, carOdometerKms, pw)
		}
	}
	
	def transmissionReport(ServiceDTO serviceDTO, BigDecimal carOdometerKms, PrintWriter pw) {
		pw.println "Transmission Service - Frequency: ${nf.format(serviceDTO.transmissionFrequency)}"
		
		serviceDTO.transmissionList.each {se ->
			pw.println "\t${sdf.format(se.serviceEventDate)} (${cf.format(se.serviceEventCost)}) ServiceName: ${se.serviceEventName} Car odometer: ${nf.format(se.serviceEventOdometer).padRight(7, ' ')} at ${se.serviceEventLocation}"
		}
		
		ServiceEventDTO lastTransmission = serviceDTO.transmissionList[-1]
		BigDecimal nextServiceSchedule = lastTransmission.serviceEventOdometer.add(serviceDTO.transmissionFrequency)
		BigDecimal nextServiceRemaining = nextServiceSchedule.subtract(carOdometerKms)
		pw.println "\tNext service is due in: ${nf.format(nextServiceRemaining)} car Kms. At ${nf.format(nextServiceSchedule)}."
	}

	def caravanReport(ServiceDTO serviceDTO, BigDecimal totalCaravanKms, PrintWriter pw) {
		pw.println "Caravan Service - Frequency: ${nf.format(serviceDTO.caravanFrequency)}"
		
		serviceDTO.caravanList.each {se ->
			pw.println "\t${sdf.format(se.serviceEventDate)} (${cf.format(se.serviceEventCost)}) ServiceName: ${se.serviceEventName} Caravan odometer: ${nf.format(se.serviceEventOdometer).padRight(6, ' ')} at ${se.serviceEventLocation}"
		}
		
		ServiceEventDTO lastCaravan = serviceDTO.caravanList[-1]
		BigDecimal nextServiceSchedule = lastCaravan.serviceEventOdometer.add(serviceDTO.caravanFrequency)
		BigDecimal nextServiceRemaining = nextServiceSchedule.subtract(totalCaravanKms)
		pw.println "\tNext service is due in: ${nf.format(nextServiceRemaining)} caravan Kms."
	}

	def carReport(ServiceDTO serviceDTO, BigDecimal carOdometerKms, PrintWriter pw) {
		pw.println "Car Service - Frequency: ${nf.format(serviceDTO.carFrequency)}"
		
		ServiceEventDTO prev = null
		serviceDTO.carList.each {se ->
			//durations
			if (prev) {
				LocalDate start = prev.serviceEventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
				LocalDate end = se.serviceEventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
				Period p = Period.between(start, end)
				String dateDuration = formatPeriod(p)
				BigDecimal odometerDuration = se.serviceEventOdometer.subtract(prev.serviceEventOdometer)
				pw.println "\t\tDurations: $dateDuration; ${nf.format(odometerDuration)} Kms."
			}
			pw.println "\t${sdf.format(se.serviceEventDate)} (${cf.format(se.serviceEventCost)}) Scheduled: ${nf.format(se.serviceEventSchedule).padRight(7, ' ')} Car odometer: ${nf.format(se.serviceEventOdometer).padRight(7, ' ')} at ${se.serviceEventLocation}"
			prev = se
		}
		
		ServiceEventDTO lastCar = serviceDTO.carList[-1]
		BigDecimal nextServiceSchedule = lastCar.serviceEventSchedule.add(serviceDTO.carFrequency)
		BigDecimal nextServiceRemaining = nextServiceSchedule.subtract(carOdometerKms)
		pw.println "\tNext service is due in: ${nf.format(nextServiceRemaining)} car Kms. At ${nf.format(nextServiceSchedule)}."
	}

	def carTyresReport(ServiceDTO serviceDTO, BigDecimal carOdometerKms, PrintWriter pw) {
		pw.println 'Car Tyres'
		
		ServiceEventDTO prev = null
		serviceDTO.carTyresList.each {se ->
			//durations
			if (prev) {
				LocalDate start = prev.serviceEventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
				LocalDate end = se.serviceEventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
				Period p = Period.between(start, end)
				String dateDuration = formatPeriod(p)
				BigDecimal odometerDuration = se.serviceEventOdometer.subtract(prev.serviceEventOdometer)
				pw.println "\t\tDurations: $dateDuration; ${nf.format(odometerDuration)} Kms."
			}
			pw.println "\t${sdf.format(se.serviceEventDate)} (${cf.format(se.serviceEventCost)}) Car odometer: ${nf.format(se.serviceEventOdometer)} at ${se.serviceEventLocation}"
			prev = se
		}
		
		ServiceEventDTO lastCarTyres = serviceDTO.carTyresList[-1]
		BigDecimal travelDistance = carOdometerKms.subtract(lastCarTyres.serviceEventOdometer)
		pw.println "\tDistance travelled: ${nf.format(travelDistance)} Kms."
	}

	def caravanTyresReport(ServiceDTO serviceDTO, BigDecimal totalCaravanKms, PrintWriter pw) {
		pw.println 'Caravan Tyres'
		
		ServiceEventDTO prev = null
		serviceDTO.caravanTyresList.each {se ->
			//durations
			if (prev) {
				LocalDate start = prev.serviceEventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
				LocalDate end = se.serviceEventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
				Period p = Period.between(start, end)
				String dateDuration = formatPeriod(p)
				BigDecimal odometerDuration = se.serviceEventOdometer.subtract(prev.serviceEventOdometer)
				pw.println "\t\tDurations: $dateDuration; ${nf.format(odometerDuration)} Kms."
			}
			pw.println "\t${sdf.format(se.serviceEventDate)} (${cf.format(se.serviceEventCost)}) Caravan odometer: ${nf.format(se.serviceEventOdometer)} at ${se.serviceEventLocation}"
			prev = se
		}
		
		ServiceEventDTO lastCaravanTyres = serviceDTO.caravanTyresList[-1]
		BigDecimal travelDistance = totalCaravanKms.subtract(lastCaravanTyres.serviceEventOdometer)
		pw.println "\tDistance travelled: ${nf.format(travelDistance)} Kms."
	}

	def fuelFilterReport(ServiceDTO serviceDTO, BigDecimal carOdometerKms, PrintWriter pw) {
		pw.println 'Fuel Filter'
		
		ServiceEventDTO prev = null
		serviceDTO.fuelFilterList.each {se ->
			//durations
			if (prev) {
				LocalDate start = prev.serviceEventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
				LocalDate end = se.serviceEventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
				Period p = Period.between(start, end)
				String dateDuration = formatPeriod(p)
				BigDecimal odometerDuration = se.serviceEventOdometer.subtract(prev.serviceEventOdometer)
				pw.println "\t\tDurations: $dateDuration; ${nf.format(odometerDuration)} Kms."
			}
			pw.println "\t${sdf.format(se.serviceEventDate)} (${cf.format(se.serviceEventCost)}) Car odometer: ${nf.format(se.serviceEventOdometer).padRight(7, ' ')} at ${se.serviceEventLocation}"
			prev = se
		}
		
		ServiceEventDTO lastFuelFilter = serviceDTO.fuelFilterList[-1]
		BigDecimal travelDistance = carOdometerKms.subtract(lastFuelFilter.serviceEventOdometer)
		pw.println "\tDistance travelled: ${nf.format(travelDistance)} Kms."
	}

	String formatPeriod(Period p) {
		if (p.years > 0) {
			return "${p.years} years, ${p.months} months and ${p.days} days"
		}
		else if (p.months > 0) {
			return "${p.months} months and ${p.days} days"
		}
		else {
			return "${p.days} days"
		}
	}
}
