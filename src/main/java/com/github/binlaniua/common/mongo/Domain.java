package com.github.binlaniua.common.mongo;

import java.io.Serializable;

public interface Domain<ID extends Serializable> {

    ID getId();
}
