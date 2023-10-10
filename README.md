# xid
common sequence base on database

```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.jsbxyyx</groupId>
    <artifactId>xid</artifactId>
    <version>main-SNAPSHOT</version>
</dependency>

create table tb_sequence (
    seq_name varchar(128) not null,
    current_val bigint(20) not null,
    increment_val int(11) not null default '1',
    primary key(seq_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

insert into tb_sequence values ('xid', 0, 1);
```

# sample
```
// only once
Xid.setDataSource(dataSource);

long id = Xid.get();
```