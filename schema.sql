
CREATE SCHEMA poc_schm;

create table poc_schm.user_recs
(
    user_id            serial primary key,
    first_name         varchar(20),
    last_name          varchar(20),
    email              varchar(100),
    city               varchar(100),
    state              varchar(100),
    country            varchar(50),
    zipcode            varchar(10),
    date_of_birth      date
);