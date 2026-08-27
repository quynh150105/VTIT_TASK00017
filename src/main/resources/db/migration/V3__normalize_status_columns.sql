alter table users modify status varchar(20) not null;
alter table wallets modify status varchar(20) not null;
alter table transactions modify status varchar(20) not null;
alter table budgets modify status varchar(20) not null;
alter table saving_goals modify status varchar(20) not null;
alter table report_exports modify status varchar(20) not null;
