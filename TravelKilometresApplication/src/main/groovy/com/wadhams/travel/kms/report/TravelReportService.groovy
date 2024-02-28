package com.wadhams.travel.kms.report

import java.math.MathContext
import java.text.NumberFormat
import java.text.SimpleDateFormat

import com.wadhams.travel.kms.dto.TravelDTO

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
		Date startingDate = travelList[0].travelDate
		
		int maxDepartureLocationSize = maxDepartureLocationSize(travelList)
		//println "maxDepartureLocationSize...: $maxDepartureLocationSize"
		int maxArrivalLocationSize = maxArrivalLocationSize(travelList)
		//println "maxArrivalLocationSize.....: $maxArrivalLocationSize"

		report(travelList, startingDate, maxDepartureLocationSize, maxArrivalLocationSize, pw)
	}
	
	def report(List<TravelDTO> travelList, Date startingDate, int maxDepartureLocationSize, int maxArrivalLocationSize, PrintWriter pw) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
		NumberFormat nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)
		NumberFormat pf = NumberFormat.getPercentInstance()
		pf.setMaximumFractionDigits(2)

		BigDecimal totalCaravanKms = new BigDecimal(0.0)
		BigDecimal totalVehicleOnlyKms = new BigDecimal(0.0)
		
		TravelDTO previous = null

		travelList.each {t ->
			//vehicle kilometers when a previous TravelDTO exists
			if (previous) {
				//carOnlyList reporting
				if (previous.carOnlyList.size() > 0) {
					List<String> carOnlyLocationList = previous.carOnlyList.collect {it.location}
					pw.print "Car Only locations.: ${carOnlyLocationList[0]}"
					if (carOnlyLocationList.size() > 1) {
						carOnlyLocationList[1..-1].each {location ->
							pw.print ", $location"
						}
					}
					pw.println ''
				}
				
				BigDecimal vehicleOnlyKms = t.departureOdometer.subtract(previous.arrivalOdometer)
				if (vehicleOnlyKms == 0) {
					pw.println "Overnight in ${previous.arrivalLocation}"
					pw.println ''
				}
				else {
					//pw.println "Vehicle kilometers around ${previous.arrivalLocation}: ${nf.format(vehicleOnlyKms)}"
					pw.println "Car Only kilometers: ${nf.format(vehicleOnlyKms)}"
					pw.println ''
					totalVehicleOnlyKms = totalVehicleOnlyKms.add(vehicleOnlyKms)
				}
			}
			
			BigDecimal caravanKms = t.arrivalOdometer.subtract(t.departureOdometer)
			String departureLocation = t.departureLocation.padRight(maxDepartureLocationSize+2, ' ')
			String arrivalLocation = t.arrivalLocation.padRight(maxArrivalLocationSize+2, ' ')
			pw.println "${sdf.format(t.travelDate)}  $departureLocation$arrivalLocation  ${nf.format(t.departureOdometer).padLeft(9, ' ')}   ${nf.format(t.arrivalOdometer).padLeft(9, ' ')}   ${nf.format(caravanKms).padLeft(7, ' ')}   ${t.arrivalCampsite}"
			
			totalCaravanKms = totalCaravanKms.add(caravanKms)
			
			previous = t
		}
		
		BigDecimal combinedKilometers = totalCaravanKms.add(totalVehicleOnlyKms)
		BigDecimal caravanPercentage = totalCaravanKms.divide(combinedKilometers, MathContext.DECIMAL64)
		BigDecimal vehicleOnlyPercentage = totalVehicleOnlyKms.divide(combinedKilometers, MathContext.DECIMAL64)
		
		pw.println ''
		pw.println "Total caravan kilometers........: ${nf.format(totalCaravanKms).padLeft(8, ' ')} (${pf.format(caravanPercentage)})"
		pw.println "Total vehicle-only kilometers...: ${nf.format(totalVehicleOnlyKms).padLeft(8, ' ')} (${pf.format(vehicleOnlyPercentage)})"
		
		pw.println ''
		pw.println "${nf.format(combinedKilometers)} Kms (combined caravan and vehicle) since: ${sdf.format(startingDate)}  (i.e. Caravan pickup in Melbourne)"
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
