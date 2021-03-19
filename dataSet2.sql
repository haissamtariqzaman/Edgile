use lms;

insert into student values ('18L-0916', 'l180916@lhr.nu.edu.pk', '12345', 'Haissam', 'Zaman', '1999/1/1', 18,5,'CS'),
('18L-1164', 'l181164@lhr.nu.edu.pk', '12345', 'Zainab', 'Rehan', '1999/2/3', 18,5,'CS'),
('18L-1169', 'l181169@lhr.nu.edu.pk', '12345', 'Saad', 'Abdullah', '1999/3/10', 18,5,'CS'),
('17L-1324', 'l171324@lhr.nu.edu.pk', '12345', 'Talha', 'Jameel', '1998/12/12', 17,7,'EE'),
('17L-4321', 'l174321@lhr.nu.edu.pk', '12345', 'Hamas', 'Malik', '1997/4/9', 17,7,'CV');

insert into teacher values('saira.karim@nu.edu.pk', '12345', 'Saira', 'Karim', 'CS', '1986/8/25','Assistant Professor'),
('zareen.alamgir@nu.edu.pk', '12345', 'Zareen', 'Alamgir', 'CS', '1985/1/12','Assistant Professor'),
('aamir.raheem@nu.edu.pk', '12345', 'Aamir', 'Raheem', 'CS', '1987/3/23','Assistant Professor'),
('abeeda.akram@nu.edu.pk', '12345', 'Abeeda', 'Akram', 'CS', '1988/3/18','Assistant Professor'),
('lehmia.kiran@nu.edu.pk', '12345', 'Lehmia', 'Kiran', 'CS', '1983/2/16','Assistant Professor'),
('noshaba.nasir@nu.edu.pk', '12345', 'Noshaba', 'Nasir', 'CS', '1989/4/19','Assistant Professor'),
('sarim.baig@nu.edu.pk', '12345', 'Sarim', 'Baig', 'CS', '1981/6/23','Assistant Professor'),
('saad.farooq@nu.edu.pk', '12345', 'Saad', 'Farooq', 'CS', '1992/3/5','Lecturer');

insert into course values('CS123','Data Structures',3),
('CS324','Database',3),
('CS342','Operating System',3),
('CS442','Object Oriented Programming',3),
('CS515','Programming Fundamentals',3),
('CS617','Theory of Automata',3),
('CS766','Design and Analysis of Algorithm',3),
('CS811','Opearating System Lab',1),
('CS777', 'Discrete Structures', 3);

insert into section values(1,'CS123',null,'CS1232019A',3,40,0),
(2,'CS123',null,'CS1232019C',3,40,0),
(3,'CS123',null,'CS1232019B',3,40,0),
(4,'CS324',null,'CS3242020A',4,35,0),
(5,'CS324',null,'CS3242020B',4,35,0),
(6,'CS342',null,'CS3422020A',4,38,0),
(7,'CS342',null,'CS3422020B',4,38,0),
(8,'CS442',null,'CS4422019A',2,45,0),
(9,'CS515',null,'CS5152018A',1,35,0),
(10,'CS617','noshaba.nasir@nu.edu.pk','CS6172020A',5,42,1),
(11,'CS766',null,'CS7662020A',4,35,0),
(12,'CS811',null,'CS8112020A',4,40,0),
(13, 'CS777', null, 'CS7772020A',4,1,0),
(14,'CS123','saira.karim@nu.edu.pk','CS1232020A',3,40,1),
(15,'CS123','saira.karim@nu.edu.pk','CS1232020C',3,40,1),
(16,'CS123','abeeda.akram@nu.edu.pk','CS1232020B',3,40,1),
(17,'CS324','zareen.alamgir@nu.edu.pk','CS3242020A',4,35,1),
(18,'CS324','zareen.alamgir@nu.edu.pk','CS3242020B',4,35,1),
(19,'CS342','aamir.raheem@nu.edu.pk','CS3422020A',4,38,1),
(20,'CS342','aamir.raheem@nu.edu.pk','CS3422020B',4,38,1),
(21,'CS442','abeeda.akram@nu.edu.pk','CS4422020A',2,45,1),
(22,'CS515','lehmia.kiran@nu.edu.pk','CS5152020A',1,35,1),
(23,'CS766','sarim.baig@nu.edu.pk','CS7662020A',4,35,1),
(24,'CS811','saad.farooq@nu.edu.pk','CS8112020A',4,40,1),
(25, 'CS777', 'saira.karim@nu.edu.pk', 'CS7772020A',4,1,1);

insert into registeration values('18L-0916',10,5,'-'),
('18L-0916',1,3,'A'),
('18L-0916',4,3,'A-'),
('18L-0916',6,4,'B-'),
('18L-0916',8,2,'B+'),
('18L-0916',9,1,'A+'),
('18L-0916',12,4,'C'),
('18L-1164',10,5,'-'),
('18L-1164',1,3,'A+'),
('18L-1164',4,3,'A+'),
('18L-1164',6,4,'A+'),
('18L-1164',8,2,'A+'),
('18L-1164',9,1,'A+'),
('18L-1164',12,4,'A+'),
('18L-1169',10,5,'-'),
('18L-1169',1,3,'A'),
('18L-1169',4,3,'A-'),
('18L-1169',6,4,'B'),
('18L-1169',8,2,'B-'),
('18L-1169',9,1,'B+'),
('18L-1169',12,4,'A+'),
('17L-1324',10,5,'A+'),
('17L-1324',2,3,'B'),
('17L-1324',5,3,'B-'),
('17L-1324',7,4,'B+'),
('17L-1324',8,2,'C'),
('17L-1324',9,1,'A'),
('17L-1324',12,4,'B-'),
('17L-4321',10,5,'A+'),
('17L-4321',3,3,'B+'),
('17L-4321',5,3,'B'),
('17L-4321',7,4,'A-'),
('17L-4321',8,2,'C+'),
('17L-4321',9,1,'A+'),
('17L-4321',12,4,'B');

insert into evaluation (s_id,[name],[type],weightage,total_marks) 
values(1,'Quiz 1','Quiz',5,20),
(1,'Quiz 2','Quiz',5,10),
(1,'Sessional 1','Sessional',15,20),
(2,'Quiz 1','Quiz',3.33,5),
(2,'Quiz 2','Quiz',3.33,10),
(2,'Sessional 1','Sessional',15,60),
(3,'Quiz 1','Quiz',5,20),
(3,'Quiz 2','Quiz',5,10),
(3,'Sessional 1','Sessional',10,40),
(4,'Quiz 1','Quiz',3.33,20),
(4,'Quiz 2','Quiz',3.33,10),
(4,'Sessional 1','Sessional',15,30),
(5,'Quiz 1','Quiz',5,20),
(5,'Quiz 2','Quiz',5,15),
(5,'Project','Project',10,20),
(6,'Quiz 1','Quiz',5,20),
(6,'Quiz 2','Quiz',5,10),
(6,'Assignment 1','Assignemt',5,25),
(7,'Quiz 1','Quiz',5,20),
(7,'Quiz 2','Quiz',5,10),
(7,'Sessional 1','Sessional',15,40),
(8,'Quiz 1','Quiz',2,20),
(8,'Quiz 2','Quiz',2,10),
(8,'Sessional 1','Sessional',15,20),
(9,'Quiz 1','Quiz',5,20),
(9,'Quiz 2','Quiz',5,10),
(9,'Sessional 1','Sessional',15,20),
(10,'Quiz 1','Quiz',2,20),
(10,'Quiz 2','Quiz',2,10),
(10,'Sessional 1','Sessional',15,20),
(11,'Quiz 1','Quiz',5,20),
(11,'Quiz 2','Quiz',5,10),
(11,'Sessional 1','Sessional',20,20),
(12,'Quiz 1','Quiz',3.33,20),
(12,'Quiz 2','Quiz',3.33,10),
(12,'Sessional 1','Sessional',15,20);

insert into attendance values('2018/4/23','18L-0916',9,'P',1.5),
('2018/4/23','18L-1164',9,'P',1.5),
('2018/4/23','18L-1169',9,'P',1.5),
('2018/4/25','18L-0916',9,'P',1.5),
('2018/4/25','18L-1164',9,'P',1.5),
('2018/4/25','18L-1169',9,'P',1.5),
('2020/11/18','18L-0916',10,'P',1.5),
('2020/11/18','18L-1164',10,'P',1.5),
('2020/11/18','18L-1169',10,'P',1.5),
('2020/11/18','17L-1324',10,'P',1.5),
('2020/11/18','17L-4321',10,'P',1.5),
('2020/11/20','18L-0916',10,'P',1.5),
('2020/11/20','18L-1164',10,'P',1.5),
('2020/11/20','18L-1169',10,'P',1.5),
('2020/11/20','17L-1324',10,'P',1.5),
('2020/11/20','17L-4321',10,'P',1.5),
('2020/11/24','18L-0916',10,'P',1.5),
('2020/11/24','18L-1164',10,'P',1.5),
('2020/11/24','18L-1169',10,'P',1.5),
('2020/11/24','17L-1324',10,'P',1.5),
('2020/11/24','17L-4321',10,'P',1.5);

insert into marks values('18L-1164',3,90),
('18L-1164',10,20),
('18L-0916',26,10),
('18L-1169',35,10),
('17L-1324',28,20),
('17L-4321',30,20),
('17L-4321',27,20),
('18L-0916',28,18),
('18L-0916',29,5),
('18L-0916',30,15),
('18L-0916',31,12),
('18L-0916',32,9),
('18L-0916',33,19);

insert into waiting_list values('18L-0916',13,'2020-12-09T08:00:00'),
('18L-1164',13,'2020-12-09T09:00:00');

insert into imp_dates values(1,'REG OPEN','2020-12-24T23:59:59','FALL2020'),
(2,'REG CLOSED','2020-12-30T23:59:59','FALL2020'),
(3,'WITHDRAW DEADLINE', '2021-01-01T23:59:59','FALL2020');