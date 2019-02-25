package com.toba;

import java.util.Optional;

public interface IterableDataSource<T> {
    Optional<T> get();
}
