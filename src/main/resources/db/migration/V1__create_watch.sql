create table city
(
    id           uuid primary key default gen_random_uuid(),
    code         varchar(3)   not null unique,
    name         varchar(255) not null,
    country_code varchar(2)   not null
);

create table airport
(
    id        uuid primary key    default gen_random_uuid(),
    iata      varchar(3) not null unique,
    name      varchar(255),
    city_id   uuid       not null references city (id) on delete cascade,
    is_active boolean    not null default true
);

create table watch
(
    id                  uuid primary key     default gen_random_uuid(),
    origin_city_id      uuid        not null references city (id),
    destination_city_id uuid        not null references city (id),
    depart_date         date        not null,
    return_date         date,
    threshold_price     numeric(10, 2),
    currency            varchar(3)  not null default 'RUB',
    alert_on_any_drop   boolean     not null default false,
    last_notified_price numeric(10, 2),
    enabled             boolean     not null default true,
    created_at          timestamptz not null default now(),
    updated_at          timestamptz not null default now(),


    constraint watch_dates_chk check (return_date is null or return_date >= depart_date),
    constraint watch_route_chk check (origin_city_id <> destination_city_id)
);

create index watch_enabled_idx on watch (enabled) where enabled;
