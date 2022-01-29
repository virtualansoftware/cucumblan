package io.virtualan.cucumblan.props.hook;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventHandler;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestRunStarted;
import io.virtualan.cucumblan.props.util.ScenarioContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class FeatureScope implements ConcurrentEventListener {

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestRunStarted.class, setup);
        eventPublisher.registerHandlerFor(TestRunFinished.class, teardown);
    }

    private EventHandler<TestRunStarted> setup = event -> {
        beforeAll();
    };

    private void beforeAll() {
        ScenarioContext.setContext(String.valueOf(Thread.currentThread().getId()), new HashMap<>());
    }

    private EventHandler<TestRunFinished> teardown = event -> {
        afterAll();
    };

    private void afterAll() {
        ScenarioContext.remove(String.valueOf(Thread.currentThread().getId()));
    }
}