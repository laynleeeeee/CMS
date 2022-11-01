-- Description: mysql views for AR Line Analysis


drop view if exists V_AR_LINE_ANALYSIS;

create view V_AR_LINE_ANALYSIS as
select 'AR Transaction' as SOURCE,
	concat('ART-', AL.AR_LINE_ID) as ID,
	1 as SOURCE_ID,
	ART.SEQUENCE_NO as SEQ_NO,
	FW.CURRENT_STATUS_ID as STATUS_ID,
	FS.DESCRIPTION as STATUS,
	FW.IS_COMPLETE as COMPLETE,
	ACA.COMPANY_ID as COMPANY_ID,
	AL.AR_LINE_SETUP_ID as AL_SETUP_ID,
	COALESCE(AL.UNITOFMEASUREMENT_ID, 0) as UOM_ID,
	ART.TRANSACTION_DATE as RECEIPT_DATE,
	ART.GL_DATE as MATURITY_DATE,
	ART.CUSTOMER_ID as CUSTOMER_ID,
	ART.CUSTOMER_ACCOUNT_ID as CUSTOMER_ACCT_ID,
	ART.TRANSACTION_DATE as REF_DATE,
	ART.TRANSACTION_NUMBER as REF_NUMBER,
	AC.NAME as CUSTOMER,
	ACA.NAME as CUSTOMER_ACCT,
	COALESCE(AL.QUANTITY, 0) as QUANTITY,
	COALESCE(AL.UP_AMOUNT, 0) as UNIT_PRICE,
	AL.AMOUNT as AMOUNT,
	0 AS VAT_AMOUNT from AR_LINE AL
inner join AR_TRANSACTION ART on ART.AR_TRANSACTION_ID = AL.AR_TRANSACTION_ID
inner join AR_CUSTOMER AC on AC.AR_CUSTOMER_ID = ART.CUSTOMER_ID
inner join AR_CUSTOMER_ACCOUNT ACA on ACA.AR_CUSTOMER_ACCOUNT_ID = ART.CUSTOMER_ACCOUNT_ID
inner join FORM_WORKFLOW FW on FW.FORM_WORKFLOW_ID = ART.FORM_WORKFLOW_ID
inner join FORM_STATUS FS on FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID
where ART.AR_TRANSACTION_TYPE_ID != 4 and ART.AR_TRANSACTION_TYPE_ID != 5
and ART.AR_TRANSACTION_TYPE_ID != 7 and ART.AR_TRANSACTION_TYPE_ID != 8
and ART.AR_TRANSACTION_TYPE_ID != 9

-- TODO : change this to SERVICE_SETTING
-- union all

--  select 'AR Miscellaneous' as SOURCE,
-- 	concat('ARM-', AML.AR_MISCELLANEOUS_LINE_ID) as ID,
-- 	2 as SOURCE_ID,
-- 	ARM.SEQUENCE_NO as SEQ_NO,
-- 	FW.CURRENT_STATUS_ID as STATUS_ID,
-- 	FS.DESCRIPTION as STATUS,
-- 	FW.IS_COMPLETE as COMPLETE,
-- 	ACA.COMPANY_ID as COMPANY_ID,
-- 	AML.AR_LINE_SETUP_ID as AL_SETUP_ID,
-- 	COALESCE(AML.UNITOFMEASUREMENT_ID, 0) as UOM_ID,
-- 	ARM.RECEIPT_DATE as RECEIPT_DATE,
-- 	ARM.MATURITY_DATE as MATURITY_DATE,
-- 	ARM.AR_CUSTOMER_ID as CUSTOMER_ID,
-- 	ARM.AR_CUSTOMER_ACCOUNT_ID as CUSTOMER_ACCT_ID,
-- 	ARM.RECEIPT_DATE as REF_DATE,
-- 	ARM.RECEIPT_NUMBER as REF_NUMBER,
-- 	AC.NAME as CUSTOMER,
-- 	ACA.NAME as CUSTOMER_ACCT,
-- 	COALESCE(AML.QUANTITY, 0) as QUANTITY,
-- 	COALESCE(AML.UP_AMOUNT, 0) as UNIT_PRICE,
-- 	AML.AMOUNT as AMOUNT,
-- 	0 AS VAT_AMOUNT from AR_MISCELLANEOUS_LINE AML
-- inner join AR_MISCELLANEOUS ARM on ARM.AR_MISCELLANEOUS_ID = AML.AR_MISCELLANEOUS_ID
-- inner join AR_CUSTOMER AC on AC.AR_CUSTOMER_ID = ARM.AR_CUSTOMER_ID
-- inner join AR_CUSTOMER_ACCOUNT ACA on ACA.AR_CUSTOMER_ACCOUNT_ID = ARM.AR_CUSTOMER_ACCOUNT_ID
-- inner join FORM_WORKFLOW FW on FW.FORM_WORKFLOW_ID = ARM.FORM_WORKFLOW_ID
-- inner join FORM_STATUS FS on FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID

union all

select 'Account Sales' as SOURCE,
	concat('AS-', AL.AR_LINE_ID) as ID,
	3 as SOURCE_ID,
	ART.SEQUENCE_NO as SEQ_NO,
	FW.CURRENT_STATUS_ID as STATUS_ID,
	FS.DESCRIPTION as STATUS,
	FW.IS_COMPLETE as COMPLETE,
	ACA.COMPANY_ID as COMPANY_ID,
	AL.AR_LINE_SETUP_ID as AL_SETUP_ID,
	COALESCE(AL.UNITOFMEASUREMENT_ID, 0) as UOM_ID,
	ART.TRANSACTION_DATE as RECEIPT_DATE,
	ART.DUE_DATE as MATURITY_DATE,
	ART.CUSTOMER_ID as CUSTOMER_ID,
	ART.CUSTOMER_ACCOUNT_ID as CUSTOMER_ACCT_ID,
	ART.TRANSACTION_DATE as REF_DATE,
	ART.TRANSACTION_NUMBER as REF_NUMBER,
	AC.NAME as CUSTOMER,
	ACA.NAME as CUSTOMER_ACCT,
	COALESCE(AL.QUANTITY, 0) as QUANTITY,
	COALESCE(AL.UP_AMOUNT, 0) as UNIT_PRICE,
	AL.AMOUNT as AMOUNT,
	0 AS VAT_AMOUNT from AR_LINE AL
inner join AR_TRANSACTION ART on ART.AR_TRANSACTION_ID = AL.AR_TRANSACTION_ID
inner join AR_CUSTOMER AC on AC.AR_CUSTOMER_ID = ART.CUSTOMER_ID
inner join AR_CUSTOMER_ACCOUNT ACA on ACA.AR_CUSTOMER_ACCOUNT_ID = ART.CUSTOMER_ACCOUNT_ID
inner join FORM_WORKFLOW FW on FW.FORM_WORKFLOW_ID = ART.FORM_WORKFLOW_ID
inner join FORM_STATUS FS on FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID
where ART.AR_TRANSACTION_TYPE_ID = 4

union all

select 'Cash Sales' as SOURCE,
	concat('CS-', CSAL.CASH_SALE_AR_LINE_ID) as ID,
	4 as SOURCE_ID,
	CS.CS_NUMBER as SEQ_NO,
	FW.CURRENT_STATUS_ID as STATUS_ID,
	FS.DESCRIPTION as STATUS,
	FW.IS_COMPLETE as COMPLETE,
	CS.COMPANY_ID as COMPANY_ID,
	CSAL.AR_LINE_SETUP_ID as AL_SETUP_ID,
	COALESCE(CSAL.UNITOFMEASUREMENT_ID, 0) as UOM_ID,
	CS.RECEIPT_DATE as RECEIPT_DATE,
	CS.MATURITY_DATE as MATURITY_DATE,
	CS.AR_CUSTOMER_ID as CUSTOMER_ID,
	CS.AR_CUSTOMER_ACCOUNT_ID as CUSTOMER_ACCT_ID,
	CS.RECEIPT_DATE as REF_DATE,
	CS.SALE_INVOICE_NO as REF_NUMBER,
	AC.NAME as CUSTOMER,
	ACA.NAME as CUSTOMER_ACCT,
	COALESCE(CSAL.QUANTITY, 0) as QUANTITY,
	COALESCE(CSAL.UP_AMOUNT, 0) as UNIT_PRICE,
	CSAL.AMOUNT + (COALESCE(CSAL.QUANTITY, 0) * COALESCE(CSAL.VAT_AMOUNT, 0)) as AMOUNT,
	0 AS VAT_AMOUNT from CASH_SALE_AR_LINE CSAL
inner join CASH_SALE CS on CS.CASH_SALE_ID = CSAL.CASH_SALE_ID
inner join AR_CUSTOMER AC on AC.AR_CUSTOMER_ID = CS.AR_CUSTOMER_ID
inner join AR_CUSTOMER_ACCOUNT ACA on ACA.AR_CUSTOMER_ACCOUNT_ID = CS.AR_CUSTOMER_ACCOUNT_ID
inner join FORM_WORKFLOW FW on FW.FORM_WORKFLOW_ID = CS.FORM_WORKFLOW_ID
inner join FORM_STATUS FS on FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID
where CS.CASH_SALE_TYPE_ID = 1

union all

select 'Paid in Advance Delivery' as SOURCE,
	concat('CAPD-', CAPDAL.CAP_DELIVERY_AR_LINE_ID) as ID,
	5 as SOURCE_ID,
	CAPD.CAPD_NUMBER as SEQ_NO,
	FW.CURRENT_STATUS_ID as STATUS_ID,
	FS.DESCRIPTION as STATUS,
	FW.IS_COMPLETE as COMPLETE,
	CAPD.COMPANY_ID as COMPANY_ID,
	CAPDAL.AR_LINE_SETUP_ID as AL_SETUP_ID,
	COALESCE(CAPDAL.UNITOFMEASUREMENT_ID, 0) as UOM_ID,
	CAP.RECEIPT_DATE as RECEIPT_DATE,
	CAP.MATURITY_DATE as MATURITY_DATE,
	CAPD.AR_CUSTOMER_ID as CUSTOMER_ID,
	CAPD.AR_CUSTOMER_ACCOUNT_ID as CUSTOMER_ACCT_ID,
	CAP.RECEIPT_DATE as REF_DATE,
	CAPD.SALES_INVOICE_NO as REF_NUMBER,
	AC.NAME as CUSTOMER,
	ACA.NAME as CUSTOMER_ACCT,
	COALESCE(CAPDAL.QUANTITY, 0) as QUANTITY,
	COALESCE(CAPDAL.UP_AMOUNT, 0) as UNIT_PRICE,
	CAPDAL.AMOUNT as AMOUNT,
	0 AS VAT_AMOUNT from CAP_DELIVERY_AR_LINE CAPDAL
inner join CAP_DELIVERY CAPD on CAPD.CAP_DELIVERY_ID = CAPDAL.CAP_DELIVERY_ID
inner join CUSTOMER_ADVANCE_PAYMENT CAP on CAP.CUSTOMER_ADVANCE_PAYMENT_ID = CAPD.CUSTOMER_ADVANCE_PAYMENT_ID
inner join AR_CUSTOMER AC on AC.AR_CUSTOMER_ID = CAPD.AR_CUSTOMER_ID
inner join AR_CUSTOMER_ACCOUNT ACA on ACA.AR_CUSTOMER_ACCOUNT_ID = CAPD.AR_CUSTOMER_ACCOUNT_ID
inner join FORM_WORKFLOW FW on FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID
inner join FORM_STATUS FS on FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID
where CAPD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 1

union all

select 'Account Collection' as SOURCE,
	concat('AC-', ACARLINE.AC_AR_LINE_ID) as ID,
	6 as SOURCE_ID,
	ARR.REF_NUMBER as SEQ_NO,
	FW.CURRENT_STATUS_ID as STATUS_ID,
	FS.DESCRIPTION as STATUS,
	FW.IS_COMPLETE as COMPLETE,
	ARR.COMPANY_ID as COMPANY_ID,
	ACARLINE.AR_LINE_SETUP_ID as AL_SETUP_ID,
	COALESCE(ACARLINE.UNITOFMEASUREMENT_ID, 0) as UOM_ID,
	ARR.RECEIPT_DATE as RECEIPT_DATE,
	ARR.MATURITY_DATE as MATURITY_DATE,
	ARR.AR_CUSTOMER_ID as CUSTOMER_ID,
	ARR.AR_CUSTOMER_ACCOUNT_ID as CUSTOMER_ACCT_ID,
	ARR.RECEIPT_DATE as REF_DATE,
	ARR.RECEIPT_NUMBER as REF_NUMBER,
	AC.NAME as CUSTOMER,
	ACA.NAME as CUSTOMER_ACCT,
	COALESCE(ACARLINE.QUANTITY, 0) as QUANTITY,
	COALESCE(ACARLINE.UP_AMOUNT, 0) as UNIT_PRICE,
	ACARLINE.AMOUNT as AMOUNT,
	0 AS VAT_AMOUNT from AC_AR_LINE ACARLINE
inner join AR_RECEIPT ARR on ARR.AR_RECEIPT_ID = ACARLINE.AR_RECEIPT_ID
inner join AR_CUSTOMER AC on AC.AR_CUSTOMER_ID = ARR.AR_CUSTOMER_ID
inner join AR_CUSTOMER_ACCOUNT ACA on ACA.AR_CUSTOMER_ACCOUNT_ID = ARR.AR_CUSTOMER_ACCOUNT_ID
inner join FORM_WORKFLOW FW on FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID
inner join FORM_STATUS FS on FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID

union all

select 'Cash Sales - IS' as SOURCE,
	concat('CS-IS-', CSAL.CASH_SALE_AR_LINE_ID) as ID,
	9 as SOURCE_ID,
	CSW.CS_NUMBER as SEQ_NO,
	FW.CURRENT_STATUS_ID as STATUS_ID,
	FS.DESCRIPTION as STATUS,
	FW.IS_COMPLETE as COMPLETE,
	CSW.COMPANY_ID as COMPANY_ID,
	CSAL.AR_LINE_SETUP_ID as AL_SETUP_ID,
	COALESCE(CSAL.UNITOFMEASUREMENT_ID, 0) as UOM_ID,
	CSW.RECEIPT_DATE as RECEIPT_DATE,
	CSW.MATURITY_DATE as MATURITY_DATE,
	CSW.AR_CUSTOMER_ID as CUSTOMER_ID,
	CSW.AR_CUSTOMER_ACCOUNT_ID as CUSTOMER_ACCT_ID,
	CSW.RECEIPT_DATE as REF_DATE,
	CSW.SALE_INVOICE_NO as REF_NUMBER,
	AC.NAME as CUSTOMER,
	ACA.NAME as CUSTOMER_ACCT,
	COALESCE(CSAL.QUANTITY, 0) as QUANTITY,
	COALESCE(CSAL.UP_AMOUNT, 0) as UNIT_PRICE,
	CSAL.AMOUNT as AMOUNT,
	0 AS VAT_AMOUNT from CASH_SALE_AR_LINE CSAL
inner join CASH_SALE CSW on CSW.CASH_SALE_ID = CSAL.CASH_SALE_ID
inner join AR_CUSTOMER AC on AC.AR_CUSTOMER_ID = CSW.AR_CUSTOMER_ID
inner join AR_CUSTOMER_ACCOUNT ACA on ACA.AR_CUSTOMER_ACCOUNT_ID = CSW.AR_CUSTOMER_ACCOUNT_ID
inner join FORM_WORKFLOW FW on FW.FORM_WORKFLOW_ID = CSW.FORM_WORKFLOW_ID
inner join FORM_STATUS FS on FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID
where CSW.CASH_SALE_TYPE_ID = 3

union all

select 'Account Sales - IS' as SOURCE,
	concat('AS-IS-', AL.AR_LINE_ID) as ID,
	10 as SOURCE_ID,
	ART.SEQUENCE_NO as SEQ_NO,
	FW.CURRENT_STATUS_ID as STATUS_ID,
	FS.DESCRIPTION as STATUS,
	FW.IS_COMPLETE as COMPLETE,
	ACA.COMPANY_ID as COMPANY_ID,
	AL.AR_LINE_SETUP_ID as AL_SETUP_ID,
	COALESCE(AL.UNITOFMEASUREMENT_ID, 0) as UOM_ID,
	ART.TRANSACTION_DATE as RECEIPT_DATE,
	ART.DUE_DATE as MATURITY_DATE,
	ART.CUSTOMER_ID as CUSTOMER_ID,
	ART.CUSTOMER_ACCOUNT_ID as CUSTOMER_ACCT_ID,
	ART.TRANSACTION_DATE as REF_DATE,
	ART.TRANSACTION_NUMBER as REF_NUMBER,
	AC.NAME as CUSTOMER,
	ACA.NAME as CUSTOMER_ACCT,
	COALESCE(AL.QUANTITY, 0) as QUANTITY,
	COALESCE(AL.UP_AMOUNT, 0) as UNIT_PRICE,
	AL.AMOUNT as AMOUNT,
	0 AS VAT_AMOUNT from AR_LINE AL
inner join AR_TRANSACTION ART on ART.AR_TRANSACTION_ID = AL.AR_TRANSACTION_ID
inner join AR_CUSTOMER AC on AC.AR_CUSTOMER_ID = ART.CUSTOMER_ID
inner join AR_CUSTOMER_ACCOUNT ACA on ACA.AR_CUSTOMER_ACCOUNT_ID = ART.CUSTOMER_ACCOUNT_ID
inner join FORM_WORKFLOW FW on FW.FORM_WORKFLOW_ID = ART.FORM_WORKFLOW_ID
inner join FORM_STATUS FS on FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID
where ART.AR_TRANSACTION_TYPE_ID = 10

union all

select 'Processing Report' as SOURCE,
	concat('PR-', OC.PR_OTHER_CHARGE_ID) as ID,
	11 as SOURCE_ID,
	PR.SEQUENCE_NO as SEQ_NO,
	FW.CURRENT_STATUS_ID as STATUS_ID,
	FS.DESCRIPTION as STATUS,
	FW.IS_COMPLETE as COMPLETE,
	PR.COMPANY_ID as COMPANY_ID,
	OC.AR_LINE_SETUP_ID as AL_SETUP_ID,
	COALESCE(OC.UNITOFMEASUREMENT_ID, 0) as UOM_ID,
	PR.DATE as RECEIPT_DATE,
	PR.DATE as MATURITY_DATE,
	-1 as CUSTOMER_ID,
	-1 as CUSTOMER_ACCT_ID,
	PR.DATE as REF_DATE,
	PR.REF_NUMBER as REF_NUMBER,
	'' as CUSTOMER,
	'' as CUSTOMER_ACCT,
	COALESCE(OC.QUANTITY, 0) as QUANTITY,
	COALESCE(OC.UP_AMOUNT, 0) as UNIT_PRICE,
	OC.AMOUNT as AMOUNT,
	0 AS VAT_AMOUNT from PR_OTHER_CHARGE OC
inner join PROCESSING_REPORT PR on PR.PROCESSING_REPORT_ID = OC.PROCESSING_REPORT_ID
inner join FORM_WORKFLOW FW on FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID
inner join FORM_STATUS FS on FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID

union all

select 'Cash Sales Return' as SOURCE,
	concat('CSR-', CSAL.CASH_SALE_RETURN_AR_LINE_ID) as ID,
	12 as SOURCE_ID,
	CSR.CSR_NUMBER as SEQ_NO,
	FW.CURRENT_STATUS_ID as STATUS_ID,
	FS.DESCRIPTION as STATUS,
	FW.IS_COMPLETE as COMPLETE,
	CSR.COMPANY_ID as COMPANY_ID,
	CSAL.AR_LINE_SETUP_ID as AL_SETUP_ID,
	COALESCE(CSAL.UNITOFMEASUREMENT_ID, 0) as UOM_ID,
	CSR.DATE as RECEIPT_DATE,
	CSR.DATE as MATURITY_DATE,
	CSR.AR_CUSTOMER_ID as CUSTOMER_ID,
	CSR.AR_CUSTOMER_ACCOUNT_ID as CUSTOMER_ACCT_ID,
	CSR.DATE as REF_DATE,
	CSR.SALE_INVOICE_NO as REF_NUMBER,
	AC.NAME as CUSTOMER,
	ACA.NAME as CUSTOMER_ACCT,
	COALESCE(CSAL.QUANTITY, 0) as QUANTITY,
	COALESCE(CSAL.UP_AMOUNT, 0) as UNIT_PRICE,
	CSAL.AMOUNT + (COALESCE(CSAL.QUANTITY, 0) * COALESCE(CSAL.VAT_AMOUNT, 0)) as AMOUNT,
	0 AS VAT_AMOUNT from CASH_SALE_RETURN_AR_LINE CSAL
inner join CASH_SALE_RETURN CSR on CSR.CASH_SALE_RETURN_ID = CSAL.CASH_SALE_RETURN_ID
inner join AR_CUSTOMER AC on AC.AR_CUSTOMER_ID = CSR.AR_CUSTOMER_ID
inner join AR_CUSTOMER_ACCOUNT ACA on ACA.AR_CUSTOMER_ACCOUNT_ID = CSR.AR_CUSTOMER_ACCOUNT_ID
inner join FORM_WORKFLOW FW on FW.FORM_WORKFLOW_ID = CSR.FORM_WORKFLOW_ID
inner join FORM_STATUS FS on FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID
where CSR.CASH_SALE_TYPE_ID = 1

union all

select 'Account Sales Return' as SOURCE,
	concat('ASR-', AL.AR_LINE_ID) as ID,
	13 as SOURCE_ID,
	ART.SEQUENCE_NO as SEQ_NO,
	FW.CURRENT_STATUS_ID as STATUS_ID,
	FS.DESCRIPTION as STATUS,
	FW.IS_COMPLETE as COMPLETE,
	ACA.COMPANY_ID as COMPANY_ID,
	AL.AR_LINE_SETUP_ID as AL_SETUP_ID,
	COALESCE(AL.UNITOFMEASUREMENT_ID, 0) as UOM_ID,
	ART.TRANSACTION_DATE as RECEIPT_DATE,
	ART.TRANSACTION_DATE as MATURITY_DATE,
	ART.CUSTOMER_ID as CUSTOMER_ID,
	ART.CUSTOMER_ACCOUNT_ID as CUSTOMER_ACCT_ID,
	ART.TRANSACTION_DATE as REF_DATE,
	ART.TRANSACTION_NUMBER as REF_NUMBER,
	AC.NAME as CUSTOMER,
	ACA.NAME as CUSTOMER_ACCT,
	COALESCE(AL.QUANTITY, 0) as QUANTITY,
	COALESCE(AL.UP_AMOUNT, 0) as UNIT_PRICE,
	AL.AMOUNT as AMOUNT,
	0 AS VAT_AMOUNT from AR_LINE AL
inner join AR_TRANSACTION ART on ART.AR_TRANSACTION_ID = AL.AR_TRANSACTION_ID
inner join AR_CUSTOMER AC on AC.AR_CUSTOMER_ID = ART.CUSTOMER_ID
inner join AR_CUSTOMER_ACCOUNT ACA on ACA.AR_CUSTOMER_ACCOUNT_ID = ART.CUSTOMER_ACCOUNT_ID
inner join FORM_WORKFLOW FW on FW.FORM_WORKFLOW_ID = ART.FORM_WORKFLOW_ID
inner join FORM_STATUS FS on FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID
where ART.AR_TRANSACTION_TYPE_ID = 5

union all

select 'Paid in Advance Delivery - IS' as SOURCE,
	concat('CAPD-IS-', CAPDAL.CAP_DELIVERY_AR_LINE_ID) as ID,
	14 as SOURCE_ID,
	CAPD.CAPD_NUMBER as SEQ_NO,
	FW.CURRENT_STATUS_ID as STATUS_ID,
	FS.DESCRIPTION as STATUS,
	FW.IS_COMPLETE as COMPLETE,
	CAPD.COMPANY_ID as COMPANY_ID,
	CAPDAL.AR_LINE_SETUP_ID as AL_SETUP_ID,
	COALESCE(CAPDAL.UNITOFMEASUREMENT_ID, 0) as UOM_ID,
	CAP.RECEIPT_DATE as RECEIPT_DATE,
	CAP.MATURITY_DATE as MATURITY_DATE,
	CAPD.AR_CUSTOMER_ID as CUSTOMER_ID,
	CAPD.AR_CUSTOMER_ACCOUNT_ID as CUSTOMER_ACCT_ID,
	CAP.RECEIPT_DATE as REF_DATE,
	CAPD.SALES_INVOICE_NO as REF_NUMBER,
	AC.NAME as CUSTOMER,
	ACA.NAME as CUSTOMER_ACCT,
	COALESCE(CAPDAL.QUANTITY, 0) as QUANTITY,
	COALESCE(CAPDAL.UP_AMOUNT, 0) as UNIT_PRICE,
	CAPDAL.AMOUNT as AMOUNT,
	0 AS VAT_AMOUNT from CAP_DELIVERY_AR_LINE CAPDAL
inner join CAP_DELIVERY CAPD on CAPD.CAP_DELIVERY_ID = CAPDAL.CAP_DELIVERY_ID
inner join CUSTOMER_ADVANCE_PAYMENT CAP on CAP.CUSTOMER_ADVANCE_PAYMENT_ID = CAPD.CUSTOMER_ADVANCE_PAYMENT_ID
inner join AR_CUSTOMER AC on AC.AR_CUSTOMER_ID = CAPD.AR_CUSTOMER_ID
inner join AR_CUSTOMER_ACCOUNT ACA on ACA.AR_CUSTOMER_ACCOUNT_ID = CAPD.AR_CUSTOMER_ACCOUNT_ID
inner join FORM_WORKFLOW FW on FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID
inner join FORM_STATUS FS on FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID
where CAPD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 3

union all

select 'Paid in Advance Delivery - WIPSO' as SOURCE,
	concat('CAPD-', CAPDAL.CAP_DELIVERY_AR_LINE_ID) as ID,
	23 as SOURCE_ID,
	CAPD.CAPD_NUMBER as SEQ_NO,
	FW.CURRENT_STATUS_ID as STATUS_ID,
	FS.DESCRIPTION as STATUS,
	FW.IS_COMPLETE as COMPLETE,
	CAPD.COMPANY_ID as COMPANY_ID,
	CAPDAL.AR_LINE_SETUP_ID as AL_SETUP_ID,
	COALESCE(CAPDAL.UNITOFMEASUREMENT_ID, 0) as UOM_ID,
	AR.RECEIPT_DATE as RECEIPT_DATE,
	AR.MATURITY_DATE as MATURITY_DATE,
	CAPD.AR_CUSTOMER_ID as CUSTOMER_ID,
	CAPD.AR_CUSTOMER_ACCOUNT_ID as CUSTOMER_ACCT_ID,
	AR.RECEIPT_DATE as REF_DATE,
	CAPD.SALES_INVOICE_NO as REF_NUMBER,
	AC.NAME as CUSTOMER,
	ACA.NAME as CUSTOMER_ACCT,
	COALESCE(CAPDAL.QUANTITY, 0) as QUANTITY,
	COALESCE(CAPDAL.UP_AMOUNT, 0) as UNIT_PRICE,
	CAPDAL.AMOUNT as AMOUNT,
	0 AS VAT_AMOUNT from CAP_DELIVERY_AR_LINE CAPDAL
inner join CAP_DELIVERY CAPD on CAPD.CAP_DELIVERY_ID = CAPDAL.CAP_DELIVERY_ID
inner join CAP_DELIVERY_TRANSACTION CAPDT on CAPDT.CAP_DELIVERY_ID = CAPD.CAP_DELIVERY_ID
inner join AR_RECEIPT_TRANSACTION ART on ART.AR_TRANSACTION_ID = CAPDT.AR_TRANSACTION_ID
inner join AR_RECEIPT AR on AR.AR_RECEIPT_ID = ART.AR_RECEIPT_ID
inner join AR_CUSTOMER AC on AC.AR_CUSTOMER_ID = CAPD.AR_CUSTOMER_ID
inner join AR_CUSTOMER_ACCOUNT ACA on ACA.AR_CUSTOMER_ACCOUNT_ID = CAPD.AR_CUSTOMER_ACCOUNT_ID
inner join FORM_WORKFLOW FW on FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID
inner join FORM_STATUS FS on FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID
where CAPD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 5;