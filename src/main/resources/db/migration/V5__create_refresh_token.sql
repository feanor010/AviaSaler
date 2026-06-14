create table refresh_token(
    id UUID primary key DEFAULT gen_random_uuid(),
    user_id UUID not null references users(id) on delete cascade,
    token_hash varchar(64) not null unique,
    expires_at timestamptz not null,
    revoked boolean not null default false,
    created_at timestamptz not null default now()
);

create index refresh_token_user_id_idx on refresh_token(user_id);