package com.wadhams.travel.kms.type

enum Vehicle {
	KimberleyKamper('KIMBERLEY_KAMPER','Kimberley Kamper'),
	SaluteCaravan('SALUTE_CARAVAN','Salute Caravan'),
	ToyotaLandCruiser('TOYOTA_LANDCRUISER','Toyota LandCruiser'),
	
	NoTrailer('NoTrailer','No Trailer'),

	Invalid('Invalid','Invalid'),
	Unknown('Unknown','Unknown');
	
	private static EnumSet<Vehicle> allEnums = EnumSet.allOf(Vehicle.class)
	
	private final String xmlName
	private final String reportName
	
	Vehicle(String xmlName, String reportName) {
		this.xmlName = xmlName
		this.reportName = reportName
	}
	public static Vehicle findByXMLName(String text) {
		if (text) {
			text = text.toUpperCase()
			for (Vehicle e : allEnums) {
				if (e.xmlName.equals(text)) {
					return e
				}
			}
		}
		else {
			return Vehicle.Unknown
		}
		
		println ''
		println "ZZZZ Invalid asset text: $text"
		println ''
		return Vehicle.Invalid
	}

	public String getReportName() {
		return reportName
	}

}
