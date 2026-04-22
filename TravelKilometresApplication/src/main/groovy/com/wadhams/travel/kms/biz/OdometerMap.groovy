package com.wadhams.travel.kms.biz

import com.wadhams.travel.kms.type.Vehicle

class OdometerMap {
	Map<Vehicle, BigDecimal> odometerVehicleMap = [:]
	
	def addOdometer(Vehicle vehicle, BigDecimal odometer) {
		odometerVehicleMap[vehicle] = odometer
	}
	
	BigDecimal getOdometer(Vehicle vehicle) {
		return odometerVehicleMap[vehicle]
	}
}
