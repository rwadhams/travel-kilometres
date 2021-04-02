package com.wadhams.travel.kms.dto

import java.text.SimpleDateFormat

class FuelEconomyDTO {
	TravelKilometerDTO fuelStart
	TravelKilometerDTO fuelEnd

	List<DepartureArrivalPair> dapList = []
	
	BigDecimal caravanKilometres = new BigDecimal(0)
	BigDecimal vehicleKilometres = new BigDecimal(0)
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
		StringBuilder sb = new StringBuilder()
		
		sb.append("FuelEconomyDTO\n")
		sb.append("fuelStart: ${sdf.format(fuelStart.activityDate)} ${fuelStart.odometer}\n")
		sb.append("fuelEnd..: ${sdf.format(fuelEnd.activityDate)} ${fuelEnd.odometer}\n")
		
		sb.append("DepartureArrivalPair List:\n")
		dapList.each {dap ->
			sb.append("\tDeparture: ${sdf.format(dap.departure.activityDate)} ${dap.departure.odometer} ${dap.departure.location}\n")
			sb.append("\tArrival..: ${sdf.format(dap.arrival.activityDate)} ${dap.arrival.odometer} ${dap.arrival.location}\n")
		}
		
		sb.append("caravanKilometres..: ${caravanKilometres}\n")
		sb.append("vehicleKilometres..: ${vehicleKilometres}\n")
		
		return sb.toString()
	}
	
}
