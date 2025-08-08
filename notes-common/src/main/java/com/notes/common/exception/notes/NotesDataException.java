package com.notes.common.exception.notes;

public class NotesDataException extends NotesException{
    public NotesDataException(Object[] args) {
        super("数据异常，不存在", args);
    }
}
