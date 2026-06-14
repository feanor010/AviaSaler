create table users
(
    id           uuid primary key     default gen_random_uuid(),
    email        varchar     not null unique,
    display_name varchar,
    enabled      bool        not null default true,
    created_at   timestamptz not null default NOW(),
    updated_at   timestamptz not null default NOW()
);

create table auth_identity
(
    id               uuid primary key     default gen_random_uuid(),
    user_id          uuid not null references users (id) on delete cascade,
    provider         varchar(20) not null,
    provider_user_id varchar     not null,
    password_hash    varchar,
    created_at       timestamptz not null default NOW(),
    updated_at       timestamptz not null default NOW(),
    unique (provider, provider_user_id)
);
create index auth_identity_user_id_idx on auth_identity (user_id);

create table roles
(
    id   uuid primary key default gen_random_uuid(),
    name varchar not null unique
);

create table user_roles
(
    user_id uuid references users (id) on delete cascade,
    role_id uuid references roles (id),
    primary key (user_id, role_id)
);

insert into roles (name) values ('ROLE_USER'), ('ROLE_ADMIN');
