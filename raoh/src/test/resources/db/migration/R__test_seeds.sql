INSERT INTO students(name, status)
VALUES
    ('Alice', 'ACTIVE'),
    ('Bob', 'INACTIVE'),
    ('Charlie', 'ACTIVE'),
    ('David', 'INACTIVE'),
    ('Eve', 'ACTIVE');

INSERT INTO courses(name, description)
VALUES
    ('Mathematics', 'Basic Mathematics Course'),
    ('Science', 'Basic Science Course'),
    ('History', 'World History Course'),
    ('Literature', 'English Literature Course'),
    ('Art', 'Art Appreciation Course');

INSERT INTO job_offers(title, description, offer_expire_date, offer_type,
    settlement_method, number_of_workers, budget_type, budget_lower_bound, budget_upper_bound)
VALUES
    ('Web Development Project', 'Build a corporate website', '2026-06-01', 'PROJECT',
     'FIXED', 3, 'RANGE', 100000, 500000);

INSERT INTO job_offers(title, description, offer_expire_date, offer_type,
    rate_per_task_unit, number_of_task_units, limit_task_units_type, limit_task_units_value)
VALUES
    ('Data Entry Task', 'Process survey responses', '2026-04-15', 'TASK',
     100, 1000, 'LIMITED', 50);

INSERT INTO job_offers(title, description, offer_expire_date, offer_type,
    contract_price_type)
VALUES
    ('Logo Design Competition', 'Design a company logo', '2026-05-01', 'COMPETITION',
     'STANDARD');

INSERT INTO tours(tour_code, name, capacity)
VALUES
    ('T001', 'Tokyo City Tour', 20),
    ('T002', 'Kyoto Temple Walk', 15),
    ('T003', 'Osaka Food Tour', 10);

INSERT INTO customers(name, email)
VALUES
    ('Tanaka Taro', 'tanaka@example.com'),
    ('Suzuki Hanako', 'suzuki@example.com');