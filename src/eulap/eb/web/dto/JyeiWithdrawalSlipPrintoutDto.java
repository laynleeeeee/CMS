package eulap.eb.web.dto;

import java.util.List;
/**
 * Data transfer object for JYEI Withdrawal Slip Printout

 *
 */
public class JyeiWithdrawalSlipPrintoutDto {
	List<WithdrawalSlipDto> jyeiWsPrintoutDto;

	public List<WithdrawalSlipDto> getJyeiWsPrintoutDto() {
		return jyeiWsPrintoutDto;
	}

	public void setJyeiWsPrintoutDto(List<WithdrawalSlipDto> jyeiWsPrintoutDto) {
		this.jyeiWsPrintoutDto = jyeiWsPrintoutDto;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JyeiWithdrawalSlipPrintoutDto [jyeiWsPrintoutDto=").append(jyeiWsPrintoutDto).append("]");
		return builder.toString();
	}
}
