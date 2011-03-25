
create table media (_id primary key autoincrement, playlist foreign key autoincrement, title text, description text, mediaId text unique, stars integer);
create table playlist_members (_id primary key autoincrement integer , playlistId foreign key integer, mediaId foreign key integer);
create table playlist (_id primary key autoincrement, title text, description text);

drop table playlist;

select media.title, media.description from media;

select media.title, media.description from media, playlist where playlist.title = 'Joes Top Hits';

select title, description from media order by title;

select title, description group by artist having (stars > 3);

update media set stars = 3 where title = 'Joes Favorite Song';
