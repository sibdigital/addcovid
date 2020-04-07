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
	ogrn varchar(13),
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
    reject_comment: text,
    old_department_id: integer
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
	patronymic varchar(100)/*,
	is_agree boolean*/
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
