CREATE VIEW view_income AS
SELECT income.id,
       income.date,
       family_members.first_name,
       family_members.last_name,
       income_sources.name AS income_source_name,
       income_categories.name AS income_category_name,
       income_subcategories.name AS income_subcategory_name,
       income.amount,
       income.description,
       income.status,
       income.comment
FROM income
         JOIN family_members ON income.family_member_id = family_members.id
         JOIN income_sources ON income.income_source_id = income_sources.id
         JOIN income_categories ON income_sources.income_category_id = income_categories.id
         JOIN income_subcategories ON income_categories.subcategory_id = income_categories.id;