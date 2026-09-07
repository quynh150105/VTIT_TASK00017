create table users (
    id bigint not null auto_increment,
    username varchar(100) not null,
    email varchar(150) not null,
    phone varchar(20),
    password_hash varchar(255) not null,
    full_name varchar(150),
    avatar_url varchar(255),
    role varchar(20) not null,
    status varchar(20) not null,
    last_login_at datetime,
    created_at datetime not null,
    updated_at datetime not null,
    primary key (id),
    unique key uk_users_username (username),
    unique key uk_users_email (email),
    unique key uk_users_phone (phone)
);

create table password_reset_tokens (
    id bigint not null auto_increment,
    user_id bigint not null,
    channel varchar(20) not null,
    destination varchar(255) not null,
    token_hash varchar(255) not null,
    expires_at datetime not null,
    used_at datetime,
    created_at datetime not null,
    primary key (id),
    constraint fk_password_reset_tokens_user foreign key (user_id) references users (id)
);

create table wallets (
    id bigint not null auto_increment,
    user_id bigint not null,
    name varchar(100) not null,
    wallet_type varchar(30) not null,
    currency_code varchar(3) not null,
    opening_balance decimal(19,2) not null,
    current_balance decimal(19,2) not null,
    target_amount decimal(19,2),
    target_date date,
    is_default boolean not null,
    status varchar(20) not null,
    created_at datetime not null,
    updated_at datetime not null,
    primary key (id),
    constraint fk_wallets_user foreign key (user_id) references users (id)
);

create table categories (
    id bigint not null auto_increment,
    user_id bigint,
    parent_id bigint,
    name varchar(100) not null,
    category_type varchar(20) not null,
    icon varchar(50),
    color varchar(30),
    is_system boolean not null,
    is_active boolean not null,
    created_at datetime not null,
    updated_at datetime not null,
    primary key (id),
    constraint fk_categories_user foreign key (user_id) references users (id),
    constraint fk_categories_parent foreign key (parent_id) references categories (id)
);

create table transactions (
    id bigint not null auto_increment,
    user_id bigint not null,
    wallet_id bigint not null,
    category_id bigint not null,
    transfer_wallet_id bigint,
    transaction_type varchar(20) not null,
    amount decimal(19,2) not null,
    currency_code varchar(3) not null,
    transaction_date date not null,
    title varchar(150) not null,
    note text,
    payment_method varchar(30) not null,
    status varchar(20) not null,
    created_at datetime not null,
    updated_at datetime not null,
    primary key (id),
    constraint fk_transactions_user foreign key (user_id) references users (id),
    constraint fk_transactions_wallet foreign key (wallet_id) references wallets (id),
    constraint fk_transactions_category foreign key (category_id) references categories (id),
    constraint fk_transactions_transfer_wallet foreign key (transfer_wallet_id) references wallets (id)
);

create table transaction_attachments (
    id bigint not null auto_increment,
    transaction_id bigint not null,
    file_name varchar(255) not null,
    file_url varchar(255) not null,
    file_type varchar(100),
    file_size bigint,
    uploaded_at datetime not null,
    primary key (id),
    constraint fk_transaction_attachments_transaction foreign key (transaction_id) references transactions (id)
);

create table budgets (
    id bigint not null auto_increment,
    user_id bigint not null,
    category_id bigint,
    name varchar(100) not null,
    limit_amount decimal(19,2) not null,
    currency_code varchar(3) not null,
    start_date date not null,
    end_date date not null,
    period_type varchar(20) not null,
    status varchar(20) not null,
    created_at datetime not null,
    updated_at datetime not null,
    primary key (id),
    constraint fk_budgets_user foreign key (user_id) references users (id),
    constraint fk_budgets_category foreign key (category_id) references categories (id)
);

create table saving_goals (
    id bigint not null auto_increment,
    user_id bigint not null,
    wallet_id bigint,
    name varchar(100) not null,
    target_amount decimal(19,2) not null,
    current_amount decimal(19,2) not null,
    currency_code varchar(3) not null,
    target_date date,
    status varchar(20) not null,
    created_at datetime not null,
    updated_at datetime not null,
    primary key (id),
    constraint fk_saving_goals_user foreign key (user_id) references users (id),
    constraint fk_saving_goals_wallet foreign key (wallet_id) references wallets (id)
);

create table goal_contributions (
    id bigint not null auto_increment,
    goal_id bigint not null,
    transaction_id bigint,
    amount decimal(19,2) not null,
    contribution_date date not null,
    note text,
    created_at datetime not null,
    primary key (id),
    constraint fk_goal_contributions_goal foreign key (goal_id) references saving_goals (id),
    constraint fk_goal_contributions_transaction foreign key (transaction_id) references transactions (id)
);

create table report_exports (
    id bigint not null auto_increment,
    user_id bigint not null,
    report_type varchar(30) not null,
    from_date date not null,
    to_date date not null,
    file_url varchar(255),
    status varchar(20) not null,
    created_at datetime not null,
    completed_at datetime,
    primary key (id),
    constraint fk_report_exports_user foreign key (user_id) references users (id)
);
