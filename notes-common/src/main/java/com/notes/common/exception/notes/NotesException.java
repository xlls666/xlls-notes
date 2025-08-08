package com.notes.common.exception.notes;

import com.notes.common.exception.base.BaseException;

public class NotesException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public NotesException(String code, Object[] args)
    {
        super("notes", code, args, null);
    }

}