package com.briar.server.patterns.unitofwork;


public interface IUnitOfWork<T> {
    public boolean insert(T objectToInsert);

    public boolean modify(T objectToModify);

    public boolean delete(T objectToDelete);
}
