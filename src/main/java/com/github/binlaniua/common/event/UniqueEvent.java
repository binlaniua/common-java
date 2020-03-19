package com.github.binlaniua.common.event;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@Setter
@Getter
public class UniqueEvent<ID extends Serializable> extends Event {

    private ID id;

    public UniqueEvent(final ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final UniqueEvent<?> that = (UniqueEvent<?>) o;
        return new EqualsBuilder()
                .append(this.id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(this.id)
                .toHashCode();
    }
}
