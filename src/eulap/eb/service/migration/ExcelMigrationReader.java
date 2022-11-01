package eulap.eb.service.migration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.BusinessClassification;
import eulap.eb.domain.hibernate.BusinessRegistrationType;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemAddOn;
import eulap.eb.domain.hibernate.ItemAddOnType;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.ItemDiscount;
import eulap.eb.domain.hibernate.ItemDiscountType;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.domain.hibernate.ItemVatType;
import eulap.eb.domain.hibernate.NormalBalance;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.TimePeriod;
import eulap.eb.domain.hibernate.TimePeriodStatus;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserGroup;

/**
 * Class that handles the reading of formatted excel file for data migration

 *
 */
public class ExcelMigrationReader {
	private final MigrationDataHandler dataHandler;

	private enum MigrationSheet {
		COMPANY("Company"),
		DIVISION ("Division"),
		ACCOUNT_TYPE ("Account Type"),
		ACCOUNT ("Chart of Accounts"),
		USER_POSITION ("User Position"),
		USER_GROUP ("User Group"),
		USER ("User"),
		TERM ("Term"),
		TIME_PERIOD ("Time Period"),
		CUSTOMER ("Customer"),
		SUPPLIER ("Supplier"),
		ITEM ("Item");

		private String name;

		MigrationSheet (String name) {
			this.name = name;
		}

		public static MigrationSheet getMigrationSheet (String name) {
			for (MigrationSheet sheetName : values()){
				if (name.equals(sheetName.name))
					return sheetName;
			}
			return null;
		}
	}

	public ExcelMigrationReader (MigrationDataHandler dataHandler) {
		this.dataHandler = dataHandler;
	}

	/**
	 * Read the excel file that is formated to CMS migration template. 
	 * @param fis the file input stream. 
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public void readFile(InputStream fis) throws InvalidFormatException, IOException {
		dataHandler.initDataHandler();
		Workbook workBook = WorkbookFactory.create(fis);
		for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
			Sheet sheet = workBook.getSheetAt(i);
			MigrationSheet ms = MigrationSheet.getMigrationSheet(sheet.getSheetName());
			if (ms == null)
				continue;
			switch (ms) {
				case COMPANY:
					handleCompanySheet(sheet);
					break;
				case DIVISION:
					handleDivisionSheet(sheet);
					break;
				case ACCOUNT_TYPE:
					handleAccountTypeSheet(sheet);
					break;
				case ACCOUNT:
					handleAccountSheet(sheet);
					break;
				case USER_POSITION:
					handleUserPositionSheet(sheet);
					break;
				case USER_GROUP:
					handleUserGroupSheet(sheet);
					break;
				case USER:
					handleUserSheet(sheet);
					break;
				case TERM:
					handleTermSheet(sheet);
					break;
				case TIME_PERIOD:
					handleTimePeriodSheet(sheet);
					break;
				case CUSTOMER:
					handleCustomerSheet(sheet);
					break;
				case SUPPLIER:
					handleSupplierSheet(sheet);
					break;
				case ITEM:
					handleItemSheet(sheet);
					break;
			}
		}
		dataHandler.end();
	}

	private void handleItemSheet (Sheet sheet) {
		dataHandler.beginItem();
		Iterator<Row> rowIterator = sheet.iterator();

		int stockCodeIndex = 0;
		int descriptionIndex = 1;
		int partNumberIndex = 2;
		int uomIndex = 3;
		int itemCategoryIndex = 4;
		int reorderingPointIndex = 5;
		int maxOrderingPointIndex = 6;
		int sellingPriceIndex = 7;
		int vatTypeIndex = 8;

		ItemSrp itemSrp = null;
		List<ItemSrp> itemSrps = null;
		List<ItemDiscount> itemDiscounts = null;
		List<ItemAddOn> itemAddOns = null;
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if(row.getRowNum() < 1) {
				continue;
			}
			Iterator<Cell> cellIterator = row.cellIterator();
			String cellValue = null;
			String stockCode = null;
			String description = null;
			String partNumber = null;
			String uom = null;
			String itemCategory = null;
			Integer reorderingPoint = null;
			Integer maxOrderingPoint = null;
			Double sellingPrice = null;
			String vatType = null;

			// Initialize item discount.
			ItemDiscount itemDiscount = new ItemDiscount();

			// Initialize item addon.
			ItemAddOn itemAddOn = new ItemAddOn();

			itemSrp = new ItemSrp();
			itemSrps = new ArrayList<>();
			itemDiscounts = new ArrayList<>();
			itemAddOns = new ArrayList<>();
			boolean isFirstLineComplete = false;
			while (cellIterator.hasNext()) {
				if (!isFirstLineComplete) {
					isFirstLineComplete =true;
					continue;
				}
				Cell cell = cellIterator.next();
				cellValue = getCellValue(cell);
				int cellIndex = cell.getColumnIndex();
				if (stockCode == null && cellIndex == stockCodeIndex) {
					stockCode = cellValue;
				} else if (description == null && cellIndex == descriptionIndex) {
					description = cellValue;
				} else if (partNumber == null && cellIndex == partNumberIndex) {
					partNumber = cellValue;
				} else if (uom == null && cellIndex == uomIndex) {
					uom = cellValue;
				} else if (itemCategory == null && cellIndex == itemCategoryIndex) {
					itemCategory = cellValue;
				} else if (reorderingPoint == null && cellIndex == reorderingPointIndex) {
					if (isNumeric(cellValue)) {
						Double dblValue = Double.parseDouble(cellValue);
						reorderingPoint = dblValue.intValue();
					}
				} else if (maxOrderingPoint == null && cellIndex == maxOrderingPointIndex) {
					if (isNumeric(cellValue)) {
						Double dblValue = Double.parseDouble(cellValue);
						maxOrderingPoint = dblValue.intValue();
					}
				} else if (sellingPrice == null && cellIndex == sellingPriceIndex) {
					if (isNumeric(cellValue)) {
						sellingPrice = Double.parseDouble(cellValue);
						itemSrp.setSrp(sellingPrice);
					}
				} else if (vatType == null && cellIndex == vatTypeIndex) {
					vatType = cellValue;
				} else if (cellIndex > vatTypeIndex) {
					if (cellValue != null && !cellValue.trim().isEmpty() && isNumeric(cellValue.trim())) {
						Double value = Double.parseDouble(cellValue);
						if (value < 0.0) { // Discount
							itemDiscount.setValue(value);
							// Get the discount name.
							Cell discNameCell = row.getCell(cellIndex - 2);
							itemDiscount.setName(discNameCell.getStringCellValue());

							// Get the discount type name.
							Cell discTypeCell = row.getCell(cellIndex - 1);
							ItemDiscountType type = new ItemDiscountType();
							type.setName(discTypeCell.getStringCellValue());

							itemDiscount.setItemDiscountType(type);
							itemDiscounts.add(itemDiscount);
						} else { // Add On
							itemAddOn.setValue(value);
							Cell addOnNameCell = row.getCell(cellIndex - 2);
							itemAddOn.setName(addOnNameCell.getStringCellValue());
							Cell addOnTypeCell = row.getCell(cellIndex - 1);
							ItemAddOnType type = new ItemAddOnType();
							type.setName(addOnTypeCell.getStringCellValue());
							itemAddOn.setItemAddOnType(type);
							itemAddOns.add(itemAddOn);
						}
					} 
				}
			}

			itemSrps.add(itemSrp);

			Item item = new Item();
			item.setStockCode(stockCode);
			item.setDescription(description);
			item.setManufacturerPartNo(partNumber);
			item.setReorderingPoint(reorderingPoint);
			item.setMaxOrderingPoint(maxOrderingPoint);

			UnitMeasurement unitOfMeasurement = new UnitMeasurement();
			unitOfMeasurement.setName(uom);

			ItemCategory ic = new ItemCategory();
			ic.setName(itemCategory);
			item.setUnitMeasurement(unitOfMeasurement);
			item.setItemCategory(ic);

			item.setItemSrps(itemSrps);
			item.setItemDiscounts(itemDiscounts);
			item.setItemAddOns(itemAddOns);

			ItemVatType itemVatType = new ItemVatType();
			itemVatType.setName(vatType);
			item.setItemVatType(itemVatType);

			dataHandler.handleParsedItem(item);
		}
		System.out.println(sheet.getSheetName());
		dataHandler.endItem();
	}

	private void handleSupplierSheet(Sheet sheet) {
		dataHandler.beginSupplier();

		Iterator<Row> rowIterator = sheet.iterator();

		int typeIndex = 0;
		int nameIndex = 1;
		int firstNameIndex = 2;
		int middleNameIndex = 3;
		int lastNameIndex = 4;
		int stBrgyIndex = 5;
		int cityProvinceInex = 6;
		int accountNameIndex = 7;
		int termIndex = 8;
		int tinIndex = 9;
		int contactPersonIndex = 10;
		int contactNumberIndex = 11;
		int busiRegIndex = 12;

		Supplier supplier = null;
		SupplierAccount supplierAccount = null;
		Term term = null;
		BusinessRegistrationType businessRegistrationType = null;
		BusinessClassification busClassification = null;
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if(row.getRowNum() < 1) {
				continue;
			}
			Iterator<Cell> cellIterator = row.cellIterator();
			String cellValue = null;

			supplier = new Supplier();
			supplierAccount = new SupplierAccount();
			boolean isFirstLineComplete = false;
			while (cellIterator.hasNext()) {
				if (!isFirstLineComplete) {
					isFirstLineComplete =true;
					continue;
				}
				Cell cell = cellIterator.next();
				cellValue = getCellValue(cell);
				int cellIndex = cell.getColumnIndex();
				if (cellIndex == typeIndex) {
					busClassification = new BusinessClassification();
					busClassification.setName(cellValue);
					supplier.setBusinessClassification(busClassification);
				} else if (cellIndex == nameIndex) {
					supplier.setName(cellValue);
				} else if (cellIndex == firstNameIndex) {
					supplier.setFirstName(cellValue);
				} else if (cellIndex == middleNameIndex) {
					supplier.setMiddleName(cellValue);
				} else if (cellIndex == lastNameIndex) {
					supplier.setLastName(cellValue);
				} else if (cellIndex == stBrgyIndex) {
					supplier.setStreetBrgy(cellValue);
				} else if (cellIndex == cityProvinceInex) {
					supplier.setCityProvince(cellValue);
				} else if (cellIndex == accountNameIndex) {
					dataHandler.beginSupplierAccount();
					supplierAccount.setName(cellValue);
				} else if (cellIndex == termIndex) {
					term = new Term();
					term.setName(cellValue);
					supplierAccount.setTerm(term);
				} else if (cellIndex == tinIndex) {
					supplier.setTin(cellValue);
				}  else if (cellIndex == contactPersonIndex) {
					supplier.setContactPerson(cellValue);
				} else if (cellIndex == contactNumberIndex) {
					supplier.setContactNumber(cellValue);
				} else if (cellIndex == busiRegIndex) {
					businessRegistrationType = new BusinessRegistrationType();
					businessRegistrationType.setName(cellValue);
					supplier.setBusRegType(businessRegistrationType);
					dataHandler.handleParsedSupplier(supplier);
				}
			}
			if (supplier != null) {
				supplierAccount.setSupplier(supplier);
				supplierAccount.setTerm(term);
				dataHandler.handleParsedSupplierAccount(supplierAccount);
			}
			dataHandler.endSupplierAccount();
		}
		dataHandler.endSupplier();
	}

	private void handleCompanySheet(Sheet sheet) {
		dataHandler.beginCompany();

		Iterator<Row> rowIterator = sheet.iterator();

		int rowIndex = 0;
		int numberIndex = rowIndex++;
		int nameIndex = rowIndex++;
		int addressIndex = rowIndex++;
		int codeIndex = rowIndex++;
		int tinIndex = rowIndex++;
		int contactNumberIndex = rowIndex++;
		int emailIndex = rowIndex;

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if(row.getRowNum() < 1) {
				continue;
			}
			Iterator<Cell> cellIterator = row.cellIterator();
			String cellValue = null;
			String number = null;
			String name = null;
			String address = null;
			String code = null;
			String tin = null;
			String contactNumber = null;
			String email = null;

			boolean isFirstLineComplete = false;
			while (cellIterator.hasNext()) {
				if (!isFirstLineComplete) {
					isFirstLineComplete =true;
					continue;
				}
				Cell cell = cellIterator.next();
				cellValue = getCellValue(cell);
				int cellIndex = cell.getColumnIndex();

				if (number == null && cellIndex == numberIndex) {
					number = formatNumberToString(cellValue);
				} else if (name == null && cellIndex == nameIndex) {
					name = cellValue;
				} else if (address == null && cellIndex == addressIndex) {
					address = cellValue;
				} else if (code == null && cellIndex == codeIndex) {
					code = cellValue;
				} else if (tin == null && cellIndex == tinIndex) {
					tin = cellValue;
				} else if (contactNumber == null && cellIndex == contactNumberIndex) {
					contactNumber = cellValue;
				} else if (email == null && cellIndex == emailIndex) {
					email = cellValue;
				}
			}

			Company company = new Company();
			company.setCompanyNumber(number);
			company.setName(name);
			company.setCompanyCode(code);
			company.setAddress(address);
			company.setTin(tin);
			company.setContactNumber(contactNumber);
			company.setEmailAddress(email);

			dataHandler.handleParsedCompany(company);
		}
		System.out.println(sheet.getSheetName());
		dataHandler.endCompany();
	}

	private void handleDivisionSheet(Sheet sheet) {
		dataHandler.beginDivision();
		Iterator<Row> rowIterator = sheet.iterator();

		int rowIndex = 0;
		int numberIndex = rowIndex++;
		int nameIndex = rowIndex++;
		int descriptionIndex = rowIndex;

		while (rowIterator.hasNext()) {
			Row row = (Row) rowIterator.next();
			if(row.getRowNum() < 1) {
				continue;
			}
			Iterator<Cell> cellIterator = row.cellIterator();
			String cellValue = null;
			String number = null;
			String name = null;
			String description = null;

			boolean isFirstLineComplete = false;
			while (cellIterator.hasNext()) {
				if (!isFirstLineComplete) {
					isFirstLineComplete =true;
					continue;
				}
				Cell cell = (Cell) cellIterator.next();
				cellValue = getCellValue(cell);
				int cellIndex = cell.getColumnIndex();

				if(number == null && cellIndex == numberIndex) {
					number = formatNumberToString(cellValue);
				} else if(name == null && cellIndex == nameIndex) {
					name = cellValue;
				} else if(description == null && cellIndex == descriptionIndex) {
					description = cellValue;
				}
			}

			Division division = new Division();
			division.setNumber(number);
			division.setName(name);
			division.setDescription(description);
			dataHandler.handleParsedDivision(division);
		}
		dataHandler.endDivision();
	}

	private void handleAccountTypeSheet(Sheet sheet) {
		dataHandler.beginAccountType();
		Iterator<Row> rowIterator = sheet.iterator();

		int currnetIndex = 0;
		int nameIndex = currnetIndex++;
		int normalBalanceIndex = currnetIndex++;
		int contraAccountIndex = currnetIndex;

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if(row.getRowNum() < 1) {
				continue;
			}
			Iterator<Cell> cellIterator = row.cellIterator();
			String cellValue = null;
			String name = null;
			Integer normalBalanceId = null;
			boolean isContraAcccount  = false;

			boolean isFirstLineComplete = false;
			while (cellIterator.hasNext()) {
				if(!isFirstLineComplete) {
					isFirstLineComplete = true;
					continue;
				}
				Cell cell = (Cell) cellIterator.next();
				cellValue = getCellValue(cell);
				int cellIndex = cell.getColumnIndex();

				if(name == null && cellIndex == nameIndex) {
					name = cellValue;
				} else if(normalBalanceId == null && cellIndex == normalBalanceIndex) {
					normalBalanceId = cellValue.equalsIgnoreCase("DEBIT")
							? NormalBalance.DEBIT : NormalBalance.CREDIT;
				} else if(!isContraAcccount && cellIndex == contraAccountIndex) {
					isContraAcccount = cellValue.equalsIgnoreCase("TRUE");
				}
			}

			AccountType accountType = new AccountType();
			accountType.setName(name);
			accountType.setContraAccount(isContraAcccount);
			accountType.setNormalBalanceId(normalBalanceId);
			dataHandler.handleParsedAccountType(accountType);
		}
		dataHandler.endAccountType();
	}

	private void handleAccountSheet(Sheet sheet) {
		dataHandler.beginAccount();
		Iterator<Row> rowIterator = sheet.iterator();

		int currentIndex = 0;
		int numberIndex = currentIndex++;
		int nameIndex = currentIndex++;
		int descriptionIndex = currentIndex++;
		int accountTypeIndex = currentIndex;

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if(row.getRowNum() < 1) {
				continue;
			}
			Iterator<Cell> cellIterator = row.cellIterator();
			String cellValue = null;
			String number = null;
			String name = null;
			String description = null;
			String accountTypeName = null;

			boolean isFirstLineComplete = false;
			while (cellIterator.hasNext()) {
				if(!isFirstLineComplete) {
					isFirstLineComplete = true;
					continue;
				}
				Cell cell = (Cell) cellIterator.next();
				cellValue = getCellValue(cell);
				int cellIndex = cell.getColumnIndex();

				if(number == null && cellIndex == numberIndex) {
					number = cellValue;
				} else if(name == null && cellIndex == nameIndex) {
					name = cellValue;
				} else if(description == null && cellIndex == descriptionIndex) {
					description = cellValue;
				} else if(accountTypeName == null && cellIndex == accountTypeIndex) {
					accountTypeName = cellValue;
				}
			}

			Account account = new Account();
			account.setNumber(number);
			account.setAccountName(name);
			account.setDescription(description);
			account.setAccountTypeName(accountTypeName);
			dataHandler.handleParsedAccount(account);
		}
		dataHandler.endAccount();
	}

	private void handleUserPositionSheet(Sheet sheet) {
		dataHandler.beginUserPosition();
		Iterator<Row> rowIterator = sheet.iterator();

		int nameIndex = 0;

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if(row.getRowNum() < 1) {
				continue;
			}
			Iterator<Cell> cellIterator = row.cellIterator();
			String cellValue = null;
			String name = null;

			boolean isFirstLineComplete = false;
			while (cellIterator.hasNext()) {
				if(!isFirstLineComplete) {
					isFirstLineComplete = true;
					continue;
				}
				Cell cell = (Cell) cellIterator.next();
				cellValue = getCellValue(cell);
				int cellIndex = cell.getColumnIndex();

				if(name == null && cellIndex == nameIndex) {
					name = cellValue;
				}
			}

			Position position = new Position();
			position.setName(name);
			dataHandler.handleParsedUserPosition(position);
		}
		dataHandler.endUserPosition();
	}

	private void handleUserGroupSheet(Sheet sheet) {
		dataHandler.beginUserGroup();
		Iterator<Row> rowIterator = sheet.iterator();

		int currentIndex = 0;
		int nameIndex = currentIndex++;
		int descriptionIndex = currentIndex;

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if(row.getRowNum() < 1) {
				continue;
			}
			Iterator<Cell> cellIterator = row.cellIterator();
			String cellValue = null;
			String name = null;
			String description = null;

			boolean isFirstLineComplete = false;
			while (cellIterator.hasNext()) {
				if(!isFirstLineComplete) {
					isFirstLineComplete = true;
					continue;
				}
				Cell cell = (Cell) cellIterator.next();
				cellValue = getCellValue(cell);
				int cellIndex = cell.getColumnIndex();

				if(name == null && cellIndex == nameIndex) {
					name = cellValue;
				} else if(description == null && cellIndex == descriptionIndex) {
					description = cellValue;
				}
			}

			UserGroup userGroup = new UserGroup();
			userGroup.setName(name);
			userGroup.setDescription(description);
			userGroup.setActive(true);

			dataHandler.handleParsedUserGroup(userGroup);
		}
		dataHandler.endUserGroup();
	}

	private void handleUserSheet(Sheet sheet) {
		dataHandler.beginUser();
		Iterator<Row> rowIterator = sheet.iterator();

		int userNameIndex = 0;
		int passwordIndex = 1;
		int userGroupIndex = 2;
		int positionIndex = 3;
		int firstNameIndex = 4;
		int middleNameIndex = 5;
		int lastNameIndex = 6;
		int birthDateIndex = 7;
		int contactNoIndex = 8;
		int emailAddIndex = 9;
		int addressIndex = 10;

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if(row.getRowNum() < 1) {
				continue;
			}
			Iterator<Cell> cellIterator = row.cellIterator();
			User user = new User();
			String cellValue = null;

			boolean isFirstLineComplete = false;
			while (cellIterator.hasNext()) {
				if(!isFirstLineComplete) {
					isFirstLineComplete = true;
					continue;
				}
				Cell cell = (Cell) cellIterator.next();
				cellValue = getCellValue(cell);
				int cellIndex = cell.getColumnIndex();

				if(cellIndex == userNameIndex) {
					user.setUsername(cellValue);
				} else if(cellIndex == passwordIndex) {
					user.setPassword(cellValue);
				} else if(cellIndex == userGroupIndex) {
					UserGroup userGroup = new UserGroup();
					userGroup.setName(cellValue);
					user.setUserGroup(userGroup);
				} else if(cellIndex == positionIndex) {
					Position position = new Position();
					position.setName(cellValue);
					user.setPosition(position);
				} else if(cellIndex == firstNameIndex) {
					user.setFirstName(cellValue);
				} else if(cellIndex == middleNameIndex) {
					user.setMiddleName(cellValue);
				} else if(cellIndex == lastNameIndex) {
					user.setLastName(cellValue);
				} else if(cellIndex == contactNoIndex) {
					user.setContactNumber(cellValue);
				} else if(cellIndex == emailAddIndex) {
					user.setEmailAddress(cellValue);
				} else if(cellIndex == addressIndex) {
					user.setAddress(cellValue);
				} else if(cellIndex == birthDateIndex) {
					Date birthDate = eulap.common.util.DateUtil.parseDate(cellValue);
					user.setBirthDate(birthDate);
				}
			}

			dataHandler.handleParsedUser(user);
		}
		dataHandler.endUser();
	}

	private void handleTermSheet(Sheet sheet) {
		dataHandler.beginTerm();
		Iterator<Row> rowIterator = sheet.iterator();

		int currentIndex = 0;
		int nameIndex = currentIndex++;
		int daysIndex = currentIndex;

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if(row.getRowNum() < 1) {
				continue;
			}
			Iterator<Cell> cellIterator = row.cellIterator();
			Term term = new Term();
			String cellValue = null;

			boolean isFirstLineComplete = false;
			while (cellIterator.hasNext()) {
				if(!isFirstLineComplete) {
					isFirstLineComplete = true;
					continue;
				}
				Cell cell = (Cell) cellIterator.next();
				cellValue = getCellValue(cell);
				int cellIndex = cell.getColumnIndex();

				if(cellIndex == nameIndex) {
					term.setName(cellValue);
				} else if(cellIndex == daysIndex) {
					String strDays = formatNumberToString(cellValue);
					int days = isNumeric(strDays) ? Integer.valueOf(strDays) : 0;
					term.setDays(days);
				}
			}

			term.setActive(true);

			dataHandler.handleParsedTerm(term);
		}
		dataHandler.endTerm();
	}

	private void handleTimePeriodSheet(Sheet sheet) {
		dataHandler.beginTimePeriod();
		Iterator<Row> rowIterator = sheet.iterator();

		int currentIndex = 0;
		int nameIndex = currentIndex++;
		int dateFromIndex = currentIndex++;
		int dateToIndex = currentIndex++;
		int statusIndex = currentIndex;

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if(row.getRowNum() < 1) {
				continue;
			}
			Iterator<Cell> cellIterator = row.cellIterator();
			TimePeriod timePeriod = new TimePeriod();
			String cellValue = null;

			boolean isFirstLineComplete = false;
			while (cellIterator.hasNext()) {
				if(!isFirstLineComplete) {
					isFirstLineComplete = true;
					continue;
				}
				Cell cell = (Cell) cellIterator.next();
				cellValue = getCellValue(cell);
				if(cellValue.isEmpty()) {
					continue;
				}
				int cellIndex = cell.getColumnIndex();

				if(cellIndex == nameIndex) {
					timePeriod.setName(cellValue);
				} else if(cellIndex == dateFromIndex) {
					Date dateFrom = eulap.common.util.DateUtil.parseDate(cellValue);
					timePeriod.setDateFrom(dateFrom);
				} else if(cellIndex == dateToIndex) {
					Date dateTo = eulap.common.util.DateUtil.parseDate(cellValue);
					timePeriod.setDateTo(dateTo);
				} else if(cellIndex == statusIndex) {
					TimePeriodStatus timePeriodStatus = new TimePeriodStatus();
					timePeriodStatus.setName(cellValue);
					timePeriod.setPeriodStatus(timePeriodStatus);
				}
			}

			dataHandler.handleParsedTimePeriod(timePeriod);
		}
		dataHandler.endTimePeriod();
	}

	private void handleCustomerSheet(Sheet sheet) {
		dataHandler.beginCustomer();
		Iterator<Row> rowIterator = sheet.iterator();
		// Column number in excel template.
		int typeIndex = 0;
		int nameIndex = 1;
		int firstNameIndex = 2;
		int middleNameIndex = 3;
		int lastNameIndex = 4;
		int stBrgyIndex = 5;
		int cityProvinceInex = 6;
		int accountNameIndex = 7;
		int termIndex = 8;
		int tinIndex = 9;
		int contactPersonIndex = 10;
		int contactNumberIndex = 11;
		int emailAddressIndex = 12;
		int creditLimitIndex = 13;

		ArCustomer arCustomer = null;
		ArCustomerAccount arCustomerAccount = null;
		Term term = null;
		BusinessClassification busClassification = null;
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if(row.getRowNum() < 1) {
				continue;
			}
			Iterator<Cell> cellIterator = row.cellIterator();
			arCustomer = new ArCustomer();
			String cellValue = null;

			boolean isFirstLineComplete = false;
			while (cellIterator.hasNext()) {
				if(!isFirstLineComplete) {
					isFirstLineComplete = true;
					continue;
				}
				Cell cell = (Cell) cellIterator.next();
				cellValue = getCellValue(cell);
				if(cellValue.isEmpty()) {
					continue;
				}
				int cellIndex = cell.getColumnIndex();
				if (cellIndex == typeIndex) {
					busClassification = new BusinessClassification();
					busClassification.setName(cellValue);
					arCustomer.setBusinessClassification(busClassification);
				} else if (cellIndex == nameIndex) {
					arCustomer.setName(cellValue);
				} else if (cellIndex == firstNameIndex) {
					arCustomer.setFirstName(cellValue);
				} else if (cellIndex == middleNameIndex) {
					arCustomer.setMiddleName(cellValue);
				} else if (cellIndex == lastNameIndex) {
					arCustomer.setLastName(cellValue);
				} else if (cellIndex == stBrgyIndex) {
					arCustomer.setStreetBrgy(cellValue);
				} else if (cellIndex == cityProvinceInex) {
					arCustomer.setCityProvince(cellValue);
				} else if (cellIndex == accountNameIndex) {
					dataHandler.beginCustomerAccount();
					arCustomerAccount = new ArCustomerAccount();
					arCustomerAccount.setName(cellValue);
				} else if (cellIndex == termIndex) {
					term = new Term();
					term.setName(cellValue);
				} else if (cellIndex == tinIndex) {
					arCustomer.setTin(cellValue);
				} else if (cellIndex == contactPersonIndex) {
					arCustomer.setContactPerson(cellValue);
				} else if (cellIndex == contactNumberIndex) {
					arCustomer.setContactNumber(cellValue);
				} else if (cellIndex == emailAddressIndex) {
					arCustomer.setEmailAddress(cellValue);
				} else if (cellIndex == creditLimitIndex) {
					arCustomer.setMaxAmount(Double.valueOf(cellValue));
					dataHandler.handleParsedCustomer(arCustomer);
				}
			}

			if (arCustomer != null) {
				arCustomerAccount.setArCustomer(arCustomer);
				arCustomerAccount.setActive(true);
				arCustomerAccount.setTerm(term);
				dataHandler.handleParsedCustomerAccount(arCustomerAccount);
			}
			dataHandler.endCustomerAccount();
		}
		dataHandler.endCustomer();
	}

	private String getCellValue(Cell cell) {
		String cellValue = "";
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				cellValue = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if(DateUtil.isCellDateFormatted(cell)) {
					cellValue = eulap.common.util
							.DateUtil.formatDate(cell.getDateCellValue());
				} else {
					Double numVal = cell.getNumericCellValue();
					BigDecimal bdNumeric = new BigDecimal(numVal.toString());
					cellValue = bdNumeric.toPlainString();
				}
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				cellValue = cell.getBooleanCellValue() + "";
			case Cell.CELL_TYPE_BLANK:
				break;
		}
		return cellValue;
	}

	/**
	 * Function that converts the string double value to integer.
	 * This is to remove .0 from the string.
	 * @param cellValue The cell value to be converted.
	 * @return Converted integer value.
	 */
	private String formatNumberToString(String cellValue) {
		if (isNumeric(cellValue)) {
			Double dblValue = Double.parseDouble(cellValue);
			return String.valueOf(dblValue.intValue());
		}
		return cellValue;
	}

	private boolean isNumeric (String strNumber) {
		try {
			double value = Double.parseDouble(strNumber);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	public static void main(String args[]) {
		InputStream inputStream = null;

		MigrationDataHandler handler = new MigrationDataHandler() {

			@Override
			public void initDataHandler() {
			}

			@Override
			public void end() {
				
			}
			
			@Override
			public void handleParsedItem(Item item) {
				System.out.println(item);
				System.out.println("REORDERING POINT: " + item.getReorderingPoint() + "\n");

				List<ItemSrp> srps = item.getItemSrps();
				if (srps != null) {
					for (ItemSrp srp : srps) {
						System.out.println("SRP Selling Price: " + srp.getSellingPrice());
						System.out.println();
					}
				}

				List<ItemDiscount> discounts = item.getItemDiscounts();
				if (discounts != null) {
					for (ItemDiscount id : discounts) {
						System.out.println("Discount Name : " + id.getName());
						System.out.println("Discount Value: " + id.getValue());

						ItemDiscountType idt = id.getItemDiscountType();
						System.out.println("Discount Type: " + idt.getName());
						System.out.println();
					}
				}
				List<ItemAddOn> addOns = item.getItemAddOns();
				if (addOns != null) {
					for (ItemAddOn ad : addOns) {
						System.out.println("AddOn Name : " + ad.getName());
						System.out.println("AddOn Value: " + ad.getValue());

						ItemAddOnType adt = ad.getItemAddOnType();
						System.out.println("AddOn Type: " + adt.getName());
						System.out.println();
					}
				}
			}

			@Override
			public void endItem() {

			}

			@Override
			public void beginItem() {

			}

			@Override
			public void beginSupplier() {

			}

			@Override
			public void handleParsedSupplier(Supplier supplier) {
				System.out.println(supplier);
			}

			@Override
			public void endSupplier() {

			}

			@Override
			public void beginCompany() {

			}

			@Override
			public void handleParsedCompany(Company company) {
				System.out.println(company);
			}

			@Override
			public void endCompany() {

			}

			@Override
			public void beginDivision() {

			}

			@Override
			public void handleParsedDivision(Division division) {
				System.out.println(division);
			}

			@Override
			public void endDivision() {

			}

			@Override
			public void beginAccountType() {

			}

			@Override
			public void handleParsedAccountType(AccountType accountType) {
				System.out.println(accountType);
			}

			@Override
			public void endAccountType() {

			}

			@Override
			public void beginAccount() {

			}

			@Override
			public void handleParsedAccount(Account account) {
				System.out.println(account);
			}

			@Override
			public void endAccount() {

			}

			@Override
			public void beginUserPosition() {
			}

			@Override
			public void handleParsedUserPosition(Position position) {
				System.out.println(position);
			}

			@Override
			public void endUserPosition() {
			}

			@Override
			public void beginUserGroup() {

			}

			@Override
			public void handleParsedUserGroup(UserGroup userGroup) {
				System.out.println(userGroup);
			}

			@Override
			public void endUserGroup() {

			}

			@Override
			public void beginUser() {

			}

			@Override
			public void handleParsedUser(User user) {
				System.out.println(user);
			}

			@Override
			public void endUser() {

			}

			@Override
			public void beginTerm() {

			}

			@Override
			public void handleParsedTerm(Term term) {
				System.out.println(term);
			}

			@Override
			public void endTerm() {

			}

			@Override
			public void beginTimePeriod() {

			}

			@Override
			public void handleParsedTimePeriod(TimePeriod timePeriod) {
				System.out.println(timePeriod);
			}

			@Override
			public void endTimePeriod() {

			}

			@Override
			public void beginCustomer() {

			}

			@Override
			public void beginCustomerAccount() {
				
			}
			
			@Override
			public void beginCustomerBalance() {
				
			}
			@Override
			public void endCustomerAccount() {
			}
			
			@Override
			public void endCustomerBalance() {
				
			}
			@Override
			public void handleParsedCustomer(ArCustomer customer) {
				System.out.println(customer);
			}
			@Override
			public void handleParsedCustomerAccount(
					ArCustomerAccount customerAccount) {
				System.out.println(customerAccount);
			}

			@Override
			public void handleParsedCustomerBalance(ArTransaction arTransaction) {
				System.out.println(arTransaction);
			}
			@Override
			public void endCustomer() {

			}
			
			@Override
			public void beginSupplierAccount() {
				
			}

			@Override
			public void handleParsedSupplierAccount(
					SupplierAccount supplierAccount) {
				
			}

			@Override
			public void endSupplierAccount() {
				
			}

			@Override
			public void beginSupplierBeginingBalance() {
				
			}

			@Override
			public void endSupplierBeginningBalance() {
				
			}

			@Override
			public void handleParsedSupplierBeginningBalance(APInvoice apInvoice) {
				
			}
		};

		ExcelMigrationReader reader = new ExcelMigrationReader(handler);
		try {
			// File file = new File("migration_template.xls");
			// In my local computer it cannot find the file migration_template.xls even I place it
			// the same directory as the class.
			File file = new File("../eb-fa/src/eulap/eb/service/migration/migration_template.xls");
			inputStream = new FileInputStream(file);
			reader.readFile(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
		}
	}
}
