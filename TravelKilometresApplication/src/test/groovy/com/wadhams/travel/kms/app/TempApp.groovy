package com.wadhams.travel.kms.app

import com.wadhams.travel.kms.controller.TempController

class TempApp {
	static main(args) {
		println 'TempApp started...'
		println ''

		TempController controller = new TempController()
		controller.execute()
		
		println ''
		println 'TempApp ended.'
	}
}
