# DATABASE TABLES

- created_at
- updated_at

## Users

### user_info

- id
- uid: int, optimus(id)
- phone
- email
- email_status
- password
- nickname
- avatar
- status

### user_extra

- uid
- country
- channel
- channel_ext_1
- channel_ext_2
- register_ip
- last_login_at

## Assets

### asset

- id
- asset_id: string
- owner: user, api_app, ...
- owner_id
- type: face-source, face-target, matting-background
- bundle: ticket_id, task_id, ...
- path: `/{data, app}/c0/$owner_id/assets/$type/$date/$bundle`
- status
- last_access_at

## Tasks

### task

- id
- task_id: string
- owner
- owner_id
- type: face-swap-batch, matting, matting-preview?
- status
- payload: json
- details: json
- started_at
- finished_at
- pt_log_id

## Payments & Points

### point_sku

- id
- sku_id: string
- name
- note
- price: int, 100 = 1RMB
- pt_value
- status

### point_record

- id
- tid: string
- uid
- amount
- op_type, payment, trial, task ...
- op_ext_1 =? pay_id
- op_ext_2 =? sku_id

### payment_record

- id
- uid
- amount
- status: success, pending, cancelled, failed, ...
- channel
- channel_ext_1
- channel_ext_2

### point_balance

- id
- uid
- balance

## API

### api_apply:

- id
- uid
- status
- req_note
- review_note

### api_app

- id
- uid
- name
- app_key

### api_token

- id
- app_id
- secret_key
- status: active, disabled, expired ...

### api_req_log

- id
- request_id
- ...

## Admin

### admin_user

- id
- username
- password
- nickname
- status

### admin_oplog

- id
- uid
- code
- comment
- op_ext_1
- op_ext_2
