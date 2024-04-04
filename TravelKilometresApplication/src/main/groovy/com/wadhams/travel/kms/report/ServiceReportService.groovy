package com.wadhams.travel.kms.report

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import com.wadhams.travel.kms.dto.ServiceContainerDTO
import com.wadhams.travel.kms.dto.ServiceDTO
import com.wadhams.travel.kms.dto.ServiceEventDTO
import com.wadhams.travel.kms.type.Vehicle
import com.wadhams.travel.kms.type.Reporting
import com.wadhams.travel.kms.type.ServiceTiming

class ServiceReportService {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
	
		NumberFormat nf
		NumberFormat cf

	def ServiceReportService() {
		nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)
		cf = NumberFormat.getCurrencyInstance()
		cf.setMaximumFractionDigits(2)

	}
	
	def execute(ServiceContainerDTO serviceContainer, BigDecimal totalCaravanKms, BigDecimal carOdometerKms) {
		File f = new File("out/service-report.txt")
		
		f.withPrintWriter {pw ->
			pw.println 'SERVICE REPORT'
			pw.println '-------------'
			pw.println "Total car Kms....: ${nf.format(carOdometerKms)}"
			pw.println "Total caravan Kms: ${nf.format(totalCaravanKms)}"
			pw.println ''
			
			serviceContainer.serviceDTOList.each {s ->
				if (s.reporting == Reporting.Service) {
					if (s.vehicle == Vehicle.Car) {
						reportService(s, carOdometerKms, pw)
					}
					else {
						reportService(s, totalCaravanKms, pw)
					}
				}
				else if (s.reporting == Reporting.Consumable) {
					if (s.vehicle == Vehicle.Car) {
						reportConsumable(s, carOdometerKms, pw)
					}
					else {
						reportConsumable(s, totalCaravanKms, pw)
					}
				}
				else {
					pw.println "Unknown reporting: $s"
				}
			}
		}
	}
	
	def reportService(ServiceDTO s, BigDecimal kilometres, PrintWriter pw) {
		pw.println "${s.name} - Frequency: ${nf.format(s.frequency)}"
		
		ServiceEventDTO prev = null
		s.serviceEventDTOList.each {se ->
			//duration
			if (prev) {
				LocalDate start = prev.serviceEventDate
				LocalDate end = se.serviceEventDate
				Period p = Period.between(start, end)
				String dateDuration = formatPeriod(p)
				BigDecimal odometerDuration = se.serviceEventOdometer.subtract(prev.serviceEventOdometer)
				pw.println "\t\tDuration: $dateDuration; ${nf.format(odometerDuration)} Kms."
			}
			
			String s1 = se.serviceEventDate.format(dtf)
			String s2 = "(${cf.format(se.serviceEventCost)})"
			String s3
			if (s.serviceTiming == ServiceTiming.UnScheduled) {
				s3 = "ServiceName: ${se.serviceEventName}"
			}
			else {
				s3 = "Scheduled: ${nf.format(se.serviceEventScheduled).padRight(7, ' ')}"
			}
			String s4 = (s.vehicle == Vehicle.Car ) ? 'Car odometer: ' : 'Caravan odometer: '
			String s5 = nf.format(se.serviceEventOdometer).padRight(7, ' ')
			String s6 = se.serviceEventLocation
			pw.println "\t$s1 $s2 $s3 $s4 $s5 at $s6."
			prev = se
		}
		
		ServiceEventDTO last = s.serviceEventDTOList[-1]
		BigDecimal nextServiceSchedule
		if (s.serviceTiming == ServiceTiming.UnScheduled) {
			nextServiceSchedule = last.serviceEventOdometer.add(s.frequency)
		}
		else {
			nextServiceSchedule = last.serviceEventScheduled.add(s.frequency)
		}

		BigDecimal nextServiceRemaining = nextServiceSchedule.subtract(kilometres)
		String s1 = 'Next service is due in:'
		String s2 = nf.format(nextServiceRemaining)
		String s3 = (s.vehicle == Vehicle.Car) ? 'car ' : 'caravan '
		String s4 = nf.format(nextServiceSchedule)
		pw.println "\t$s1 $s2 $s3 Kms. At $s4."
		pw.println ''
	}

	def reportConsumable(ServiceDTO s, BigDecimal kilometres, PrintWriter pw) {
		pw.println s.name
		
		ServiceEventDTO prev = null
		s.serviceEventDTOList.each {se ->
			//duration
			if (prev) {
				LocalDate start = prev.serviceEventDate
				LocalDate end = se.serviceEventDate
				Period p = Period.between(start, end)
				String dateDuration = formatPeriod(p)
				BigDecimal odometerDuration = se.serviceEventOdometer.subtract(prev.serviceEventOdometer)
				pw.println "\t\tDuration: $dateDuration; ${nf.format(odometerDuration)} Kms."
			}
			String s1 = se.serviceEventDate.format(dtf)
			String s2 = (s.vehicle == Vehicle.Car) ? 'Car odometer:' : 'Caravan odometer:'
			String s3 = nf.format(se.serviceEventOdometer).padRight(7, ' ')
			String s4 = se.serviceEventLocation
			pw.println "\t$s1 $s2 $s3 at $s4"
			prev = se
		}
		
		ServiceEventDTO last = s.serviceEventDTOList[-1]
		BigDecimal travelDistance = kilometres.subtract(last.serviceEventOdometer)
		pw.println "\tDistance travelled: ${nf.format(travelDistance)} Kms."
		pw.println ''
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
