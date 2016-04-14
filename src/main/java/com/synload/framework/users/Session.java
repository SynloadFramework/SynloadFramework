package com.synload.framework.users;

import com.synload.framework.modules.annotations.sql.BigIntegerColumn;
import com.synload.framework.modules.annotations.sql.HasOne;
import com.synload.framework.modules.annotations.sql.SQLTable;
import com.synload.framework.modules.annotations.sql.StringColumn;

@SQLTable(name = "Session Model", version = 1.0, description = "keeps login data")
public class Session {
    @BigIntegerColumn(length = 20)
    public long id;

    @StringColumn(length = 255)
    public String ip;

    @StringColumn(length = 128)
    public String session;

    @HasOne(key = "id", of = User.class)
    @BigIntegerColumn(length = 20)
    public long user;
}
