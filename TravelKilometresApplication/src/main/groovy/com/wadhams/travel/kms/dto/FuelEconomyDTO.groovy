package com.wadhams.travel.kms.dto

import java.text.SimpleDateFormat

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
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
		StringBuilder sb = new StringBuilder()
		
		sb.append("FuelEconomyDTO\n")
		sb.append("fuelStart: ${sdf.format(fuelStart.fuelDate)} ${fuelStart.odometer}\n")
		sb.append("fuelEnd..: ${sdf.format(fuelEnd.fuelDate)} ${fuelEnd.odometer}\n")
		
		sb.append("Travel List:\n")
		travelList.each {t ->
			sb.append("\tTravel Date: ${sdf.format(t.travelDate)}\n")
			sb.append("\tDeparture: ${t.departureOdometer} ${t.departureLocation}\n")
			sb.append("\tArrival..: ${t.arrivalOdometer} ${t.arrivalLocation}\n")
		}
		
		sb.append("caravanKilometres..: ${caravanKilometres}\n")
		sb.append("vehicleKilometres..: ${vehicleKilometres}\n")
		
		return sb.toString()
	}
	
}
