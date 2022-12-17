-- auto-generated definition
create table admin_oplog
(
    id         varchar(10) not null,
    uid        varchar(10) not null,
    code       integer,
    comment    varchar(255),
    op_ext_1   varchar,
    op_ext_2   varchar,
    created_at timestamp(6) default CURRENT_TIMESTAMP,
    updated_at timestamp(6) default CURRENT_TIMESTAMP
);

comment on table admin_oplog is '管理员日志表';

comment on column admin_oplog.id is '管理员日志id';

comment on column admin_oplog.uid is '用户id';

comment on column admin_oplog.comment is '内容';

comment on column admin_oplog.created_at is '创建时间';

comment on column admin_oplog.updated_at is '更新时间';

alter table admin_oplog
    owner to admin;

create unique index admin_oplog_id_uindex
    on admin_oplog (id);

-- auto-generated definition
create table admin_user
(
    id         varchar(10)  not null,
    username   varchar(255) not null,
    password   varchar(266) not null,
    nickname   varchar(255),
    status     integer      default 1,
    created_at timestamp(6) default CURRENT_TIMESTAMP,
    updated_at timestamp(6) default CURRENT_TIMESTAMP
);

comment on table admin_user is '管理员用户表';

comment on column admin_user.id is '管理员用户id';

comment on column admin_user.username is '用户名';

comment on column admin_user.password is '密码';

comment on column admin_user.nickname is '用户昵称';

comment on column admin_user.status is '用户状态 默认为1
1--启动
2--禁用
';

comment on column admin_user.created_at is '创建时间';

comment on column admin_user.updated_at is '更新时间';

alter table admin_user
    owner to admin;

create unique index admin_user_id_uindex
    on admin_user (id);

-- auto-generated definition
create table api_app
(
    id         varchar(10)                         not null
        constraint api_app_pk
            primary key,
    uid        varchar(10)                         not null,
    name       varchar                             not null,
    app_key    varchar,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);

alter table api_app
    owner to admin;

create unique index api_app_id_uindex
    on api_app (id);

-- auto-generated definition
create table api_apply
(
    id          varchar(10)                         not null
        constraint api_apply_pk
            primary key,
    uid         varchar(10)                         not null,
    status      integer                             not null,
    req_note    varchar                             not null,
    review_note varchar,
    created_at  timestamp default CURRENT_TIMESTAMP not null,
    updated_at  timestamp default CURRENT_TIMESTAMP not null
);

comment on table api_apply is 'api申请';

comment on column api_apply.uid is '用户id';

comment on column api_apply.status is '状态';

comment on column api_apply.req_note is '申请人的申请说明';

comment on column api_apply.review_note is '审批时的审批说明';

alter table api_apply
    owner to admin;

create unique index api_apply_id_uindex
    on api_apply (id);

-- auto-generated definition
create table api_token
(
    id         varchar(10)                         not null
        constraint api_token_pk
            primary key,
    app_id     bigint                              not null,
    secret_key varchar                             not null,
    status     integer                             not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);

comment on column api_token.secret_key is '密钥';

comment on column api_token.status is '状态
active
disabled
expired';

alter table api_token
    owner to admin;

create unique index api_token_id_uindex
    on api_token (id);

-- auto-generated definition
create table asset
(
    id             varchar(20) not null,
    owner          varchar,
    owner_id       varchar(10),
    type           integer,
    bundle         varchar,
    path           varchar(255),
    status         integer      default 1,
    last_access_at timestamp(6),
    created_at     timestamp(6) default CURRENT_TIMESTAMP,
    updated_at     timestamp(6) default CURRENT_TIMESTAMP
);

comment on table asset is '资产表';

comment on column asset.owner is '物主 user, api_app';

comment on column asset.owner_id is '物主id';

comment on column asset.type is '类型
1--face-source
2--face-target
3--matting-image
4--matting-output
5--face-output
6--system-matting-scene
7--system-face-source
8--system-matting-source';

comment on column asset.bundle is 'ticket_id, task_id, ...';

comment on column asset.path is '/{data, app}/c0/$owner_id/assets/$type/$date/$bundle';

comment on column asset.status is '状态 1--启用 2--禁用 3--逻辑删除';

comment on column asset.last_access_at is '最后访问时间';

comment on column asset.created_at is '创建时间';

comment on column asset.updated_at is '更新时间';

alter table asset
    owner to admin;

create unique index asset_id_uindex
    on asset (id);

-- auto-generated definition
create table payment_record
(
    id            varchar(10)                         not null
        constraint payment_record_pk
            primary key,
    uid           varchar(10)                         not null,
    amount        integer                             not null,
    status        integer                             not null,
    channel       varchar                             not null,
    channel_ext_1 varchar,
    channel_ext_2 varchar,
    created_at    timestamp default CURRENT_TIMESTAMP not null,
    updated_at    timestamp default CURRENT_TIMESTAMP not null
);

comment on table payment_record is '支付记录';

comment on column payment_record.uid is '用户id';

comment on column payment_record.amount is '总计';

comment on column payment_record.status is 'success,penging,cancelled,failed，……';

comment on column payment_record.channel is '渠道';

comment on column payment_record.created_at is '创建时间';

comment on column payment_record.updated_at is '创建时间';

alter table payment_record
    owner to admin;

create unique index payment_record_id_uindex
    on payment_record (id);

-- auto-generated definition
create table point_balance
(
    id         varchar(10)                         not null
        constraint point_balance_pk
            primary key,
    uid        varchar(10)                         not null,
    balance    integer                             not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);

comment on table point_balance is '点数余额';

comment on column point_balance.balance is '余额';

comment on column point_balance.created_at is '创建时间';

comment on column point_balance.updated_at is '更新时间';

alter table point_balance
    owner to admin;

create unique index point_balance_id_uindex
    on point_balance (id);

-- auto-generated definition
create table point_record
(
    id         varchar(10)                         not null
        constraint point_record_pk
            primary key,
    tid        varchar                             not null,
    uid        varchar(10)                         not null,
    amount     integer                             not null,
    op_type    integer                             not null,
    op_ext_1   varchar,
    op_ext_2   varchar,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);

comment on table point_record is '点数记录';

comment on column point_record.tid is '事务id';

comment on column point_record.uid is '用户id';

comment on column point_record.amount is '金额';

comment on column point_record.op_type is '操作类型
payment
trial
task';

comment on column point_record.op_ext_1 is '扩展字段 pay_id';

comment on column point_record.op_ext_2 is '扩展字段 sku_id';

comment on column point_record.created_at is '创建时间';

comment on column point_record.updated_at is '更新时间';

alter table point_record
    owner to admin;

create unique index point_record_tid_uindex
    on point_record (tid);

create unique index point_record_id_uindex
    on point_record (id);

-- auto-generated definition
create table point_sku
(
    id         varchar(10)                         not null
        constraint point_sku_pk
            primary key,
    name       varchar(30)                         not null,
    note       varchar(200),
    price      integer                             not null,
    pt_value   integer                             not null,
    status     integer                             not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);

comment on table point_sku is '点数sku';

comment on column point_sku.name is '商品名称';

comment on column point_sku.note is '备注';

comment on column point_sku.price is '金额
1RMB=100Point';

comment on column point_sku.pt_value is '点数值';

comment on column point_sku.status is '状态
1 --> 启用
2 --> 禁用';

comment on column point_sku.created_at is '创建时间';

comment on column point_sku.updated_at is '创建时间';

alter table point_sku
    owner to admin;

create unique index point_sku_id_uindex
    on point_sku (id);

-- auto-generated definition
create table task
(
    id          varchar(20) not null,
    owner       varchar(255),
    owner_id    varchar(32),
    type        integer,
    status      integer,
    payload     json,
    details     json,
    started_at  timestamp(6),
    finished_at timestamp(6),
    pt_log_id   bigint,
    created_at  timestamp(6) default CURRENT_TIMESTAMP,
    updated_at  timestamp(6) default CURRENT_TIMESTAMP
);

comment on table task is '任务表';

comment on column task.owner is '物主';

comment on column task.owner_id is '物主id';

comment on column task.type is '类型
0--face-swap
1--matting-image';

comment on column task.status is '状态
0--创建
1--分配
2--成功
3--停止
-1--移除';

comment on column task.payload is '有效载荷';

comment on column task.details is '状态详情';

comment on column task.started_at is '开始时间';

comment on column task.finished_at is '结束时间';

comment on column task.created_at is '创建时间';

comment on column task.updated_at is '更新时间';

alter table task
    owner to admin;

create unique index task_id_uindex
    on task (id);

-- auto-generated definition
create table user_extra
(
    id            varchar(32) not null
        constraint user_extra_pk
            primary key,
    country       varchar(255),
    channel       varchar(255),
    channel_ext_1 varchar(255),
    channel_ext_2 varchar(255),
    register_ip   varchar,
    last_login_at timestamp(6),
    created_at    timestamp(6) default CURRENT_TIMESTAMP,
    updated_at    timestamp(6) default CURRENT_TIMESTAMP
);

comment on table user_extra is '用户扩展表';

comment on column user_extra.id is '用户扩展id';

comment on column user_extra.country is '国家';

comment on column user_extra.channel is '渠道 支付宝 微信';

comment on column user_extra.channel_ext_1 is '渠道扩展字段1';

comment on column user_extra.channel_ext_2 is '渠道扩展字段2';

comment on column user_extra.register_ip is 'ip地址';

comment on column user_extra.last_login_at is '最后登陆时间';

comment on column user_extra.created_at is '创建时间';

comment on column user_extra.updated_at is '更新时间';

alter table user_extra
    owner to admin;

create unique index user_extra_id_uindex
    on user_extra (id);


-- auto-generated definition
create table user_info
(
    id           varchar(32)                         not null
        constraint user_pk
            primary key,
    phone        varchar(11),
    email        varchar,
    email_status integer,
    password     varchar,
    nickname     varchar,
    avatar       varchar,
    status       integer                             not null,
    created_at   timestamp default CURRENT_TIMESTAMP not null,
    updated_at   timestamp default CURRENT_TIMESTAMP not null
);

comment on table user_info is '用户表';

comment on column user_info.email_status is '邮箱状态
1 -> 激活
2 -> 待激活';

comment on column user_info.password is '登陆密码';

comment on column user_info.nickname is '用户昵称';

comment on column user_info.avatar is '头像url';

comment on column user_info.status is '用户状态
1 -> 启用
2 -> 禁用';

comment on column user_info.created_at is '创建时间';

comment on column user_info.updated_at is '更新时间';

alter table user_info
    owner to admin;

create unique index user_id_uindex
    on user_info (id);


