package com.googlecode.japi.checker.maven.plugin;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.logging.Log;

public class RecorderLog implements Log {
    public enum Kind {ERROR, WARNING, INFO, DEBUG};
    private Log delegate;
    private List<Record> records = new ArrayList<Record>();
    
    public RecorderLog(Log delegate) {
        this.delegate = delegate;
    }

    public RecorderLog(Mojo mojo) {
        this(mojo.getLog());
        mojo.setLog(this);
    }
    
    @Override
    public void debug(CharSequence arg0) {
        this.delegate.debug(arg0);
        records.add(new Record(Kind.DEBUG, arg0, null));
    }

    @Override
    public void debug(Throwable arg0) {
        this.delegate.debug(arg0);
        records.add(new Record(Kind.DEBUG, null, arg0));
    }

    @Override
    public void debug(CharSequence arg0, Throwable arg1) {
        this.delegate.debug(arg0, arg1);
        records.add(new Record(Kind.DEBUG, arg0, arg1));
 }

    @Override
    public void error(CharSequence arg0) {
        this.delegate.error(arg0);
        records.add(new Record(Kind.ERROR, arg0, null));
    }

    @Override
    public void error(Throwable arg0) {
        this.delegate.error(arg0);
        records.add(new Record(Kind.ERROR, null, arg0));
    }

    @Override
    public void error(CharSequence arg0, Throwable arg1) {
        this.delegate.error(arg0, arg1);
        records.add(new Record(Kind.ERROR, arg0, arg1));
    }

    @Override
    public void info(CharSequence arg0) {
        this.delegate.info(arg0);
        records.add(new Record(Kind.INFO, arg0, null));
    }

    @Override
    public void info(Throwable arg0) {
        this.delegate.info(arg0);
        records.add(new Record(Kind.INFO, null, arg0));
    }

    @Override
    public void info(CharSequence arg0, Throwable arg1) {
        this.delegate.info(arg0, arg1);
        records.add(new Record(Kind.INFO, arg0, arg1));
    }

    @Override
    public boolean isDebugEnabled() {
        return this.delegate.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return this.delegate.isErrorEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return this.delegate.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return this.delegate.isWarnEnabled();
    }

    @Override
    public void warn(CharSequence arg0) {
        this.delegate.warn(arg0);
        records.add(new Record(Kind.WARNING, arg0, null));
    }

    @Override
    public void warn(Throwable arg0) {
        this.delegate.warn(arg0);
        records.add(new Record(Kind.WARNING, null, arg0));
    }

    @Override
    public void warn(CharSequence arg0, Throwable arg1) {
        this.delegate.warn(arg0, arg1);
        records.add(new Record(Kind.WARNING, arg0, arg1));
    }
    
    
    public boolean contains(Kind kind, String substring) {
        for (Record record : records) {
            if (record.kind == kind && record.line != null && record.line.contains(substring)) {
                return true;
            }
        }
        return false;
    }
    
    class Record {
        public Kind kind;
        public String line;
        public Throwable throwable;
        public Record(Kind kind, CharSequence line, Throwable throwable) {
            this.kind = kind;
            this.line = line.toString();
            this.throwable = throwable;
        }
    }

}
