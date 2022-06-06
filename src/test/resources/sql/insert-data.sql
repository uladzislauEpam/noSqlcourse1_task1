insert into users values (default, 'Alan', 'alan@gmail.com'),
                         (default, 'Kate', 'kate@gmail.com'),
                         (default, 'Max', 'max@gmail.com'),
                         (default, 'Sara', 'sara@gmail.com'),
                         (default, 'Alex', 'alex@gmail.com'),
                         (default, 'Alex', 'anotheralex@gmail.com');

insert into events values (default, 'First event', '2022-05-18 15:30', 100),
                          (default, 'Second event', '2022-05-15, 21:00', 300),
                          (default, 'Third event', '2022-05-16 12:00', 500),
                          (default, 'Fourth event', '2022-05-15 21:00', 450),
                          (default, 'Third event', '2022-05-25 9:10', 1000),
                          (default, 'Fifth event', '2022-06-1 14:20', 230);

insert into tickets values (default, 1, 1, 10, 'BAR'),
                           (default, 4, 3, 2, 'PREMIUM'),
                           (default, 2, 2, 4, 'STANDARD'),
                           (default, 1, 4, 20, 'BAR'),
                           (default, 5, 1, 11, 'PREMIUM'),
                           (default, 3, 5, 1, 'STANDARD');

select * from events;