package com.wadhams.travel.kms.report

import java.text.NumberFormat
import java.text.SimpleDateFormat

import javax.xml.stream.events.StartDocument

import com.wadhams.travel.kms.dto.FuelDTO
import com.wadhams.travel.kms.dto.TravelDTO
import com.wadhams.travel.kms.dto.TripDTO

class TripReportService {
	def execute(List<TripDTO> tripList, List<FuelDTO> fuelList, List<TravelDTO> travelList) {
		File f = new File("out/trip-report.txt")
		
		f.withPrintWriter {pw ->
			pw.println 'TRIP REPORT'
			pw.println '-----------'
	
			report(tripList, fuelList, travelList, pw)
		}
	}
	
	def report(List<TripDTO> tripList, List<FuelDTO> fuelList, List<TravelDTO> travelList, PrintWriter pw) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
		NumberFormat nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)
		
		Date previousDate = null
		
		tripList.each {trip ->
			TravelDTO startTravelDTO = findTravelDTO(trip.startOdometer, travelList)
			TravelDTO endTravelDTO = findTravelDTO(trip.endOdometer, travelList)
			println startTravelDTO
			println endTravelDTO
			
			if (previousDate) {
				int gap
				use(groovy.time.TimeCategory) {
					def duration = startTravelDTO.travelDate - previousDate
					gap = duration.days
				}
				pw.println "Duration between trips: $gap days"
				2.times {pw.println ''}
			}
			
			String s1 = trip.tripName
			String s2 = sdf.format(startTravelDTO.travelDate)
			String s3 = sdf.format(endTravelDTO.travelDate)
			
			int days
			use(groovy.time.TimeCategory) {
				def duration = endTravelDTO.travelDate - startTravelDTO.travelDate
				days = duration.days + 1
			}
			
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
