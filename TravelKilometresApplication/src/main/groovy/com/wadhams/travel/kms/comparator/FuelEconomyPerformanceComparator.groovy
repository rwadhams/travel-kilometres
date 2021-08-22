package com.wadhams.travel.kms.comparator

import com.wadhams.travel.kms.dto.FuelEconomyDTO

class FuelEconomyPerformanceComparator implements Comparator<FuelEconomyDTO> {

	@Override
	public int compare(FuelEconomyDTO dto1, FuelEconomyDTO dto2) {
		return dto1.fuelEconomy.compareTo(dto2.fuelEconomy)
	}
}
