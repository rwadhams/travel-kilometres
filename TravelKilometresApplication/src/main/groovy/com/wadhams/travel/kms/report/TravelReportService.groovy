package com.wadhams.travel.kms.report

import java.math.MathContext
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import com.wadhams.travel.kms.dto.TravelDTO
import com.wadhams.travel.kms.dto.TravelReportingDTO
import com.wadhams.travel.kms.type.Vehicle

class TravelReportService {
	def execute(List<TravelDTO> travelList) {
		File f = new File("out/travel-report.txt")
		
		f.withPrintWriter {pw ->
			pw.println 'TRAVEL REPORT'
			pw.println '-------------'
	
			report(travelList, pw)
		}
	}
	
	def report(List<TravelDTO> travelList, PrintWriter pw) {
		LocalDate startingDate = travelList[0].travelDate
		
		int maxDepartureLocationSize = maxDepartureLocationSize(travelList)
		//println "maxDepartureLocationSize...: $maxDepartureLocationSize"
		int maxArrivalLocationSize = maxArrivalLocationSize(travelList)
		//println "maxArrivalLocationSize.....: $maxArrivalLocationSize"
		
		List<TravelReportingDTO> travelReportingList = buildTravelReportingDTOList(travelList)
//		travelReportingList.each {tr ->
//			println tr
//		}

		report(travelReportingList, startingDate, maxDepartureLocationSize, maxArrivalLocationSize, pw)
	}
	
	def report(List<TravelReportingDTO> travelReportingList, LocalDate startingDate, int maxDepartureLocationSize, int maxArrivalLocationSize, PrintWriter pw) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
		NumberFormat nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)
		NumberFormat pf = NumberFormat.getPercentInstance()
		pf.setMaximumFractionDigits(2)

		Map<Vehicle, BigDecimal> kmsVehicleMap = [:]
		kmsVehicleMap[Vehicle.KimberleyKamper] = new BigDecimal(0.0)
		kmsVehicleMap[Vehicle.SaluteCaravan] = new BigDecimal(0.0)
		kmsVehicleMap[Vehicle.ToyotaLandCruiser] = new BigDecimal(0.0)
		kmsVehicleMap[Vehicle.NoTrailer] = new BigDecimal(0.0)	//represents location to location travel without a trailer (e.g. Kingscote)
		BigDecimal totalLocationToLocationKms = new BigDecimal(0.0)	//not used in reporting
		BigDecimal totalKmsAroundLocation = new BigDecimal(0.0)		//not used in reporting
		
		travelReportingList.each {tr ->
			TravelDTO t = tr.travelDTO
			
			BigDecimal vehicleKms = kmsVehicleMap[t.vehicle].add(tr.locationToLocationKms)
			totalLocationToLocationKms = totalLocationToLocationKms.add(tr.locationToLocationKms)
			if (tr.kmsAroundLocation != null) {
				vehicleKms = vehicleKms.add(tr.kmsAroundLocation)
				totalKmsAroundLocation = totalKmsAroundLocation.add(tr.kmsAroundLocation)
			}
			kmsVehicleMap[t.vehicle] = vehicleKms
			kmsVehicleMap[t.trailer] = kmsVehicleMap[t.trailer].add(tr.locationToLocationKms)

			String departureLocation = t.departureLocation.padRight(maxDepartureLocationSize+2, ' ')
			String arrivalLocation = t.arrivalLocation.padRight(maxArrivalLocationSize+2, ' ')
			pw.println "${t.travelDate.format(dtf)}  ${t.vehicle.getReportName()} with ${t.trailer.getReportName()}"
			pw.println "$departureLocation$arrivalLocation  ${nf.format(t.departureOdometer).padLeft(9, ' ')}   ${nf.format(t.arrivalOdometer).padLeft(9, ' ')}   ${nf.format(tr.locationToLocationKms).padLeft(7, ' ')}   ${t.arrivalCampsite}"
			if (tr.kmsAroundLocation != null) {
				if (tr.kmsAroundLocation == 0) {
					pw.println "Overnight in ${t.arrivalLocation}"
				}
				else {
					pw.println "${t.vehicle.getReportName()} kilometers around ${t.arrivalLocation}: ${nf.format(tr.kmsAroundLocation)}"
				}
			}
			pw.println ''
		}

//		println "ToyotaLandCruiser\t\t${kmsVehicleMap[Vehicle.ToyotaLandCruiser]}"
//		println "SaluteCaravan\t\t\t${kmsVehicleMap[Vehicle.SaluteCaravan]}"
//		println "KimberleyKamper\t\t\t${kmsVehicleMap[Vehicle.KimberleyKamper]}"
//		println "NoTrailer\t\t\t${kmsVehicleMap[Vehicle.NoTrailer]}"
//		println ''
//		println "totalLocationToLocationKms\t\t$totalLocationToLocationKms"
//		println "totalKmsAroundLocation\t\t$totalKmsAroundLocation"
//		println ''
		
		BigDecimal trailerKms = kmsVehicleMap[Vehicle.SaluteCaravan].add(kmsVehicleMap[Vehicle.KimberleyKamper])
		BigDecimal toyotaLandCruiserOnlyKms = kmsVehicleMap[Vehicle.ToyotaLandCruiser].subtract(trailerKms)
//		println "trailerKms\t\t\t$trailerKms"
//		println "toyotaLandCruiserOnlyKms\t$toyotaLandCruiserOnlyKms"
		
		BigDecimal combinedKilometers = trailerKms.add(toyotaLandCruiserOnlyKms)
		BigDecimal trailerPercentage = trailerKms.divide(combinedKilometers, MathContext.DECIMAL64)
		BigDecimal toyotaLandCruiserOnlyPercentage = toyotaLandCruiserOnlyKms.divide(combinedKilometers, MathContext.DECIMAL64)
		
		pw.println ''
		pw.println "Total trailer kilometers........: ${nf.format(trailerKms).padLeft(8, ' ')} (${pf.format(trailerPercentage)})"
		pw.println "Total vehicle-only kilometers...: ${nf.format(toyotaLandCruiserOnlyKms).padLeft(8, ' ')} (${pf.format(toyotaLandCruiserOnlyPercentage)})"
		
		pw.println ''
		pw.println "${nf.format(combinedKilometers)} Kms (combined caravan and vehicle) since: ${startingDate.format(dtf)}  (i.e. Caravan pickup in Melbourne)"
	}

	List<TravelReportingDTO> buildTravelReportingDTOList(List<TravelDTO> travelList) {
		List<TravelReportingDTO> travelReportingList = []
		
		travelList.each {t ->
			TravelReportingDTO tr = new TravelReportingDTO()
			tr.travelDTO = t
			tr.locationToLocationKms = new BigDecimal(t.arrivalOdometer).subtract(t.departureOdometer)
			travelReportingList << tr
		}
		
		TravelReportingDTO previous = travelReportingList[0]
		travelReportingList[1..-1].each {tr ->
			previous.kmsAroundLocation =  tr.travelDTO.departureOdometer.subtract(previous.travelDTO.arrivalOdometer)
			previous = tr
		}
		
		return travelReportingList
	}
	
	int maxDepartureLocationSize(List<TravelDTO> travelList) {
		int maxLocationSize = 0
		travelList.each {t ->
			if (t.departureLocation.size() > maxLocationSize) {
				maxLocationSize = t.departureLocation.size()
			}
		}
		return maxLocationSize
	}

	int maxArrivalLocationSize(List<TravelDTO> travelList) {
		int maxLocationSize = 0
		travelList.each {t ->
			if (t.arrivalLocation.size() > maxLocationSize) {
				maxLocationSize = t.arrivalLocation.size()
			}
		}
		return maxLocationSize
	}
}
