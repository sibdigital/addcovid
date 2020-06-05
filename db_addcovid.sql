create database addcovid;

create table cls_department
(
	id integer not null
		constraint cls_department_pkey
			primary key,
	name varchar(255),
	description text,
	status_import integer default 0,
	time_import timestamp
)
;

alter table cls_department owner to postgres
;

create table cls_organization
(
	id serial not null
		constraint cls_organization_pkey
			primary key,
	name text,
	short_name varchar(255),
	inn varchar(12),
	ogrn varchar(15),
	address_jur varchar(255),
	okved_add text,
	okved text,
	email varchar(100),
	phone varchar(100),
	status_import integer default 0,
	time_import timestamp,
	hash_code text
)
;

alter table cls_organization owner to postgres
;

create table doc_request
(
	id serial not null
		constraint doc_request_pkey
			primary key,
	person_office_cnt integer,
	person_remote_cnt integer,
	person_slry_save_cnt integer,
	id_organization integer not null
		constraint fk_org
			references cls_organization,
	id_department integer not null
		constraint doc_request_cls_department_id_fk
			references cls_department,
	attachment_path varchar(255),
	status_review integer default 0,
	time_create timestamp,
	status_import integer default 0,
	time_import timestamp,
	time_review timestamp,
    req_basis text default '',
    is_agree boolean ,
    is_protect boolean,
    org_hash_code text,
    reject_comment text,
    old_department_id integer
)
;

alter table doc_request owner to postgres
;

create index fki_organization
	on doc_request (id_organization)
;

create table doc_person
(
	id serial not null
		constraint doc_person_pk
			primary key,
	id_request integer not null
		constraint doc_person_doc_request_id_fk
			references doc_request
				on delete cascade,
	lastname varchar(100),
	firstname varchar(100),
	patronymic varchar(100),
	/*is_agree boolean*/
	status_import integer default 0,
	time_import timestamp
)
;

alter table doc_person owner to postgres
;

create index fki_request
	on doc_person (id_request)
;

create table doc_address_fact
(
	id serial not null
		constraint doc_address_fact_pk
			primary key,
	address_fact varchar(255),
	person_office_fact_cnt integer,
	id_request integer not null
		constraint fk_req_addr
			references doc_request
)
;

alter table doc_address_fact owner to postgres
;

create index fki_request_addr
	on doc_address_fact (id_request)
;

create table dep_user
(
	id serial not null
		constraint dep_user_pk
			primary key,
	id_department integer not null
		constraint dep_user_cls_department_id_fk
			references cls_department,
	lastname varchar(100),
	firstname varchar(100),
	patronymic varchar(100),
	login varchar(100) not null,
	password varchar(100) not null
)
;

alter table dep_user owner to postgres
;

create unique index fki_dep_user_login
	on dep_user (login)
;

create table dep_user
(
    id serial not null
        constraint dep_user_pk
            primary key,
    id_department integer not null
        constraint dep_user_cls_department_id_fk
            references cls_department,

    lastname varchar(100) not null,
    firstname varchar(100) not null,
    patronymic varchar(100),

    login varchar(100) not null,
    password varchar(100) not null
);

create unique index fki_dep_user_login
    on dep_user (login);

alter table dep_user owner to postgres;


alter table cls_organization alter column ogrn type varchar(15);
alter table  doc_request alter column attachment_path type varchar(512);
alter table doc_person add column status_import integer default 0;
alter table doc_person add column time_import timestamp;

create table if not exists doc_dacha
(
    id serial not null
        constraint doc_dacha_pk
            primary key,
    district varchar(255), --район дачи
    address text,        -- ДНТ/СНТ, населенный пункт дачи
    valid_date TIMESTAMP WITHOUT TIME ZONE, --дата действия
    link varchar(255),
    raion varchar(100), -- район убытия
    naspunkt text, -- населенный пункт убытия
    is_agree boolean,
    is_protect boolean,
    time_create timestamp,
    status_import integer,
    time_import timestamp,
    status_review integer,
    time_review timestamp,
    reject_comment text,
    phone varchar(100),
    email varchar(100)
);

alter table doc_dacha owner to postgres;

create table if not exists doc_dacha_person
(
    id serial not null
        constraint doc_dacha_addr_pk
            primary key,
    id_doc_dacha integer not null
        constraint doc_dacha_addr_doc_dacha_id_fk
            references doc_dacha
            on delete cascade,
    lastname varchar(100),
    firstname varchar(100),
    patronymic varchar(100),
    age integer
);

-- create table if not exists doc_dacha_addr
-- (
--     id serial not null
--         constraint doc_dacha_addr_pk
--             primary key,
--     id_doc_dacha integer not null
--         constraint doc_dacha_addr_doc_dacha_id_fk
--             references doc_dacha
--             on delete cascade,
--     district varchar(255),
--     address text
-- );

-- alter table doc_dacha_addr owner to postgres;

create or replace view v_doc_person_and_org_info as (
    select pers.*,  org.inn, org.short_name from (
                                                     select *
                                                     from doc_person as dp
                                                 ) as pers
                                                     inner join ( select
                                                                      dr.id as id_request, co.inn, co.short_name
                                                                  from doc_request dr
                                                                           inner join cls_organization as co on dr.id_organization = co.id
                                                         where dr.status_review = 1
    ) as org using (id_request)
);

CREATE TABLE public.reg_statistic (
    id serial not null
    constraint reg_statistic_pk
    primary key,
    lastname character varying(100),
    firstname character varying(100),
    patronymic character varying(100),
    inn character varying(15),
    reg_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    additional_info text,
    results integer NOT NULL
);

ALTER TABLE public.reg_statistic OWNER TO postgres;

create table if not exists cls_excel
(
    id serial not null
        constraint cls_excel_pkey
            primary key,
    name text,
    status integer,
    description text,
    time_upload timestamp not null
)
;

alter table cls_excel owner to postgres
;

alter table  doc_request alter column attachment_path type text;

create unique index fki_dep_user_login
    on dep_user (login);

alter table dep_user owner to postgres;

alter table doc_request add column id_reassigned_user integer;

alter table doc_request
    add constraint doc_request_reassigned_user_id_fk
        foreign key (id_reassigned_user) references dep_user;

alter table doc_request add column id_processed_user integer;

alter table doc_request
    add constraint doc_request_processed_user_id_fk
        foreign key (id_processed_user) references dep_user;

alter table  doc_request add  column id_type_request integer;
alter table  cls_organization add  column id_type_request integer;

create table reg_history_request
(
    id                   serial  not null
        constraint reg_history_request_pkey
            primary key,
    person_office_cnt    integer,
    person_remote_cnt    integer,
    person_slry_save_cnt integer,
    id_organization      integer not null
        constraint fk_reg_history_request_org
            references cls_organization,
    id_department        integer not null
        constraint reg_history_request_cls_department_id_fk
            references cls_department,
    attachment_path      text,
    status_review        integer default 0,
    time_create          timestamp,
    status_import        integer default 0,
    time_import          timestamp,
    time_review          timestamp,
    req_basis            text    default ''::text,
    is_agree             boolean,
    is_protect           boolean,
    org_hash_code        text,
    reject_comment       text,
    old_department_id    integer,
    id_processed_user    integer
        constraint reg_history_request_processed_user_id_fk
            references dep_user,
    id_reassigned_user   integer
        constraint reg_history_request_reassigned_user_id_fk
            references dep_user,
    id_type_request      integer,
    id_doc_request       integer,
    id_user              integer
        constraint reg_history_request_id_user_fk
            references dep_user,
    reg_time             timestamp not null default current_timestamp
);

create table if not exists cls_type_request(
    id integer not null
	    constraint cls_type_request_pkey
			primary key, --соответствует нынешщнему idTypeRequest
    activity_kind   text, --вид деятельности для вывода в заголовок формы
    id_department        integer
        constraint rcls_type_request_department_id_fk
            references cls_department, --министерство по умолчанию в выпадающий список министерств
    prescription text, --текст предприсания роспотребнадзора, форматированный в хтмл
    prescription_link text, --ссылка на файл с предписанием, пока прозапас
    settings text, --JSON с настройками, пока прозапас на будущее через него можно будет передавать видимость элементов если что.
    status_registration integer not null default 0-- статус регистрации 0 - закрыта, 1 - используется для регистрации
);

alter table cls_type_request add column status_visible int default 0; -- отображение в списк
alter table cls_type_request add column begin_registration timestamp; -- начало возможности проверки
alter table cls_type_request add column end_registration timestamp; -- завершение возможности проверки
alter table cls_type_request add column begin_visible timestamp; --  начало возможности видимости
alter table cls_type_request add column end_visible timestamp;  -- завершение возможности видимости
alter table cls_type_request add column sort_weight integer default 0;  --для сортировки

alter table doc_request add additional_attributes jsonb; -- дополнительные аттрибуты