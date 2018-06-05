package com.demo.application.util;

public class ApplicationClass {

	public static void main(String[] args) {
		RestUtil.callRest("https://my87.geotab.com/apiv1", "{\"method\":\"Get\",\"params\":{\"credentials\":{\"database\":\"murphy_oil\",\"password\":\"$tr3amlin3\",\"userName\":\"SVC_IOP@murphyoilcorp.com\"},\"typeName\":\"Zone\"}}", "POST", null, null);
	}

}
