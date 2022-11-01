package eulap.common.domain;

import java.util.Map;

/**
 * Class that handles the access right of the user.

 *
 */
public class AccessRight {
	private final int pAccessFlag;
	private final Map<Integer, Integer> product2MAFlag;
	private final Map<Integer, Integer> module2FAFlag;
	
	public AccessRight(int pAccessFlag, Map<Integer, Integer> uGMAccessRight, 
			Map<Integer, Integer> uGMFAccessRight) {
		this.pAccessFlag = pAccessFlag;
		this.product2MAFlag = uGMAccessRight;
		this.module2FAFlag = uGMFAccessRight;
	}
	
	/**
	 * Validate if the user has product access right.
	 * @param productCode The bit coded product. Refer to PRODUCT_CODE
	 * @return True if the user has access to product, otherwise false.
	 */
	public boolean hasProductAccess (int productCode) {
		return (pAccessFlag & productCode) == productCode;
	}
	
	/**
	 * Validate if the use can access this module.
	 * @param productCodeId The product code of the module.
	 * @param moduleCode The module code.
	 * @return True if the user has access to this module, otherwise false
	 */
	public boolean hasModuleAccess (int productCodeId, int moduleCode) {
		if (!hasProductAccess(productCodeId))
			return false;
		if (!product2MAFlag.containsKey(productCodeId))
			return false;
		int moduleAccessFlag = product2MAFlag.get(productCodeId);
		return (moduleAccessFlag & moduleCode) == moduleCode;
	}
	
	/**
	 * Validate if the user can assess the module function.
	 * @param productCode The product code of the module.
	 * @param moduleCodeId the module code id  that will be used as a hash key.
	 * @param mfCode The module function code.
	 * @return True if the user has access to this function, otherwise false
	 */
	public boolean hasMFAccess (int productCode, int moduleCodeId, int mfCode) {
		if (!hasProductAccess(productCode))
			return false;
		if (!module2FAFlag.containsKey(moduleCodeId))
			return false;
		return (module2FAFlag.get(moduleCodeId) & mfCode) == mfCode;
	}
}
