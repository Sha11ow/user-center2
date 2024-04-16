create table course
(
    course_id   int auto_increment
        primary key,
    course_name varchar(255) not null
)
    charset = utf8mb3
    row_format = DYNAMIC;

create table course_schedule
(
    semester   int          not null comment '01-春，02-夏，03-秋，04-冬',
    course_id  int          not null,
    teacher_id int          not null,
    time       varchar(255) not null,
    capacity   int          not null,
    primary key (semester, course_id, teacher_id)
)
    charset = utf8mb3
    row_format = DYNAMIC;

create table course_selection
(
    semester   int not null comment '01-春，02-夏，03-秋，04-冬',
    course_id  int not null,
    teacher_id int not null,
    student_id int not null,
    primary key (semester, course_id, teacher_id, student_id)
)
    charset = utf8mb3
    row_format = DYNAMIC;

create table score
(
    semester   int not null comment '01-春，02-夏，03-秋，04-冬',
    course_id  int not null,
    student_id int not null,
    score      int null,
    primary key (semester, course_id, student_id)
)
    charset = utf8mb3
    row_format = DYNAMIC;

create table user
(
    id       int auto_increment
        primary key,
    password varchar(255) not null,
    name     varchar(255) not null,
    role     int          not null comment '0-管理员，1-学生，2-老师'
)
    charset = utf8mb3
    row_format = DYNAMIC;


