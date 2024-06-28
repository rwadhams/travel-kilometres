package com.wadhams.travel.kms.report

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import com.wadhams.travel.kms.dto.TravelDTO
import com.wadhams.travel.kms.dto.TripDTO

class TripReportService {
	def execute(List<TripDTO> tripList, List<TravelDTO> travelList) {
		File f = new File("out/trip-report.txt")
		
		f.withPrintWriter {pw ->
			pw.println 'TRIP REPORT'
			pw.println '-----------'
	
			report(tripList, travelList, pw)
		}
	}
	
	def report(List<TripDTO> tripList, List<TravelDTO> travelList, PrintWriter pw) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
		NumberFormat nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)
		
		LocalDate previousDate = null
		
		tripList.each {trip ->
			TravelDTO startTravelDTO = findTravelDTO(trip.startOdometer, travelList)
			TravelDTO endTravelDTO = findTravelDTO(trip.endOdometer, travelList)
			//println startTravelDTO
			//println endTravelDTO
			
			if (previousDate) {
				long gap = ChronoUnit.DAYS.between(previousDate, startTravelDTO.travelDate)
				pw.println "Duration between trips: $gap days"
				2.times {pw.println ''}
			}
			
			String s1 = trip.tripName
			String s2 = startTravelDTO.travelDate.format(dtf)
			String s3 = endTravelDTO.travelDate.format(dtf)
			
			long days = ChronoUnit.DAYS.between(startTravelDTO.travelDate, endTravelDTO.travelDate)
			
			String s4 = days.toString().padRight(3, ' ')
			
			BigDecimal distance = endTravelDTO.arrivalOdometer.subtract(startTravelDTO.departureOdometer)
			String s5 = nf.format(distance)
			
			pw.println "$s1"
			pw.println "\tStarted: $s2  Finished: $s3  Duration(days): $s4  Distance(Kms): $s5"
			
			List<TravelDTO> tripTravelDTOList = findAllTravelDTO(trip.startOdometer, trip.endOdometer, travelList)
			//println "tripTravelDTOList size: ${tripTravelDTOList.size()}"
			
			pw.println "\t\t${tripTravelDTOList[0].departureLocation}"
			tripTravelDTOList.each {travel ->
				pw.println "\t\t${travel.arrivalLocation} (${travel.arrivalCampsite})"
			}
			
			2.times {pw.println ''}
			
			previousDate = endTravelDTO.travelDate
		}
		
	}
	
	TravelDTO findTravelDTO(BigDecimal odometer, List<TravelDTO> travelList) {
		travelList.find() {travel->
			travel.departureOdometer.equals(odometer) || travel.arrivalOdometer == odometer
		}
	}

	List<TravelDTO> findAllTravelDTO(BigDecimal startOdometer, BigDecimal endOdometer, List<TravelDTO> travelList) {
		travelList.findAll() {travel->
			travel.departureOdometer >= startOdometer && travel.arrivalOdometer <= endOdometer
		}
	}

}
