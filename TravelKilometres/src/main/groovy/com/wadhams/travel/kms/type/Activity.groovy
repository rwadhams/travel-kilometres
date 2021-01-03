package com.wadhams.travel.kms.type

enum Activity {
	InitialFuel('INITIAL_FUEL'),
	FuelFillUp('FUEL_FILL_UP'),
	
	Departure('DEPARTURE'),
	Arrival('ARRIVAL'),
	
	Unknown('Unknown');
	
	private static EnumSet<Activity> allEnums = EnumSet.allOf(Activity.class)
	
	private final String name

	Activity(String name) {
		this.name = name
	}
	
	public static Activity findByName(String text) {
		if (text) {
			text = text.toUpperCase()
			for (Activity e : allEnums) {
				if (e.name.equals(text)) {
					return e
				}
			}
		}
		else {
			return Activity.Unknown
		}
		return Activity.Unknown
	}

}
