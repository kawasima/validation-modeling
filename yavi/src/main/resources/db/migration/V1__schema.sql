CREATE TABLE students (
    student_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE courses (
    course_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE enrollments (
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);


CREATE TABLE job_offers (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    offer_expire_date DATE NOT NULL,
    offer_type VARCHAR(20) NOT NULL,

    -- PROJECT: settlement
    settlement_method VARCHAR(20),
    number_of_workers BIGINT,

    -- PROJECT/FIXED: budget
    budget_type VARCHAR(20),
    budget_lower_bound BIGINT,
    budget_upper_bound BIGINT,
    budget_limit BIGINT,

    -- PROJECT/PER_HOUR
    hourly_rate VARCHAR(30),
    work_hours_per_week BIGINT,
    offer_duration VARCHAR(40),

    -- TASK
    rate_per_task_unit BIGINT,
    number_of_task_units BIGINT,
    limit_task_units_type VARCHAR(20),
    limit_task_units_value BIGINT,

    -- COMPETITION
    contract_price_type VARCHAR(20),
    contract_price_value BIGINT
);

CREATE TABLE tours (
    tour_id SERIAL PRIMARY KEY,
    tour_code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    capacity INT NOT NULL
);

CREATE TABLE customers (
    customer_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

CREATE TABLE reservations (
    reservation_id SERIAL PRIMARY KEY,
    tour_id INT NOT NULL,
    customer_id INT NOT NULL,
    adult_count INT NOT NULL,
    child_count INT NOT NULL,
    remarks VARCHAR(80),
    FOREIGN KEY (tour_id) REFERENCES tours(tour_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);