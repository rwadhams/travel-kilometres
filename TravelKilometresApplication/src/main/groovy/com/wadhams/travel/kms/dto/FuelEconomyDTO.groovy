package com.wadhams.travel.kms.dto

import java.time.format.DateTimeFormatter

class FuelEconomyDTO {
	FuelDTO fuelStart
	FuelDTO fuelEnd

	List<TravelDTO> travelList = []
	
	BigDecimal caravanKilometres = new BigDecimal(0)
	BigDecimal vehicleKilometres = new BigDecimal(0)
	BigDecimal totalKilometres = new BigDecimal(0)
	
	BigDecimal fuelEconomy = new BigDecimal(0)
	
	@Override
	public String toString() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
		StringBuilder sb = new StringBuilder()
		
		sb.append("FuelEconomyDTO\n")
		sb.append("fuelStart: ${fuelStart.fuelDate.format(dtf)} ${fuelStart.odometer}\n")
		sb.append("fuelEnd..: ${fuelEnd.fuelDate.format(dtf)} ${fuelEnd.odometer}\n")
		
		sb.append("Travel List:\n")
		travelList.each {t ->
			sb.append("\tTravel Date: ${t.travelDate.format(dtf)}\n")
			sb.append("\tDeparture: ${t.departureOdometer} ${t.departureLocation}\n")
			sb.append("\tArrival..: ${t.arrivalOdometer} ${t.arrivalLocation}\n")
		}
		
		sb.append("caravanKilometres..: ${caravanKilometres}\n")
		sb.append("vehicleKilometres..: ${vehicleKilometres}\n")
		
		return sb.toString()
	}
	
}
