--liquibase formatted sql
--changeset chenbaining:3
create table if not exists payment
(
    id          varchar(50)                         not null
        constraint payment_pk
            primary key,
    uid         varchar(32)                         not null,
    type        varchar                             not null,
    cash        integer   default 0                 not null,
    point       integer   default 0                 not null,
    status      varchar                             not null,
    channel     varchar                             not null,
    purchase_id varchar                             not null,
    related_to  varchar,
    meta        json,
    created_at  timestamp default CURRENT_TIMESTAMP not null,
    updated_at  timestamp default CURRENT_TIMESTAMP,
    finished_at timestamp
);

comment on table payment is '资金表';

comment on column payment.id is '流水号（交易号）';

comment on column payment.uid is '用户编号';

comment on column payment.type is '类型
income（充值）
redeem(兑换)
refund（退款）
spending（消费）';

comment on column payment.cash is '金额';

comment on column payment.point is '点数';

comment on column payment.status is '交易状态';

comment on column payment.channel is '渠道(wechat、alipay)';

comment on column payment.purchase_id is '订单表编号';

comment on column payment.related_to is '关联流水号';

comment on column payment.meta is 'platform、ip、os、user_snapshot';

comment on column payment.created_at is '创建时间';

comment on column payment.updated_at is '修改时间';

comment on column payment.finished_at is '完成时间';

create table if not exists purchase
(
    id          varchar(50)                         not null
        constraint order_pk
            primary key,
    uid         varchar(32)                         not null,
    payment_id  varchar(50)                         not null
        constraint order_pk_2
            unique,
    sku_id      varchar(50)                         not null,
    amount      integer                             not null,
    shipped     integer   default 0                 not null,
    product_id  varchar,
    status      varchar                             not null,
    created_at  timestamp default CURRENT_TIMESTAMP not null,
    updated_at  timestamp default CURRENT_TIMESTAMP,
    finished_at timestamp
);

comment on table purchase is '订单表';

comment on column purchase.id is '订单id';

comment on column purchase.uid is '用户id';

comment on column purchase.payment_id is '资金表id';

comment on column purchase.sku_id is 'sku表id';

comment on column purchase.amount is '数量';

comment on column purchase.shipped is '完成数量';

comment on column purchase.product_id is 'task表id 或 payment表id';

comment on column purchase.status is '状态(pending、succeed、 cancelled)';

comment on column purchase.created_at is '创建时间';

comment on column purchase.updated_at is '修改时间';

comment on column purchase.finished_at is '完成时间';

create table if not exists sku
(
    id         varchar(50)                         not null
        constraint sku_pk
            primary key,
    sku        varchar(50)                         not null,
    revision   integer                             not null,
    name       varchar(20)                         not null,
    price      integer                             not null,
    type       varchar                             not null,
    slogan     varchar                             not null,
    status     varchar                             not null,
    props      json,
    created_at timestamp default CURRENT_TIMESTAMP not null
);

comment on table sku is 'sku表';

comment on column sku.id is '编号';

comment on column sku.sku is 'sku';

comment on column sku.revision is '版本号';

comment on column sku.name is '名称';

comment on column sku.price is '价格(发布后不可修改)';

comment on column sku.type is 'sku类型
point-gift(赠点)
point-pack(点数套餐)
批量换脸';

comment on column sku.slogan is '点数描述';

comment on column sku.status is '状态
上架-enable
下架-disable';

comment on column sku.props is '目前存放points(integer)点数信息,点数有效期：expiration_period(integer)以天数为单位';

comment on column sku.created_at is '创建时间';

ALTER TABLE admin_user
    ALTER COLUMN "status" TYPE varchar(20);

--preconditions onFail:CONTINUE onError:CONTINUE
INSERT INTO sku(id, sku, revision, name, price, type, slogan, status, props)
VALUES ('N811bmpX5', 'x5q0GLdd55', 1, '点数赠送套餐', 0, 'point-gift', '赠送', 'enable', '{"points":10,"expiration_period": 365}');

--preconditions onFail:CONTINUE onError:CONTINUE
INSERT INTO admin_user(id, username, password, nickname, status)
VALUES ('101010', 'admin', 'passwd', '系统管理员', 'enable');