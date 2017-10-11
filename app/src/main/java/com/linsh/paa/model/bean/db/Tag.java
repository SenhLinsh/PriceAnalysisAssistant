package com.linsh.paa.model.bean.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/11
 *    desc   :
 * </pre>
 */
public class Tag extends RealmObject implements Sortable {

    @PrimaryKey
    private String name;
    private int sort;

    @Deprecated
    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
