create table USER_TABLE
(
    USER_ID    binary(16)                              not null comment '유저아이디(UUID)',
    CREATED_AT timestamp default current_timestamp() null comment '생성시간',
    primary key (USER_ID)
)
    comment '유저테이블';

create table PLACE
(
    PLACE_ID   binary(16)                              not null comment '장소아이디(UUID)',
    CREATED_AT timestamp default current_timestamp() not null comment '생성일시',
    primary key (PLACE_ID)
) ENGINE = InnoDB
    comment '장소 테이블';

create table REVIEW
(
    REVIEW_ID  binary(16)                              not null comment '리뷰아이디(UUID)',
    CONTENT    text                                  null comment '리뷰내용',
    USER_ID    binary(16)                              not null comment '유저아이디(UUID)',
    PLACE_ID   binary(16)                              not null comment '장소아이디(UUID)',
    CREATED_AT timestamp default current_timestamp() null comment '생성시간',
    UPDATED_AT timestamp                             null comment '수정시간',
    primary key (REVIEW_ID),
    constraint REVIEW_PLACE_PLACE_ID_FK
        foreign key (PLACE_ID) references PLACE (PLACE_ID)
            on update cascade on delete cascade,
    constraint REVIEW_USER_USER_ID_FK
        foreign key (USER_ID) references USER_TABLE (USER_ID)
            on update cascade on delete cascade,
    INDEX REVIEW_USER_ID_PLACE_ID_IDX(USER_ID, PLACE_ID)
) ENGINE = InnoDB
    comment '리뷰 테이블';

create table PHOTO
(
    PHOTO_ID   binary(16)                              not null comment '포토아이디(UUID)',
    REVIEW_ID  binary(16)                              not null comment '리뷰아이디(UUID) FK',
    CREATED_AT timestamp default current_timestamp() not null comment '생성시간',
    primary key (PHOTO_ID),
    constraint PHOTO_REVIEW_REVIEW_ID_FK
        foreign key (REVIEW_ID) references REVIEW (REVIEW_ID)
            on delete cascade
) ENGINE = InnoDB
    comment '리뷰 첨부 이미지 테이블';

create table POINT_HISTORY
(
    HISTORY_ID binary(16)  comment '히스토리아이디(UUID)',
    USER_ID    binary(16)                              not null comment '유저아이디(UUID)',
    REVIEW_ID  binary(16)                              not null comment '리뷰아이디(UUID)',
    POINT      int                                   not null comment '포인트 변동 값',
    TAG        char(1)                               not null comment '포인트 변동 태그(C:내용, I:이미지, B:첫글)',
    REASON     varchar(50)                           not null comment '포인트 변동 이유(작성,삭제 등..)',
    CREATED_AT timestamp default current_timestamp() null comment '생성일시',
    primary key (HISTORY_ID),
    INDEX POINT_HISTORY_USER_ID_IDX(USER_ID),
    INDEX POINT_HISTORY_REVIEW_ID_TAG_IDX(REVIEW_ID, TAG)
) ENGINE = InnoDB
    comment '포인트 적립 히스토리';
