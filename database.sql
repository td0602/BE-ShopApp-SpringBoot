CREATE DATABASE shopapp;
use shopapp;
-- khách hàng khi muốn mua hàng -> phải đăng ký tài khoản -> bảng users
-- Ở đây password có thể trống do có thể login từ facebook/google ... 
-- 
CREATE table users(
    id int primary key auto_increment,
    fullname varchar(100) default '', -- mục đích để '' để tránh exception NullPointerException do null khi lấy dữ liệu trong java
    phone_number varchar(10) not null,
    address varchar(200) default '', 
    password varchar(100) not null default '',
    created_at datetime,
    updated_at datetime,
    is_active tinyint(1) default 1,
    date_of_birth date,
    facebook_account_id int default 0,
    goole_account_id int default 0
);
alter table users add column role_id int;
-- Vai trò user
create table roles(
    id int primary key,
    name varchar(20) not null
);
alter table users add foreign key (role_id) references roles(id);
-- token là chuỗi ký tự rất dài --> từ token có thể trích xuất tt email, username, ...
-- khi users khi đăng nhập vào hệ thống có thể họ dc cấp token (như chìa khóa)
-- lần sau vào hệ thống thì dùng token chứ không dùng password
-- nếu muốn lấy thêm thông tin khác thì phải có token
create table tokens(
    id int primary key auto_increment,
    token varchar(255) unique not null,
    token_type varchar(50) not null,
    expiration_date datetime, -- ngày hết hạn tránh hacker
    revoked tinyint(1) not null,
    expired tinyint(1) not null,
    user_id int, -- FK
    foreign key (user_id) references users(id)
);
-- hỗ trợ đăng nhập từ Facebook và Google
create table social_accounts(
    id int primary key auto_increment,
    provider varchar(20) not null comment 'Tên nhà social network', -- chú thích khi xem mô tả bảng
    provider_id varchar(50) not null,
    email varchar(150) not null comment 'Email tài khoản',
    name varchar(100) not null comment 'Tên người dùng',
    user_id int,
    foreign key (user_id) references users(id)
);
-- Bảng danh mục sp
create table categories(
    id int primary key auto_increment,
    name varchar(100) not null default '' comment 'Tên danh mục'
);
-- Bảng chứa sản phẩm
create table products(
    id int primary key auto_increment,
    name varchar(350) comment 'Tên sản phẩm', 
    price float not null check(price >=0),
    thumbnail varchar(300) default '',
    description longtext default '',
    created_at datetime,
    updated_at datetime,
    category_id int,
    foreign key (category_id) references categories(id)
);
-- Bảng chứ danh sách các ảnh của products
create table product_images(
    id int primary key auto_increment,
    product_id int, 
    foreign key (product_id) references products(id),
    -- ràng buộc nếu product bị xóa thì product_images cũng bị xóa theo: cascade
    constraint fk_product_images_product_id
        foreign key (product_id) references products (id) on delete cascade,
    image_url varchar(300)
);
-- Bảng đặt hàng
-- tại sao bảng users có fullname/email ... rồi lại cần fullname ở đây? khi đặt hàng thông tin
-- đặt hàng có thể khác tên/email ...
create table orders(
    id int primary key auto_increment,
    user_id int,
    foreign key (user_id) references users(id),
    fullname varchar(100) default '', 
    email varchar(100) default '', 
    phone_number varchar(20) not null,
    address varchar(200) not null,
    note varchar(100) default '',
    order_date datetime default current_timestamp,
    status varchar(20), 
    total_money float check(total_money >= 0)
);
alter table orders add column shipping_method varchar(100);
alter table orders add column shipping_address varchar(100);
alter table orders add column shipping_date date;
alter table orders add column tracking_number varchar(100);
alter table orders add column payment_method varchar(100);
alter table orders add column active tinyint(1);
alter table orders
modify column status enum('pending', 'processing', 'shiped', 'delivered', 'cancelled') 
comment 'Trạng thái đơn hàng';
-- Một đơn hàng có thể có nhiểu thông tin sản phẩm, mỗi sản phẩm là một chi
-- tiết đơn hàng
create table order_details(
    id int primary key auto_increment,
    order_id int,
    foreign key (order_id) references orders(id),
    product_id int,
    foreign key (product_id) references products(id),
    price float check(price >= 0),
    number_of_products int check(number_of_products > 0),
    total_money float check(total_money >= 0),
    color varchar(20) default ''
);
