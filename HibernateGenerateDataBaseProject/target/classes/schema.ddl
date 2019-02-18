
    create table hibernate_sequence (
        next_val bigint
    );

    insert into hibernate_sequence values ( 1 );

    create table PassengerVehicle2 (
        idVehicle integer not null,
        manufacturer varchar(255),
        noOfpassengers integer not null,
        primary key (idVehicle)
    );

    create table PassengerVehicle3 (
        noOfpassengers integer not null,
        idVehicle integer not null,
        primary key (idVehicle)
    );

    create table Player (
        id bigint not null auto_increment,
        name varchar(255),
        num integer,
        position varchar(255),
        team_id bigint not null,
        primary key (id)
    );

    create table post (
        id bigint not null auto_increment,
        version integer,
        title varchar(255),
        primary key (id)
    );

    create table post_comment (
        id bigint not null auto_increment,
        version integer,
        review varchar(255),
        post_id bigint,
        primary key (id)
    );

    create table post_details (
        id bigint not null auto_increment,
        version integer,
        created_by varchar(255),
        created_on date,
        post_id bigint,
        primary key (id)
    );

    create table post_tag (
        post_id bigint not null,
        tag_id bigint not null,
        primary key (post_id, tag_id)
    );

    create table tag (
        id bigint not null auto_increment,
        version integer,
        name varchar(255),
        primary key (id)
    );

    create table Team (
        id bigint not null auto_increment,
        name varchar(255),
        primary key (id)
    );

    create table TransportationVehicle2 (
        idVehicle integer not null,
        manufacturer varchar(255),
        loadCapacity integer not null,
        primary key (idVehicle)
    );

    create table TransportationVehicle3 (
        loadCapacity integer not null,
        idVehicle integer not null,
        primary key (idVehicle)
    );

    create table Vehicle (
        VEHICLE_TYPE varchar(31) not null,
        idVehicle integer not null auto_increment,
        manufacturer varchar(255),
        loadCapacity integer,
        noOfpassengers integer,
        primary key (idVehicle)
    );

    create table Vehicle3 (
        idVehicle integer not null auto_increment,
        manufacturer varchar(255),
        primary key (idVehicle)
    );

    alter table PassengerVehicle3 
        add constraint FKe0s4hywmsfd4r9vydm8xs422y 
        foreign key (idVehicle) 
        references Vehicle3 (idVehicle);

    alter table Player 
        add constraint FKqfn7q18rx1dwkwui2tyl30e08 
        foreign key (team_id) 
        references Team (id);

    alter table post_comment 
        add constraint FKna4y825fdc5hw8aow65ijexm0 
        foreign key (post_id) 
        references post (id);

    alter table post_details 
        add constraint FKmcgdm1k7iriyxsq4kukebj4ei 
        foreign key (post_id) 
        references post (id);

    alter table post_tag 
        add constraint FKac1wdchd2pnur3fl225obmlg0 
        foreign key (tag_id) 
        references tag (id);

    alter table post_tag 
        add constraint FKc2auetuvsec0k566l0eyvr9cs 
        foreign key (post_id) 
        references post (id);

    alter table TransportationVehicle3 
        add constraint FK71mvivid3q9w7nrweliw1t42o 
        foreign key (idVehicle) 
        references Vehicle3 (idVehicle);

    create table hibernate_sequence (
        next_val bigint
    );

    insert into hibernate_sequence values ( 1 );

    create table JOINED_PassengerVehicle (
        noOfpassengers integer not null,
        idVehicle integer not null,
        primary key (idVehicle)
    );

    create table JOINED_TransportationVehicle (
        loadCapacity integer not null,
        idVehicle integer not null,
        primary key (idVehicle)
    );

    create table JOINED_Vehicle (
        idVehicle integer not null auto_increment,
        manufacturer varchar(255),
        primary key (idVehicle)
    );

    create table Player (
        id bigint not null auto_increment,
        name varchar(255),
        num integer,
        position varchar(255),
        team_id bigint not null,
        primary key (id)
    );

    create table post (
        id bigint not null auto_increment,
        version integer,
        title varchar(255),
        primary key (id)
    );

    create table post_comment (
        id bigint not null auto_increment,
        version integer,
        review varchar(255),
        post_id bigint,
        primary key (id)
    );

    create table post_details (
        id bigint not null auto_increment,
        version integer,
        created_by varchar(255),
        created_on date,
        post_id bigint,
        primary key (id)
    );

    create table post_tag (
        post_id bigint not null,
        tag_id bigint not null,
        primary key (post_id, tag_id)
    );

    create table SINGLE_TABLE_Vehicle (
        VEHICLE_TYPE varchar(31) not null,
        idVehicle integer not null auto_increment,
        manufacturer varchar(255),
        noOfpassengers integer,
        loadCapacity integer,
        primary key (idVehicle)
    );

    create table TABLE_PER_CLASS_PassengerVehicle (
        idVehicle integer not null,
        manufacturer varchar(255),
        noOfpassengers integer not null,
        primary key (idVehicle)
    );

    create table TABLE_PER_CLASS_TransportationVehicle (
        idVehicle integer not null,
        manufacturer varchar(255),
        loadCapacity integer not null,
        primary key (idVehicle)
    );

    create table tag (
        id bigint not null auto_increment,
        version integer,
        name varchar(255),
        primary key (id)
    );

    create table Team (
        id bigint not null auto_increment,
        name varchar(255),
        primary key (id)
    );

    alter table JOINED_PassengerVehicle 
        add constraint FKa45vd30r5d698kp97sbitv3ph 
        foreign key (idVehicle) 
        references JOINED_Vehicle (idVehicle);

    alter table JOINED_TransportationVehicle 
        add constraint FK4hrsnbbqvxlsigs7g4wc6ns4f 
        foreign key (idVehicle) 
        references JOINED_Vehicle (idVehicle);

    alter table Player 
        add constraint FKqfn7q18rx1dwkwui2tyl30e08 
        foreign key (team_id) 
        references Team (id);

    alter table post_comment 
        add constraint FKna4y825fdc5hw8aow65ijexm0 
        foreign key (post_id) 
        references post (id);

    alter table post_details 
        add constraint FKmcgdm1k7iriyxsq4kukebj4ei 
        foreign key (post_id) 
        references post (id);

    alter table post_tag 
        add constraint FKac1wdchd2pnur3fl225obmlg0 
        foreign key (tag_id) 
        references tag (id);

    alter table post_tag 
        add constraint FKc2auetuvsec0k566l0eyvr9cs 
        foreign key (post_id) 
        references post (id);
