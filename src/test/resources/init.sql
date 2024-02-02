drop table if exists borrows;
drop table if exists books;
drop table if exists users;

create table books(
   id int GENERATED ALWAYS AS IDENTITY,
   code char(20) unique not null,
   isbn char(20) not null,
   primary key (id)
);

create table users(
   id int GENERATED ALWAYS AS IDENTITY,
   account char(20) unique not null,
   name char(20) not null,
   primary key(id)
);

create table borrows(
   id int GENERATED ALWAYS AS IDENTITY,
   user_id int not null,
   book_id int not null,
   borrowed date not null,
   expired date not null,
   returned date,
   constraint user_fk foreign key(user_id) references users(id),
   constraint book_fk foreign key(book_id) references books(id)
);


insert into books(code, isbn) values ('a1','1338878921');
insert into books(code, isbn) values ('a2','1338878921');
insert into books(code, isbn) values ('b1','9780439358064');
insert into books(code, isbn) values ('b2','9780439358064');
insert into books(code, isbn) values ('b3','9780439358064');


insert into users(account, name) values ('1234', 'Anne');
insert into users(account, name) values ('2345', 'Bob');
insert into users(account, name) values ('2468', 'Chai');
