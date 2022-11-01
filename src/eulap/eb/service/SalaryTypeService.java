package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.SalaryTypeDao;
import eulap.eb.domain.hibernate.SalaryType;

/**
 * Service class of {@code SalaryType}

 *
 */
@Service
public class SalaryTypeService  {
	@Autowired
	private SalaryTypeDao salaryTypeDao;

	/**
	 * Get all active salary types.
	 * @return List of active salary types.
	 */
	public List<SalaryType> getAllActive() {
		return salaryTypeDao.getAllActive();
	}

}
