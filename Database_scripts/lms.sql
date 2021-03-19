create database lms
go
use lms
go

create table teacher
(
email varchar(50) primary key not null,
[password] varchar(50) not null,
f_name varchar(15) not null,
l_name varchar(15) not null,
department varchar(30) not null,
dob date not null,
[rank] varchar(30)
)
go

create table student
(
roll_no varchar(20) primary key not null,
email varchar(50) unique not null,
[password] varchar(50) not null,
f_name varchar(15) not null,
l_name varchar(15) not null,
dob date not null,
batch int not null,
c_semester int not null,
discipline varchar(30) not null
)
go

create table imp_dates
(
id int primary key not null,
[name] varchar(30) not null,
[date] datetime not null,
semester varchar(10) not null
)
go

create table course
(
c_id varchar(20) primary key not null,
c_name varchar(50) not null,
crhr int not null
)
go

create table section
(
s_id int primary key not null,
c_id varchar(20) foreign key references course(c_id) on delete cascade on update cascade not null,
teacher_email varchar(50) foreign key references teacher(email) on update cascade,
section varchar(20) not null,
semester int not null,
max_students int not null,
offered bit
)
go

create table waiting_list
(
roll_no varchar(20) foreign key references student(roll_no) on delete cascade on update cascade not null,
sec_id int foreign key references section(s_id) on delete cascade on update cascade not null,
date_time Datetime not null,
constraint pk_waiting_list primary key(roll_no,sec_id)
)
go

create table registeration
(
roll_no varchar(20) foreign key references student(roll_no) on update cascade not null,
sec_id int foreign key references section(s_id) on update cascade not null,
std_semester int not null,
grade varchar(2),
constraint pk_registeration primary key(roll_no,sec_id)
)
go

create table attendance
(
[date] date not null,
roll_no varchar(20) foreign key references student(roll_no) on update cascade not null,
sec_id int foreign key references section(s_id) on update cascade not null,
presence char not null,
duration float not null,
constraint pk_attendance primary key([date],roll_no,sec_id)
)
go

create table evaluation
(
e_id int identity(1,1) primary key not null,
s_id int foreign key references section(s_id) on update cascade on delete cascade not null,
[name] varchar(50) not null,
[type] varchar(50) not null,
weightage decimal not null,
total_marks int not null
)
go

create table marks
(
roll_no varchar(20) foreign key references student(roll_no) on update cascade not null,
e_id int foreign key references evaluation(e_id) on delete cascade on update cascade not null, 
score decimal not null,
constraint pk_marks primary key(roll_no,e_id)
)
go