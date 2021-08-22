package com.wadhams.travel.kms.comparator

import com.wadhams.travel.kms.dto.FuelEconomyDTO

class FuelEconomyDateComparator implements Comparator<FuelEconomyDTO> {

	@Override
	public int compare(FuelEconomyDTO dto1, FuelEconomyDTO dto2) {
		return dto1.fuelStart.fuelDate.compareTo(dto2.fuelStart.fuelDate)
	}
}
