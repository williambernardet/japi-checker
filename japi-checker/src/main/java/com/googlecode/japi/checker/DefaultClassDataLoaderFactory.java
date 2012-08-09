package com.googlecode.japi.checker;


class DefaultClassDataLoaderFactory implements ClassDataLoaderFactory {

    @Override
    public ClassDataLoader createClassDataLoader() {
        return new DefaultClassDataLoader();
    }

}
