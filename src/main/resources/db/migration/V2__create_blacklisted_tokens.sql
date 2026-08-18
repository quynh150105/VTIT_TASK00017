create table blacklisted_tokens (
    id bigint not null auto_increment,
    token_hash varchar(64) not null,
    expires_at datetime(6) not null,
    primary key (id),
    unique key uk_blacklisted_tokens_hash (token_hash)
);
