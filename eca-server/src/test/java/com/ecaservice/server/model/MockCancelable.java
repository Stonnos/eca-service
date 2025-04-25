package com.ecaservice.server.model;

public class MockCancelable implements CancelableTask {
    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void cancel() {
    }
}
