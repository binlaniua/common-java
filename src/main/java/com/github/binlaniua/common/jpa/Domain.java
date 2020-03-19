package com.github.binlaniua.common.jpa;

import java.io.Serializable;

public interface Domain<ID extends Serializable> {

    ID getId();
}
