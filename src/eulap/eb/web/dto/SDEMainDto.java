package eulap.eb.web.dto;

import java.util.List;

/**
 * A class that handles the Main Sales Delivery Efficiency report data.

 * 
 */
public class SDEMainDto {
	private List<SalesDeliveryEfficiencyDto> sdes;
	private List<SDEByCustomerAndMonthDto> sdebcDto;
	private List<SDEByCustomerAndMonthDto> sdebcbmDto;

	public List<SalesDeliveryEfficiencyDto> getSdes() {
		return sdes;
	}
	public void setSdes(List<SalesDeliveryEfficiencyDto> sdes) {
		this.sdes = sdes;
	}

	public List<SDEByCustomerAndMonthDto> getSdebcDto() {
		return sdebcDto;
	}
	public void setSdebcDto(List<SDEByCustomerAndMonthDto> sdebcDto) {
		this.sdebcDto = sdebcDto;
	}

	public List<SDEByCustomerAndMonthDto> getSdebcbmDto() {
		return sdebcbmDto;
	}
	public void setSdebcbmDto(List<SDEByCustomerAndMonthDto> sdebcbmDto) {
		this.sdebcbmDto = sdebcbmDto;
	}

	@Override
	public String toString() {
		return "SDEMainDto [sdes=" + sdes + ", sdebcDto=" + sdebcDto + ", sdebcbmDto=" + sdebcbmDto + "]";
	}
}
