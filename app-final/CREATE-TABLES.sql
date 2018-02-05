CREATE TABLE log_reader.log_data (
	id INT not null auto_increment,
	execution_date datetime default null,
	ip varchar(15) default null,
	request varchar(200),
	status int,
	user_agent varchar(200),
	
	primary key (id)
);

CREATE TABLE log_reader.ip_blocked (
	id INT not null auto_increment,
	ip varchar(15) default null,
	reason varchar(200),
	primary key (id)
);