
-- Description	: Insert script to add ALPHANUMERIC_TAX_CODE default data

INSERT INTO BIR_ATC (NAME, DESCRIPTION, ACTIVE) VALUES
('WI 010', 'Professional fees (Lawyers, CPA''s, Engineers, etc.) - if the gross income for the current year did not exceed P3M', 1),
('WI 011', 'Professional fees (Lawyers, CPA''s, Engineers, etc.) - if gross income is more than 3M or VAT', 1),
('WI 050', 'Management and technical consultants - if the gross income for the current year did not exceed P3M', 1),
('WI 051', 'Management and technical consultants - if gross income is more than 3M or VAT registered regardless of amount', 1),
('WC 050', 'Management and technical consultants - if gross income for the current year did not exceed P720,000', 1),
('WC 051', 'Management and technical consultants - if gross income exceeds P720,000', 1);