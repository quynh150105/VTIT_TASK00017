alter table password_reset_tokens
    add column attempt_count int not null default 0;

alter table users
    add column password_changed_at datetime;

alter table wallets
    add column version bigint not null default 0;
